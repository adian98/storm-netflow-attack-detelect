package com.huirong.storage.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月14日
 */
public class UnidirectionalSessionRecord {

	private int id;
	private String  startTime;
	private String  endTime;
	private double  duration;
	private String  protocol;
	private String  srcIp;
	private String srcPort;
	private String dstIp;
	private String dstPort;
	private int packets;
	private long bytes;
	private double bps;
	private int bpp;
	private int pps;
	
	private String lastUpdate; // 最近一次更新该session的时间
	private double lastBps;
	private int lastPackets;
	private Long lastBytes;
	private String key;
	
	private List<TimeDurationPair> cache = new ArrayList<TimeDurationPair>();
	
	public UnidirectionalSessionRecord(){
		
	}
	
	
	public UnidirectionalSessionRecord(String startTime, String endTime, double duration, String protocol,
			String srcIp, String srcPort, String dstIp, String dstPort, int packets, long bytes, double bps, int bpp,
			int pps) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.protocol = protocol;
		this.srcIp = srcIp;
		this.srcPort = srcPort;
		this.dstIp = dstIp;
		this.dstPort = dstPort;
		this.packets = packets;
		this.bytes = bytes;
		this.bps = bps;
		this.bpp = bpp;
		this.pps = pps;
	}



	public List<TimeDurationPair> getCache() {
		return cache;
	}
	public void setCache(List<TimeDurationPair> cache) {
		this.cache = cache;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public double getLastBps() {
		return lastBps;
	}
	public void setLastBps(double lastBps) {
		this.lastBps = lastBps;
	}
	public int getLastPackets() {
		return lastPackets;
	}
	public void setLastPackets(int lastPackets) {
		this.lastPackets = lastPackets;
	}
	public Long getLastBytes() {
		return lastBytes;
	}
	public void setLastBytes(Long lastBytes) {
		this.lastBytes = lastBytes;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getSrcIp() {
		return srcIp;
	}
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}
	public String getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	public String getDstIp() {
		return dstIp;
	}
	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}
	public String getDstPort() {
		return dstPort;
	}
	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}
	public int getPackets() {
		return packets;
	}
	public void setPackets(int packets) {
		this.packets = packets;
	}
	public long getBytes() {
		return bytes;
	}
	public void setBytes(long bytes) {
		this.bytes = bytes;
	}
	public double getBps() {
		return bps;
	}
	public void setBps(double bps) {
		this.bps = bps;
	}
	public int getBpp() {
		return bpp;
	}
	public void setBpp(int bpp) {
		this.bpp = bpp;
	}
	public int getPps() {
		return pps;
	}
	public void setPps(int pps) {
		this.pps = pps;
	}
	
	@Override
	public boolean equals(Object o){
		
		if(o != null && o instanceof UnidirectionalSessionRecord){
			return this.getKey().equals(((UnidirectionalSessionRecord)o).getKey());
		}
		
		return false;
	}
}
