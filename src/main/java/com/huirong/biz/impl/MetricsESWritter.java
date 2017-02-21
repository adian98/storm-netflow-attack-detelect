package com.huirong.biz.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import com.huirong.biz.BusinessLogic;
import com.huirong.bolt.MetricsBolt;
import com.huirong.util.NetflowSource;
import com.huirong.storage.StorageManager;
import com.huirong.storage.vo.DDoSMetricsVO;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;

/**
 * 
 * 
 * @author yaoxin   
 * 
 * 2016年3月17日
 */
@Deprecated
public class MetricsESWritter implements BusinessLogic {

	public final static String ES_PUSH_CURL_PREFIX = "curl -XPOST '59.67.152.231:9200/_bulk' -d '\n";
	
	
	public void execute(Tuple tuple, OutputCollector collector) {
		try {
			ByteArrayInputStream byteArray = new ByteArrayInputStream(
					tuple.getBinaryByField(MetricsBolt.OUTPUT_FIELD_NAME));
			ObjectInputStream inputStream = new ObjectInputStream(byteArray);
			
			DDoSMetricsVO vo = (DDoSMetricsVO) inputStream.readObject();
			inputStream.close();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
			bw.write("MetricsESWritter get new DDoSMetricsVO from DDoSMetricsGenerator :\n");
			bw.write(vo.getTimeWindow() + "\n");
			bw.close();
			
			push2ElasticsearchWithShell(vo);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				// 输出错误日志, 后期考虑用logback等日志工具实现 : )
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
	
	
	StringBuilder genShellCommand(DDoSMetricsVO vo){
		// 该值将作为ES document的id
		String id = vo.getTimeWindow().replace(" ", "_").replace(":", "-");
		
		StringBuilder sb = new StringBuilder(ES_PUSH_CURL_PREFIX);
		
		// ntp
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics\", \"_type\" : \"ntp\", \"_id\" : \"" + id + "\" } }\n")
		  .append("{\"time_window\": \"" + id + "\", ")
		  .append("\"ntp_num\": " + vo.getNtp_num() + ", ")
		  .append("\"ntp_to_flow_ratio\" : " + vo.getNtp_to_flow_ratio() + ", ")
		  .append("\"ntp_to_udp_ratio\" : " + vo.getNtp_to_udp_ratio() + "}\n ");
		
		// syn
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics\", \"_type\" : \"syn\", \"_id\" : \"" + id + "\" } }\n")
		  .append("{\"time_window\": \"" + id + "\", ")
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
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics\", \"_type\" : \"ack\", \"_id\" : \"" + id + "\" } }\n")
		  .append("{\"time_window\": \"" + id + "\", ")
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
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics\", \"_type\" : \"fin\", \"_id\" : \"" + id + "\" } }\n")
		  .append("{\"time_window\": \"" + id + "\", ")
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
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics\", \"_type\" : \"udp\", \"_id\" : \"" + id + "\" } }\n")
		  .append("{\"time_window\": \"" + id + "\", ")
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
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics\", \"_type\" : \"icmp\", \"_id\" : \"" + id + "\" } }\n")
		  .append("{\"time_window\": \"" + id + "\", ")
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
		sb.append("{ \"create\" : { \"_index\" : \"edu_network_load_metrics\", \"_type\" : \"inval_flag\", \"_id\" : \"" + id + "\" } }\n")
		  .append("{\"time_window\": \"" + id + "\", ")
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
			
			// System.out.println(cmd);
			
			Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", cmd});
			process.waitFor();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
			bw.write(sb.toString() + "\n");
			bw.close();
			
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

	public void prepare(Map config, NetflowSource from, StorageManager storageManager, List<String> inputFieldList) {
		
	}

	public void cleanup() {
		
	}

}
