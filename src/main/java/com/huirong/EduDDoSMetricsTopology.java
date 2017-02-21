package com.huirong;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


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
 * 2015年12月30日
 */

@Deprecated
public class EduDDoSMetricsTopology {
	public final static String TOPOLOGY_NAME = "EagleEye-Storm-Attack-Detect";
	public final static String EDU_NETFLOW_TOPIC_NAME = "edu-netflow-topic";
	public final static String EDU_NETFLOW_CONSUMER_READ_OFFSITE = "/edu-netflow-consume-state";
	public final static String EDU_NETFLOW_SPOUT_ID = "Kafka-Spout";
	
	/*public final static String EDU_PREPROCESSOR_BOLT_ID = "Metrics-Preprocessor-Bolt";
	public final static String EDU_PROCESSOR_BOLT_ID = "Metrics-Calculate-Bolt";*/
	
	public final static String EDU_PREPROCESSOR_BOLT_ID = "Attack-Detector-Preprocessor-Bolt";
	public final static String EDU_PROCESSOR_BOLT_ID = "Attack-Detector-Bolt";
	public final static String SYN_THRESHOLD = "syn_threshold";
	public final static String ACK_THRESHOLD = "ack_threshold";
	public final static String ICMP_THRESHOLD = "icmp_threshold";
	public final static String NTP_THRESHOLD = "ntp_threshold";
	
	public static void main(String[] args) {
		try {
			
			BrokerHosts hosts = new ZkHosts("311b-node-2:2181,310b-node-1:2181");
			
			TopologyBuilder builder = new TopologyBuilder();
			
			int workers = Integer.parseInt(args[0]);
			int synThre = Integer.parseInt(args[1]);
			int ackThre = Integer.parseInt(args[2]);
			int icmpThre = Integer.parseInt(args[3]);
			int ntpThre = Integer.parseInt(args[4]);
			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			SpoutConfig sc = new SpoutConfig(hosts, EDU_NETFLOW_TOPIC_NAME, EDU_NETFLOW_CONSUMER_READ_OFFSITE,
					EDU_NETFLOW_SPOUT_ID);
			sc.scheme = new SchemeAsMultiScheme(new StringScheme());
			KafkaSpout spout = new KafkaSpout(sc);
			
			builder.setSpout(EDU_NETFLOW_SPOUT_ID,spout , 1);	
			
			/*builder.setBolt(EDU_PREPROCESSOR_BOLT_ID, null, 2)
	       	   .shuffleGrouping(EDU_NETFLOW_SPOUT_ID);
			builder.setBolt(EDU_PROCESSOR_BOLT_ID, null, 2)
	           .fieldsGrouping(EDU_PREPROCESSOR_BOLT_ID, 
	        		   new Fields(EduNetflowPreprocessorBolt.EDU_PREPROCESSOR_BOLT_FIELD_TIME_FRAME));*/		
			
			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			Config config = new Config();
			config.setNumWorkers(workers);
			config.setDebug(false);
			config.put(SYN_THRESHOLD, synThre);
			config.put(ACK_THRESHOLD, ackThre);
			config.put(ICMP_THRESHOLD, icmpThre);
			config.put(NTP_THRESHOLD, ntpThre);
			StormSubmitter.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/log.txt", true));
				bw.write(e.getMessage());
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
		
	}
}