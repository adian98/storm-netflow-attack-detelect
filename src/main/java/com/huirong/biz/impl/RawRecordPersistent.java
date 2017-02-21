package com.huirong.biz.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huirong.biz.BusinessLogic;
import com.huirong.storage.vo.NetflowRecord;
import com.huirong.util.NetflowSource;
import com.huirong.storage.StorageManager;
import com.huirong.storage.vo.FlowInfo;
import com.huirong.util.FlowDirectionEnum;
import com.huirong.util.NetflowParser;
import com.huirong.util.NetflowTools;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月6日
 */
public class RawRecordPersistent implements BusinessLogic {

	/**
	 * 不再由storm来保存原始Netflow数据, 暂存文件形式的Netflow文件即可
	 */
	
	private StorageManager storageManager ;
	private NetflowSource source;
	public final static int RAW_RECORD_CACHE_SIZE = 300;

	List<NetflowRecord> backwardRawRecordCache = new ArrayList<NetflowRecord>();
	List<NetflowRecord> forwardRawRecordCache = new ArrayList<NetflowRecord>();
	private NetflowParser parser = new NetflowParser();
	
	public void execute(Tuple tuple, OutputCollector outputCollector) {
		try {
			NetflowRecord nfRecord = parser.parse(tuple.getString(0));
			
			if(nfRecord != null){
				
				String srcIp = nfRecord.getSrcIp();
				String dstIp = nfRecord.getDstIp();
				
				if(!NetflowTools.belongToEduNetworkSegment(srcIp, dstIp)){
					return ;
				}
				
				if(this.source == NetflowSource.TJUT)
					parser.adjustTimeField(nfRecord);
				
				FlowInfo info = null;
				
				if(this.source == NetflowSource.EDU)
					info = NetflowTools.recogniseEduFlow(nfRecord);
				else if(this.source == NetflowSource.TJUT)
					info = NetflowTools.recogniseTjutFlow(nfRecord);
				
				if(info.getDirection() == FlowDirectionEnum.IN){
					this.backwardRawRecordCache.add(nfRecord);
					
					if(this.backwardRawRecordCache.size() >= RAW_RECORD_CACHE_SIZE){
						// 把原始netflow写入存储设备并清空缓存 
						//this.storageManager.addBackwardRawNetflowRecord(this.backwardRawRecordCache);
						this.backwardRawRecordCache.clear();
					}
				}
				else if(info.getDirection() == FlowDirectionEnum.OUT){
					this.forwardRawRecordCache.add(nfRecord);
					
					if(this.forwardRawRecordCache.size() >= RAW_RECORD_CACHE_SIZE){
						// 把原始netflow写入存储设备并清空缓存 
						//this.storageManager.addForwardRawNetflowRecord(this.forwardRawRecordCache);
						this.forwardRawRecordCache.clear();
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
	}

	public void prepare(Map config,NetflowSource from, StorageManager storageManager, List<String> inputFieldList) {
		this.storageManager = storageManager;
		this.source = from;
		
	}

	public void cleanup() {
		if(this.backwardRawRecordCache != null && backwardRawRecordCache.size() > 0){
			//this.storageManager.addBackwardRawNetflowRecord(this.backwardRawRecordCache);
			backwardRawRecordCache.clear();
		}
		
		if(this.forwardRawRecordCache != null && forwardRawRecordCache.size() > 0){
			//this.storageManager.addForwardRawNetflowRecord(this.forwardRawRecordCache);
			forwardRawRecordCache.clear();
		}
		
	}
	
}
