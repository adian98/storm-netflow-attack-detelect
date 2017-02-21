package com.huirong.bolt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * 2015年10月30日
 * 
 * testing...
 */

@Deprecated
public class NetflowPreprocessorBolt implements IRichBolt{

	OutputCollector _collector ;
	public final static String PREPROCESSOR_OUTPUT_FIELD_NAME = "test";
	
	// for testing...
	List<String> cache = new ArrayList<String>();
	
	public void cleanup() {
		cache.clear();
		cache = null;
	}

	public void execute(Tuple arg0) {
		String rawNetflowRecord = arg0.getString(0);
		
		// for testing...
		cache.add(rawNetflowRecord);
		
		if(cache.size() > 5){
			try {
				
				Class.forName("com.mysql.jdbc.Driver");
				String url = "jdbc:mysql://310b-node-2:3306/yaoxin";
				String user = "root";
				String password = "sanyaolingb";
				Connection conn = DriverManager.getConnection(url, user, password);
				Statement statement = conn.createStatement();
				
				for(String s : cache){
					String sql = "insert into storm_test(messages) values('" + s + "')";
					statement.execute(sql);
				}
				
				statement.close();
				conn.close();
				cache.clear();
			} catch (Exception e) {
				// TODO: handle exception
				StringBuilder sb = new StringBuilder();
				sb.append("error in bolt! " + "\n");
				sb.append(e.getMessage());
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/storm-log.txt",true));
					bw.write(sb.toString());
					bw.close();
				} catch (IOException e1) {
					
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		_collector.ack(arg0);
		
	}

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		this._collector = arg2;
		
	}

	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		arg0.declare(new Fields(PREPROCESSOR_OUTPUT_FIELD_NAME));
	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
