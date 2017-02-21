package com.huirong.bolt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huirong.biz.BusinessLogic;
import com.huirong.biz.impl.MetricsESWritter;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

@Deprecated
public class MetricsESWriterBolt implements IRichBolt{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3104148814188547738L;

	
	OutputCollector outputCollector;
	private List<BusinessLogic> biz = new ArrayList<BusinessLogic>();

	public void cleanup() {
		try {
			for(BusinessLogic bl : this.biz){
				bl.cleanup();
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

	public void execute(Tuple arg0) {
		try {

			for(BusinessLogic bl : this.biz){
				bl.execute(arg0, outputCollector);
			}
			
			outputCollector.ack(arg0);
			
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

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		try {
			this.outputCollector = arg2;
			
			// 添加需要执行的业务逻辑
			biz.add(new MetricsESWritter());
			
			for(BusinessLogic bl : this.biz){
				bl.prepare(null, null, null, null);
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

	public void declareOutputFields(OutputFieldsDeclarer arg0) {

	}

	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
