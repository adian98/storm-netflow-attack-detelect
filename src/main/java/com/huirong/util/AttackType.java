package com.huirong.util;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月25日
 */
public enum AttackType {

	CONNECTION_FLOOD_LAND("Connection-Flood(Land)", 1), 
	STREAM_FLOOD_INVALID_TCP_FLAG("Stream-Flood(Invalid_TCP_Flag)", 2),
	SYN_FLOOD_INVALID("SYN-Flood(SYN_Invalid)", 3), 
	ACK_FLOOD("ACK-Flood", 4), 
	UDP_FLOOD_PORT_RULE("UDP-Flood(Port_Rule)", 5),
	ICMP_FLOOD("ICMP-Flood", 6), 
	NTP_FLOOD("NTP-Flood", 7);
	
	private String description;
	private int code;
	
	AttackType(String des, int c){
		description = des;
		code = c;
	}
	
	public String getDescription(int c){
		for(AttackType at : AttackType.values()){
			if(at.getCode() == c){
				return at.getDescription();
			}
		}
		
		return null;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
