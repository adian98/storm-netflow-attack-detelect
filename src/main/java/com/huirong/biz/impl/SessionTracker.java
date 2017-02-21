package com.huirong.biz.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huirong.biz.BusinessLogic;
import com.huirong.storage.vo.NetflowRecord;
import com.huirong.storage.vo.TimeDurationPair;
import com.huirong.storage.vo.UnidirectionalSessionRecord;
import com.huirong.storage.StorageManager;
import com.huirong.storage.vo.FlowInfo;
import com.huirong.util.EndTimeComparator;
import com.huirong.util.FlowDirectionEnum;
import com.huirong.util.NetflowSource;
import com.huirong.util.TimeWindowComparator;
import com.huirong.util.NetflowTools;
import com.huirong.util.StartTimeComparator;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月14日
 */
public class SessionTracker implements BusinessLogic {

	// 单向会话超时时间. 我们假设无论面对怎样的数据采样过程, 一个持续活跃的单向session一定会在连续的五个时间窗口中至少出现一次  
	public final static int SESSION_TIME_OUT_THRESHOLD = 8; 
	public final static double COMPENSATION_FACTOR = 0.6; // 补偿因采样而造成的数据丢失
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private StorageManager storageManager;
	private NetflowSource source;
	private List<String> tupleFieldList;
	private int SAMPLE_RATIO;
	private Map<String, UnidirectionalSessionRecord> backwardSessionCache;  // key是flow 5元组
	private Map<String, UnidirectionalSessionRecord> forwardSessionCache;
	private Map<String, List<UnidirectionalSessionRecord>> backwardCacheByLastUpdate; // key是最近一次更新时间戳
	private Map<String, List<UnidirectionalSessionRecord>> forwardCacheByLastUpdate;
	private String[] currentBackwardTimeWindow = new String[SESSION_TIME_OUT_THRESHOLD];
	private String[] currentForwardTimeWindow = new String[SESSION_TIME_OUT_THRESHOLD];
	int numOfBackwardTimeWindow = 0;   // currentTimeWindow中有效元素个数...
	int numOfForwardTimeWindow = 0;
	
	public void execute(Tuple tuple, OutputCollector collector) {
		try {
			
			String timeFrame = tuple.getStringByField(this.tupleFieldList.get(0));
			byte[] bytes = tuple.getBinaryByField(this.tupleFieldList.get(1));
			String key = tuple.getStringByField(this.tupleFieldList.get(2));

			
			ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
			ObjectInputStream inputStream = new ObjectInputStream(byteArray);
			
			NetflowRecord nfRecord = (NetflowRecord) inputStream.readObject();
			inputStream.close();

			
			FlowInfo info = null;
			
			if(this.source == NetflowSource.EDU)
				info = NetflowTools.recogniseEduFlow(nfRecord);
			else if(this.source == NetflowSource.TJUT)
				info = NetflowTools.recogniseTjutFlow(nfRecord);
			
			
			if(info.getDirection() == FlowDirectionEnum.IN){
				int ret = process(info, backwardSessionCache, backwardCacheByLastUpdate, nfRecord, 
						numOfBackwardTimeWindow, currentBackwardTimeWindow, timeFrame,
						timeWindowInBackwardCacheNow(timeFrame), key);
				
				if(ret != 0)
					numOfBackwardTimeWindow = ret;
			}
			else if(info.getDirection() == FlowDirectionEnum.OUT){
				int ret = process(info, forwardSessionCache, forwardCacheByLastUpdate, nfRecord,
						numOfForwardTimeWindow, currentForwardTimeWindow, timeFrame,
						timeWindowInForwardCacheNow(timeFrame), key);
				
				if(ret != 0)
					numOfForwardTimeWindow = ret;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	int process(FlowInfo info, Map<String, UnidirectionalSessionRecord> sessionCache, 
			Map<String, List<UnidirectionalSessionRecord>> lastUpdateCache, NetflowRecord record,
			int numOfTimeWindow, String[] currentTimeWindow,
			String timeFrame, boolean inTimeWindowNow, String key) throws ParseException{
		
		try {
			if(!inTimeWindowNow){
				// 发现新时间窗口
				
				if(!isLegalTimeFrame(timeFrame, numOfTimeWindow, currentTimeWindow)){
					return numOfTimeWindow;
				}
				
				List<UnidirectionalSessionRecord> tmp = new ArrayList<UnidirectionalSessionRecord>();
				lastUpdateCache.put(timeFrame, tmp);
				
				if(numOfTimeWindow < SESSION_TIME_OUT_THRESHOLD){
					currentTimeWindow[numOfTimeWindow ++] = timeFrame;
				}
				else{
					String toBeExpire = currentTimeWindow[0];
					currentTimeWindow[0] = timeFrame;
					
					expireSessionRecord(info.getDirection(), toBeExpire, sessionCache, lastUpdateCache);
				}
				
				if(numOfTimeWindow == SESSION_TIME_OUT_THRESHOLD)
					Arrays.sort(currentTimeWindow, new TimeWindowComparator());
				
			}
			

			// 更新sessionCache和lastUpdateCache
			
			if(!sessionCache.containsKey(key)){
				UnidirectionalSessionRecord uds = new UnidirectionalSessionRecord();
				uds.setProtocol(record.getProtocol());
				uds.setSrcIp(record.getSrcIp());
				uds.setSrcPort(record.getSrcPort());
				uds.setDstIp(record.getDstIp());
				uds.setDstPort(record.getDstPort());
				uds.setPackets(0);
				uds.setBytes(0);
				uds.setKey(key);
				
				sessionCache.put(key, uds);
			}
			
			UnidirectionalSessionRecord usr = sessionCache.get(key);
			String lastTimestamp = usr.getLastUpdate();
			
			usr.setPackets(usr.getPackets() + record.getPackets());
			usr.setBytes(usr.getBytes() + record.getBytes());
			usr.setLastUpdate(timeFrame);
			
			TimeDurationPair pair = new TimeDurationPair();
			pair.setTime(record.getDate());
			pair.setDuration(record.getDuration());
			
			Calendar c1 = Calendar.getInstance();
			String[] tmp1 = pair.getTime().split(" ");
			String[] tmp2 = tmp1[0].split("-");
			String[] tmp3 = tmp1[1].split(":");
			c1.set(Integer.parseInt(tmp2[0]), Integer.parseInt(tmp2[1]) - 1, Integer.parseInt(tmp2[2]),
					Integer.parseInt(tmp3[0]), Integer.parseInt(tmp3[1]), Integer.parseInt(tmp3[2]));
			c1.add(Calendar.SECOND, (int)pair.getDuration());
			
			pair.setEndTimeStr(sdf.format(c1.getTime()));
			
			usr.getCache().add(pair);
			
			if(lastTimestamp != null){
				lastUpdateCache.get(lastTimestamp).remove(usr);
			}
			
			lastUpdateCache.get(timeFrame).add(usr);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return numOfTimeWindow;
	}
	
	void expireSessionRecord(FlowDirectionEnum direction, String expireTime, Map<String, UnidirectionalSessionRecord> sessionCache, 
			Map<String, List<UnidirectionalSessionRecord>> lastUpdateCache){
		
		try {
			List<UnidirectionalSessionRecord> ls = lastUpdateCache.get(expireTime);
			
			completeInformation(ls);
			
			if(direction == FlowDirectionEnum.IN)
				this.storageManager.addBackwardSessionRecord(ls);
			else if(direction == FlowDirectionEnum.OUT)
				this.storageManager.addForwardSessionRecord(ls);
			
			for(UnidirectionalSessionRecord u : ls){
				sessionCache.remove(u.getKey());
			}
			
			lastUpdateCache.remove(expireTime);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	
	void completeInformation(List<UnidirectionalSessionRecord> ls) {
		try {
			for(UnidirectionalSessionRecord u : ls){
				
				TimeDurationPair[] tmp = u.getCache().toArray(new TimeDurationPair[0]);
				
				Arrays.sort(tmp, new StartTimeComparator());
				String startTime = tmp[0].getTime();
				
				Arrays.sort(tmp, new EndTimeComparator());
				String endTime = tmp[tmp.length - 1].getEndTimeStr();
				
				u.setStartTime(startTime);
				u.setEndTime(endTime);
				
				Date startD = sdf.parse(startTime);
				Date endD = sdf.parse(endTime);
				long diff = endD.getTime() - startD.getTime();
				double seconds = diff / 1000;
				
				u.setDuration(seconds);  // 以秒为单位
				
				if(seconds == 0){
					seconds = 1;
				}
				
				double bps = u.getBytes() * 8 * SAMPLE_RATIO * COMPENSATION_FACTOR / 1024L * 1024 * seconds; 
				double bpp = u.getBytes() / u.getPackets();
				double pps = u.getPackets() / seconds;
				
				u.setBps(bps);
				u.setBpp((int) bpp);
				u.setPps((int) pps);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void prepare(Map config,NetflowSource from, StorageManager storageManager, List<String> inputFieldList) {
		this.backwardSessionCache = new HashMap<String, UnidirectionalSessionRecord>();
		this.forwardSessionCache = new HashMap<String, UnidirectionalSessionRecord>();
		this.backwardCacheByLastUpdate = new HashMap<String, List<UnidirectionalSessionRecord>>();
		this.forwardCacheByLastUpdate = new HashMap<String, List<UnidirectionalSessionRecord>>();
		
		this.storageManager = storageManager;
		this.source = from;
		this.tupleFieldList = inputFieldList;
		if(this.source == NetflowSource.EDU)
			this.SAMPLE_RATIO = 300;
		else if(this.source == NetflowSource.TJUT)
			this.SAMPLE_RATIO = 1000;
		
	}

	boolean isLegalTimeFrame(String timeFrame, int num, String[] timeWindow) throws ParseException{
		try {
			if(num < SESSION_TIME_OUT_THRESHOLD)
				return true;
			
			Date d1 = sdf.parse(timeWindow[0]);
			Date d2 = sdf.parse(timeFrame);
			
			return d1.before(d2);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return false;
	}
	
	public boolean timeWindowInBackwardCacheNow(String timeFlag){
		for(String s : currentBackwardTimeWindow){
			if(s != null && s.equals(timeFlag)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean timeWindowInForwardCacheNow(String timeFlag){
		for(String s : currentForwardTimeWindow){
			if(s != null && s.equals(timeFlag)){
				return true;
			}
		}
		
		return false;
	}
	
	public void cleanup(){
		
		/*if(this.backwardCacheByLastUpdate != null){
			for(Entry<String, List<UnidirectionalSessionRecord>> e : this.backwardCacheByLastUpdate.entrySet()){
				String time = e.getKey();
				expireSessionRecord(FlowDirectionEnum.IN, time, this.backwardSessionCache, this.backwardCacheByLastUpdate);
			}
		}
		
		if(this.forwardCacheByLastUpdate != null){
			for(Entry<String, List<UnidirectionalSessionRecord>> e : this.forwardCacheByLastUpdate.entrySet()){
				String time = e.getKey();
				expireSessionRecord(FlowDirectionEnum.OUT, time, this.forwardSessionCache, this.forwardCacheByLastUpdate);
			}
		}*/
		
		this.backwardCacheByLastUpdate.clear();
		this.backwardCacheByLastUpdate = null;
		this.backwardSessionCache.clear();
		this.backwardSessionCache = null;
		
		this.forwardCacheByLastUpdate.clear();
		this.forwardCacheByLastUpdate = null;
		this.forwardSessionCache.clear();
		this.forwardSessionCache = null;
		
	}

}
