package com.huirong.bolt;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.huirong.MainTopology;
import com.huirong.storage.vo.NetflowRecord;
import com.huirong.util.NetflowParser;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月24日
 */
public class NetflowParserBolt implements IRichBolt{


	/**
	 * 
	 */
	private static final long serialVersionUID = 3256203934123727219L;
	public final static String TIME_FRAME_FIELD = "time_frame";
	public final static String OBJ_FIELD = "record_obj";
	public final static String FLOW_KEY = "flow_key";
	OutputCollector outputCollector;
	private String dataSource ;

	private NetflowParser parser = new NetflowParser();
	
	public void cleanup() {
		
	}

	public void execute(Tuple arg0) {
		NetflowRecord rc = parser.parse(arg0.getString(0));
		
		if(rc != null){
			try {
				
				// tjut的netflow数据时间戳字段有问题, 需要调整一下
				if(this.dataSource != null && this.dataSource.equalsIgnoreCase("tjut")){
					parser.adjustTimeField(rc);
				}
				
				String key = keyGen(rc.getSrcIp(), rc.getSrcPort(), rc.getDstIp(), 
						rc.getDstPort(), rc.getProtocol());
				
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
				ObjectOutputStream outputStream = new ObjectOutputStream(byteArray);
				outputStream.writeObject(rc);
				
				outputCollector.emit(arg0, new Values(rc.getTimeFrame(), byteArray.toByteArray(), key));
				
				outputStream.close();

			} catch (Exception e) {
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
		
		this.outputCollector.ack(arg0);
	}

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		this.outputCollector = arg2;
		this.dataSource = (String) arg0.get(MainTopology.DATA_SOURCE);
	}

	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		arg0.declare(new Fields(TIME_FRAME_FIELD, OBJ_FIELD, FLOW_KEY));
	}

	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	String keyGen(String srcIp, String srcPort, String dstIp, String dstPort, String protocal){
		return srcIp + ":" + srcPort + "->" + dstIp + ":" + dstPort + "@" + protocal;
	}
}
