package com.huirong.bolt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.huirong.MainTopology;
import com.huirong.biz.BusinessLogic;
import com.huirong.biz.impl.DDoSMetricsGenerator;
import com.huirong.storage.StorageManager;
import com.huirong.storage.impl.EduStorageManager;
import com.huirong.storage.impl.TjutStorageManager;
import com.huirong.util.NetflowSource;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月24日
 */
public class MetricsBolt implements IRichBolt{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2761730157260914522L;
	public final static String OUTPUT_FIELD_NAME = "";
	
	OutputCollector outputCollector;
	private List<BusinessLogic> biz = new ArrayList<BusinessLogic>();
	private NetflowSource dataSource ;

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
			String ds = (String) arg0.get(MainTopology.DATA_SOURCE);
			StorageManager storageManager = null;
			
			if(ds.equalsIgnoreCase("tjut")){
				this.dataSource = NetflowSource.TJUT;
				// storageManager = new TjutTestStorageManager();
				
				storageManager = new TjutStorageManager();
			}
			else if(ds.equalsIgnoreCase("edu")){
				this.dataSource = NetflowSource.EDU;
				// storageManager = new EduTestStorageManager();
				
				storageManager = new EduStorageManager();
			}
			
			// 添加需要执行的业务逻辑
			biz.add(new DDoSMetricsGenerator());
			
			for(BusinessLogic bl : this.biz){
				bl.prepare(arg0, this.dataSource, storageManager, 
						Arrays.asList(NetflowParserBolt.TIME_FRAME_FIELD,
								NetflowParserBolt.OBJ_FIELD));
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
