package com.huirong.storage.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.huirong.biz.impl.ApplicationsCatchObj;
import com.huirong.biz.impl.ApplicationsEvent;
import com.huirong.storage.vo.AttackEvent;
import com.huirong.storage.vo.NetflowRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.huirong.storage.StorageManager;
import com.huirong.storage.vo.BlacklistIpSession;
import com.huirong.storage.vo.DDoSMetricsVO;
import com.huirong.storage.vo.UnidirectionalSessionRecord;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年3月13日
 */
public class TjutStorageManager implements StorageManager{

	public final static String BLACK_LIST_TABLE = "tjut_black_list";
	public final static String BLACK_LIST_IP_SESSION_TABLE = "tjut_blacklist_ip_session";
	public final static String BACKWARD_SESSION_RECORD = "tjut_unidirectional_backward_session_record";
	public final static String FORWARD_SESSION_RECORD = "tjut_unidirectional_forward_session_record";
	public final static String REALTIME_METRICS = "tjut_realtime_ddos_metrics";
	public final static String ATTACK_EVENT = "tjut_attack_event";

	public static final String APPLICATION_NETFLOW = "Application_netflow";
	public static final String APPLICATION_METRICS = "Application_metrics";
	
	public static Configuration config = null;
	static{
		config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", "311b-node-2,310b-node-1");
		
		//config.addResource(new Path(System.getenv("HBASE_CONF_DIR"), "hbase-site.xml"));
		//config.addResource(new Path(System.getenv("HADOOP_CONF_DIR"), "core-site.xml"));
	}

	@Override
	public void addApplicationsMetrics(ApplicationsCatchObj catchObj, String port) {
		try{
			HTable table = new HTable(config, APPLICATION_METRICS);
			String rowKey =  catchObj.getTime()+ "-" + port;
			Put put = new Put(Bytes.toBytes(rowKey));
			put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("time"), Bytes.toBytes(String.valueOf(catchObj.getTime())));
			put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("bytes"), Bytes.toBytes(String.valueOf(catchObj.getBytes())));
			put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("packets"), Bytes.toBytes(String.valueOf(catchObj.getPackets())));
			put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("links"), Bytes.toBytes(String.valueOf(catchObj.getLinks())));
			table.put(put);
			table.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void addApplicationsEvents(ApplicationsCatchObj catchObj, String port) {
		try{
			HTable table = new HTable(config, APPLICATION_NETFLOW);
			Set<ApplicationsEvent> events = catchObj.getApplicationsEvents();
			for (ApplicationsEvent event: events){
				String rowKey = event.getTime() + "-" + event.getSrcIp() + "-" + event.getDstIp() + "-" + event.getProtocol();
				Put put = new Put(Bytes.toBytes(rowKey));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("time"), Bytes.toBytes(String.valueOf(event.getTime())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("srcIp"), Bytes.toBytes(String.valueOf(event.getSrcIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("srcPort"), Bytes.toBytes(String.valueOf(event.getSrcPort())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("dstIp"), Bytes.toBytes(String.valueOf(event.getDstIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("dstPort"), Bytes.toBytes(String.valueOf(event.getDstPort())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("protocal"), Bytes.toBytes(String.valueOf(event.getProtocol())));
				table.put(put);
			}
			table.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public Set<String> getBlacklist() {
		
		Set<String> ret = new HashSet<String>();
		HTable table = null;
		ResultScanner rs = null;
		
		try {
			Scan scan = new Scan();
	        table = new HTable(config, Bytes.toBytes(BLACK_LIST_TABLE));
	        rs = table.getScanner(scan);
	        
            for (Result r : rs) {
            	
                for (KeyValue kv : r.list()) {
                	String rowkey = Bytes.toString(kv.getRow());
                	String cf = Bytes.toString(kv.getFamily());
                	String id = Bytes.toString(kv.getQualifier());
                	
                	if(cf != null && cf.equalsIgnoreCase("default_cf") && id != null && id.equalsIgnoreCase("ip")){
                		ret.add(Bytes.toString(kv.getValue()));
                		
                	}
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
		finally{
			
			rs.close();
            try {
				table.close();
			} catch (IOException e) {
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
		
		return ret;
	}

	public void addBlacklistIpSession(List<BlacklistIpSession> ls) {
		HTable table = null;
		
		try {
			table = new HTable(config, Bytes.toBytes(BLACK_LIST_IP_SESSION_TABLE));// HTabel负责跟记录相关的操作如增删改查等
			
			for(BlacklistIpSession bi : ls){
				
				String rowkey = bi.getIp() + "@" + bi.getRecord().getDate();
				
				NetflowRecord nf = bi.getRecord();
				
				Put put = new Put(Bytes.toBytes(rowkey));// 设置rowkey
				
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("ip"), Bytes.toBytes(String.valueOf(bi.getIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("date"), Bytes.toBytes(String.valueOf(nf.getDate())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("duration"), Bytes.toBytes(String.valueOf(nf.getDuration())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("protocol"), Bytes.toBytes(String.valueOf(nf.getProtocol())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("srcIp"), Bytes.toBytes(String.valueOf(nf.getSrcIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("srcPort"), Bytes.toBytes(String.valueOf(nf.getSrcPort())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("dstIp"), Bytes.toBytes(String.valueOf(nf.getDstIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("dstPort"), Bytes.toBytes(String.valueOf(nf.getDstPort())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("flags"), Bytes.toBytes(String.valueOf(nf.getFlags())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("tos"), Bytes.toBytes(String.valueOf(nf.getTos())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("packets"), Bytes.toBytes(String.valueOf(nf.getPackets())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("bytes"), Bytes.toBytes(String.valueOf(nf.getBytes())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("pps"), Bytes.toBytes(String.valueOf(nf.getPps())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("bps"), Bytes.toBytes(String.valueOf(nf.getBps())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("bytespp"), Bytes.toBytes(String.valueOf(nf.getBytespp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("flows"), Bytes.toBytes(String.valueOf(nf.getFlows())));
		                                                                   
		        
		        table.put(put);
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
		finally{
			try {
				table.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	// 工具函数
	void addSessionRecord(List<UnidirectionalSessionRecord> ls, HTable table){
		try {
			
			for(UnidirectionalSessionRecord bi : ls){
				
				String rowkey = bi.getSrcIp() + "-" + bi.getDstIp() + "-" + bi.getStartTime();
				
				Put put = new Put(Bytes.toBytes(rowkey));// 设置rowkey
				
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("start_time"), Bytes.toBytes(String.valueOf(bi.getStartTime())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("end_time"), Bytes.toBytes(String.valueOf(bi.getEndTime())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("duration"), Bytes.toBytes(String.valueOf(bi.getDuration())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("protocol"), Bytes.toBytes(String.valueOf(bi.getProtocol())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("srcIp"), Bytes.toBytes(String.valueOf(bi.getSrcIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("srcPort"), Bytes.toBytes(String.valueOf(bi.getSrcPort())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("dstIp"), Bytes.toBytes(String.valueOf(bi.getDstIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("dstPort"), Bytes.toBytes(String.valueOf(bi.getDstPort())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("packets"), Bytes.toBytes(String.valueOf(bi.getPackets())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("bytes"), Bytes.toBytes(String.valueOf(bi.getBytes())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("bps"), Bytes.toBytes(String.valueOf(bi.getBps())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("bpp"), Bytes.toBytes(String.valueOf(bi.getBpp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("pps"), Bytes.toBytes(String.valueOf(bi.getPps())));
		                                                                   
		        
		        table.put(put);
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
	
	public void addBackwardSessionRecord(List<UnidirectionalSessionRecord> ls) {
		HTable table = null;
		
		try {
			table = new HTable(config, Bytes.toBytes(BACKWARD_SESSION_RECORD));// HTabel负责跟记录相关的操作如增删改查等
			addSessionRecord(ls, table);
			
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
		finally{
			try {
				table.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void addForwardSessionRecord(List<UnidirectionalSessionRecord> ls) {
		
		HTable table = null;
		
		try {
			table = new HTable(config, Bytes.toBytes(FORWARD_SESSION_RECORD));// HTabel负责跟记录相关的操作如增删改查等
			addSessionRecord(ls, table);
			
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
		finally{
			try {
				table.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	
	public void addDDoSMetrics(DDoSMetricsVO vo) {
		HTable table = null;
		
		try {
			table = new HTable(config, Bytes.toBytes(REALTIME_METRICS));
			
			String rowkey = vo.getTimeWindow();
			
			Put put = new Put(Bytes.toBytes(rowkey));// 设置rowkey
			
			put.add(Bytes.toBytes("default"), Bytes.toBytes("time_window"), Bytes.toBytes(String.valueOf(vo.getTimeWindow())));
			put.add(Bytes.toBytes("default"), Bytes.toBytes("record_creation_num"), Bytes.toBytes(String.valueOf(vo.getRecord_creation_num())));
			
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_num"), Bytes.toBytes(String.valueOf(vo.getSyn_num())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_to_flow_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_to_flow_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_to_tcp_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_to_tcp_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_less_5"), Bytes.toBytes(String.valueOf(vo.getSyn_src_less_5())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_less_10"), Bytes.toBytes(String.valueOf(vo.getSyn_src_less_10())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_less_20"), Bytes.toBytes(String.valueOf(vo.getSyn_src_less_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_large_20"), Bytes.toBytes(String.valueOf(vo.getSyn_src_large_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_less_5"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_less_5())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_less_10"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_less_10())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_less_20"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_less_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_large_20"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_large_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_src_less_5_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_src_less_10_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_src_less_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_src_large_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_less_5_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_less_10_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_less_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_large_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_src_top_20"), Bytes.toBytes(String.valueOf(vo.getSyn_src_top_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("syn_dst_top_20"), Bytes.toBytes(String.valueOf(vo.getSyn_dst_top_20())));
			
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_num"), Bytes.toBytes(String.valueOf(vo.getAck_num())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_to_flow_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_to_flow_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_to_tcp_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_to_tcp_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_less_5"), Bytes.toBytes(String.valueOf(vo.getAck_src_less_5())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_less_10"), Bytes.toBytes(String.valueOf(vo.getAck_src_less_10())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_less_20"), Bytes.toBytes(String.valueOf(vo.getAck_src_less_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_large_20"), Bytes.toBytes(String.valueOf(vo.getAck_src_large_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_less_5"), Bytes.toBytes(String.valueOf(vo.getAck_dst_less_5())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_less_10"), Bytes.toBytes(String.valueOf(vo.getAck_dst_less_10())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_less_20"), Bytes.toBytes(String.valueOf(vo.getAck_dst_less_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_large_20"), Bytes.toBytes(String.valueOf(vo.getAck_dst_large_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_src_less_5_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_src_less_10_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_src_less_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_src_large_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_dst_less_5_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_dst_less_10_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_dst_less_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getAck_dst_large_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_src_top_20"), Bytes.toBytes(String.valueOf(vo.getAck_src_top_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("ack_dst_top_20"), Bytes.toBytes(String.valueOf(vo.getAck_dst_top_20())));
			
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_num"), Bytes.toBytes(String.valueOf(vo.getFin_num())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_to_flow_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_to_flow_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_to_tcp_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_to_tcp_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_less_5"), Bytes.toBytes(String.valueOf(vo.getFin_src_less_5())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_less_10"), Bytes.toBytes(String.valueOf(vo.getFin_src_less_10())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_less_20"), Bytes.toBytes(String.valueOf(vo.getFin_src_less_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_large_20"), Bytes.toBytes(String.valueOf(vo.getFin_src_large_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_less_5"), Bytes.toBytes(String.valueOf(vo.getFin_dst_less_5())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_less_10"), Bytes.toBytes(String.valueOf(vo.getFin_dst_less_10())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_less_20"), Bytes.toBytes(String.valueOf(vo.getFin_dst_less_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_large_20"), Bytes.toBytes(String.valueOf(vo.getFin_dst_large_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_src_less_5_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_src_less_10_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_src_less_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_src_large_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_dst_less_5_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_dst_less_10_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_dst_less_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getFin_dst_large_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_src_top_20"), Bytes.toBytes(String.valueOf(vo.getFin_src_top_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("fin_dst_top_20"), Bytes.toBytes(String.valueOf(vo.getFin_dst_top_20())));
			
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_num"), Bytes.toBytes(String.valueOf(vo.getInval_flag_num())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_to_flow_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_to_flow_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_to_tcp_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_to_tcp_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_less_5"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_less_5())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_less_10"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_less_10())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_less_20"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_less_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_large_20"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_large_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_less_5"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_less_5())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_less_10"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_less_10())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_less_20"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_less_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_large_20"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_large_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_less_5_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_less_10_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_less_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_large_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_less_5_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_less_10_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_less_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_large_20_ratio())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_src_top_20"), Bytes.toBytes(String.valueOf(vo.getInval_flag_src_top_20())));
			put.add(Bytes.toBytes("tcp"), Bytes.toBytes("inval_flag_dst_top_20"), Bytes.toBytes(String.valueOf(vo.getInval_flag_dst_top_20())));
			
			
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_num"), Bytes.toBytes(String.valueOf(vo.getUdp_num())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_to_flow_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_to_flow_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_less_5"), Bytes.toBytes(String.valueOf(vo.getUdp_src_less_5())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_less_10"), Bytes.toBytes(String.valueOf(vo.getUdp_src_less_10())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_less_20"), Bytes.toBytes(String.valueOf(vo.getUdp_src_less_20())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_large_20"), Bytes.toBytes(String.valueOf(vo.getUdp_src_large_20())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_less_5"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_less_5())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_less_10"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_less_10())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_less_20"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_less_20())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_large_20"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_large_20())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_src_less_5_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_src_less_10_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_src_less_20_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_src_large_20_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_less_5_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_less_10_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_less_20_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_large_20_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_src_top_20"), Bytes.toBytes(String.valueOf(vo.getUdp_src_top_20())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("udp_dst_top_20"), Bytes.toBytes(String.valueOf(vo.getUdp_dst_top_20())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("ntp_num"), Bytes.toBytes(String.valueOf(vo.getNtp_num())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("ntp_to_flow_ratio"), Bytes.toBytes(String.valueOf(vo.getNtp_to_flow_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("ntp_to_udp_ratio"), Bytes.toBytes(String.valueOf(vo.getNtp_to_udp_ratio())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("ntp_src_top_20"), Bytes.toBytes(String.valueOf(vo.getNtp_src_top_20())));
			put.add(Bytes.toBytes("udp"), Bytes.toBytes("ntp_dst_top_20"), Bytes.toBytes(String.valueOf(vo.getNtp_dst_top_20())));
			
			
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_num"), Bytes.toBytes(String.valueOf(vo.getIcmp_num())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_to_flow_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_to_flow_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_less_5"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_less_5())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_less_10"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_less_10())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_less_20"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_less_20())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_large_20"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_large_20())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_less_5"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_less_5())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_less_10"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_less_10())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_less_20"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_less_20())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_large_20"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_large_20())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_less_5_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_less_10_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_less_20_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_large_20_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_less_5_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_less_5_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_less_10_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_less_10_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_less_20_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_less_20_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_large_20_ratio"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_large_20_ratio())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_src_top_20"), Bytes.toBytes(String.valueOf(vo.getIcmp_src_top_20())));
			put.add(Bytes.toBytes("icmp"), Bytes.toBytes("icmp_dst_top_20"), Bytes.toBytes(String.valueOf(vo.getIcmp_dst_top_20())));
			
			table.put(put);
			
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
		finally{
			try {
				table.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void addAttackEvents(List<AttackEvent> ls) {
		
		HTable table = null;
		try {
			table = new HTable(config, Bytes.toBytes(ATTACK_EVENT));
			
			for(AttackEvent ae : ls){
				
				String rowkey = ae.getSrcIp() + "-" + ae.getDstIp() + "-" + ae.getDate() + "-" + ae.getType().getCode();
				
				Put put = new Put(Bytes.toBytes(rowkey));// 设置rowkey
				
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("date"), Bytes.toBytes(String.valueOf(ae.getDate())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("srcIp"), Bytes.toBytes(String.valueOf(ae.getSrcIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("srcPort"), Bytes.toBytes(String.valueOf(ae.getSrcPort())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("dstIp"), Bytes.toBytes(String.valueOf(ae.getDstIp())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("dstPort"), Bytes.toBytes(String.valueOf(ae.getDstPort())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("protocal"), Bytes.toBytes(String.valueOf(ae.getProtocal())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("flag"), Bytes.toBytes(String.valueOf(ae.getFlag())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("typeDescription"), Bytes.toBytes(String.valueOf(ae.getType().getDescription())));
				put.add(Bytes.toBytes("default_cf"), Bytes.toBytes("typeCode"), Bytes.toBytes(String.valueOf(ae.getType().getCode())));
				
				table.put(put);
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
		finally{
			try {
				table.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

}
