package com.huirong.storage.vo;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月6日
 */
public class BlacklistIpSession {

	private Integer id;
	private String ip;
	private NetflowRecord record;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public NetflowRecord getRecord() {
		return record;
	}
	public void setRecord(NetflowRecord record) {
		this.record = record;
	}
	
}
