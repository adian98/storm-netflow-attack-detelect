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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.huirong.MainTopology;
import com.huirong.biz.BusinessLogic;
import com.huirong.storage.vo.AttackEvent;
import com.huirong.storage.vo.NetflowRecord;
import com.huirong.storage.vo.RecognisedAttackCacheObj;
import com.huirong.util.AttackType;
import com.huirong.util.NetflowSource;
import com.huirong.storage.StorageManager;
import com.huirong.util.NetflowTools;
import com.huirong.util.TimeWindowComparator;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月3日
 */
public class AttackDetectionRules implements BusinessLogic {
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static int WINDOW_SIZE = 3;
	public final static String ES_PUSH_CMD_PREFIX = "curl -XPOST '59.67.152.231:9200/_bulk' -d '\n";
	
	// 默认参数
	private long SYN_THRESHOLD = 10;
	private long ACK_THRESHOLD = 10;
	private long ICMP_THRESHOLD = 10;
	private long NTP_THRESHOLD = 10;
	private boolean dropOutterIp = true;
	
	private StorageManager storageManager ;
	private NetflowSource source;
	private List<String> tupleFieldList;

	/**
	 * key	时间窗口
	 * value	RecognisedAttackCacheObj ->	代表一分钟内的攻击事件统计信息
	 */
	Map<String, RecognisedAttackCacheObj> attackCache = new HashMap<String, RecognisedAttackCacheObj>();
	String[] currentTimeWindow = new String[WINDOW_SIZE];  // currentTimeWindow是已排序的数组,为升序
	int numOfTimeWindow = 0;   // currentTimeWindow中有效元素个数...

	
	public void execute(Tuple tuple, OutputCollector outputCollector) {
		try {
			String timeFrame = tuple.getStringByField(this.tupleFieldList.get(0));
			
			ByteArrayInputStream byteArray = new ByteArrayInputStream(
					tuple.getBinaryByField(this.tupleFieldList.get(1)));
			ObjectInputStream inputStream = new ObjectInputStream(byteArray);
			
			NetflowRecord nfRecord = (NetflowRecord) inputStream.readObject();
			inputStream.close();
			
			process(timeFrame, nfRecord);
			
			
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
	
	
	void process(String timeWindow, NetflowRecord nf){
		
		try {
			
			// 发现新的时间窗口
			if(!timeWindowInCacheNow(timeWindow)){
				
				// IMPORTANT ! 一定要把由于网络不通畅造成的重传数据全部过滤掉, 否则会严重影响程序的正常运行
				if(!isLegalTimeFrame(timeWindow, this.numOfTimeWindow, this.currentTimeWindow)){
					return ;
				}
				
				RecognisedAttackCacheObj item = new RecognisedAttackCacheObj();
				
				this.attackCache.put(timeWindow, item);
				
				if(this.numOfTimeWindow < WINDOW_SIZE){
					// 系统初始化阶段, 创建一条新记录即可
					this.currentTimeWindow[this.numOfTimeWindow ++] = timeWindow;
				}
				else{
					// expire the oldest time window
					String toBeExpired = this.currentTimeWindow[0];
					this.currentTimeWindow[0] = timeWindow;
					
					RecognisedAttackCacheObj obj =  this.attackCache.get(toBeExpired);
					
					expireAttackInfo(toBeExpired, obj);
				}
				
				if(this.numOfTimeWindow == WINDOW_SIZE)
					Arrays.sort(this.currentTimeWindow, new TimeWindowComparator());
				
			}
			
			// 更新缓存
			RecognisedAttackCacheObj attackObj = this.attackCache.get(timeWindow);
			
			String srcIp = nf.getSrcIp();
			String dstIp = nf.getDstIp();
			String flags = nf.getFlags();
			
			
			if(isLandAttack(nf) && !drop(dstIp, dstIp)){
				attackObj.getLandAttackCache().add(nf);
			}
			else if(nf.getProtocol().equalsIgnoreCase("TCP")){
				
				if(isStreamFlood(flags) && !drop(dstIp, dstIp)){
					attackObj.getStreamFloodCache().add(nf);
				}
				else if(isSYNFlood(flags)){
					
					if(!attackObj.getSynSrcCounter().containsKey(srcIp)){
						attackObj.getSynSrcCounter().put(srcIp, 0);
					}
					attackObj.getSynSrcCounter().put(srcIp, attackObj.getSynSrcCounter().get(srcIp) + 1);
					
					if(!attackObj.getSynDstCounter().containsKey(dstIp)){
						attackObj.getSynDstCounter().put(dstIp, 0);
					}
					attackObj.getSynDstCounter().put(dstIp, attackObj.getSynDstCounter().get(dstIp) + 1);
					
					if(!drop(dstIp, dstIp)){
						if(!attackObj.getSynSrcDstMappin().containsKey(srcIp)){
							attackObj.getSynSrcDstMappin().put(srcIp, new ArrayList<AttackEvent>());
						}
						if(!attackObj.getSynDstSrcMappin().containsKey(dstIp)){
							attackObj.getSynDstSrcMappin().put(dstIp, new ArrayList<AttackEvent>());
						}
						AttackEvent ae = new AttackEvent(nf);
						attackObj.getSynSrcDstMappin().get(srcIp).add(ae);
						attackObj.getSynDstSrcMappin().get(dstIp).add(ae);
					}
					
				}
				else if(isACKFlood(flags)){
					
					if(!attackObj.getAckSrcCounter().containsKey(srcIp)){
						attackObj.getAckSrcCounter().put(srcIp, 0);
					}
					attackObj.getAckSrcCounter().put(srcIp, attackObj.getAckSrcCounter().get(srcIp) + 1);
					
					if(!attackObj.getAckDstCounter().containsKey(dstIp)){
						attackObj.getAckDstCounter().put(dstIp, 0);
					}
					attackObj.getAckDstCounter().put(dstIp, attackObj.getAckDstCounter().get(dstIp) + 1);
					
					// 注意drop() 参数与上文的区别
					// 这是为了对应ddos日志中部分stream-flood攻击记录
					if(!drop(srcIp, dstIp)){
						if(!attackObj.getAckSrcDstMappin().containsKey(srcIp)){
							attackObj.getAckSrcDstMappin().put(srcIp, new ArrayList<AttackEvent>());
						}
						if(!attackObj.getAckDstSrcMappin().containsKey(dstIp)){
							attackObj.getAckDstSrcMappin().put(dstIp, new ArrayList<AttackEvent>());
						}
						
						AttackEvent ae = new AttackEvent(nf);
						attackObj.getAckSrcDstMappin().get(srcIp).add(ae);
						attackObj.getAckDstSrcMappin().get(dstIp).add(ae);
					}
					
				}
				
			}
			else if(nf.getProtocol().equalsIgnoreCase("UDP")){
				
				if(isUDPFlood(nf) && !drop(dstIp, dstIp)){
					attackObj.getUdpFloodCache().add(nf);
				}
				
				if(isNtpFlood(nf)){
					
					if(!attackObj.getNtpSrcCounter().containsKey(srcIp)){
						attackObj.getNtpSrcCounter().put(srcIp, 0);
					}
					attackObj.getNtpSrcCounter().put(srcIp, attackObj.getNtpSrcCounter().get(srcIp) + 1);
					
					if(!attackObj.getNtpSrcDstMappin().containsKey(srcIp)){
						attackObj.getNtpSrcDstMappin().put(srcIp, new ArrayList<AttackEvent>());
					}
					AttackEvent ae = new AttackEvent(nf);
					attackObj.getNtpSrcDstMappin().get(srcIp).add(ae);
					
				}
				
			}
			else if(nf.getProtocol().equalsIgnoreCase("ICMP")){
				
				if(isIcmpFlood(nf) && !drop(dstIp, dstIp)){
					attackObj.getIcmpFloodCache().add(nf);
				}
				
				if(!attackObj.getIcmpSrcCounter().containsKey(srcIp)){
					attackObj.getIcmpSrcCounter().put(srcIp, 0);
				}
				attackObj.getIcmpSrcCounter().put(srcIp, attackObj.getIcmpSrcCounter().get(srcIp) + 1);
				
				if(!attackObj.getIcmpDstCounter().containsKey(dstIp)){
					attackObj.getIcmpDstCounter().put(dstIp, 0);
				}
				attackObj.getIcmpDstCounter().put(dstIp, attackObj.getIcmpDstCounter().get(dstIp) + 1);
				
				if(!drop(dstIp, dstIp)){
					if(!attackObj.getIcmpSrcDstMappin().containsKey(srcIp)){
						attackObj.getIcmpSrcDstMappin().put(srcIp, new ArrayList<AttackEvent>());
					}
					if(!attackObj.getIcmpDstSrcMappin().containsKey(dstIp)){
						attackObj.getIcmpDstSrcMappin().put(dstIp, new ArrayList<AttackEvent>());
					}
					
					AttackEvent ae = new AttackEvent(nf);
					attackObj.getIcmpSrcDstMappin().get(srcIp).add(ae);
					attackObj.getIcmpDstSrcMappin().get(dstIp).add(ae);
				}
				
			}
			
		} catch (Exception e) {
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
	
	boolean drop(String ip1, String ip2){
		if(!this.dropOutterIp || (this.dropOutterIp && NetflowTools.belongToTjutNetworkSegment(ip1, ip2)))
			return false;
		
		return true;
		
	}
	
	boolean hasNumFields(int num, String flags){
		int counter = 0;
		for(int i = 0; i < flags.length(); i ++){
			char c = flags.charAt(i);
			if(c != '.'){
				counter ++;
			}
		}
		
		return counter == num;
	}

	/**
	 * Stream_Flood 将flag字段仅包含S.F 或0x开头
	 * @param flags
	 * @return
     */
	boolean isStreamFlood(String flags){
		
		if(flags == null || flags.length() != 6 || flags.startsWith("0x") || 
				(flags.charAt(4) == 'S' && flags.charAt(5) == 'F' && hasNumFields(2, flags))){
			return true;
		}
		
		return false;
	}

	/**
	 * SYN_Flood flag中仅包含S
	 * @param flags
	 * @return
     */
	boolean isSYNFlood(String flags){
		
		if(flags != null && flags.length() == 6){
			if(flags.charAt(4) == 'S' && hasNumFields(1, flags)){
				return true;
			}
		}
			
		return false;
	}

	/**
	 * LAND 型Connection-Flood 源ip与目的ip相同
	 * @param nr
	 * @return
     */
	boolean isLandAttack(NetflowRecord nr){
		if(nr.getSrcIp().equals(nr.getDstIp())){
			return true;
		}
		
		return false;
	}

	/**
	 * UDP_Flood 目的端口为80
	 * @param nf
	 * @return
     */
	boolean isUDPFlood(NetflowRecord nf){
		if(nf.getDstPort().equals("80")){
			return true;
		}
		
		return false;
	}

	/**
	 * ACK_Flood 包含A 或包含A+P
	 * @param flags
	 * @return
     */
	boolean isACKFlood(String flags){
		
		if(flags != null && flags.length() == 6){
			if((flags.charAt(1) == 'A' && hasNumFields(1, flags))
					|| (flags.charAt(1) == 'A' && flags.charAt(2) == 'P' && hasNumFields(2, flags))){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * NTP_Flood 源端口是123
	 * @param nf
	 * @return
     */
	boolean isNtpFlood(NetflowRecord nf){
		
		// 目前只考虑NTP反射攻击
		if(nf.getSrcPort().equals("123")){
			return true;
		}
		
		return false;
	}

	/**
	 * ICMP——Flood 持续时间大于15
	 * @param nf
	 * @return
     */
	boolean isIcmpFlood(NetflowRecord nf){
		if(nf.getDuration() > 15){
			return true;
		}
		
		return false;
	}
	
	
	public boolean isLegalTimeFrame(String timeFrame, int num, String[] timeWindow) throws ParseException{
		if(num < WINDOW_SIZE)
			return true;
		
		Date d1 = sdf.parse(timeWindow[0]);
		Date d2 = sdf.parse(timeFrame);
		
		return d1.before(d2);
	}
	

	String keyGen(String srcIp, String srcPort, String dstIp, String dstPort, String protocal, String date){
		return srcIp + ":" + srcPort + "->" + dstIp + ":" + dstPort + "@" + protocal + "@" + date;
	}
	
	public List<AttackEvent> convertToAttackEventList(Map<String, Integer> counter, 
			Map<String, List<AttackEvent>> mappin, Set<String> keyCache, long threshold, AttackType type){
		
		List<AttackEvent> ls = new ArrayList<AttackEvent>();
		
		for(Entry<String, Integer> e : counter.entrySet()){
			String ip = e.getKey();
			int ct = e.getValue();
			
			if(ct >= threshold && mappin.containsKey(ip)){
				for(AttackEvent ae : mappin.get(ip)){
					ae.setType(type);
					
					String key = keyGen(ae.getSrcIp(), ae.getSrcPort(), 
							ae.getDstIp(), ae.getDstPort(), ae.getProtocal(), ae.getDate());
					
					if(!keyCache.contains(key)){
						ls.add(ae);
						keyCache.add(key);
					}
					
				}
			}
		}
		return ls;
	}
	
	public List<AttackEvent> convertToAttackEventList(List<NetflowRecord> rc, AttackType type){
		List<AttackEvent> ls = new ArrayList<AttackEvent>();
		
		for(NetflowRecord n : rc){
			AttackEvent ae = new AttackEvent(n);
			ae.setType(type);
			ls.add(ae);
		}
		
		return ls;
	}
	
	public void expireAttackInfo(String timeWindow, RecognisedAttackCacheObj me){
		
		try {
			

			// Connection-flood(land)
			List<AttackEvent> l1 = convertToAttackEventList(me.getLandAttackCache(), AttackType.CONNECTION_FLOOD_LAND);
			// 存入HBase
			this.storageManager.addAttackEvents(l1);
			// 存入Elasticsearch
			push2ElasticsearchWithShell(l1);
			
			
			// stream-flood
			List<AttackEvent> l2 = convertToAttackEventList(me.getStreamFloodCache(), AttackType.STREAM_FLOOD_INVALID_TCP_FLAG);
			this.storageManager.addAttackEvents(l2);
			push2ElasticsearchWithShell(l2);
			
			Set<String> keyCache = new HashSet<String>();
			keyCache.clear();
			// syn-flood srcIp计数规则
			List<AttackEvent> l3 = convertToAttackEventList(me.getSynSrcCounter(), me.getSynSrcDstMappin(),
				      keyCache, SYN_THRESHOLD, AttackType.SYN_FLOOD_INVALID);
			this.storageManager.addAttackEvents(l3);
			push2ElasticsearchWithShell(l3);
			
			// syn-flood dstIp计数规则
			List<AttackEvent> l4 = convertToAttackEventList(me.getSynDstCounter(), me.getSynDstSrcMappin(),
				      keyCache, SYN_THRESHOLD, AttackType.SYN_FLOOD_INVALID);
			this.storageManager.addAttackEvents(l4);
			push2ElasticsearchWithShell(l4);

			keyCache.clear();
			// ACK-Flood srcIp规则  ---- 暂时禁用
			/*this.storageManager.addAttackEvents(
					convertToAttackEventList(me.getAckSrcCounter(), me.getAckSrcDstMappin(),
					      keyCache, ACK_THRESHOLD, AttackType.ACK_FLOOD));
			
			// ACK-Flood dstIp计数规则
			this.storageManager.addAttackEvents(
					convertToAttackEventList(me.getAckDstCounter(), me.getAckDstSrcMappin(),
							keyCache, ACK_THRESHOLD, AttackType.ACK_FLOOD));*/
			
			
			// UDP-Flood & PortRule
			List<AttackEvent> l5 = convertToAttackEventList(me.getUdpFloodCache(), AttackType.UDP_FLOOD_PORT_RULE);
			this.storageManager.addAttackEvents(l5);
			push2ElasticsearchWithShell(l5);
			
			
			keyCache.clear();
			// ICMP-Flood srcIp规则
			List<AttackEvent> l6 = convertToAttackEventList(me.getIcmpSrcCounter(), me.getIcmpSrcDstMappin(),
					keyCache, ICMP_THRESHOLD, AttackType.ICMP_FLOOD);
			this.storageManager.addAttackEvents(l6);
			push2ElasticsearchWithShell(l6);
			
			
			// ICMP-Flood dstIp规则
			List<AttackEvent> l7 = convertToAttackEventList(me.getIcmpDstCounter(), me.getIcmpDstSrcMappin(),
					keyCache, ICMP_THRESHOLD, AttackType.ICMP_FLOOD);
			this.storageManager.addAttackEvents(l7);		
			push2ElasticsearchWithShell(l7);
			
			
			List<AttackEvent> l8 = convertToAttackEventList(me.getIcmpFloodCache(), AttackType.ICMP_FLOOD);
			this.storageManager.addAttackEvents(l8);
			push2ElasticsearchWithShell(l8);
			
			
			// ntp ---- 暂时禁用NTP攻击检测规则
			keyCache.clear();
			/*this.storageManager.addAttackEvents(
					convertToAttackEventList(me.getNtpSrcCounter(), me.getNtpSrcDstMappin(),
							keyCache, NTP_THRESHOLD, AttackType.NTP_FLOOD));*/
			
			// 清除相关缓存
			this.attackCache.remove(timeWindow);
			
		} catch (Exception e) {
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
	
	
	StringBuilder genShellCommand(List<AttackEvent> ls){
		
		
		StringBuilder sb = new StringBuilder(ES_PUSH_CMD_PREFIX);
		
		for(AttackEvent ae : ls){
			
			String type = null;
			
			switch(ae.getType()){
			case AttackType.ACK_FLOOD:
				type = "ACK-Flood";
				break;
			case AttackType.CONNECTION_FLOOD_LAND:
				type = "Connection-Flood";
				break;
			case AttackType.ICMP_FLOOD:
				type = "ICMP-Flood";
				break;
			case AttackType.NTP_FLOOD:
				type = "NTP-Flood";
				break;
			case AttackType.STREAM_FLOOD_INVALID_TCP_FLAG:
				type = "Stream-Flood";
				break;
			case AttackType.SYN_FLOOD_INVALID:
				type = "SYN-Flood";
				break;
			case AttackType.UDP_FLOOD_PORT_RULE:
				type = "UDP-Flood";
				break;
			default:
				type = "unknown";
			}
			
			// 拼接指令
			sb.append("{ \"create\" : { \"_index\" : \"edu_attack_event\", \"_type\" : \"" + type + "\" } }\n")
			  .append("{ \"date\" : \"" + ae.getDate() + "\", ")
			  .append("\"srcIp\": \"" + ae.getSrcIp() + "\", ")
			  .append("\"srcPort\" : \"" + ae.getSrcPort() + "\", ")
			  .append("\"dstIp\" : \"" + ae.getDstIp() + "\", ")
			  .append("\"dstPort\" : \"" + ae.getDstPort() + "\", ")
			  .append("\"protocal\" : \"" + ae.getProtocal() + "\", ")
			  .append("\"flag\" : \"" + ae.getFlag() + "\", ")
			  .append("\"typeDescription\" : \"" + ae.getType().getDescription() + "\", ")
			  .append("\"typeCode\" : " + ae.getType().getCode() + "}\n ");
		}
		
		sb.append("'");
		
		return sb;
	}
	
	// 将计算得到的网络负载指标写入ES中以供kibana用图像渲染出来(调用命令行)
	void push2ElasticsearchWithShell(List<AttackEvent> ls){
		try {
			
			if(ls != null && ls.size() > 0){
				
				// 调用Elasticsearch 的bulk API 写数据
				StringBuilder sb = genShellCommand(ls);
				String cmd = sb.toString();
				
				// System.out.println(cmd);
				
				Process process = Runtime.getRuntime().exec(new String[]{"bash","-c",cmd});
				process.waitFor();
				
				/*BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(cmd + "\n");
				bw.close();*/
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
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
	
	
	public boolean timeWindowInCacheNow(String timeFlag){
		for(String s : this.currentTimeWindow){
			if(s != null && s.equals(timeFlag)){
				return true;
			}
		}
		
		return false;
	}
	
	public void prepare(Map config,NetflowSource from, StorageManager storageManager, List<String> inputFieldList) {
		this.storageManager = storageManager;
		this.source = from;
		this.tupleFieldList = inputFieldList;
		this.SYN_THRESHOLD = (Long) config.get(MainTopology.SYN_THRESHOLD);
		this.ACK_THRESHOLD = (Long) config.get(MainTopology.ACK_THRESHOLD);
		this.ICMP_THRESHOLD = (Long) config.get(MainTopology.ICMP_THRESHOLD);
		this.NTP_THRESHOLD = (Long) config.get(MainTopology.NTP_THRESHOLD);
		this.dropOutterIp = (Boolean) config.get(MainTopology.DROP_OUTTER_IP);
		
	}
	
	public void cleanup() {
		
	}
	
	@Deprecated
	public void expireToFile(String timeWindow, RecognisedAttackCacheObj me){
		
		try {
			// 输出结果
			String outdir = "OUTPUT_DIR" + timeWindow.substring(0, 10) + "/";

			// stream-flood
			String streamOutput = outdir + "stream-flood.txt";
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(streamOutput, true));
			StringBuilder sb = new StringBuilder();
			for(NetflowRecord n : me.getStreamFloodCache()){
				AttackEvent ae = new AttackEvent(n);
				//ae.setType("Stream-Flood(INVALID_TCP_FLAG)");
				sb.append(ae.toString() + "\n");
			}
			bw1.write(sb.toString());
			bw1.close();
			
			// syn-flood
			String synOutput = outdir + "syn-flood.txt";
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(synOutput, true));
			StringBuilder sb2 = new StringBuilder();
			for(Entry<String, Integer> e : me.getSynSrcCounter().entrySet()){
				String srcIp = e.getKey();
				int ct = e.getValue();
				
				if(ct >= SYN_THRESHOLD && me.getSynSrcDstMappin().containsKey(srcIp)){
					for(AttackEvent ae : me.getSynSrcDstMappin().get(srcIp)){
						//ae.setType("SYN-Flood(SYN_INVALID)");
						sb2.append(ae.toString() + "\n");
					}
					sb2.append("\n");
				}
			}
			bw2.write(sb2.toString());
			bw2.close();
			
			// Connection-flood & land
			String landOutput = outdir + "connection-flood.txt";
			BufferedWriter bw3 = new BufferedWriter(new FileWriter(landOutput, true));
			StringBuilder sb3 = new StringBuilder();
			for(NetflowRecord n : me.getLandAttackCache()){
				AttackEvent ae = new AttackEvent(n);
				//ae.setType("Connection-Flood(LAND)");
				sb3.append(ae.toString() + "\n");
			}
			bw3.write(sb3.toString());
			bw3.close();
			
			
			// UDP-Flood & PortRule
			String udpOutput = outdir + "udp-flood.txt";
			BufferedWriter bw4 = new BufferedWriter(new FileWriter(udpOutput, true));
			StringBuilder sb4 = new StringBuilder();
			for(NetflowRecord n : me.getUdpFloodCache()){
				AttackEvent ae = new AttackEvent(n);
				//ae.setType("UDP-Flood(PortRule)");
				sb4.append(ae.toString() + "\n");
			}
			bw4.write(sb4.toString());
			bw4.close();
			
			
			// ACK-Flood
			String ackOutput = outdir + "ack-flood.txt";
			BufferedWriter bw5 = new BufferedWriter(new FileWriter(ackOutput, true));
			StringBuilder sb5 = new StringBuilder();
			for(Entry<String, Integer> e : me.getAckSrcCounter().entrySet()){
				String srcIp = e.getKey();
				int ct = e.getValue();
				
				if(ct >= ACK_THRESHOLD && me.getAckSrcDstMappin().containsKey(srcIp)){
					for(AttackEvent ae : me.getAckSrcDstMappin().get(srcIp)){
						//ae.setType("ACK-Flood");
						sb5.append(ae.toString() + "\n");
					}
					sb5.append("\n");
				}
			}
			bw5.write(sb5.toString());
			bw5.close();
			
			
			// ICMP-Flood
			String icmpOutput = outdir + "icmp-flood.txt";
			BufferedWriter bw6 = new BufferedWriter(new FileWriter(icmpOutput, true));
			StringBuilder sb6 = new StringBuilder();
			for(Entry<String, Integer> e : me.getIcmpSrcCounter().entrySet()){
				String srcIp = e.getKey();
				int ct = e.getValue();
				
				if(ct >= ICMP_THRESHOLD && me.getIcmpSrcDstMappin().containsKey(srcIp)){
					for(AttackEvent ae : me.getIcmpSrcDstMappin().get(srcIp)){
						//ae.setType("ICMP-Flood(Proto)");
						sb6.append(ae.toString() + "\n");
					}
					sb6.append("\n");
				}
			}
			for(NetflowRecord n : me.getIcmpFloodCache()){
				AttackEvent ae = new AttackEvent(n);
				//ae.setType("ICMP-Flood(Proto)");
				sb6.append(ae.toString() + "\n");
			}
			sb6.append("\n");
			bw6.write(sb6.toString());
			bw6.close();
			
			// ntp
			String ntpOutput = outdir + "ntp-flood.txt";
			BufferedWriter bw7 = new BufferedWriter(new FileWriter(ntpOutput, true));
			StringBuilder sb7 = new StringBuilder();
			for(Entry<String, Integer> e : me.getNtpSrcCounter().entrySet()){
				String srcIp = e.getKey();
				int ct = e.getValue();
				
				if(ct >= NTP_THRESHOLD && me.getNtpSrcDstMappin().containsKey(srcIp)){
					for(AttackEvent ae : me.getNtpSrcDstMappin().get(srcIp)){
						//ae.setType("NTP-Flood");
						sb7.append(ae.toString() + "\n");
					}
					sb7.append("\n");
				}
			}
			bw7.write(sb7.toString());
			bw7.close();
			
			
			// 清除相关缓存
			this.attackCache.remove(timeWindow);
			
		} catch (Exception e) {
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
	
	
	// 测试
	public static void main(String[] args) {
		try {
			AttackDetectionRules ar = new AttackDetectionRules();
			
			List<AttackEvent> ls = new ArrayList<AttackEvent>();
			
			AttackType at = AttackType.ACK_FLOOD;
			AttackEvent ae = new AttackEvent("2016-03-16 16:16:16", "202.113.76.229", "888",
					"202.113.76.229", "888", "TCP", ".A....", at);
			AttackEvent ae2 = new AttackEvent("2016-03-17 16:16:16", "202.113.76.229", "888",
					"202.113.76.229", "888", "TCP", ".A....", at);
			AttackEvent ae3 = new AttackEvent("2016-03-18 16:16:16", "202.113.76.229", "888",
					"202.113.76.229", "888", "TCP", ".A....", at);
			ls.add(ae);
			ls.add(ae2);
			ls.add(ae3);
			
			ar.push2ElasticsearchWithShell(ls);
			
			System.out.println("done!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}