package com.huirong.biz;

import java.util.List;
import java.util.Map;

import com.huirong.util.NetflowSource;
import com.huirong.storage.StorageManager;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月6日
 */
public interface BusinessLogic {

	public void execute(Tuple tuple, OutputCollector collector);
	
	public void prepare(Map config, NetflowSource from, StorageManager storageManager, List<String> inputFieldList);
	
	public void cleanup();
	
}
