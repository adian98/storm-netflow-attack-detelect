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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.huirong.biz.BusinessLogic;
import com.huirong.storage.vo.DDoSMetricsCacheObj;
import com.huirong.storage.vo.MetricsRangeVO;
import com.huirong.storage.vo.NetflowRecord;
import com.huirong.util.NetflowSource;
import com.huirong.util.TopIpComparator;
import com.huirong.storage.StorageManager;
import com.huirong.storage.vo.DDoSMetricsVO;
import com.huirong.util.TimeWindowComparator;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年12月30日
 */
public class DDoSMetricsGenerator implements BusinessLogic {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static int WINDOW_SIZE = 3;
	public final static String ES_PUSH_CURL_PREFIX = "curl -XPOST '59.67.152.231:9200/_bulk' -d '\n";
	
	
	private StorageManager storageManager ;
	private NetflowSource source;
	private List<String> tupleFieldList;

	Map<String, DDoSMetricsCacheObj> metricsCache = new HashMap<String, DDoSMetricsCacheObj>();
	String[] currentTimeWindow = new String[WINDOW_SIZE];  // currentTimeWindow是已排序的数组
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
			e.printStackTrace();
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(e.getMessage() + "\n");
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
				
				DDoSMetricsCacheObj item = new DDoSMetricsCacheObj();
				
				this.metricsCache.put(timeWindow, item);
				
				if(this.numOfTimeWindow < WINDOW_SIZE){
					// 系统初始化阶段, 创建一条新记录即可
					this.currentTimeWindow[this.numOfTimeWindow ++] = timeWindow;
				}
				else{
					// expire the oldest time window
					String toBeExpired = this.currentTimeWindow[0];
					this.currentTimeWindow[0] = timeWindow;
					
					DDoSMetricsCacheObj metrics =  this.metricsCache.get(toBeExpired);
					
					expireDDoSMetrics(toBeExpired, metrics);
				}
				
				if(this.numOfTimeWindow == WINDOW_SIZE)
					Arrays.sort(this.currentTimeWindow, new TimeWindowComparator());
				
			}
			
			// 更新缓存
			DDoSMetricsCacheObj metricsObj = this.metricsCache.get(timeWindow);
			
			metricsObj.setRecord_creation_num(metricsObj.getRecord_creation_num() + 1);
			
			if(nf.getProtocol().equalsIgnoreCase("TCP")){
				
				metricsObj.setTcp_num(metricsObj.getTcp_num() + 1);
				
				String srcIp = nf.getSrcIp();
				String dstIp = nf.getDstIp();
				String flags = nf.getFlags();
				
				if(isSYNFlood(flags)){
					metricsObj.setSyn_num(metricsObj.getSyn_num() + 1);
					
					if(!metricsObj.getSyn_src().containsKey(srcIp)){
						metricsObj.getSyn_src().put(srcIp, 0);
					}
					metricsObj.getSyn_src().put(srcIp, metricsObj.getSyn_src().get(srcIp) + 1);
					
					if(!metricsObj.getSyn_dst().containsKey(dstIp)){
						metricsObj.getSyn_dst().put(dstIp, 0);
					}
					metricsObj.getSyn_dst().put(dstIp, metricsObj.getSyn_dst().get(dstIp) + 1);
					
				}
				else if(isACKFlood(flags)){
					metricsObj.setAck_num(metricsObj.getAck_num() + 1);
					
					if(!metricsObj.getAck_src().containsKey(srcIp)){
						metricsObj.getAck_src().put(srcIp, 0);
					}
					metricsObj.getAck_src().put(srcIp, metricsObj.getAck_src().get(srcIp) + 1);
					
					if(!metricsObj.getAck_dst().containsKey(dstIp)){
						metricsObj.getAck_dst().put(dstIp, 0);
					}
					metricsObj.getAck_dst().put(dstIp, metricsObj.getAck_dst().get(dstIp) + 1);
					
				}
				else if(isFINFlood(flags)){
					metricsObj.setFin_num(metricsObj.getFin_num() + 1);
					
					if(!metricsObj.getFin_src().containsKey(srcIp)){
						metricsObj.getFin_src().put(srcIp, 0);
					}
					metricsObj.getFin_src().put(srcIp, metricsObj.getFin_src().get(srcIp) + 1);
					
					if(!metricsObj.getFin_dst().containsKey(dstIp)){
						metricsObj.getFin_dst().put(dstIp, 0);
					}
					metricsObj.getFin_dst().put(dstIp, metricsObj.getFin_dst().get(dstIp) + 1);
				}
				else if(isInvalidateFlag(flags)){
					// 非法tcp flags
					metricsObj.setInval_flag_num(metricsObj.getInval_flag_num() + 1);
					
					if(!metricsObj.getInval_flag_src().containsKey(nf.getSrcIp())){
						metricsObj.getInval_flag_src().put(nf.getSrcIp(), 0);
					}
					metricsObj.getInval_flag_src().put(nf.getSrcIp(), metricsObj.getInval_flag_src().get(nf.getSrcIp()) + 1);
					
					if(!metricsObj.getInval_flag_dst().containsKey(nf.getDstIp())){
						metricsObj.getInval_flag_dst().put(nf.getDstIp(), 0);
					}
					metricsObj.getInval_flag_dst().put(nf.getDstIp(), metricsObj.getInval_flag_dst().get(nf.getDstIp()) + 1);
					
				}
				
			}
			else if(nf.getProtocol().equalsIgnoreCase("UDP")){
				metricsObj.setUdp_num(metricsObj.getUdp_num() + 1);
				
				// UDP flood (probably)
				if(!metricsObj.getUdp_src().containsKey(nf.getSrcIp())){
					metricsObj.getUdp_src().put(nf.getSrcIp(), 0);
				}
				metricsObj.getUdp_src().put(nf.getSrcIp(), metricsObj.getUdp_src().get(nf.getSrcIp()) + 1);
				
				if(!metricsObj.getUdp_dst().containsKey(nf.getDstIp())){
					metricsObj.getUdp_dst().put(nf.getDstIp(), 0);
				}
				metricsObj.getUdp_dst().put(nf.getDstIp(), metricsObj.getUdp_dst().get(nf.getDstIp()) + 1);
				
				
				// NTP flood
				if(nf.getSrcPort().equals("123") || nf.getDstPort().equals("123")){
					metricsObj.setNtp_num(metricsObj.getNtp_num() + 1);
					
					String ntpSrc = "";
					String ntpDst = "";
					if(nf.getSrcPort().equals("123")){
						ntpSrc = nf.getSrcIp();
						ntpDst = nf.getDstIp();
					}
					else{
						ntpSrc = nf.getDstIp();
						ntpDst = nf.getSrcIp();
					}
					
					if(!metricsObj.getNtp_src().containsKey(ntpSrc)){
						metricsObj.getNtp_src().put(ntpSrc, 0);
					}
					metricsObj.getNtp_src().put(ntpSrc, metricsObj.getNtp_src().get(ntpSrc) + 1);
					
					if(!metricsObj.getNtp_dst().containsKey(ntpDst)){
						metricsObj.getNtp_dst().put(ntpDst, 0);
					}
					metricsObj.getNtp_dst().put(ntpDst, metricsObj.getNtp_dst().get(ntpDst) + 1);
				}
			
				
			}
			else if(nf.getProtocol().equalsIgnoreCase("ICMP")){
				metricsObj.setIcmp_num(metricsObj.getIcmp_num() + 1);
				
				if(!metricsObj.getIcmp_src().containsKey(nf.getSrcIp())){
					metricsObj.getIcmp_src().put(nf.getSrcIp(), 0);
				}
				metricsObj.getIcmp_src().put(nf.getSrcIp(), metricsObj.getIcmp_src().get(nf.getSrcIp()) + 1);
				
				if(!metricsObj.getIcmp_dst().containsKey(nf.getDstIp())){
					metricsObj.getIcmp_dst().put(nf.getDstIp(), 0);
				}
				metricsObj.getIcmp_dst().put(nf.getDstIp(), metricsObj.getIcmp_dst().get(nf.getDstIp()) + 1);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(e.getMessage() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
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
	
	boolean isInvalidateFlag(String flags){
		
		if(flags == null || flags.length() != 6 || flags.startsWith("0x")){
			return true;
		}
		
		return false;
	}
	
	boolean isSYNFlood(String flags){
		
		if(flags != null && flags.length() == 6){
			if(flags.charAt(4) == 'S' && hasNumFields(1, flags)){
				return true;
			}
		}
			
		return false;
	}
	
	boolean isACKFlood(String flags){
		
		if(flags != null && flags.length() == 6){
			if((flags.charAt(1) == 'A' && hasNumFields(1, flags))
					|| (flags.charAt(1) == 'A' && flags.charAt(2) == 'P' && hasNumFields(2, flags))){
				return true;
			}
		}
		
		return false;
	}
	
	boolean isFINFlood(String flags){
		
		if(flags != null && flags.length() == 6){
			if((flags.charAt(5) == 'F' && hasNumFields(1, flags))
					|| (flags.charAt(5) == 'F' && flags.charAt(1) == 'A' && hasNumFields(2, flags))
					|| (flags.charAt(5) == 'F' && flags.charAt(1) == 'A' && flags.charAt(2) == 'P' && hasNumFields(3, flags))){
				return true;
			}
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
	

	StringBuilder genShellCommand(DDoSMetricsVO vo){
		// 该值将作为ES document的id
		String id = vo.getTimeWindow().replace(" ", "_").replaceAll(":", "-");
		
		StringBuilder sb = new StringBuilder(ES_PUSH_CURL_PREFIX);
		
		// ntp
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics_ntp\", \"_type\" : \"ntp\" } }\n")
		  .append("{\"time_window\": \"" + vo.getTimeWindow() + "\", ")
		  .append("\"ntp_num\": " + vo.getNtp_num() + ", ")
		  .append("\"ntp_to_flow_ratio\" : " + vo.getNtp_to_flow_ratio() + ", ")
		  .append("\"ntp_to_udp_ratio\" : " + vo.getNtp_to_udp_ratio() + "}\n ");
		
		// syn
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics_syn\", \"_type\" : \"syn\" } }\n")
		  .append("{\"time_window\": \"" + vo.getTimeWindow() + "\", ")
		  .append("\"syn_num\": " + vo.getSyn_num() + ", ")
		  .append("\"syn_to_flow_ratio\" : " + vo.getSyn_to_flow_ratio() + ", ")
		  .append("\"syn_to_tcp_ratio\" : " + vo.getSyn_to_tcp_ratio() + ", ")
		  .append("\"syn_src_less_5\" : " + vo.getSyn_src_less_5() + ", ")
		  .append("\"syn_src_less_10\" : " + vo.getSyn_src_less_10() + ", ")
		  .append("\"syn_src_less_20\" : " + vo.getSyn_src_less_20() + ", ")
		  .append("\"syn_src_large_20\" : " + vo.getSyn_src_large_20() + ", ")
		  .append("\"syn_dst_less_5\" : " + vo.getSyn_dst_less_5() + ", ")
		  .append("\"syn_dst_less_10\" : " + vo.getSyn_dst_less_10() + ", ")
		  .append("\"syn_dst_less_20\" : " + vo.getSyn_dst_less_20() + ", ")
		  .append("\"syn_dst_large_20\" : " + vo.getSyn_dst_large_20() + ", ")
		  .append("\"syn_src_less_5_ratio\" : " + vo.getSyn_src_less_5_ratio() + ", ")
		  .append("\"syn_src_less_10_ratio\" : " + vo.getSyn_src_less_10_ratio() + ", ")
		  .append("\"syn_src_less_20_ratio\" : " + vo.getSyn_src_less_20_ratio() + ", ")
		  .append("\"syn_src_large_20_ratio\" : " + vo.getSyn_src_large_20_ratio() + ", ")
		  .append("\"syn_dst_less_5_ratio\" : " + vo.getSyn_dst_less_5_ratio() + ", ")
		  .append("\"syn_dst_less_10_ratio\" : " + vo.getSyn_dst_less_10_ratio() + ", ")
		  .append("\"syn_dst_less_20_ratio\" : " + vo.getSyn_dst_less_20_ratio() + ", ")
		  .append("\"syn_dst_large_20_ratio\" : " + vo.getSyn_dst_large_20_ratio() + "}\n ");
		  
		// ack
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics_ack\", \"_type\" : \"ack\" } }\n")
		  .append("{\"time_window\": \"" + vo.getTimeWindow() + "\", ")
		  .append("\"ack_num\": " + vo.getAck_num() + ", ")
		  .append("\"ack_to_flow_ratio\" : " + vo.getAck_to_flow_ratio() + ", ")
		  .append("\"ack_to_tcp_ratio\" : " + vo.getAck_to_tcp_ratio() + ", ")
		  .append("\"ack_src_less_5\" : " + vo.getAck_src_less_5() + ", ")
		  .append("\"ack_src_less_10\" : " + vo.getAck_src_less_10() + ", ")
		  .append("\"ack_src_less_20\" : " + vo.getAck_src_less_20() + ", ")
		  .append("\"ack_src_large_20\" : " + vo.getAck_src_large_20() + ", ")
		  .append("\"ack_dst_less_5\" : " + vo.getAck_dst_less_5() + ", ")
		  .append("\"ack_dst_less_10\" : " + vo.getAck_dst_less_10() + ", ")
		  .append("\"ack_dst_less_20\" : " + vo.getAck_dst_less_20() + ", ")
		  .append("\"ack_dst_large_20\" : " + vo.getAck_dst_large_20() + ", ")
		  .append("\"ack_src_less_5_ratio\" : " + vo.getAck_src_less_5_ratio() + ", ")
		  .append("\"ack_src_less_10_ratio\" : " + vo.getAck_src_less_10_ratio() + ", ")
		  .append("\"ack_src_less_20_ratio\" : " + vo.getAck_src_less_20_ratio() + ", ")
		  .append("\"ack_src_large_20_ratio\" : " + vo.getAck_src_large_20_ratio() + ", ")
		  .append("\"ack_dst_less_5_ratio\" : " + vo.getAck_dst_less_5_ratio() + ", ")
		  .append("\"ack_dst_less_10_ratio\" : " + vo.getAck_dst_less_10_ratio() + ", ")
		  .append("\"ack_dst_less_20_ratio\" : " + vo.getAck_dst_less_20_ratio() + ", ")
		  .append("\"ack_dst_large_20_ratio\" : " + vo.getAck_dst_large_20_ratio() + "}\n ");
		
		// fin
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics_fin\", \"_type\" : \"fin\" } }\n")
		  .append("{\"time_window\": \"" + vo.getTimeWindow() + "\", ")
		  .append("\"fin_num\": " + vo.getFin_num() + ", ")
		  .append("\"fin_to_flow_ratio\" : " + vo.getFin_to_flow_ratio() + ", ")
		  .append("\"fin_to_tcp_ratio\" : " + vo.getFin_to_tcp_ratio() + ", ")
		  .append("\"fin_src_less_5\" : " + vo.getFin_src_less_5() + ", ")
		  .append("\"fin_src_less_10\" : " + vo.getFin_src_less_10() + ", ")
		  .append("\"fin_src_less_20\" : " + vo.getFin_src_less_20() + ", ")
		  .append("\"fin_src_large_20\" : " + vo.getFin_src_large_20() + ", ")
		  .append("\"fin_dst_less_5\" : " + vo.getFin_dst_less_5() + ", ")
		  .append("\"fin_dst_less_10\" : " + vo.getFin_dst_less_10() + ", ")
		  .append("\"fin_dst_less_20\" : " + vo.getFin_dst_less_20() + ", ")
		  .append("\"fin_dst_large_20\" : " + vo.getFin_dst_large_20() + ", ")
		  .append("\"fin_src_less_5_ratio\" : " + vo.getFin_src_less_5_ratio() + ", ")
		  .append("\"fin_src_less_10_ratio\" : " + vo.getFin_src_less_10_ratio() + ", ")
		  .append("\"fin_src_less_20_ratio\" : " + vo.getFin_src_less_20_ratio() + ", ")
		  .append("\"fin_src_large_20_ratio\" : " + vo.getFin_src_large_20_ratio() + ", ")
		  .append("\"fin_dst_less_5_ratio\" : " + vo.getFin_dst_less_5_ratio() + ", ")
		  .append("\"fin_dst_less_10_ratio\" : " + vo.getFin_dst_less_10_ratio() + ", ")
		  .append("\"fin_dst_less_20_ratio\" : " + vo.getFin_dst_less_20_ratio() + ", ")
		  .append("\"fin_dst_large_20_ratio\" : " + vo.getFin_dst_large_20_ratio() + "}\n ");
		
		// udp
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics_udp\", \"_type\" : \"udp\" } }\n")
		  .append("{\"time_window\": \"" + vo.getTimeWindow() + "\", ")
		  .append("\"udp_num\": " + vo.getFin_num() + ", ")
		  .append("\"udp_to_flow_ratio\" : " + vo.getUdp_to_flow_ratio() + ", ")
		  .append("\"udp_src_less_5\" : " + vo.getUdp_src_less_5() + ", ")
		  .append("\"udp_src_less_10\" : " + vo.getUdp_src_less_10() + ", ")
		  .append("\"udp_src_less_20\" : " + vo.getUdp_src_less_20() + ", ")
		  .append("\"udp_src_large_20\" : " + vo.getUdp_src_large_20() + ", ")
		  .append("\"udp_dst_less_5\" : " + vo.getUdp_dst_less_5() + ", ")
		  .append("\"udp_dst_less_10\" : " + vo.getUdp_dst_less_10() + ", ")
		  .append("\"udp_dst_less_20\" : " + vo.getUdp_dst_less_20() + ", ")
		  .append("\"udp_dst_large_20\" : " + vo.getUdp_dst_large_20() + ", ")
		  .append("\"udp_src_less_5_ratio\" : " + vo.getUdp_src_less_5_ratio() + ", ")
		  .append("\"udp_src_less_10_ratio\" : " + vo.getUdp_src_less_10_ratio() + ", ")
		  .append("\"udp_src_less_20_ratio\" : " + vo.getUdp_src_less_20_ratio() + ", ")
		  .append("\"udp_src_large_20_ratio\" : " + vo.getUdp_src_large_20_ratio() + ", ")
		  .append("\"udp_dst_less_5_ratio\" : " + vo.getUdp_dst_less_5_ratio() + ", ")
		  .append("\"udp_dst_less_10_ratio\" : " + vo.getUdp_dst_less_10_ratio() + ", ")
		  .append("\"udp_dst_less_20_ratio\" : " + vo.getUdp_dst_less_20_ratio() + ", ")
		  .append("\"udp_dst_large_20_ratio\" : " + vo.getUdp_dst_large_20_ratio() + "}\n ");
		
		// icmp
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics_icmp\", \"_type\" : \"icmp\" } }\n")
		  .append("{\"time_window\": \"" + vo.getTimeWindow() + "\", ")
		  .append("\"icmp_num\": " + vo.getFin_num() + ", ")
		  .append("\"icmp_to_flow_ratio\" : " + vo.getIcmp_to_flow_ratio() + ", ")
		  .append("\"icmp_src_less_5\" : " + vo.getIcmp_src_less_5() + ", ")
		  .append("\"icmp_src_less_10\" : " + vo.getIcmp_src_less_10() + ", ")
		  .append("\"icmp_src_less_20\" : " + vo.getIcmp_src_less_20() + ", ")
		  .append("\"icmp_src_large_20\" : " + vo.getIcmp_src_large_20() + ", ")
		  .append("\"icmp_dst_less_5\" : " + vo.getIcmp_dst_less_5() + ", ")
		  .append("\"icmp_dst_less_10\" : " + vo.getIcmp_dst_less_10() + ", ")
		  .append("\"icmp_dst_less_20\" : " + vo.getIcmp_dst_less_20() + ", ")
		  .append("\"icmp_dst_large_20\" : " + vo.getIcmp_dst_large_20() + ", ")
		  .append("\"icmp_src_less_5_ratio\" : " + vo.getIcmp_src_less_5_ratio() + ", ")
		  .append("\"icmp_src_less_10_ratio\" : " + vo.getIcmp_src_less_10_ratio() + ", ")
		  .append("\"icmp_src_less_20_ratio\" : " + vo.getIcmp_src_less_20_ratio() + ", ")
		  .append("\"icmp_src_large_20_ratio\" : " + vo.getIcmp_src_large_20_ratio() + ", ")
		  .append("\"icmp_dst_less_5_ratio\" : " + vo.getIcmp_dst_less_5_ratio() + ", ")
		  .append("\"icmp_dst_less_10_ratio\" : " + vo.getIcmp_dst_less_10_ratio() + ", ")
		  .append("\"icmp_dst_less_20_ratio\" : " + vo.getIcmp_dst_less_20_ratio() + ", ")
		  .append("\"icmp_dst_large_20_ratio\" : " + vo.getIcmp_dst_large_20_ratio() + "}\n ");
		
		// inval_flag
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics_inval_flag\", \"_type\" : \"inval_flag\" } }\n")
		  .append("{\"time_window\": \"" + vo.getTimeWindow() + "\", ")
		  .append("\"inval_flag_num\": " + vo.getFin_num() + ", ")
		  .append("\"inval_flag_to_flow_ratio\" : " + vo.getInval_flag_to_flow_ratio() + ", ")
		  .append("\"inval_flag_to_tcp_ratio\" : " + vo.getInval_flag_to_tcp_ratio() + ", ")
		  .append("\"inval_flag_src_less_5\" : " + vo.getInval_flag_src_less_5() + ", ")
		  .append("\"inval_flag_src_less_10\" : " + vo.getInval_flag_src_less_10() + ", ")
		  .append("\"inval_flag_src_less_20\" : " + vo.getInval_flag_src_less_20() + ", ")
		  .append("\"inval_flag_src_large_20\" : " + vo.getInval_flag_src_large_20() + ", ")
		  .append("\"inval_flag_dst_less_5\" : " + vo.getInval_flag_dst_less_5() + ", ")
		  .append("\"inval_flag_dst_less_10\" : " + vo.getInval_flag_dst_less_10() + ", ")
		  .append("\"inval_flag_dst_less_20\" : " + vo.getInval_flag_dst_less_20() + ", ")
		  .append("\"inval_flag_dst_large_20\" : " + vo.getInval_flag_dst_large_20() + ", ")
		  .append("\"inval_flag_src_less_5_ratio\" : " + vo.getInval_flag_src_less_5_ratio() + ", ")
		  .append("\"inval_flag_src_less_10_ratio\" : " + vo.getInval_flag_src_less_10_ratio() + ", ")
		  .append("\"inval_flag_src_less_20_ratio\" : " + vo.getInval_flag_src_less_20_ratio() + ", ")
		  .append("\"inval_flag_src_large_20_ratio\" : " + vo.getInval_flag_src_large_20_ratio() + ", ")
		  .append("\"inval_flag_dst_less_5_ratio\" : " + vo.getInval_flag_dst_less_5_ratio() + ", ")
		  .append("\"inval_flag_dst_less_10_ratio\" : " + vo.getInval_flag_dst_less_10_ratio() + ", ")
		  .append("\"inval_flag_dst_less_20_ratio\" : " + vo.getInval_flag_dst_less_20_ratio() + ", ")
		  .append("\"inval_flag_dst_large_20_ratio\" : " + vo.getInval_flag_dst_large_20_ratio() + "}\n ");
		
		sb.append("'");
		
		return sb;
	}
	
	// 将计算得到的网络负载指标写入ES中以供kibana用图像渲染出来(调用命令行)
	void push2ElasticsearchWithShell(DDoSMetricsVO vo){
		try {
			// 调用Elasticsearch 的bulk API 写数据
			StringBuilder sb = genShellCommand(vo);
			String cmd = sb.toString();
			
			
			Process process = Runtime.getRuntime().exec(new String[]{"bash","-c",cmd});
			process.waitFor();
			
			
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
	
	// 调用java 的http client实现
	void push2ElasticsearchWithHttpClient(DDoSMetricsVO vo){
		
	}
	
	public void expireDDoSMetrics(String timeWindow, DDoSMetricsCacheObj me){
		
		try {
			// 输出timeWindow对应的实时指标(暂时输出到mysql中)
			DDoSMetricsVO vo = genDDoSMetrics(timeWindow, me);
			
			// 数据存入HBase
			this.storageManager.addDDoSMetrics(vo);
			
			// 再将结果写入Elasticsearch中
			push2ElasticsearchWithShell(vo);
			
			// 清除相关缓存
			this.metricsCache.remove(timeWindow);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(e.getMessage() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
	}

	/**
	 * 调用的函数按降序进行排列
	 * @param top
	 * @param map
     * @return
     */
	String getTopItems(int top, Map<String, Integer> map){
		
		try{
			
			String[] ls = new String[map.size()];
			int i = 0;
			for(Entry<String, Integer> e : map.entrySet()){
				ls[i ++] = e.getKey() + "#" + e.getValue();
			}
			
			Arrays.sort(ls, new TopIpComparator());
			
			StringBuilder sb = new StringBuilder();
			top = Math.min(top, map.size());
			
			for(int j = 0; j < top; j ++){
				sb.append(ls[j] + ",");
			}
			
			return sb.toString();
		}
		catch(Exception e){
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(e.getMessage() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	MetricsRangeVO metricsRangeCalculate(Map<String, Integer> map){
		try {
			MetricsRangeVO rvo = new MetricsRangeVO();
			
			for(Entry<String, Integer> e : map.entrySet()){
				if(e.getValue() < 5){
					rvo.counts[0] ++;
				}
				else if(e.getValue() < 10){
					rvo.counts[1] ++;
				}
				else if(e.getValue() < 20){
					rvo.counts[2] ++;
				}
				else{
					rvo.counts[3] ++;
				}
			}
			
			if(map != null && map.size() > 0){
				rvo.ratios[0] = (double)rvo.counts[0] / (double)map.size();
				rvo.ratios[1] = (double)rvo.counts[1] / (double)map.size();
				rvo.ratios[2] = (double)rvo.counts[2] / (double)map.size();
				rvo.ratios[3] = (double)rvo.counts[3] / (double)map.size();
			}
			
			
			return rvo;
		} catch (Exception e) {
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(e.getMessage() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	DDoSMetricsVO genDDoSMetrics(String timeWindow, DDoSMetricsCacheObj me){
		
		try{

			DDoSMetricsVO vo = new DDoSMetricsVO();
			vo.setTimeWindow(timeWindow);
			vo.setRecord_creation_num(me.getRecord_creation_num());
			
			vo.setNtp_num(me.getNtp_num());
			vo.setNtp_to_flow_ratio((double)(me.getNtp_num()) / (double)(me.getRecord_creation_num()));
			vo.setNtp_to_udp_ratio((double)(me.getNtp_num()) / (double)(me.getUdp_num()));
			vo.setNtp_src_top_20(getTopItems(20, me.getNtp_src()));
			vo.setNtp_dst_top_20(getTopItems(20, me.getNtp_dst()));
			vo.setSyn_num(me.getSyn_num());
			vo.setSyn_to_flow_ratio((double)(me.getSyn_num()) / (double)(me.getRecord_creation_num()));
			vo.setSyn_to_tcp_ratio((double)(me.getSyn_num()) / (double)(me.getTcp_num()));
			
			MetricsRangeVO synSrcRan = metricsRangeCalculate(me.getSyn_src());
			vo.setSyn_src_less_5(synSrcRan.counts[0]);
			vo.setSyn_src_less_10(synSrcRan.counts[1]);
			vo.setSyn_src_less_20(synSrcRan.counts[2]);
			vo.setSyn_src_large_20(synSrcRan.counts[3]);
			vo.setSyn_src_less_5_ratio(synSrcRan.ratios[0]);
			vo.setSyn_src_less_10_ratio(synSrcRan.ratios[1]);
			vo.setSyn_src_less_20_ratio(synSrcRan.ratios[2]);
			vo.setSyn_src_large_20_ratio(synSrcRan.ratios[3]);
			
			MetricsRangeVO synDstRan = metricsRangeCalculate(me.getSyn_dst());
			vo.setSyn_dst_less_5(synDstRan.counts[0]);
			vo.setSyn_dst_less_10(synDstRan.counts[1]);
			vo.setSyn_dst_less_20(synDstRan.counts[2]);
			vo.setSyn_dst_large_20(synDstRan.counts[3]);
			vo.setSyn_dst_less_5_ratio(synDstRan.ratios[0]);
			vo.setSyn_dst_less_10_ratio(synDstRan.ratios[1]);
			vo.setSyn_dst_less_20_ratio(synDstRan.ratios[2]);
			vo.setSyn_dst_large_20_ratio(synDstRan.ratios[3]);
			
			vo.setSyn_src_top_20(getTopItems(20, me.getSyn_src()));
			vo.setSyn_dst_top_20(getTopItems(20, me.getSyn_dst()));
			
			vo.setAck_num(me.getAck_num());
			vo.setAck_to_flow_ratio((double)(me.getAck_num()) / (double)(me.getRecord_creation_num()));
			vo.setAck_to_tcp_ratio((double)(me.getAck_num()) / (double)(me.getTcp_num()));
			
			MetricsRangeVO ackSrcRan = metricsRangeCalculate(me.getAck_src());
			vo.setAck_src_less_5(ackSrcRan.counts[0]);
			vo.setAck_src_less_10(ackSrcRan.counts[1]);
			vo.setAck_src_less_20(ackSrcRan.counts[2]);
			vo.setAck_src_large_20(ackSrcRan.counts[3]);
			vo.setAck_src_less_5_ratio(ackSrcRan.ratios[0]);
			vo.setAck_src_less_10_ratio(ackSrcRan.ratios[1]);
			vo.setAck_src_less_20_ratio(ackSrcRan.ratios[2]);
			vo.setAck_src_large_20_ratio(ackSrcRan.ratios[3]);
			
			MetricsRangeVO ackDstRan = metricsRangeCalculate(me.getAck_dst());
			vo.setAck_dst_less_5(ackDstRan.counts[0]);
			vo.setAck_dst_less_10(ackDstRan.counts[1]);
			vo.setAck_dst_less_20(ackDstRan.counts[2]);
			vo.setAck_dst_large_20(ackDstRan.counts[3]);
			vo.setAck_dst_less_5_ratio(ackDstRan.ratios[0]);
			vo.setAck_dst_less_10_ratio(ackDstRan.ratios[1]);
			vo.setAck_dst_less_20_ratio(ackDstRan.ratios[2]);
			vo.setAck_dst_large_20_ratio(ackDstRan.ratios[3]);
			
			vo.setAck_src_top_20(getTopItems(20, me.getAck_src()));
			vo.setAck_dst_top_20(getTopItems(20, me.getAck_dst()));
			
			vo.setFin_num(me.getFin_num());
			vo.setFin_to_flow_ratio((double)(me.getFin_num()) / (double)(me.getRecord_creation_num()));
			vo.setFin_to_tcp_ratio((double)(me.getFin_num()) / (double)(me.getTcp_num()));
			
			MetricsRangeVO finSrcRan = metricsRangeCalculate(me.getFin_src());
			vo.setFin_src_less_5(finSrcRan.counts[0]);
			vo.setFin_src_less_10(finSrcRan.counts[1]);
			vo.setFin_src_less_20(finSrcRan.counts[2]);
			vo.setFin_src_large_20(finSrcRan.counts[3]);
			vo.setFin_src_less_5_ratio(finSrcRan.ratios[0]);
			vo.setFin_src_less_10_ratio(finSrcRan.ratios[1]);
			vo.setFin_src_less_20_ratio(finSrcRan.ratios[2]);
			vo.setFin_src_large_20_ratio(finSrcRan.ratios[3]);
			
			MetricsRangeVO finDstRan = metricsRangeCalculate(me.getFin_dst());
			vo.setFin_dst_less_5(finDstRan.counts[0]);
			vo.setFin_dst_less_10(finDstRan.counts[1]);
			vo.setFin_dst_less_20(finDstRan.counts[2]);
			vo.setFin_dst_large_20(finDstRan.counts[3]);
			vo.setFin_dst_less_5_ratio(finDstRan.ratios[0]);
			vo.setFin_dst_less_10_ratio(finDstRan.ratios[1]);
			vo.setFin_dst_less_20_ratio(finDstRan.ratios[2]);
			vo.setFin_dst_large_20_ratio(finDstRan.ratios[3]);
			
			vo.setFin_src_top_20(getTopItems(20, me.getFin_src()));
			vo.setFin_dst_top_20(getTopItems(20, me.getFin_dst()));
			
			vo.setUdp_num(me.getUdp_num());
			vo.setUdp_to_flow_ratio((double)(me.getUdp_num()) / (double)(me.getRecord_creation_num()));
			
			MetricsRangeVO udpSrcRan = metricsRangeCalculate(me.getUdp_src());
			vo.setUdp_src_less_5(udpSrcRan.counts[0]);
			vo.setUdp_src_less_10(udpSrcRan.counts[1]);
			vo.setUdp_src_less_20(udpSrcRan.counts[2]);
			vo.setUdp_src_large_20(udpSrcRan.counts[3]);
			vo.setUdp_src_less_5_ratio(udpSrcRan.ratios[0]);
			vo.setUdp_src_less_10_ratio(udpSrcRan.ratios[1]);
			vo.setUdp_src_less_20_ratio(udpSrcRan.ratios[2]);
			vo.setUdp_src_large_20_ratio(udpSrcRan.ratios[3]);
			
			MetricsRangeVO udpDstRan = metricsRangeCalculate(me.getUdp_dst());
			vo.setUdp_dst_less_5(udpDstRan.counts[0]);
			vo.setUdp_dst_less_10(udpDstRan.counts[1]);
			vo.setUdp_dst_less_20(udpDstRan.counts[2]);
			vo.setUdp_dst_large_20(udpDstRan.counts[3]);
			vo.setUdp_dst_less_5_ratio(udpDstRan.ratios[0]);
			vo.setUdp_dst_less_10_ratio(udpDstRan.ratios[1]);
			vo.setUdp_dst_less_20_ratio(udpDstRan.ratios[2]);
			vo.setUdp_dst_large_20_ratio(udpDstRan.ratios[3]);
			
			vo.setUdp_src_top_20(getTopItems(20, me.getUdp_src()));
			vo.setUdp_dst_top_20(getTopItems(20, me.getUdp_dst()));
			
			vo.setIcmp_num(me.getIcmp_num());
			vo.setIcmp_to_flow_ratio((double)(me.getIcmp_num()) / (double)(me.getRecord_creation_num()));
			
			MetricsRangeVO IcmpSrcRan = metricsRangeCalculate(me.getIcmp_src());
			vo.setIcmp_src_less_5(IcmpSrcRan.counts[0]);
			vo.setIcmp_src_less_10(IcmpSrcRan.counts[1]);
			vo.setIcmp_src_less_20(IcmpSrcRan.counts[2]);
			vo.setIcmp_src_large_20(IcmpSrcRan.counts[3]);
			vo.setIcmp_src_less_5_ratio(IcmpSrcRan.ratios[0]);
			vo.setIcmp_src_less_10_ratio(IcmpSrcRan.ratios[1]);
			vo.setIcmp_src_less_20_ratio(IcmpSrcRan.ratios[2]);
			vo.setIcmp_src_large_20_ratio(IcmpSrcRan.ratios[3]);
			
			MetricsRangeVO IcmpDstRan = metricsRangeCalculate(me.getIcmp_dst());
			vo.setIcmp_dst_less_5(IcmpDstRan.counts[0]);
			vo.setIcmp_dst_less_10(IcmpDstRan.counts[1]);
			vo.setIcmp_dst_less_20(IcmpDstRan.counts[2]);
			vo.setIcmp_dst_large_20(IcmpDstRan.counts[3]);
			vo.setIcmp_dst_less_5_ratio(IcmpDstRan.ratios[0]);
			vo.setIcmp_dst_less_10_ratio(IcmpDstRan.ratios[1]);
			vo.setIcmp_dst_less_20_ratio(IcmpDstRan.ratios[2]);
			vo.setIcmp_dst_large_20_ratio(IcmpDstRan.ratios[3]);
			
			vo.setIcmp_src_top_20(getTopItems(20, me.getIcmp_src()));
			vo.setIcmp_dst_top_20(getTopItems(20, me.getIcmp_dst()));
			
			
			vo.setInval_flag_num(me.getInval_flag_num());
			vo.setInval_flag_to_flow_ratio((double)(me.getInval_flag_num()) / (double)(me.getRecord_creation_num()));
			vo.setInval_flag_to_tcp_ratio((double)(me.getInval_flag_num()) / (double)(me.getTcp_num()));
			
			MetricsRangeVO Inval_flagSrcRan = metricsRangeCalculate(me.getInval_flag_src());
			vo.setInval_flag_src_less_5(Inval_flagSrcRan.counts[0]);
			vo.setInval_flag_src_less_10(Inval_flagSrcRan.counts[1]);
			vo.setInval_flag_src_less_20(Inval_flagSrcRan.counts[2]);
			vo.setInval_flag_src_large_20(Inval_flagSrcRan.counts[3]);
			vo.setInval_flag_src_less_5_ratio(Inval_flagSrcRan.ratios[0]);
			vo.setInval_flag_src_less_10_ratio(Inval_flagSrcRan.ratios[1]);
			vo.setInval_flag_src_less_20_ratio(Inval_flagSrcRan.ratios[2]);
			vo.setInval_flag_src_large_20_ratio(Inval_flagSrcRan.ratios[3]);
			
			MetricsRangeVO Inval_flagDstRan = metricsRangeCalculate(me.getInval_flag_dst());
			vo.setInval_flag_dst_less_5(Inval_flagDstRan.counts[0]);
			vo.setInval_flag_dst_less_10(Inval_flagDstRan.counts[1]);
			vo.setInval_flag_dst_less_20(Inval_flagDstRan.counts[2]);
			vo.setInval_flag_dst_large_20(Inval_flagDstRan.counts[3]);
			vo.setInval_flag_dst_less_5_ratio(Inval_flagDstRan.ratios[0]);
			vo.setInval_flag_dst_less_10_ratio(Inval_flagDstRan.ratios[1]);
			vo.setInval_flag_dst_less_20_ratio(Inval_flagDstRan.ratios[2]);
			vo.setInval_flag_dst_large_20_ratio(Inval_flagDstRan.ratios[3]);
			
			vo.setInval_flag_src_top_20(getTopItems(20, me.getInval_flag_src()));
			vo.setInval_flag_dst_top_20(getTopItems(20, me.getInval_flag_dst()));
			
			return vo;
		}
		catch(Exception e){
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(e.getMessage() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
				
		return null;
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
		
	}
	
	public void cleanup() {
		
	}
	
	
	// 测试
	public static void main(String[] args) {
		DDoSMetricsGenerator dg = new DDoSMetricsGenerator();
		
		DDoSMetricsVO vo = mockMetricsData();
		
		dg.push2ElasticsearchWithShell(vo);
		
		System.out.println("done !");
	}
	
	static DDoSMetricsVO mockMetricsData(){
		DDoSMetricsVO vo = new DDoSMetricsVO("2016-03-12 12:12:12", 1, 1, 1, 1, "202.131.111.111", "111.111.111.111", 1, 2, 3, 4
				,5 ,6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, "202.131.111.111", "111.111.111.111", 
				1, 2, 3, 4
				,5 ,6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, "202.131.111.111", "111.111.111.111", 
				1, 2, 3, 4
				,5 ,6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, "202.131.111.111", "111.111.111.111",
				1, 2, 3, 4
				,5 ,6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, "202.131.111.111", "111.111.111.111", 
				1, 2, 3, 4
				,5 ,6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, "202.131.111.111", "111.111.111.111",
				1, 2, 3, 4
				,5 ,6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, "202.131.111.111", "111.111.111.111");
		
		return vo;
	}
}
