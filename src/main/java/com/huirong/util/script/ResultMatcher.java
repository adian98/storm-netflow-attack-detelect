package com.huirong.util.script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.huirong.storage.vo.AttackEvent;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月4日
 */
public class ResultMatcher {

	public static void main(String[] args) {
		String intersectionPrefix = "F:\\work-space\\project-base\\毕业设计\\ddos_analysis\\新建文件夹\\target_events\\2015-12-";
		String detectPrefix = "F:\\work-space\\project-base\\毕业设计\\ddos_analysis\\新建文件夹\\analysis_result\\2015-12-";
		String out = "F:\\work-space\\project-base\\毕业设计\\ddos_analysis\\新建文件夹\\final\\2015-12-";
		
		for(int i = 23; i < 30; i ++){
			String currentAttackLog = intersectionPrefix + i + "\\";
			String currentNfDir = detectPrefix + i + "\\";
			String output = out + i + "\\";
			doCheck(currentAttackLog, currentNfDir, output);
		}
	}
	
	
	// 分别检查5种类型的攻击的检测准确率(把匹配成功的、误报的、漏报的都统计出来)
	// 用nfrecord时间戳（分钟级）压缩attack record数据——目标是保留那些真正重叠的数据，而不是以谁为标准的问题
	// 两种数据的时间差按照2/3分钟计算(如果没有2分钟以内的对应记录, 那么就按3分钟来计算)
	// 计算准确率检测时直接暴力匹配, 不需要什么精巧的方法, 时间匹配规则同上
	// 注意输出结果的格式
	public static void doCheck(String intersectionDir, String detectionDir, String outDir){
		System.out.println(intersectionDir);
		System.out.println(detectionDir);
		System.out.println(outDir);
		
		// connection flood
		String cfdir = intersectionDir + "intersection-connection-flood.txt";
		String cfcompress = outDir + "compressed-connection-flood.txt";
		Map<String, List<AttackRcd>> connectionMap = compressTargetRecords(cfdir, cfcompress);
		String cfdetectdir = detectionDir + "connection-flood.txt";
		String cfout = outDir + "match-result-connection-flood.txt";
		match(connectionMap, cfdetectdir, cfout);
		System.out.println("connection done");
		
		// icmp
		String icmpdir = intersectionDir + "intersection-icmp-flood.txt";
		String icmpcompress = outDir + "compressed-icmp-flood.txt";
		Map<String, List<AttackRcd>> icmpMap = compressTargetRecords(icmpdir, icmpcompress);
		String icmpdetectdir = detectionDir + "icmp-flood.txt";
		String icmpout = outDir + "match-result-icmp-flood.txt";
		match(icmpMap, icmpdetectdir, icmpout);
		System.out.println("icmp done");
		
		// stream flood
		String streamdir = intersectionDir + "intersection-stream-flood.txt";
		String streamcompress = outDir + "compressed-stream-flood.txt";
		Map<String, List<AttackRcd>> streamMap = compressTargetRecords(streamdir, streamcompress);
		String streamdetectdir = detectionDir + "stream-flood.txt";
		String streamout = outDir + "match-result-stream-flood.txt";
		match(streamMap, streamdetectdir, streamout);
		System.out.println("stream done");
		
		// syn
		String syndir = intersectionDir + "intersection-syn-flood.txt";
		String synompress = outDir + "compressed-syn-flood.txt";
		Map<String, List<AttackRcd>> synMap = compressTargetRecords(syndir, synompress);
		String syndetectdir = detectionDir + "syn-flood.txt";
		String synout = outDir + "match-result-syn-flood.txt";
		match(synMap, syndetectdir, synout);
		System.out.println("syn done");
		
		// udp
		String udpdir = intersectionDir + "intersection-udp-flood.txt";
		String udpompress = outDir + "compressed-udp-flood.txt";
		Map<String, List<AttackRcd>> udpMap = compressTargetRecords(udpdir, udpompress);
		String udpdetectdir = detectionDir + "udp-flood.txt";
		String udpout = outDir + "match-result-udp-flood.txt";
		match(udpMap, udpdetectdir, udpout);
		System.out.println("udp done");
	}
	
	public static String keyGen(AttackEvent ae){
		return ae.getSrcIp() + "->" + ae.getDstIp();
	}
	
	public static void match(Map<String, List<AttackRcd>> map, String detectDir, String out){
		try {
			
			Map<String, List<AttackEvent>> result = new HashMap<String, List<AttackEvent>>();
			
			BufferedReader br = new BufferedReader(new FileReader(detectDir));
			String l = "";
			int counter = 0;
			
			while((l = br.readLine()) != null){
				if(l!= null && l.length() > 0){
					AttackEvent ae = new AttackEvent(l);
					String key  = keyGen(ae);
					
					if(map.containsKey(key)){
						// 先将所有匹配内容都写入文件
						if(!result.containsKey(key)){
							result.put(key, new ArrayList<AttackEvent>());
						}
						result.get(key).add(ae);
						counter ++;
					}
				}
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(out, true));
			
			
			for(Entry<String, List<AttackEvent>> e : result.entrySet()){
				String key  = e.getKey();
				
				StringBuilder sb = new StringBuilder();
				sb.append(key + "\n");
				sb.append("{\n");
				
				for(AttackRcd ar : map.get(key)){
					sb.append(ar + "\n");
				}
				
				for(AttackEvent ae : e.getValue()){
					sb.append(ae + "\n");
				}
				
				sb.append("}\n");
				bw.write(sb.toString());
			}
			
			int ct = 0;
			for(Entry<String, List<AttackRcd>> e : map.entrySet()){
				ct += e.getValue().size();
			}
			
			bw.write("detected keys : " + result.size() + "\n");
			bw.write("total keys : " + map.size() + "\n");
			bw.write("detected events : " + counter + "\n");
			bw.write("total events : " + ct + "\n");
			br.close();
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, List<AttackRcd>> compressTargetRecords(String interseDir, String out){
		
		Map<String, List<AttackRcd>> ret = new HashMap<String, List<AttackRcd>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(out, true));
			int totalEvent = 0;
			
			BufferedReader br = new BufferedReader(new FileReader(interseDir));
			String l = "";
			String key = "";
			List<AttackRcd> attCache = new ArrayList<AttackRcd>();
			Set<String> stdTimeWindow = new HashSet<String>();
			
			while((l = br.readLine()) != null){
				
				if(l.contains("->")){
					
					key = l;
					if(!ret.containsKey(key)){
						ret.put(key, new ArrayList<AttackRcd>());
					}
					
					attCache.clear();
					stdTimeWindow.clear();
					continue;
				}
				else if(l.startsWith("{") || l.length() == 0){
					continue;
				}
				else if(l.startsWith("[date")){
					// log record
					AttackRcd ar = new AttackRcd(l);
					attCache.add(ar);
				}
				else if(l.startsWith("[nfDate")){
					// nf record
					String[] tmp = l.split(",");
					if(tmp != null && tmp.length > 0){
						calendar.setTime(sdf.parse((tmp[0].split("="))[1]));
						calendar.set(Calendar.SECOND, 0);
						stdTimeWindow.add(sdf.format(calendar.getTime()));

						calendar.add(Calendar.MINUTE, -1);
						stdTimeWindow.add(sdf.format(calendar.getTime()));
						
						calendar.add(Calendar.MINUTE, -1);
						stdTimeWindow.add(sdf.format(calendar.getTime()));
						
						calendar.add(Calendar.MINUTE, -1);
						stdTimeWindow.add(sdf.format(calendar.getTime()));
					}
				}
				else if(l.startsWith("}")){
					// 开始过滤
					StringBuilder sb = new StringBuilder();
					sb.append(key + "\n");
					sb.append("{\n");
					for(AttackRcd ar : attCache){
						String time = ar.getDate().substring(0, 16) + ":00";
						if(stdTimeWindow.contains(time)){
							ret.get(key).add(ar);
							sb.append(ar + "\n");
							totalEvent ++;
						}
					}
					sb.append("}\n");
					
					if(ret.get(key).size() > 0){
						bw.write(sb.toString());
					}
					else{
						ret.remove(key);
					}
				}
				
			}
			
			br.close();
			
			bw.write("keys : " + ret.size() + "\n");
			bw.write("events : " + totalEvent + "\n");
			bw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
}



class AttackRcd{
	private String date;
	private String srcIp;
	private String srcPort;
	private String dstIp;
	private String dstPort;
	private String attackInfo;
	private String flag;
	
	public AttackRcd(String s){
		
		String[] tmp = s.split(",");
		if(tmp != null && tmp.length > 0){
			this.date = (tmp[0].split("="))[1];
			this.srcIp = (tmp[1].split("="))[1];
			this.srcPort = (tmp[2].split("="))[1];
			this.dstIp = (tmp[3].split("="))[1];
			this.dstPort = (tmp[4].split("="))[1];
			this.attackInfo = (tmp[5].split("="))[1];
			String flg = (tmp[6].split("="))[1];
			this.flag = flg.substring(0, flg.length() - 1);
		}
	}
	
	public String getAttackInfo() {
		return attackInfo;
	}
	public void setAttackInfo(String attackInfo) {
		this.attackInfo = attackInfo;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getSrcIp() {
		return srcIp;
	}
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}
	public String getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	public String getDstIp() {
		return dstIp;
	}
	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}
	public String getDstPort() {
		return dstPort;
	}
	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}
	
	@Override
	public String toString() {
		return "[date=" + date + ", srcIp=" + srcIp + ", srcPort=" + srcPort + ", dstIp=" + dstIp
				+ ", dstPort=" + dstPort + ", attackInfo=" + attackInfo + ", flag=" + flag + "]";
	}
	
}

