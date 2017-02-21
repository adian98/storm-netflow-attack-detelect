package com.huirong.storage.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月3日
 */
public class RecognisedAttackCacheObj {
	/**
	 * Stream FLOOD
	 */
	private List<NetflowRecord> streamFloodCache = new ArrayList<NetflowRecord>();
	/**
	 * SYN_FLOOD
	 */
	//(srcIp, count)
	private Map<String, Integer> synSrcCounter = new HashMap<String, Integer>();
	//(srcIp, attackEvent)
	private Map<String, List<AttackEvent>> synSrcDstMappin = new HashMap<String, List<AttackEvent>>();
	// 空间换时间
	//(dstIp, count)
	private Map<String, Integer> synDstCounter = new HashMap<String, Integer>();
	//(dstIp, attackEvent)
	private Map<String, List<AttackEvent>> synDstSrcMappin = new HashMap<String, List<AttackEvent>>();
	/**
	 * Land 型 Connection_Flood
	 */
	private List<NetflowRecord> landAttackCache = new ArrayList<NetflowRecord>();
	/**
	 * UDF_FLOOD
	 */
	private List<NetflowRecord> udpFloodCache = new ArrayList<NetflowRecord>();
	/**
	 * ACK_FLOOD
	 */
	private Map<String, Integer> ackSrcCounter = new HashMap<String, Integer>();
	private Map<String, List<AttackEvent>> ackSrcDstMappin = new HashMap<String, List<AttackEvent>>();
	private Map<String, Integer> ackDstCounter = new HashMap<String, Integer>();
	private Map<String, List<AttackEvent>> ackDstSrcMappin = new HashMap<String, List<AttackEvent>>();
	/**
	 * ICMP FLOOD
	 */
	private Map<String, Integer> icmpSrcCounter = new HashMap<String, Integer>();
	private Map<String, List<AttackEvent>> icmpSrcDstMappin = new HashMap<String, List<AttackEvent>>();
	private Map<String, Integer> icmpDstCounter = new HashMap<String, Integer>();
	private Map<String, List<AttackEvent>> icmpDstSrcMappin = new HashMap<String, List<AttackEvent>>();
	private List<NetflowRecord> icmpFloodCache = new ArrayList<NetflowRecord>();
	/**
	 * NTP FLOOD
	 */
	// 把ntp服务提供者看作src, 请求方作为dst
	private Map<String, Integer> ntpSrcCounter = new HashMap<String, Integer>();
	private Map<String, List<AttackEvent>> ntpSrcDstMappin = new HashMap<String, List<AttackEvent>>();
	
	
	public Map<String, Integer> getSynDstCounter() {
		return synDstCounter;
	}
	public void setSynDstCounter(Map<String, Integer> synDstCounter) {
		this.synDstCounter = synDstCounter;
	}
	public Map<String, List<AttackEvent>> getSynDstSrcMappin() {
		return synDstSrcMappin;
	}
	public void setSynDstSrcMappin(Map<String, List<AttackEvent>> synDstSrcMappin) {
		this.synDstSrcMappin = synDstSrcMappin;
	}
	public Map<String, Integer> getAckDstCounter() {
		return ackDstCounter;
	}
	public void setAckDstCounter(Map<String, Integer> ackDstCounter) {
		this.ackDstCounter = ackDstCounter;
	}
	public Map<String, List<AttackEvent>> getAckDstSrcMappin() {
		return ackDstSrcMappin;
	}
	public void setAckDstSrcMappin(Map<String, List<AttackEvent>> ackDstSrcMappin) {
		this.ackDstSrcMappin = ackDstSrcMappin;
	}
	public Map<String, Integer> getIcmpDstCounter() {
		return icmpDstCounter;
	}
	public void setIcmpDstCounter(Map<String, Integer> icmpDstCounter) {
		this.icmpDstCounter = icmpDstCounter;
	}
	public Map<String, List<AttackEvent>> getIcmpDstSrcMappin() {
		return icmpDstSrcMappin;
	}
	public void setIcmpDstSrcMappin(Map<String, List<AttackEvent>> icmpDstSrcMappin) {
		this.icmpDstSrcMappin = icmpDstSrcMappin;
	}
	public List<NetflowRecord> getStreamFloodCache() {
		return streamFloodCache;
	}
	public void setStreamFloodCache(List<NetflowRecord> streamFloodCache) {
		this.streamFloodCache = streamFloodCache;
	}
	public Map<String, Integer> getSynSrcCounter() {
		return synSrcCounter;
	}
	public void setSynSrcCounter(Map<String, Integer> synSrcCounter) {
		this.synSrcCounter = synSrcCounter;
	}
	public Map<String, List<AttackEvent>> getSynSrcDstMappin() {
		return synSrcDstMappin;
	}
	public void setSynSrcDstMappin(Map<String, List<AttackEvent>> synSrcDstMappin) {
		this.synSrcDstMappin = synSrcDstMappin;
	}
	public List<NetflowRecord> getLandAttackCache() {
		return landAttackCache;
	}
	public void setLandAttackCache(List<NetflowRecord> landAttackCache) {
		this.landAttackCache = landAttackCache;
	}
	public List<NetflowRecord> getUdpFloodCache() {
		return udpFloodCache;
	}
	public void setUdpFloodCache(List<NetflowRecord> udpFloodCache) {
		this.udpFloodCache = udpFloodCache;
	}
	public Map<String, Integer> getAckSrcCounter() {
		return ackSrcCounter;
	}
	public void setAckSrcCounter(Map<String, Integer> ackSrcCounter) {
		this.ackSrcCounter = ackSrcCounter;
	}
	public Map<String, List<AttackEvent>> getAckSrcDstMappin() {
		return ackSrcDstMappin;
	}
	public void setAckSrcDstMappin(Map<String, List<AttackEvent>> ackSrcDstMappin) {
		this.ackSrcDstMappin = ackSrcDstMappin;
	}
	public Map<String, Integer> getIcmpSrcCounter() {
		return icmpSrcCounter;
	}
	public void setIcmpSrcCounter(Map<String, Integer> icmpSrcCounter) {
		this.icmpSrcCounter = icmpSrcCounter;
	}
	public Map<String, List<AttackEvent>> getIcmpSrcDstMappin() {
		return icmpSrcDstMappin;
	}
	public void setIcmpSrcDstMappin(Map<String, List<AttackEvent>> icmpSrcDstMappin) {
		this.icmpSrcDstMappin = icmpSrcDstMappin;
	}
	public List<NetflowRecord> getIcmpFloodCache() {
		return icmpFloodCache;
	}
	public void setIcmpFloodCache(List<NetflowRecord> icmpFloodCache) {
		this.icmpFloodCache = icmpFloodCache;
	}
	public Map<String, Integer> getNtpSrcCounter() {
		return ntpSrcCounter;
	}
	public void setNtpSrcCounter(Map<String, Integer> ntpSrcCounter) {
		this.ntpSrcCounter = ntpSrcCounter;
	}
	public Map<String, List<AttackEvent>> getNtpSrcDstMappin() {
		return ntpSrcDstMappin;
	}
	public void setNtpSrcDstMappin(Map<String, List<AttackEvent>> ntpSrcDstMappin) {
		this.ntpSrcDstMappin = ntpSrcDstMappin;
	}

	
}
