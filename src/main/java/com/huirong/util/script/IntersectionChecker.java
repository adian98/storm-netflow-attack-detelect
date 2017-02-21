package com.huirong.util.script;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月2日
 */
public class IntersectionChecker {
	
	public static void main(String[] args){
		int timeExpand = Integer.parseInt(args[0]);
		
		String attacklogPrefix = "/data/edu_netflow/ddos_analysis/atklog/atkinfo-2015-12-";
		String nfdirPrefix = "/data/edu_netflow/ddos_analysis/2015-12-";
		String out = "/data/edu_netflow/ddos_analysis/target_events/";
		
		
		for(int i = 23; i <= 30; i ++){
			String currentAttackLog = attacklogPrefix + i;
			String currentNfDir = nfdirPrefix + i;
			String output = out + "2015-12-" + i + "/";
			doProcess(currentAttackLog, currentNfDir, output, timeExpand);
		}
		
	}
	
	public static void doProcess(String attacklog, String nfdir, String out, int expand ){
		try {
			
			System.out.println("+++++++++++++++++++++++++++++++++");
			System.out.println(attacklog);
			System.out.println(nfdir);
			
			Tool t = new Tool();
			Map<String, List<AttackRecord>> attackcache = new HashMap<String, List<AttackRecord>>();
			
			BufferedReader br = new BufferedReader(new FileReader(attacklog));
			String l1 = "";
			String date = "";
			while((l1 = br.readLine()) != null){
				if(l1.startsWith("20")){
					date = l1;
					continue;
				}
				
				AttackRecord ar = t.attParser(l1);
				ar.setDate(date);
				String key = keyGen(ar);
				
				if(!attackcache.containsKey(key)){
					attackcache.put(key, new ArrayList<AttackRecord>());
					
				}
				
				attackcache.get(key).add(ar);
			}
			
			br.close();
			
			System.out.println("Done loading attack log.");
			
			
			File[] fs = new File(nfdir).listFiles();
			
			// sort the files by name first!
			List<File> tmp = new ArrayList<File>();
			for(File file : fs){
				if(file != null && file.isFile() && file.getName().substring(0, 6).equals("nfcapd")){
					tmp.add(file);
				}
			}
			
			File[] target = tmp.toArray(new File[0]);
			Arrays.sort(target, new NfComparator());
			
			Map<String, List<ResultCacheObj>> resultCache = new HashMap<String, List<ResultCacheObj>>();
			
			
			for(File f : target){
				
				BufferedReader br1 = new BufferedReader(new FileReader(f));
				System.out.println(f.getName());
				String l = "";
				while((l = br1.readLine()) != null){
					NetflowRecord nr = t.nfParse(l);
					if(nr == null)
						continue;
					
					String key = keyGen(nr);
					if(attackcache.containsKey(key)){
						
						if(!resultCache.containsKey(key)){
							resultCache.put(key, new ArrayList<ResultCacheObj>());
						}
						
						ResultCacheObj obj = new ResultCacheObj();
						obj.setNfDate(nr.getDate());
						obj.setNfFile(f.getName());
						
						resultCache.get(key).add(obj);
						
					}
				}
				
				br1.close();	
			}
			
			String conn = out + "intersection-connection-flood.txt";
			BufferedWriter bw1 = new BufferedWriter(new FileWriter(conn, true));
			
			String strea = out + "intersection-stream-flood.txt";
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(strea, true));
			
			String syn = out + "intersection-syn-flood.txt";
			BufferedWriter bw3 = new BufferedWriter(new FileWriter(syn, true));
			
			String udp = out + "intersection-udp-flood.txt";
			BufferedWriter bw4 = new BufferedWriter(new FileWriter(udp, true));
			
			String icmp = out + "intersection-icmp-flood.txt";
			BufferedWriter bw5 = new BufferedWriter(new FileWriter(icmp, true));
			
			
			for(Entry<String, List<ResultCacheObj>> e : resultCache.entrySet()){
				
				String key = e.getKey();

				StringBuilder filter = new StringBuilder();
				
				filter.append(key + "\n");
				filter.append("{\n");
				
				String attType = "";
				
				for(AttackRecord ar : attackcache.get(key)){
					filter.append(ar + "\n");
					attType = ar.getAttackInfo();
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar calendar = Calendar.getInstance();
				List<AttackRecord> ls = attackcache.get(key);
				
				// 将时间窗口向前&向后各延伸30分钟
				calendar.setTime(sdf.parse(ls.get(0).getDate()));
				calendar.add(Calendar.MINUTE, -expand);
				
				Date min = calendar.getTime();
				
				calendar.setTime(sdf.parse(ls.get(ls.size() - 1).getDate()));
				calendar.add(Calendar.MINUTE, expand);
				Date max = calendar.getTime();
				
				int ct = 0;
				for(ResultCacheObj rc : e.getValue()){
					Date dt = sdf.parse(rc.getNfDate());
					
					if(dt.after(min) && dt.before(max)){
						
						filter.append(rc + "\n");
						ct ++;
					}
				}
				
				filter.append("}\n\n");
				
				if(ct > 0){
					
					if(attType.equalsIgnoreCase("Connection-Flood")){
						bw1.write(filter.toString());
					}
					else if(attType.equalsIgnoreCase("Stream-Flood")){
						bw2.write(filter.toString());
					}
					else if(attType.equalsIgnoreCase("SYN-Flood")){
						bw3.write(filter.toString());
					}
					else if(attType.equalsIgnoreCase("UDP-Flood")){
						bw4.write(filter.toString());
					}
					else if(attType.equalsIgnoreCase("ICMP-Flood")){
						bw5.write(filter.toString());
					}
					
				}
			}
			
			bw1.close();
			bw2.close();
			bw3.close();
			bw4.close();
			bw5.close();
			
			System.out.println("Done!");
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static String keyGen(NetflowRecord r){
		return r.getSrcIp() + "->" + r.getDstIp(); 
	}
	
	public static String keyGen(AttackRecord r){
		return r.getSrcIp() + "->" + r.getDstIp(); 
	}
	

}

class Tool{
	
	public AttackRecord attParser(String line){
		
		try {
			AttackRecord ar = new AttackRecord();
			String[] ls = line.split("=");
			if(ls != null && ls.length > 0){
				ar.setAttackInfo(ls[1].substring(1).split(" ")[0]);
				ar.setSrcIp(ls[2].split(" ")[0]);
				ar.setDstIp(ls[3].split(" ")[0]);
				ar.setSrcPort(ls[4].split(" ")[0]);
				ar.setDstPort(ls[5].split(" ")[0]);
				ar.setFlag(ls[6]);
			}
			
			return ar;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public NetflowRecord nfParse(String text){
		String [] splitted = text.split(" ");
		
		if(isLegalInput(splitted) && !splitted[0].contains("Date") && !splitted[0].contains("Time") 
				&& !splitted[0].contains("Total") && !splitted[0].contains("Sys") && !splitted[0].contains("Summary")){
			 
			String [] fields = new String[14];
			int length = splitted.length;
			int counter = 0;
			for(int i = 0;i < length;i ++){
				
				String content = splitted[i].trim();
				if(content != null && content.length() > 0 && !content.contains("->")){
					if(content.equals("M"))
						fields[counter - 1] = String.valueOf((long)(1024L * Double.parseDouble(fields[counter - 1])));
					else if(content.equals("G")){
						fields[counter - 1] = String.valueOf((long)(1024L * 1024 * Double.parseDouble(fields[counter - 1])));
					}
					else{
						fields[counter] = splitted[i];
						counter ++;
					}
				}
			}
			
			try {
				
				String [] srcIp = fields[4].split(":");
				String [] dstIp = fields[5].split(":");
				
				String date = fields[0] + " " + fields[1];
				date = date.substring(0, 19);
				
				NetflowRecord rc = new NetflowRecord(date, Double.parseDouble(fields[2]),fields[3],srcIp[0],srcIp[1]
						,dstIp[0],dstIp[1],fields[6],fields[7],Integer.parseInt(fields[8]),Long.parseLong(fields[9]),
						Integer.parseInt(fields[10]),Long.parseLong(fields[11]),Long.parseLong(fields[12]),
						1);
				
				rc.setTimeFrame(date.substring(0, 16) + ":00");
				
				return rc;
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
			
		}
		
		return null;
	}
	
	public static boolean isLegalInput(String[] ls){
		if(ls == null)
			return false;
		
		int counter = 0;
		String last = "";
		for(String s : ls){
			String content = s.trim();
			if(content != null && content.length() > 0){
				counter ++;
				last = content;
			}
		}
		
		return counter >= 15 && counter <= 17 && last.equals("1");
	}
}

class NetflowRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9171471945904350327L;

	private Integer id;
	private String date;    
	private Double duration;
	private String protocol;
	private String srcIp;
	private String srcPort;
	private String dstIp;
	private String dstPort;
	private String flags;
	private String tos;
	private Integer packets;
	private Long bytes;
	private Integer pps;
	private Long bps;
	private Long bytespp;
	private Integer flows;
	private String tag = null;
	private String timeFrame;

	public String getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(String timeFrame) {
		this.timeFrame = timeFrame;
	}

	public NetflowRecord(String dt,double dura,String pro,String sip,String spt,String dip,String dpt,String fla,
			String t, int packs,long sz,int p, long b, long bp, int flws){
		date = dt;
		duration = dura;
		protocol = pro;
		srcIp = sip;
		srcPort = spt;
		dstIp = dip;
		dstPort = dpt;
		flags = fla;
		tos = t;
		packets = packs;
		bytes = sz;
		pps = p;
		bps = b;
		bytespp = bp;
		flows = flws;
	}

	public Long getBps() {
		return bps;
	}

	public Long getBytes() {
		return bytes;
	}

	public Long getBytespp() {
		return bytespp;
	}

	public String getDate() {
		return date;
	}

	public String getDstIp() {
		return dstIp;
	}

	public String getDstPort() {
		return dstPort;
	}

	public Double getDuration() {
		return duration;
	}

	public String getFlags() {
		return flags;
	}

	public Integer getFlows() {
		return flows;
	}

	public Integer getId() {
		return id;
	}

	public Integer getPackets() {
		return packets;
	}

	public Integer getPps() {
		return pps;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getSrcIp() {
		return srcIp;
	}

	public String getSrcPort() {
		return srcPort;
	}

	public String getTag() {
		return tag;
	}

	public String getTos() {
		return tos;
	}

	public void setBps(Long bps) {
		this.bps = bps;
	}

	public void setBytes(Long bytes) {
		this.bytes = bytes;
	}

	public void setBytespp(Long bytespp) {
		this.bytespp = bytespp;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}

	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public void setFlows(Integer flows) {
		this.flows = flows;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setPackets(Integer packets) {
		this.packets = packets;
	}
	public void setPps(Integer pps) {
		this.pps = pps;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public void setTos(String tos) {
		this.tos = tos;
	}
	
	@Override
	public String toString(){
		return "date = " + date + ", duration = " + duration + ", protocol = " + protocol + 
				", srcIp = " + this.srcIp + ", srcPort = " + this.srcPort + ", dstIp = " + this.dstIp +
				", dstPort = " + this.dstPort + ", flags = " + this.flags + ", tos = " + this.tos +
				", packets = " + this.packets + ", bytes = " + this.bytes + ", pps = " + this.pps +
				", bbs = " + this.bps + ", bytespp = " + this.bytespp + ", flows = " + this.flows;
	}
}

class AttackRecord{
	private String date;
	private String srcIp;
	private String srcPort;
	private String dstIp;
	private String dstPort;
	private String attackInfo;
	private String flag;
	
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

class NfComparator implements Comparator<File>{

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	
	public int compare(File arg0, File arg1) {
		try {
			String date1 = arg0.getName().substring(7, 19);
			String date2 = arg1.getName().substring(7, 19);
			
			return sdf.parse(date1).compareTo(sdf.parse(date2));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
}

class ResultCacheObj{
	private String nfDate;
	private String nfFile;
	
	public String getNfDate() {
		return nfDate;
	}
	public void setNfDate(String nfDate) {
		this.nfDate = nfDate;
	}
	public String getNfFile() {
		return nfFile;
	}
	public void setNfFile(String nfFile) {
		this.nfFile = nfFile;
	}
	
	@Override
	public String toString() {
		return "[nfDate=" + nfDate + ", nfFile=" + nfFile + "]";
	}
	
}
