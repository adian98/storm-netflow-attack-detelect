package com.huirong.biz.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.huirong.biz.BusinessLogic;
import com.huirong.storage.StorageManager;
import com.huirong.storage.vo.BlacklistIpSession;
import com.huirong.storage.vo.NetflowRecord;
import com.huirong.util.NetflowSource;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月6日
 */
public class BlacklistTracker implements BusinessLogic {

	public final static int CACHE_SIZE = 200;  

	private Set<String> blacklist;
	private StorageManager storageManager;
	private NetflowSource source;
	private List<String> tupleList;
	private List<BlacklistIpSession> cache = new ArrayList<BlacklistIpSession>();
	
	public void execute(Tuple tuple, OutputCollector collector) {
		try {
			
			ByteArrayInputStream byteArray = new ByteArrayInputStream(
					tuple.getBinaryByField(this.tupleList.get(1)));
			ObjectInputStream inputStream = new ObjectInputStream(byteArray);
			
			NetflowRecord nfRecord = (NetflowRecord) inputStream.readObject();
			inputStream.close();
			
			if(this.blacklist.contains(nfRecord.getSrcIp()) || this.blacklist.contains(nfRecord.getDstIp())){
				BlacklistIpSession session = new BlacklistIpSession();
				session.setRecord(nfRecord);
				
				if(blacklist.contains(nfRecord.getSrcIp()))
					session.setIp(nfRecord.getSrcIp());
				else
					session.setIp(nfRecord.getDstIp());
				
				cache.add(session);
				
				if(cache.size() >= CACHE_SIZE){
					this.storageManager.addBlacklistIpSession(cache);
					cache.clear();
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
		this.tupleList = inputFieldList;
		this.blacklist = this.storageManager.getBlacklist();
	}

	public void cleanup() {
		if(cache != null && cache.size() > 0)
			this.storageManager.addBlacklistIpSession(cache);
		
		this.blacklist.clear();
	}

}
