package com.huirong.storage.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年12月30日
 */
public class DDoSMetricsCacheObj {

	private int record_creation_num = 0;
	private int ntp_num = 0;
	private int udp_num = 0;
	// key为ip, value为flow个数
	private Map<String, Integer> ntp_src = new HashMap<String, Integer>();
	private Map<String, Integer> ntp_dst = new HashMap<String, Integer>();
	private int syn_num = 0;
	private int tcp_num = 0;
	private Map<String, Integer> syn_src = new HashMap<String, Integer>();
	private Map<String, Integer> syn_dst = new HashMap<String, Integer>();
	private int ack_num = 0;
	private Map<String, Integer> ack_src = new HashMap<String, Integer>();
	private Map<String, Integer> ack_dst = new HashMap<String, Integer>();
	private int fin_num = 0;
	private Map<String, Integer> fin_src = new HashMap<String, Integer>();
	private Map<String, Integer> fin_dst = new HashMap<String, Integer>();
	private Map<String, Integer> udp_src = new HashMap<String, Integer>();
	private Map<String, Integer> udp_dst = new HashMap<String, Integer>();
	private int icmp_num = 0;
	private Map<String, Integer> icmp_src = new HashMap<String, Integer>();
	private Map<String, Integer> icmp_dst = new HashMap<String, Integer>();
	private int inval_flag_num = 0;
	private Map<String, Integer> inval_flag_src = new HashMap<String, Integer>();
	private Map<String, Integer> inval_flag_dst = new HashMap<String, Integer>();
	
	public int getIcmp_num() {
		return icmp_num;
	}
	public void setIcmp_num(int icmp_num) {
		this.icmp_num = icmp_num;
	}
	public Map<String, Integer> getIcmp_src() {
		return icmp_src;
	}
	public void setIcmp_src(Map<String, Integer> icmp_src) {
		this.icmp_src = icmp_src;
	}
	public Map<String, Integer> getIcmp_dst() {
		return icmp_dst;
	}
	public void setIcmp_dst(Map<String, Integer> icmp_dst) {
		this.icmp_dst = icmp_dst;
	}
	public int getInval_flag_num() {
		return inval_flag_num;
	}
	public void setInval_flag_num(int inval_flag_num) {
		this.inval_flag_num = inval_flag_num;
	}
	public Map<String, Integer> getInval_flag_src() {
		return inval_flag_src;
	}
	public void setInval_flag_src(Map<String, Integer> inval_flag_src) {
		this.inval_flag_src = inval_flag_src;
	}
	public Map<String, Integer> getInval_flag_dst() {
		return inval_flag_dst;
	}
	public void setInval_flag_dst(Map<String, Integer> inval_flag_dst) {
		this.inval_flag_dst = inval_flag_dst;
	}
	public Map<String, Integer> getAck_dst() {
		return ack_dst;
	}
	public int getAck_num() {
		return ack_num;
	}
	public Map<String, Integer> getAck_src() {
		return ack_src;
	}
	public Map<String, Integer> getFin_dst() {
		return fin_dst;
	}
	public int getFin_num() {
		return fin_num;
	}
	public Map<String, Integer> getFin_src() {
		return fin_src;
	}
	public Map<String, Integer> getNtp_dst() {
		return ntp_dst;
	}
	public int getNtp_num() {
		return ntp_num;
	}
	public Map<String, Integer> getNtp_src() {
		return ntp_src;
	}
	public int getRecord_creation_num() {
		return record_creation_num;
	}
	public Map<String, Integer> getSyn_dst() {
		return syn_dst;
	}
	public int getSyn_num() {
		return syn_num;
	}
	public Map<String, Integer> getSyn_src() {
		return syn_src;
	}
	public int getTcp_num() {
		return tcp_num;
	}
	public Map<String, Integer> getUdp_dst() {
		return udp_dst;
	}
	public int getUdp_num() {
		return udp_num;
	}
	public Map<String, Integer> getUdp_src() {
		return udp_src;
	}
	public void setAck_dst(Map<String, Integer> ack_dst) {
		this.ack_dst = ack_dst;
	}
	public void setAck_num(int ack_num) {
		this.ack_num = ack_num;
	}
	public void setAck_src(Map<String, Integer> ack_src) {
		this.ack_src = ack_src;
	}
	public void setFin_dst(Map<String, Integer> fin_dst) {
		this.fin_dst = fin_dst;
	}
	public void setFin_num(int fin_num) {
		this.fin_num = fin_num;
	}
	public void setFin_src(Map<String, Integer> fin_src) {
		this.fin_src = fin_src;
	}
	public void setNtp_dst(Map<String, Integer> ntp_dst) {
		this.ntp_dst = ntp_dst;
	}
	public void setNtp_num(int ntp_num) {
		this.ntp_num = ntp_num;
	}
	public void setNtp_src(Map<String, Integer> ntp_src) {
		this.ntp_src = ntp_src;
	}
	public void setRecord_creation_num(int record_creation_num) {
		this.record_creation_num = record_creation_num;
	}
	public void setSyn_dst(Map<String, Integer> syn_dst) {
		this.syn_dst = syn_dst;
	}
	public void setSyn_num(int syn_num) {
		this.syn_num = syn_num;
	}
	public void setSyn_src(Map<String, Integer> syn_src) {
		this.syn_src = syn_src;
	}
	public void setTcp_num(int tcp_num) {
		this.tcp_num = tcp_num;
	}
	public void setUdp_dst(Map<String, Integer> udp_dst) {
		this.udp_dst = udp_dst;
	}
	public void setUdp_num(int udp_num) {
		this.udp_num = udp_num;
	}
	public void setUdp_src(Map<String, Integer> udp_src) {
		this.udp_src = udp_src;
	}
	
	
}
