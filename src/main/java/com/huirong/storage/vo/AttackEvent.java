package com.huirong.storage.vo;

import com.huirong.util.AttackType;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月3日
 */
public class AttackEvent {

	private String date;
	private String srcIp;
	private String srcPort;
	private String dstIp;
	private String dstPort;
	private String protocal;
	private String flag;
	private AttackType type;
	
	public AttackEvent(){
		
	}
	
	public AttackEvent(String date, String srcIp, String srcPort, String dstIp, String dstPort, String protocal,
			String flag, AttackType type) {
		super();
		this.date = date;
		this.srcIp = srcIp;
		this.srcPort = srcPort;
		this.dstIp = dstIp;
		this.dstPort = dstPort;
		this.protocal = protocal;
		this.flag = flag;
		this.type = type;
	}


	public AttackEvent(NetflowRecord nf){
		this.date = nf.getDate();
		this.srcIp = nf.getSrcIp();
		this.srcPort = nf.getSrcPort();
		this.dstIp = nf.getDstIp();
		this.dstPort = nf.getDstPort();
		this.protocal = nf.getProtocol();
		this.flag = nf.getFlags();
	}
	
	public AttackEvent(String s){
		
		String[] tmp = s.split(",");
		if(tmp != null && tmp.length > 0){
			System.out.println(s);
			//this.typeDescription = tmp[0];
			this.date = (tmp[1].split("="))[1];
			this.srcIp = (tmp[2].split("="))[1];
			this.srcPort = (tmp[3].split("="))[1];
			this.dstIp = (tmp[4].split("="))[1];
			this.dstPort = (tmp[5].split("="))[1];
			this.protocal = (tmp[6].split("="))[1];
			this.flag = (tmp[7].split("="))[1];
					
		}
	}
	
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getProtocal() {
		return protocal;
	}
	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}
	public String getDate() {
		return date;
	}
	public String getDstIp() {
		return dstIp;
	}
	public String getDstPort() {
		return dstPort;
	}
	public String getSrcIp() {
		return srcIp;
	}
	public String getSrcPort() {
		return srcPort;
	}

	public void setDate(String date) {
		this.date = date;
	}
	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}
	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	public AttackType getType() {
		return type;
	}

	public void setType(AttackType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type + ",date=" + date + ",srcIp=" + srcIp + ",srcPort=" + srcPort + ",dstIp=" + dstIp
				+ ",dstPort=" + dstPort + ",protocal=" + protocal + ",flags=" + this.flag;
	}
	
	public static void main(String[] args) {
		String a = "SYN-Flood(SYN_INVALID),date=2015-12-29 00:00:57,srcIp=219.232.160.36,srcPort=19677,dstIp=202.113.68.41,dstPort=1433,protocal=TCP,flags=....S.";
		AttackEvent ar = new AttackEvent(a);
	}
	
	
}
