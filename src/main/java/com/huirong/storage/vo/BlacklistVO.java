package com.huirong.storage.vo;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月6日
 */
public class BlacklistVO {
	private Integer id;
	private String ip;
	private String insertTime;
	private String tag;
	
	
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
	public String getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
}
