package com.huirong;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.huirong.bolt.ApplicationBolt;
import com.huirong.bolt.AttackDetectionBolt;
import com.huirong.bolt.MetricsBolt;
import com.huirong.bolt.NetflowParserBolt;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月24日
 */
public class MainTopology {

	public static final String ZK_CONN_STR = "311b-node-2:2181,310b-node-1:2181";
	public static final String BIZ_METRICS = "metrics";
	public static final String BIZ_FLOW_RESTORE = "flow-restore";
	public static final String BIZ_BLACK_LIST = "black-list";
	public static final String BIZ_ATTACK_DETECTION = "attack-detection";
	public static final String DATA_SOURCE = "data-source";
	
	public final static String NETFLOW_SPOUT_ID = "Kafka-Spout";
	public static final String NETFLOW_PARSER_BOLT_ID = "Netflow-Parser";
	// 
	
	// metrics 统计功能
	public final static String METRICS_PROCESSOR_BOLT_ID = "Metrics-Calculate-Bolt";
	// public final static String METRICS_ES_WRITE_BOLT_ID = "Metrics-ES-Write-Bolt";
	
	// flow还原功能
	public final static String FLOW_RESTORE_BOLT_ID = "Flow-Restore-Bolt";
	
	// 原始数据存储功能
	public final static String BLACK_LIST_BOLT_ID = "Black-list-Bolt";

	// DDoS检测规则
	public final static String ATTACK_DETECTION_BOLT_ID = "Attack-detection-Bolt";
	public final static String SYN_THRESHOLD = "syn_threshold";
	public final static String ACK_THRESHOLD = "ack_threshold";
	public final static String ICMP_THRESHOLD = "icmp_threshold";
	public final static String NTP_THRESHOLD = "ntp_threshold";
	public final static String DROP_OUTTER_IP = "drop-outter-ip";

	//DNS
	public final static String APPLICATION_BOLT_ID = "Application";

	
	public static void main(String[] args) {
		try {
			
			// 接收运行所需的各项参数
			if(args.length < 6){
				System.exit(1);
			}
			
			String dataSource = args[0];
			String topologyName = args[1];
			String topicName = args[2];
			String topicConsumeState = args[3];
			int workers = Integer.parseInt(args[4]);
			
			TopologyBuilder builder = new TopologyBuilder();
			Config config = new Config();

			
			// 设置spout
			BrokerHosts hosts = new ZkHosts(ZK_CONN_STR);
			SpoutConfig sc = new SpoutConfig(hosts, topicName, topicConsumeState, NETFLOW_SPOUT_ID);
			sc.scheme = new SchemeAsMultiScheme(new StringScheme());
			KafkaSpout spout = new KafkaSpout(sc);
			builder.setSpout(NETFLOW_SPOUT_ID, spout , 1);
			
			
			// 设置Netflow解析功能
			builder.setBolt(NETFLOW_PARSER_BOLT_ID, new NetflowParserBolt(), 2).shuffleGrouping(NETFLOW_SPOUT_ID);

			//设置130维指标统计功能
			builder.setBolt(METRICS_PROCESSOR_BOLT_ID, new MetricsBolt(), 2).fieldsGrouping(NETFLOW_PARSER_BOLT_ID,
					new Fields(NetflowParserBolt.TIME_FRAME_FIELD));


			//设置攻击检测的阈值
			// 读取阈值
			String biz = args[5];
			String[] tmp = biz.split("#");
			config.put(DROP_OUTTER_IP, Boolean.parseBoolean(tmp[1]));
			config.put(SYN_THRESHOLD, Integer.parseInt(tmp[2]));
			config.put(ACK_THRESHOLD, Integer.parseInt(tmp[3]));
			config.put(ICMP_THRESHOLD, Integer.parseInt(tmp[4]));
			config.put(NTP_THRESHOLD, Integer.parseInt(tmp[5]));

			builder.setBolt(ATTACK_DETECTION_BOLT_ID, new AttackDetectionBolt(), 2).shuffleGrouping(NETFLOW_PARSER_BOLT_ID);
//			builder.setBolt(ATTACK_DETECTION_BOLT_ID, new AttackDetectionBolt(), 2).fieldsGrouping(NETFLOW_PARSER_BOLT_ID, new Fields(NetflowParserBolt.TIME_FRAME_FIELD));


			//添加DNS的检测模块
//			builder.setBolt(APPLICATION_BOLT_ID, new ApplicationBolt(), 2).shuffleGrouping(NETFLOW_PARSER_BOLT_ID);
			builder.setBolt(APPLICATION_BOLT_ID, new ApplicationBolt()).globalGrouping(NETFLOW_PARSER_BOLT_ID);

			
			// 设置需要使用的功能模块
//			for(int i = 5; i < args.length; i ++){
//				String biz = args[i];
//
//				if(validate(biz)){
//
//					if(biz.equalsIgnoreCase(BIZ_METRICS)){
//
//						builder.setBolt(METRICS_PROCESSOR_BOLT_ID, new MetricsBolt(), 2)
//				           .fieldsGrouping(NETFLOW_PARSER_BOLT_ID,
//				        		   new Fields(NetflowParserBolt.TIME_FRAME_FIELD));
//
//						/*builder.setBolt(METRICS_PROCESSOR_BOLT_ID, new MetricsBolt(), 1)
//				           .globalGrouping(NETFLOW_PARSER_BOLT_ID);*/
//
//					}
//					else if(biz.equalsIgnoreCase(BIZ_FLOW_RESTORE)){
//
//						builder.setBolt(FLOW_RESTORE_BOLT_ID, new SessionTrackerBolt(), 2)
//						       .fieldsGrouping(NETFLOW_PARSER_BOLT_ID,
//						    		   new Fields(NetflowParserBolt.FLOW_KEY));
//					}
//					else if(biz.equalsIgnoreCase(BIZ_BLACK_LIST)){
//						builder.setBolt(BLACK_LIST_BOLT_ID, new BlackListBolt(), 2)
//						   .shuffleGrouping(NETFLOW_PARSER_BOLT_ID);
//
//					}
//					else if(biz.startsWith(BIZ_ATTACK_DETECTION)){
//						String[] tmp = biz.split("#");
//
//						if(tmp != null){
//							// 是否只保留与理工IP有关的数据
//							config.put(DROP_OUTTER_IP, Boolean.parseBoolean(tmp[1]));
//
//							// 读取阈值
//							config.put(SYN_THRESHOLD, Integer.parseInt(tmp[2]));
//							config.put(ACK_THRESHOLD, Integer.parseInt(tmp[3]));
//							config.put(ICMP_THRESHOLD, Integer.parseInt(tmp[4]));
//							config.put(NTP_THRESHOLD, Integer.parseInt(tmp[5]));
//
//							builder.setBolt(ATTACK_DETECTION_BOLT_ID, new AttackDetectionBolt(), 2)
//							   .shuffleGrouping(NETFLOW_PARSER_BOLT_ID);
//						}
//					}
//				}
//			}
			
			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			
			config.setNumWorkers(workers);
			config.put(DATA_SOURCE, dataSource);
			config.setDebug(false);
			StormSubmitter.submitTopology(topologyName, config, builder.createTopology());

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
	
	static boolean validate(String biz){
		if(biz == null || biz.length() == 0 || 
				(!biz.equalsIgnoreCase(BIZ_METRICS)	&& !biz.equalsIgnoreCase(BIZ_FLOW_RESTORE) 
						&& !biz.equalsIgnoreCase(BIZ_BLACK_LIST) && !biz.startsWith(BIZ_ATTACK_DETECTION)))
			return false;
		
		return true;
	}
}

