package com.huirong.storage.vo;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月4日
 */
public class AccessHistoryVO {

	private Integer id;
	
	private String ip;
	private String accessDate;  // 统一起见在java代码中暂时用string表示日期吧...
	
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
	public String getAccessDate() {
		return accessDate;
	}
	public void setAccessDate(String accessDate) {
		this.accessDate = accessDate;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof AccessHistoryVO){
			AccessHistoryVO o1 = (AccessHistoryVO) o;
			return this.ip.equals(o1.getIp());
		}
		
		return false;
	}
}
