package com.huirong.storage;

import java.util.List;
import java.util.Set;

import com.huirong.biz.impl.ApplicationsCatchObj;
import com.huirong.storage.vo.AttackEvent;
import com.huirong.storage.vo.BlacklistIpSession;
import com.huirong.storage.vo.DDoSMetricsVO;
import com.huirong.storage.vo.UnidirectionalSessionRecord;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月4日
 */
public interface StorageManager {

	
	public Set<String> getBlacklist();
	
	public void addBlacklistIpSession(List<BlacklistIpSession> ls);
	
	public void addBackwardSessionRecord(List<UnidirectionalSessionRecord> ls);
	
	public void addForwardSessionRecord(List<UnidirectionalSessionRecord> ls);
	
	public void addDDoSMetrics(DDoSMetricsVO vo);
	
	public void addAttackEvents(List<AttackEvent> ls);

	public void addApplicationsMetrics(ApplicationsCatchObj catchObj, String port);

	public void addApplicationsEvents(ApplicationsCatchObj catchObj, String port);
}
