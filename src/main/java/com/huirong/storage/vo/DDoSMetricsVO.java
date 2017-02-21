package com.huirong.storage.vo;

import java.io.Serializable;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年12月30日
 */
public class DDoSMetricsVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6997882940929344198L;

	private String timeWindow;
	
	private int record_creation_num;
	private int ntp_num ;
	private double ntp_to_flow_ratio ;
	private double ntp_to_udp_ratio ;
	private String ntp_src_top_20 ;
	private String ntp_dst_top_20 ;
	private int syn_num ;
	private double syn_to_flow_ratio ;
	private double syn_to_tcp_ratio ;
	private int syn_src_less_5 ;
	private int syn_src_less_10 ;
	private int syn_src_less_20 ;
	private int syn_src_large_20 ;
	private int syn_dst_less_5 ;
	private int syn_dst_less_10 ;
	private int syn_dst_less_20 ;
	private int syn_dst_large_20 ;
	private double syn_src_less_5_ratio ;
	private double syn_src_less_10_ratio ;
	private double syn_src_less_20_ratio ;
	private double syn_src_large_20_ratio ;
	private double syn_dst_less_5_ratio ;
	private double syn_dst_less_10_ratio ;
	private double syn_dst_less_20_ratio ;
	private double syn_dst_large_20_ratio ;
	private String syn_src_top_20 ;
	private String syn_dst_top_20 ;
	private int ack_num ;
	private double ack_to_flow_ratio ;
	private double ack_to_tcp_ratio ;
	private int ack_src_less_5 ;
	private int ack_src_less_10 ;
	private int ack_src_less_20 ;
	private int ack_src_large_20 ;
	private int ack_dst_less_5 ;
	private int ack_dst_less_10 ;
	private int ack_dst_less_20 ;
	private int ack_dst_large_20 ;
	private double ack_src_less_5_ratio ;
	private double ack_src_less_10_ratio ;
	private double ack_src_less_20_ratio ;
	private double ack_src_large_20_ratio ;
	private double ack_dst_less_5_ratio ;
	private double ack_dst_less_10_ratio ;
	private double ack_dst_less_20_ratio ;
	private double ack_dst_large_20_ratio ;
	private String ack_src_top_20 ;
	private String ack_dst_top_20 ;
	private int fin_num ;
	private double fin_to_flow_ratio ;
	private double fin_to_tcp_ratio ;
	private int fin_src_less_5 ;
	private int fin_src_less_10 ;
	private int fin_src_less_20 ;
	private int fin_src_large_20 ;
	private int fin_dst_less_5 ;
	private int fin_dst_less_10 ;
	private int fin_dst_less_20 ;
	private int fin_dst_large_20 ;
	private double fin_src_less_5_ratio ;
	private double fin_src_less_10_ratio ;
	private double fin_src_less_20_ratio ;
	private double fin_src_large_20_ratio ;
	private double fin_dst_less_5_ratio ;
	private double fin_dst_less_10_ratio ;
	private double fin_dst_less_20_ratio ;
	private double fin_dst_large_20_ratio;
	private String fin_src_top_20 ;
	private String fin_dst_top_20 ;
	private int udp_num ;
	private double udp_to_flow_ratio ;
	private int udp_src_less_5 ;
	private int udp_src_less_10 ;
	private int udp_src_less_20 ;
	private int udp_src_large_20 ;
	private int udp_dst_less_5 ;
	private int udp_dst_less_10 ;
	private int udp_dst_less_20 ;
	private int udp_dst_large_20 ;
	private double udp_src_less_5_ratio ;
	private double udp_src_less_10_ratio ;
	private double udp_src_less_20_ratio ;
	private double udp_src_large_20_ratio ;
	private double udp_dst_less_5_ratio ;
	private double udp_dst_less_10_ratio ;
	private double udp_dst_less_20_ratio ;
	private double udp_dst_large_20_ratio ;
	private String udp_src_top_20 ;
	private String udp_dst_top_20 ;
	private int icmp_num ;
	private double icmp_to_flow_ratio ;
	private int icmp_src_less_5 ;
	private int icmp_src_less_10 ;
	private int icmp_src_less_20 ;
	private int icmp_src_large_20 ;
	private int icmp_dst_less_5 ;
	private int icmp_dst_less_10 ;
	private int icmp_dst_less_20 ;
	private int icmp_dst_large_20 ;
	private double icmp_src_less_5_ratio ;
	private double icmp_src_less_10_ratio ;
	private double icmp_src_less_20_ratio ;
	private double icmp_src_large_20_ratio ;
	private double icmp_dst_less_5_ratio ;
	private double icmp_dst_less_10_ratio ;
	private double icmp_dst_less_20_ratio ;
	private double icmp_dst_large_20_ratio ;
	private String icmp_src_top_20 ;
	private String icmp_dst_top_20 ;
	private int inval_flag_num ;
	private double inval_flag_to_flow_ratio ;
	private double inval_flag_to_tcp_ratio ;
	private int inval_flag_src_less_5 ;
	private int inval_flag_src_less_10 ;
	private int inval_flag_src_less_20 ;
	private int inval_flag_src_large_20 ;
	private int inval_flag_dst_less_5 ;
	private int inval_flag_dst_less_10 ;
	private int inval_flag_dst_less_20 ;
	private int inval_flag_dst_large_20 ;
	private double inval_flag_src_less_5_ratio ;
	private double inval_flag_src_less_10_ratio ;
	private double inval_flag_src_less_20_ratio ;
	private double inval_flag_src_large_20_ratio ;
	private double inval_flag_dst_less_5_ratio ;
	private double inval_flag_dst_less_10_ratio ;
	private double inval_flag_dst_less_20_ratio ;
	private double inval_flag_dst_large_20_ratio ;
	private String inval_flag_src_top_20 ;
	private String inval_flag_dst_top_20 ;
	
	public DDoSMetricsVO(){
		
	}
	
	public DDoSMetricsVO(String timeWindow, int record_creation_num, int ntp_num, double ntp_to_flow_ratio,
			double ntp_to_udp_ratio, String ntp_src_top_20, String ntp_dst_top_20, int syn_num,
			double syn_to_flow_ratio, double syn_to_tcp_ratio, int syn_src_less_5, int syn_src_less_10,
			int syn_src_less_20, int syn_src_large_20, int syn_dst_less_5, int syn_dst_less_10, int syn_dst_less_20,
			int syn_dst_large_20, double syn_src_less_5_ratio, double syn_src_less_10_ratio,
			double syn_src_less_20_ratio, double syn_src_large_20_ratio, double syn_dst_less_5_ratio,
			double syn_dst_less_10_ratio, double syn_dst_less_20_ratio, double syn_dst_large_20_ratio,
			String syn_src_top_20, String syn_dst_top_20, int ack_num, double ack_to_flow_ratio,
			double ack_to_tcp_ratio, int ack_src_less_5, int ack_src_less_10, int ack_src_less_20, int ack_src_large_20,
			int ack_dst_less_5, int ack_dst_less_10, int ack_dst_less_20, int ack_dst_large_20,
			double ack_src_less_5_ratio, double ack_src_less_10_ratio, double ack_src_less_20_ratio,
			double ack_src_large_20_ratio, double ack_dst_less_5_ratio, double ack_dst_less_10_ratio,
			double ack_dst_less_20_ratio, double ack_dst_large_20_ratio, String ack_src_top_20, String ack_dst_top_20,
			int fin_num, double fin_to_flow_ratio, double fin_to_tcp_ratio, int fin_src_less_5, int fin_src_less_10,
			int fin_src_less_20, int fin_src_large_20, int fin_dst_less_5, int fin_dst_less_10, int fin_dst_less_20,
			int fin_dst_large_20, double fin_src_less_5_ratio, double fin_src_less_10_ratio,
			double fin_src_less_20_ratio, double fin_src_large_20_ratio, double fin_dst_less_5_ratio,
			double fin_dst_less_10_ratio, double fin_dst_less_20_ratio, double fin_dst_large_20_ratio,
			String fin_src_top_20, String fin_dst_top_20, int udp_num, double udp_to_flow_ratio, int udp_src_less_5,
			int udp_src_less_10, int udp_src_less_20, int udp_src_large_20, int udp_dst_less_5, int udp_dst_less_10,
			int udp_dst_less_20, int udp_dst_large_20, double udp_src_less_5_ratio, double udp_src_less_10_ratio,
			double udp_src_less_20_ratio, double udp_src_large_20_ratio, double udp_dst_less_5_ratio,
			double udp_dst_less_10_ratio, double udp_dst_less_20_ratio, double udp_dst_large_20_ratio,
			String udp_src_top_20, String udp_dst_top_20, int icmp_num, double icmp_to_flow_ratio, int icmp_src_less_5,
			int icmp_src_less_10, int icmp_src_less_20, int icmp_src_large_20, int icmp_dst_less_5,
			int icmp_dst_less_10, int icmp_dst_less_20, int icmp_dst_large_20, double icmp_src_less_5_ratio,
			double icmp_src_less_10_ratio, double icmp_src_less_20_ratio, double icmp_src_large_20_ratio,
			double icmp_dst_less_5_ratio, double icmp_dst_less_10_ratio, double icmp_dst_less_20_ratio,
			double icmp_dst_large_20_ratio, String icmp_src_top_20, String icmp_dst_top_20, int inval_flag_num,
			double inval_flag_to_flow_ratio, double inval_flag_to_tcp_ratio, int inval_flag_src_less_5,
			int inval_flag_src_less_10, int inval_flag_src_less_20, int inval_flag_src_large_20,
			int inval_flag_dst_less_5, int inval_flag_dst_less_10, int inval_flag_dst_less_20,
			int inval_flag_dst_large_20, double inval_flag_src_less_5_ratio, double inval_flag_src_less_10_ratio,
			double inval_flag_src_less_20_ratio, double inval_flag_src_large_20_ratio,
			double inval_flag_dst_less_5_ratio, double inval_flag_dst_less_10_ratio,
			double inval_flag_dst_less_20_ratio, double inval_flag_dst_large_20_ratio, String inval_flag_src_top_20,
			String inval_flag_dst_top_20) {
		super();
		this.timeWindow = timeWindow;
		this.record_creation_num = record_creation_num;
		this.ntp_num = ntp_num;
		this.ntp_to_flow_ratio = ntp_to_flow_ratio;
		this.ntp_to_udp_ratio = ntp_to_udp_ratio;
		this.ntp_src_top_20 = ntp_src_top_20;
		this.ntp_dst_top_20 = ntp_dst_top_20;
		this.syn_num = syn_num;
		this.syn_to_flow_ratio = syn_to_flow_ratio;
		this.syn_to_tcp_ratio = syn_to_tcp_ratio;
		this.syn_src_less_5 = syn_src_less_5;
		this.syn_src_less_10 = syn_src_less_10;
		this.syn_src_less_20 = syn_src_less_20;
		this.syn_src_large_20 = syn_src_large_20;
		this.syn_dst_less_5 = syn_dst_less_5;
		this.syn_dst_less_10 = syn_dst_less_10;
		this.syn_dst_less_20 = syn_dst_less_20;
		this.syn_dst_large_20 = syn_dst_large_20;
		this.syn_src_less_5_ratio = syn_src_less_5_ratio;
		this.syn_src_less_10_ratio = syn_src_less_10_ratio;
		this.syn_src_less_20_ratio = syn_src_less_20_ratio;
		this.syn_src_large_20_ratio = syn_src_large_20_ratio;
		this.syn_dst_less_5_ratio = syn_dst_less_5_ratio;
		this.syn_dst_less_10_ratio = syn_dst_less_10_ratio;
		this.syn_dst_less_20_ratio = syn_dst_less_20_ratio;
		this.syn_dst_large_20_ratio = syn_dst_large_20_ratio;
		this.syn_src_top_20 = syn_src_top_20;
		this.syn_dst_top_20 = syn_dst_top_20;
		this.ack_num = ack_num;
		this.ack_to_flow_ratio = ack_to_flow_ratio;
		this.ack_to_tcp_ratio = ack_to_tcp_ratio;
		this.ack_src_less_5 = ack_src_less_5;
		this.ack_src_less_10 = ack_src_less_10;
		this.ack_src_less_20 = ack_src_less_20;
		this.ack_src_large_20 = ack_src_large_20;
		this.ack_dst_less_5 = ack_dst_less_5;
		this.ack_dst_less_10 = ack_dst_less_10;
		this.ack_dst_less_20 = ack_dst_less_20;
		this.ack_dst_large_20 = ack_dst_large_20;
		this.ack_src_less_5_ratio = ack_src_less_5_ratio;
		this.ack_src_less_10_ratio = ack_src_less_10_ratio;
		this.ack_src_less_20_ratio = ack_src_less_20_ratio;
		this.ack_src_large_20_ratio = ack_src_large_20_ratio;
		this.ack_dst_less_5_ratio = ack_dst_less_5_ratio;
		this.ack_dst_less_10_ratio = ack_dst_less_10_ratio;
		this.ack_dst_less_20_ratio = ack_dst_less_20_ratio;
		this.ack_dst_large_20_ratio = ack_dst_large_20_ratio;
		this.ack_src_top_20 = ack_src_top_20;
		this.ack_dst_top_20 = ack_dst_top_20;
		this.fin_num = fin_num;
		this.fin_to_flow_ratio = fin_to_flow_ratio;
		this.fin_to_tcp_ratio = fin_to_tcp_ratio;
		this.fin_src_less_5 = fin_src_less_5;
		this.fin_src_less_10 = fin_src_less_10;
		this.fin_src_less_20 = fin_src_less_20;
		this.fin_src_large_20 = fin_src_large_20;
		this.fin_dst_less_5 = fin_dst_less_5;
		this.fin_dst_less_10 = fin_dst_less_10;
		this.fin_dst_less_20 = fin_dst_less_20;
		this.fin_dst_large_20 = fin_dst_large_20;
		this.fin_src_less_5_ratio = fin_src_less_5_ratio;
		this.fin_src_less_10_ratio = fin_src_less_10_ratio;
		this.fin_src_less_20_ratio = fin_src_less_20_ratio;
		this.fin_src_large_20_ratio = fin_src_large_20_ratio;
		this.fin_dst_less_5_ratio = fin_dst_less_5_ratio;
		this.fin_dst_less_10_ratio = fin_dst_less_10_ratio;
		this.fin_dst_less_20_ratio = fin_dst_less_20_ratio;
		this.fin_dst_large_20_ratio = fin_dst_large_20_ratio;
		this.fin_src_top_20 = fin_src_top_20;
		this.fin_dst_top_20 = fin_dst_top_20;
		this.udp_num = udp_num;
		this.udp_to_flow_ratio = udp_to_flow_ratio;
		this.udp_src_less_5 = udp_src_less_5;
		this.udp_src_less_10 = udp_src_less_10;
		this.udp_src_less_20 = udp_src_less_20;
		this.udp_src_large_20 = udp_src_large_20;
		this.udp_dst_less_5 = udp_dst_less_5;
		this.udp_dst_less_10 = udp_dst_less_10;
		this.udp_dst_less_20 = udp_dst_less_20;
		this.udp_dst_large_20 = udp_dst_large_20;
		this.udp_src_less_5_ratio = udp_src_less_5_ratio;
		this.udp_src_less_10_ratio = udp_src_less_10_ratio;
		this.udp_src_less_20_ratio = udp_src_less_20_ratio;
		this.udp_src_large_20_ratio = udp_src_large_20_ratio;
		this.udp_dst_less_5_ratio = udp_dst_less_5_ratio;
		this.udp_dst_less_10_ratio = udp_dst_less_10_ratio;
		this.udp_dst_less_20_ratio = udp_dst_less_20_ratio;
		this.udp_dst_large_20_ratio = udp_dst_large_20_ratio;
		this.udp_src_top_20 = udp_src_top_20;
		this.udp_dst_top_20 = udp_dst_top_20;
		this.icmp_num = icmp_num;
		this.icmp_to_flow_ratio = icmp_to_flow_ratio;
		this.icmp_src_less_5 = icmp_src_less_5;
		this.icmp_src_less_10 = icmp_src_less_10;
		this.icmp_src_less_20 = icmp_src_less_20;
		this.icmp_src_large_20 = icmp_src_large_20;
		this.icmp_dst_less_5 = icmp_dst_less_5;
		this.icmp_dst_less_10 = icmp_dst_less_10;
		this.icmp_dst_less_20 = icmp_dst_less_20;
		this.icmp_dst_large_20 = icmp_dst_large_20;
		this.icmp_src_less_5_ratio = icmp_src_less_5_ratio;
		this.icmp_src_less_10_ratio = icmp_src_less_10_ratio;
		this.icmp_src_less_20_ratio = icmp_src_less_20_ratio;
		this.icmp_src_large_20_ratio = icmp_src_large_20_ratio;
		this.icmp_dst_less_5_ratio = icmp_dst_less_5_ratio;
		this.icmp_dst_less_10_ratio = icmp_dst_less_10_ratio;
		this.icmp_dst_less_20_ratio = icmp_dst_less_20_ratio;
		this.icmp_dst_large_20_ratio = icmp_dst_large_20_ratio;
		this.icmp_src_top_20 = icmp_src_top_20;
		this.icmp_dst_top_20 = icmp_dst_top_20;
		this.inval_flag_num = inval_flag_num;
		this.inval_flag_to_flow_ratio = inval_flag_to_flow_ratio;
		this.inval_flag_to_tcp_ratio = inval_flag_to_tcp_ratio;
		this.inval_flag_src_less_5 = inval_flag_src_less_5;
		this.inval_flag_src_less_10 = inval_flag_src_less_10;
		this.inval_flag_src_less_20 = inval_flag_src_less_20;
		this.inval_flag_src_large_20 = inval_flag_src_large_20;
		this.inval_flag_dst_less_5 = inval_flag_dst_less_5;
		this.inval_flag_dst_less_10 = inval_flag_dst_less_10;
		this.inval_flag_dst_less_20 = inval_flag_dst_less_20;
		this.inval_flag_dst_large_20 = inval_flag_dst_large_20;
		this.inval_flag_src_less_5_ratio = inval_flag_src_less_5_ratio;
		this.inval_flag_src_less_10_ratio = inval_flag_src_less_10_ratio;
		this.inval_flag_src_less_20_ratio = inval_flag_src_less_20_ratio;
		this.inval_flag_src_large_20_ratio = inval_flag_src_large_20_ratio;
		this.inval_flag_dst_less_5_ratio = inval_flag_dst_less_5_ratio;
		this.inval_flag_dst_less_10_ratio = inval_flag_dst_less_10_ratio;
		this.inval_flag_dst_less_20_ratio = inval_flag_dst_less_20_ratio;
		this.inval_flag_dst_large_20_ratio = inval_flag_dst_large_20_ratio;
		this.inval_flag_src_top_20 = inval_flag_src_top_20;
		this.inval_flag_dst_top_20 = inval_flag_dst_top_20;
	}
	public int getIcmp_num() {
		return icmp_num;
	}
	public void setIcmp_num(int icmp_num) {
		this.icmp_num = icmp_num;
	}
	public double getIcmp_to_flow_ratio() {
		return icmp_to_flow_ratio;
	}
	public void setIcmp_to_flow_ratio(double icmp_to_flow_ratio) {
		this.icmp_to_flow_ratio = icmp_to_flow_ratio;
	}
	public int getIcmp_src_less_5() {
		return icmp_src_less_5;
	}
	public void setIcmp_src_less_5(int icmp_src_less_5) {
		this.icmp_src_less_5 = icmp_src_less_5;
	}
	public int getIcmp_src_less_10() {
		return icmp_src_less_10;
	}
	public void setIcmp_src_less_10(int icmp_src_less_10) {
		this.icmp_src_less_10 = icmp_src_less_10;
	}
	public int getIcmp_src_less_20() {
		return icmp_src_less_20;
	}
	public void setIcmp_src_less_20(int icmp_src_less_20) {
		this.icmp_src_less_20 = icmp_src_less_20;
	}
	public int getIcmp_src_large_20() {
		return icmp_src_large_20;
	}
	public void setIcmp_src_large_20(int icmp_src_large_20) {
		this.icmp_src_large_20 = icmp_src_large_20;
	}
	public int getIcmp_dst_less_5() {
		return icmp_dst_less_5;
	}
	public void setIcmp_dst_less_5(int icmp_dst_less_5) {
		this.icmp_dst_less_5 = icmp_dst_less_5;
	}
	public int getIcmp_dst_less_10() {
		return icmp_dst_less_10;
	}
	public void setIcmp_dst_less_10(int icmp_dst_less_10) {
		this.icmp_dst_less_10 = icmp_dst_less_10;
	}
	public int getIcmp_dst_less_20() {
		return icmp_dst_less_20;
	}
	public void setIcmp_dst_less_20(int icmp_dst_less_20) {
		this.icmp_dst_less_20 = icmp_dst_less_20;
	}
	public int getIcmp_dst_large_20() {
		return icmp_dst_large_20;
	}
	public void setIcmp_dst_large_20(int icmp_dst_large_20) {
		this.icmp_dst_large_20 = icmp_dst_large_20;
	}
	public double getIcmp_src_less_5_ratio() {
		return icmp_src_less_5_ratio;
	}
	public void setIcmp_src_less_5_ratio(double icmp_src_less_5_ratio) {
		this.icmp_src_less_5_ratio = icmp_src_less_5_ratio;
	}
	public double getIcmp_src_less_10_ratio() {
		return icmp_src_less_10_ratio;
	}
	public void setIcmp_src_less_10_ratio(double icmp_src_less_10_ratio) {
		this.icmp_src_less_10_ratio = icmp_src_less_10_ratio;
	}
	public double getIcmp_src_less_20_ratio() {
		return icmp_src_less_20_ratio;
	}
	public void setIcmp_src_less_20_ratio(double icmp_src_less_20_ratio) {
		this.icmp_src_less_20_ratio = icmp_src_less_20_ratio;
	}
	public double getIcmp_src_large_20_ratio() {
		return icmp_src_large_20_ratio;
	}
	public void setIcmp_src_large_20_ratio(double icmp_src_large_20_ratio) {
		this.icmp_src_large_20_ratio = icmp_src_large_20_ratio;
	}
	public double getIcmp_dst_less_5_ratio() {
		return icmp_dst_less_5_ratio;
	}
	public void setIcmp_dst_less_5_ratio(double icmp_dst_less_5_ratio) {
		this.icmp_dst_less_5_ratio = icmp_dst_less_5_ratio;
	}
	public double getIcmp_dst_less_10_ratio() {
		return icmp_dst_less_10_ratio;
	}
	public void setIcmp_dst_less_10_ratio(double icmp_dst_less_10_ratio) {
		this.icmp_dst_less_10_ratio = icmp_dst_less_10_ratio;
	}
	public double getIcmp_dst_less_20_ratio() {
		return icmp_dst_less_20_ratio;
	}
	public void setIcmp_dst_less_20_ratio(double icmp_dst_less_20_ratio) {
		this.icmp_dst_less_20_ratio = icmp_dst_less_20_ratio;
	}
	public double getIcmp_dst_large_20_ratio() {
		return icmp_dst_large_20_ratio;
	}
	public void setIcmp_dst_large_20_ratio(double icmp_dst_large_20_ratio) {
		this.icmp_dst_large_20_ratio = icmp_dst_large_20_ratio;
	}
	public String getIcmp_src_top_20() {
		return icmp_src_top_20;
	}
	public void setIcmp_src_top_20(String icmp_src_top_20) {
		this.icmp_src_top_20 = icmp_src_top_20;
	}
	public String getIcmp_dst_top_20() {
		return icmp_dst_top_20;
	}
	public void setIcmp_dst_top_20(String icmp_dst_top_20) {
		this.icmp_dst_top_20 = icmp_dst_top_20;
	}
	public int getInval_flag_num() {
		return inval_flag_num;
	}
	public void setInval_flag_num(int inval_flag_num) {
		this.inval_flag_num = inval_flag_num;
	}
	public double getInval_flag_to_flow_ratio() {
		return inval_flag_to_flow_ratio;
	}
	public void setInval_flag_to_flow_ratio(double inval_flag_to_flow_ratio) {
		this.inval_flag_to_flow_ratio = inval_flag_to_flow_ratio;
	}
	public double getInval_flag_to_tcp_ratio() {
		return inval_flag_to_tcp_ratio;
	}
	public void setInval_flag_to_tcp_ratio(double inval_flag_to_tcp_ratio) {
		this.inval_flag_to_tcp_ratio = inval_flag_to_tcp_ratio;
	}
	public int getInval_flag_src_less_5() {
		return inval_flag_src_less_5;
	}
	public void setInval_flag_src_less_5(int inval_flag_src_less_5) {
		this.inval_flag_src_less_5 = inval_flag_src_less_5;
	}
	public int getInval_flag_src_less_10() {
		return inval_flag_src_less_10;
	}
	public void setInval_flag_src_less_10(int inval_flag_src_less_10) {
		this.inval_flag_src_less_10 = inval_flag_src_less_10;
	}
	public int getInval_flag_src_less_20() {
		return inval_flag_src_less_20;
	}
	public void setInval_flag_src_less_20(int inval_flag_src_less_20) {
		this.inval_flag_src_less_20 = inval_flag_src_less_20;
	}
	public int getInval_flag_src_large_20() {
		return inval_flag_src_large_20;
	}
	public void setInval_flag_src_large_20(int inval_flag_src_large_20) {
		this.inval_flag_src_large_20 = inval_flag_src_large_20;
	}
	public int getInval_flag_dst_less_5() {
		return inval_flag_dst_less_5;
	}
	public void setInval_flag_dst_less_5(int inval_flag_dst_less_5) {
		this.inval_flag_dst_less_5 = inval_flag_dst_less_5;
	}
	public int getInval_flag_dst_less_10() {
		return inval_flag_dst_less_10;
	}
	public void setInval_flag_dst_less_10(int inval_flag_dst_less_10) {
		this.inval_flag_dst_less_10 = inval_flag_dst_less_10;
	}
	public int getInval_flag_dst_less_20() {
		return inval_flag_dst_less_20;
	}
	public void setInval_flag_dst_less_20(int inval_flag_dst_less_20) {
		this.inval_flag_dst_less_20 = inval_flag_dst_less_20;
	}
	public int getInval_flag_dst_large_20() {
		return inval_flag_dst_large_20;
	}
	public void setInval_flag_dst_large_20(int inval_flag_dst_large_20) {
		this.inval_flag_dst_large_20 = inval_flag_dst_large_20;
	}
	public double getInval_flag_src_less_5_ratio() {
		return inval_flag_src_less_5_ratio;
	}
	public void setInval_flag_src_less_5_ratio(double inval_flag_src_less_5_ratio) {
		this.inval_flag_src_less_5_ratio = inval_flag_src_less_5_ratio;
	}
	public double getInval_flag_src_less_10_ratio() {
		return inval_flag_src_less_10_ratio;
	}
	public void setInval_flag_src_less_10_ratio(double inval_flag_src_less_10_ratio) {
		this.inval_flag_src_less_10_ratio = inval_flag_src_less_10_ratio;
	}
	public double getInval_flag_src_less_20_ratio() {
		return inval_flag_src_less_20_ratio;
	}
	public void setInval_flag_src_less_20_ratio(double inval_flag_src_less_20_ratio) {
		this.inval_flag_src_less_20_ratio = inval_flag_src_less_20_ratio;
	}
	public double getInval_flag_src_large_20_ratio() {
		return inval_flag_src_large_20_ratio;
	}
	public void setInval_flag_src_large_20_ratio(double inval_flag_src_large_20_ratio) {
		this.inval_flag_src_large_20_ratio = inval_flag_src_large_20_ratio;
	}
	public double getInval_flag_dst_less_5_ratio() {
		return inval_flag_dst_less_5_ratio;
	}
	public void setInval_flag_dst_less_5_ratio(double inval_flag_dst_less_5_ratio) {
		this.inval_flag_dst_less_5_ratio = inval_flag_dst_less_5_ratio;
	}
	public double getInval_flag_dst_less_10_ratio() {
		return inval_flag_dst_less_10_ratio;
	}
	public void setInval_flag_dst_less_10_ratio(double inval_flag_dst_less_10_ratio) {
		this.inval_flag_dst_less_10_ratio = inval_flag_dst_less_10_ratio;
	}
	public double getInval_flag_dst_less_20_ratio() {
		return inval_flag_dst_less_20_ratio;
	}
	public void setInval_flag_dst_less_20_ratio(double inval_flag_dst_less_20_ratio) {
		this.inval_flag_dst_less_20_ratio = inval_flag_dst_less_20_ratio;
	}
	public double getInval_flag_dst_large_20_ratio() {
		return inval_flag_dst_large_20_ratio;
	}
	public void setInval_flag_dst_large_20_ratio(double inval_flag_dst_large_20_ratio) {
		this.inval_flag_dst_large_20_ratio = inval_flag_dst_large_20_ratio;
	}
	public String getInval_flag_src_top_20() {
		return inval_flag_src_top_20;
	}
	public void setInval_flag_src_top_20(String inval_flag_src_top_20) {
		this.inval_flag_src_top_20 = inval_flag_src_top_20;
	}
	public String getInval_flag_dst_top_20() {
		return inval_flag_dst_top_20;
	}
	public void setInval_flag_dst_top_20(String inval_flag_dst_top_20) {
		this.inval_flag_dst_top_20 = inval_flag_dst_top_20;
	}
	public int getAck_dst_large_20() {
		return ack_dst_large_20;
	}
	public double getAck_dst_large_20_ratio() {
		return ack_dst_large_20_ratio;
	}
	public int getAck_dst_less_10() {
		return ack_dst_less_10;
	}
	public double getAck_dst_less_10_ratio() {
		return ack_dst_less_10_ratio;
	}
	public int getAck_dst_less_20() {
		return ack_dst_less_20;
	}
	public double getAck_dst_less_20_ratio() {
		return ack_dst_less_20_ratio;
	}
	public int getAck_dst_less_5() {
		return ack_dst_less_5;
	}
	public double getAck_dst_less_5_ratio() {
		return ack_dst_less_5_ratio;
	}
	public String getAck_dst_top_20() {
		return ack_dst_top_20;
	}
	public int getAck_num() {
		return ack_num;
	}
	public int getAck_src_large_20() {
		return ack_src_large_20;
	}
	public double getAck_src_large_20_ratio() {
		return ack_src_large_20_ratio;
	}
	public int getAck_src_less_10() {
		return ack_src_less_10;
	}
	public double getAck_src_less_10_ratio() {
		return ack_src_less_10_ratio;
	}
	public int getAck_src_less_20() {
		return ack_src_less_20;
	}
	public double getAck_src_less_20_ratio() {
		return ack_src_less_20_ratio;
	}
	public int getAck_src_less_5() {
		return ack_src_less_5;
	}
	public double getAck_src_less_5_ratio() {
		return ack_src_less_5_ratio;
	}
	public String getAck_src_top_20() {
		return ack_src_top_20;
	}
	public double getAck_to_flow_ratio() {
		return ack_to_flow_ratio;
	}
	public double getAck_to_tcp_ratio() {
		return ack_to_tcp_ratio;
	}
	public int getFin_dst_large_20() {
		return fin_dst_large_20;
	}
	public double getFin_dst_large_20_ratio() {
		return fin_dst_large_20_ratio;
	}
	public int getFin_dst_less_10() {
		return fin_dst_less_10;
	}
	public double getFin_dst_less_10_ratio() {
		return fin_dst_less_10_ratio;
	}
	public int getFin_dst_less_20() {
		return fin_dst_less_20;
	}
	public double getFin_dst_less_20_ratio() {
		return fin_dst_less_20_ratio;
	}
	public int getFin_dst_less_5() {
		return fin_dst_less_5;
	}
	public double getFin_dst_less_5_ratio() {
		return fin_dst_less_5_ratio;
	}
	public String getFin_dst_top_20() {
		return fin_dst_top_20;
	}
	public int getFin_num() {
		return fin_num;
	}
	public int getFin_src_large_20() {
		return fin_src_large_20;
	}
	public double getFin_src_large_20_ratio() {
		return fin_src_large_20_ratio;
	}
	public int getFin_src_less_10() {
		return fin_src_less_10;
	}
	public double getFin_src_less_10_ratio() {
		return fin_src_less_10_ratio;
	}
	public int getFin_src_less_20() {
		return fin_src_less_20;
	}
	public double getFin_src_less_20_ratio() {
		return fin_src_less_20_ratio;
	}
	public int getFin_src_less_5() {
		return fin_src_less_5;
	}
	public double getFin_src_less_5_ratio() {
		return fin_src_less_5_ratio;
	}
	public String getFin_src_top_20() {
		return fin_src_top_20;
	}
	public double getFin_to_flow_ratio() {
		return fin_to_flow_ratio;
	}
	public double getFin_to_tcp_ratio() {
		return fin_to_tcp_ratio;
	}
	public String getNtp_dst_top_20() {
		return ntp_dst_top_20;
	}
	public int getNtp_num() {
		return ntp_num;
	}
	public String getNtp_src_top_20() {
		return ntp_src_top_20;
	}
	public double getNtp_to_flow_ratio() {
		return ntp_to_flow_ratio;
	}
	public double getNtp_to_udp_ratio() {
		return ntp_to_udp_ratio;
	}
	public int getRecord_creation_num() {
		return record_creation_num;
	}
	public int getSyn_dst_large_20() {
		return syn_dst_large_20;
	}
	public double getSyn_dst_large_20_ratio() {
		return syn_dst_large_20_ratio;
	}
	public int getSyn_dst_less_10() {
		return syn_dst_less_10;
	}
	public double getSyn_dst_less_10_ratio() {
		return syn_dst_less_10_ratio;
	}
	public int getSyn_dst_less_20() {
		return syn_dst_less_20;
	}
	public double getSyn_dst_less_20_ratio() {
		return syn_dst_less_20_ratio;
	}
	public int getSyn_dst_less_5() {
		return syn_dst_less_5;
	}
	public double getSyn_dst_less_5_ratio() {
		return syn_dst_less_5_ratio;
	}
	public String getSyn_dst_top_20() {
		return syn_dst_top_20;
	}
	public int getSyn_num() {
		return syn_num;
	}
	public int getSyn_src_large_20() {
		return syn_src_large_20;
	}
	public double getSyn_src_large_20_ratio() {
		return syn_src_large_20_ratio;
	}
	public int getSyn_src_less_10() {
		return syn_src_less_10;
	}
	public double getSyn_src_less_10_ratio() {
		return syn_src_less_10_ratio;
	}
	public int getSyn_src_less_20() {
		return syn_src_less_20;
	}
	public double getSyn_src_less_20_ratio() {
		return syn_src_less_20_ratio;
	}
	public int getSyn_src_less_5() {
		return syn_src_less_5;
	}
	public double getSyn_src_less_5_ratio() {
		return syn_src_less_5_ratio;
	}
	public String getSyn_src_top_20() {
		return syn_src_top_20;
	}
	public double getSyn_to_flow_ratio() {
		return syn_to_flow_ratio;
	}
	public double getSyn_to_tcp_ratio() {
		return syn_to_tcp_ratio;
	}
	public int getUdp_dst_large_20() {
		return udp_dst_large_20;
	}
	public double getUdp_dst_large_20_ratio() {
		return udp_dst_large_20_ratio;
	}
	public int getUdp_dst_less_10() {
		return udp_dst_less_10;
	}
	public double getUdp_dst_less_10_ratio() {
		return udp_dst_less_10_ratio;
	}
	public int getUdp_dst_less_20() {
		return udp_dst_less_20;
	}
	public double getUdp_dst_less_20_ratio() {
		return udp_dst_less_20_ratio;
	}
	public int getUdp_dst_less_5() {
		return udp_dst_less_5;
	}
	public double getUdp_dst_less_5_ratio() {
		return udp_dst_less_5_ratio;
	}
	public String getUdp_dst_top_20() {
		return udp_dst_top_20;
	}
	public int getUdp_num() {
		return udp_num;
	}
	public int getUdp_src_large_20() {
		return udp_src_large_20;
	}
	public double getUdp_src_large_20_ratio() {
		return udp_src_large_20_ratio;
	}
	public int getUdp_src_less_10() {
		return udp_src_less_10;
	}
	public double getUdp_src_less_10_ratio() {
		return udp_src_less_10_ratio;
	}
	public int getUdp_src_less_20() {
		return udp_src_less_20;
	}
	public double getUdp_src_less_20_ratio() {
		return udp_src_less_20_ratio;
	}
	public int getUdp_src_less_5() {
		return udp_src_less_5;
	}
	public double getUdp_src_less_5_ratio() {
		return udp_src_less_5_ratio;
	}
	public String getUdp_src_top_20() {
		return udp_src_top_20;
	}
	public double getUdp_to_flow_ratio() {
		return udp_to_flow_ratio;
	}
	public void setAck_dst_large_20(int ack_dst_large_20) {
		this.ack_dst_large_20 = ack_dst_large_20;
	}
	public void setAck_dst_large_20_ratio(double ack_dst_large_20_ratio) {
		this.ack_dst_large_20_ratio = ack_dst_large_20_ratio;
	}
	public void setAck_dst_less_10(int ack_dst_less_10) {
		this.ack_dst_less_10 = ack_dst_less_10;
	}
	public void setAck_dst_less_10_ratio(double ack_dst_less_10_ratio) {
		this.ack_dst_less_10_ratio = ack_dst_less_10_ratio;
	}
	public void setAck_dst_less_20(int ack_dst_less_20) {
		this.ack_dst_less_20 = ack_dst_less_20;
	}
	public void setAck_dst_less_20_ratio(double ack_dst_less_20_ratio) {
		this.ack_dst_less_20_ratio = ack_dst_less_20_ratio;
	}
	public void setAck_dst_less_5(int ack_dst_less_5) {
		this.ack_dst_less_5 = ack_dst_less_5;
	}
	public void setAck_dst_less_5_ratio(double ack_dst_less_5_ratio) {
		this.ack_dst_less_5_ratio = ack_dst_less_5_ratio;
	}
	public void setAck_dst_top_20(String ack_dst_top_20) {
		this.ack_dst_top_20 = ack_dst_top_20;
	}
	public void setAck_num(int ack_num) {
		this.ack_num = ack_num;
	}
	public void setAck_src_large_20(int ack_src_large_20) {
		this.ack_src_large_20 = ack_src_large_20;
	}
	public void setAck_src_large_20_ratio(double ack_src_large_20_ratio) {
		this.ack_src_large_20_ratio = ack_src_large_20_ratio;
	}
	public void setAck_src_less_10(int ack_src_less_10) {
		this.ack_src_less_10 = ack_src_less_10;
	}
	public void setAck_src_less_10_ratio(double ack_src_less_10_ratio) {
		this.ack_src_less_10_ratio = ack_src_less_10_ratio;
	}
	public void setAck_src_less_20(int ack_src_less_20) {
		this.ack_src_less_20 = ack_src_less_20;
	}
	public void setAck_src_less_20_ratio(double ack_src_less_20_ratio) {
		this.ack_src_less_20_ratio = ack_src_less_20_ratio;
	}
	public void setAck_src_less_5(int ack_src_less_5) {
		this.ack_src_less_5 = ack_src_less_5;
	}
	public void setAck_src_less_5_ratio(double ack_src_less_5_ratio) {
		this.ack_src_less_5_ratio = ack_src_less_5_ratio;
	}
	public void setAck_src_top_20(String ack_src_top_20) {
		this.ack_src_top_20 = ack_src_top_20;
	}
	public void setAck_to_flow_ratio(double ack_to_flow_ratio) {
		this.ack_to_flow_ratio = ack_to_flow_ratio;
	}
	public void setAck_to_tcp_ratio(double ack_to_tcp_ratio) {
		this.ack_to_tcp_ratio = ack_to_tcp_ratio;
	}
	public void setFin_dst_large_20(int fin_dst_large_20) {
		this.fin_dst_large_20 = fin_dst_large_20;
	}
	public void setFin_dst_large_20_ratio(double fin_dst_large_20_ratio) {
		this.fin_dst_large_20_ratio = fin_dst_large_20_ratio;
	}
	public void setFin_dst_less_10(int fin_dst_less_10) {
		this.fin_dst_less_10 = fin_dst_less_10;
	}
	public void setFin_dst_less_10_ratio(double fin_dst_less_10_ratio) {
		this.fin_dst_less_10_ratio = fin_dst_less_10_ratio;
	}
	public void setFin_dst_less_20(int fin_dst_less_20) {
		this.fin_dst_less_20 = fin_dst_less_20;
	}
	public void setFin_dst_less_20_ratio(double fin_dst_less_20_ratio) {
		this.fin_dst_less_20_ratio = fin_dst_less_20_ratio;
	}
	public void setFin_dst_less_5(int fin_dst_less_5) {
		this.fin_dst_less_5 = fin_dst_less_5;
	}
	public void setFin_dst_less_5_ratio(double fin_dst_less_5_ratio) {
		this.fin_dst_less_5_ratio = fin_dst_less_5_ratio;
	}
	public void setFin_dst_top_20(String fin_dst_top_20) {
		this.fin_dst_top_20 = fin_dst_top_20;
	}
	public void setFin_num(int fin_num) {
		this.fin_num = fin_num;
	}
	public void setFin_src_large_20(int fin_src_large_20) {
		this.fin_src_large_20 = fin_src_large_20;
	}
	public void setFin_src_large_20_ratio(double fin_src_large_20_ratio) {
		this.fin_src_large_20_ratio = fin_src_large_20_ratio;
	}
	public void setFin_src_less_10(int fin_src_less_10) {
		this.fin_src_less_10 = fin_src_less_10;
	}
	public void setFin_src_less_10_ratio(double fin_src_less_10_ratio) {
		this.fin_src_less_10_ratio = fin_src_less_10_ratio;
	}
	public void setFin_src_less_20(int fin_src_less_20) {
		this.fin_src_less_20 = fin_src_less_20;
	}
	public void setFin_src_less_20_ratio(double fin_src_less_20_ratio) {
		this.fin_src_less_20_ratio = fin_src_less_20_ratio;
	}
	public void setFin_src_less_5(int fin_src_less_5) {
		this.fin_src_less_5 = fin_src_less_5;
	}
	public void setFin_src_less_5_ratio(double fin_src_less_5_ratio) {
		this.fin_src_less_5_ratio = fin_src_less_5_ratio;
	}
	public void setFin_src_top_20(String fin_src_top_20) {
		this.fin_src_top_20 = fin_src_top_20;
	}
	public void setFin_to_flow_ratio(double fin_to_flow_ratio) {
		this.fin_to_flow_ratio = fin_to_flow_ratio;
	}
	public void setFin_to_tcp_ratio(double fin_to_tcp_ratio) {
		this.fin_to_tcp_ratio = fin_to_tcp_ratio;
	}
	public void setNtp_dst_top_20(String ntp_dst_top_20) {
		this.ntp_dst_top_20 = ntp_dst_top_20;
	}
	public void setNtp_num(int ntp_num) {
		this.ntp_num = ntp_num;
	}
	public void setNtp_src_top_20(String ntp_src_top_20) {
		this.ntp_src_top_20 = ntp_src_top_20;
	}
	public void setNtp_to_flow_ratio(double ntp_to_flow_ratio) {
		this.ntp_to_flow_ratio = ntp_to_flow_ratio;
	}
	public void setNtp_to_udp_ratio(double ntp_to_udp_ratio) {
		this.ntp_to_udp_ratio = ntp_to_udp_ratio;
	}
	public void setRecord_creation_num(int record_creation_num) {
		this.record_creation_num = record_creation_num;
	}
	public void setSyn_dst_large_20(int syn_dst_large_20) {
		this.syn_dst_large_20 = syn_dst_large_20;
	}
	public void setSyn_dst_large_20_ratio(double syn_dst_large_20_ratio) {
		this.syn_dst_large_20_ratio = syn_dst_large_20_ratio;
	}
	public void setSyn_dst_less_10(int syn_dst_less_10) {
		this.syn_dst_less_10 = syn_dst_less_10;
	}
	public void setSyn_dst_less_10_ratio(double syn_dst_less_10_ratio) {
		this.syn_dst_less_10_ratio = syn_dst_less_10_ratio;
	}
	public void setSyn_dst_less_20(int syn_dst_less_20) {
		this.syn_dst_less_20 = syn_dst_less_20;
	}
	public void setSyn_dst_less_20_ratio(double syn_dst_less_20_ratio) {
		this.syn_dst_less_20_ratio = syn_dst_less_20_ratio;
	}
	public void setSyn_dst_less_5(int syn_dst_less_5) {
		this.syn_dst_less_5 = syn_dst_less_5;
	}
	public void setSyn_dst_less_5_ratio(double syn_dst_less_5_ratio) {
		this.syn_dst_less_5_ratio = syn_dst_less_5_ratio;
	}
	public void setSyn_dst_top_20(String syn_dst_top_20) {
		this.syn_dst_top_20 = syn_dst_top_20;
	}
	public void setSyn_num(int syn_num) {
		this.syn_num = syn_num;
	}
	public void setSyn_src_large_20(int syn_src_large_20) {
		this.syn_src_large_20 = syn_src_large_20;
	}
	public void setSyn_src_large_20_ratio(double syn_src_large_20_ratio) {
		this.syn_src_large_20_ratio = syn_src_large_20_ratio;
	}
	public void setSyn_src_less_10(int syn_src_less_10) {
		this.syn_src_less_10 = syn_src_less_10;
	}
	public void setSyn_src_less_10_ratio(double syn_src_less_10_ratio) {
		this.syn_src_less_10_ratio = syn_src_less_10_ratio;
	}
	public void setSyn_src_less_20(int syn_src_less_20) {
		this.syn_src_less_20 = syn_src_less_20;
	}
	public void setSyn_src_less_20_ratio(double syn_src_less_20_ratio) {
		this.syn_src_less_20_ratio = syn_src_less_20_ratio;
	}
	public void setSyn_src_less_5(int syn_src_less_5) {
		this.syn_src_less_5 = syn_src_less_5;
	}
	public void setSyn_src_less_5_ratio(double syn_src_less_5_ratio) {
		this.syn_src_less_5_ratio = syn_src_less_5_ratio;
	}
	public void setSyn_src_top_20(String syn_src_top_20) {
		this.syn_src_top_20 = syn_src_top_20;
	}
	public void setSyn_to_flow_ratio(double syn_to_flow_ratio) {
		this.syn_to_flow_ratio = syn_to_flow_ratio;
	}
	public void setSyn_to_tcp_ratio(double syn_to_tcp_ratio) {
		this.syn_to_tcp_ratio = syn_to_tcp_ratio;
	}
	public void setUdp_dst_large_20(int udp_dst_large_20) {
		this.udp_dst_large_20 = udp_dst_large_20;
	}
	public void setUdp_dst_large_20_ratio(double udp_dst_large_20_ratio) {
		this.udp_dst_large_20_ratio = udp_dst_large_20_ratio;
	}
	public void setUdp_dst_less_10(int udp_dst_less_10) {
		this.udp_dst_less_10 = udp_dst_less_10;
	}
	public void setUdp_dst_less_10_ratio(double udp_dst_less_10_ratio) {
		this.udp_dst_less_10_ratio = udp_dst_less_10_ratio;
	}
	public void setUdp_dst_less_20(int udp_dst_less_20) {
		this.udp_dst_less_20 = udp_dst_less_20;
	}
	public void setUdp_dst_less_20_ratio(double udp_dst_less_20_ratio) {
		this.udp_dst_less_20_ratio = udp_dst_less_20_ratio;
	}
	public void setUdp_dst_less_5(int udp_dst_less_5) {
		this.udp_dst_less_5 = udp_dst_less_5;
	}
	public void setUdp_dst_less_5_ratio(double udp_dst_less_5_ratio) {
		this.udp_dst_less_5_ratio = udp_dst_less_5_ratio;
	}
	public void setUdp_dst_top_20(String udp_dst_top_20) {
		this.udp_dst_top_20 = udp_dst_top_20;
	}
	public void setUdp_num(int udp_num) {
		this.udp_num = udp_num;
	}
	public void setUdp_src_large_20(int udp_src_large_20) {
		this.udp_src_large_20 = udp_src_large_20;
	}
	public void setUdp_src_large_20_ratio(double udp_src_large_20_ratio) {
		this.udp_src_large_20_ratio = udp_src_large_20_ratio;
	}
	public void setUdp_src_less_10(int udp_src_less_10) {
		this.udp_src_less_10 = udp_src_less_10;
	}
	public void setUdp_src_less_10_ratio(double udp_src_less_10_ratio) {
		this.udp_src_less_10_ratio = udp_src_less_10_ratio;
	}
	public void setUdp_src_less_20(int udp_src_less_20) {
		this.udp_src_less_20 = udp_src_less_20;
	}
	public void setUdp_src_less_20_ratio(double udp_src_less_20_ratio) {
		this.udp_src_less_20_ratio = udp_src_less_20_ratio;
	}
	public void setUdp_src_less_5(int udp_src_less_5) {
		this.udp_src_less_5 = udp_src_less_5;
	}
	public void setUdp_src_less_5_ratio(double udp_src_less_5_ratio) {
		this.udp_src_less_5_ratio = udp_src_less_5_ratio;
	}
	public void setUdp_src_top_20(String udp_src_top_20) {
		this.udp_src_top_20 = udp_src_top_20;
	}
	public void setUdp_to_flow_ratio(double udp_to_flow_ratio) {
		this.udp_to_flow_ratio = udp_to_flow_ratio;
	}
	
	public String getTimeWindow() {
		return timeWindow;
	}
	public void setTimeWindow(String timeWindow) {
		this.timeWindow = timeWindow;
	}
	@Override
	public String toString() {
		return "DDoSMetricsVO [timeWindow=" + timeWindow + ", record_creation_num=" + record_creation_num + ", ntp_num="
				+ ntp_num + ", ntp_to_flow_ratio=" + ntp_to_flow_ratio + ", ntp_to_udp_ratio=" + ntp_to_udp_ratio
				+ ", ntp_src_top_20=" + ntp_src_top_20 + ", ntp_dst_top_20=" + ntp_dst_top_20 + ", syn_num=" + syn_num
				+ ", syn_to_flow_ratio=" + syn_to_flow_ratio + ", syn_to_tcp_ratio=" + syn_to_tcp_ratio
				+ ", syn_src_less_5=" + syn_src_less_5 + ", syn_src_less_10=" + syn_src_less_10 + ", syn_src_less_20="
				+ syn_src_less_20 + ", syn_src_large_20=" + syn_src_large_20 + ", syn_dst_less_5=" + syn_dst_less_5
				+ ", syn_dst_less_10=" + syn_dst_less_10 + ", syn_dst_less_20=" + syn_dst_less_20
				+ ", syn_dst_large_20=" + syn_dst_large_20 + ", syn_src_less_5_ratio=" + syn_src_less_5_ratio
				+ ", syn_src_less_10_ratio=" + syn_src_less_10_ratio + ", syn_src_less_20_ratio="
				+ syn_src_less_20_ratio + ", syn_src_large_20_ratio=" + syn_src_large_20_ratio
				+ ", syn_dst_less_5_ratio=" + syn_dst_less_5_ratio + ", syn_dst_less_10_ratio=" + syn_dst_less_10_ratio
				+ ", syn_dst_less_20_ratio=" + syn_dst_less_20_ratio + ", syn_dst_large_20_ratio="
				+ syn_dst_large_20_ratio + ", syn_src_top_20=" + syn_src_top_20 + ", syn_dst_top_20=" + syn_dst_top_20
				+ ", ack_num=" + ack_num + ", ack_to_flow_ratio=" + ack_to_flow_ratio + ", ack_to_tcp_ratio="
				+ ack_to_tcp_ratio + ", ack_src_less_5=" + ack_src_less_5 + ", ack_src_less_10=" + ack_src_less_10
				+ ", ack_src_less_20=" + ack_src_less_20 + ", ack_src_large_20=" + ack_src_large_20
				+ ", ack_dst_less_5=" + ack_dst_less_5 + ", ack_dst_less_10=" + ack_dst_less_10 + ", ack_dst_less_20="
				+ ack_dst_less_20 + ", ack_dst_large_20=" + ack_dst_large_20 + ", ack_src_less_5_ratio="
				+ ack_src_less_5_ratio + ", ack_src_less_10_ratio=" + ack_src_less_10_ratio + ", ack_src_less_20_ratio="
				+ ack_src_less_20_ratio + ", ack_src_large_20_ratio=" + ack_src_large_20_ratio
				+ ", ack_dst_less_5_ratio=" + ack_dst_less_5_ratio + ", ack_dst_less_10_ratio=" + ack_dst_less_10_ratio
				+ ", ack_dst_less_20_ratio=" + ack_dst_less_20_ratio + ", ack_dst_large_20_ratio="
				+ ack_dst_large_20_ratio + ", ack_src_top_20=" + ack_src_top_20 + ", ack_dst_top_20=" + ack_dst_top_20
				+ ", fin_num=" + fin_num + ", fin_to_flow_ratio=" + fin_to_flow_ratio + ", fin_to_tcp_ratio="
				+ fin_to_tcp_ratio + ", fin_src_less_5=" + fin_src_less_5 + ", fin_src_less_10=" + fin_src_less_10
				+ ", fin_src_less_20=" + fin_src_less_20 + ", fin_src_large_20=" + fin_src_large_20
				+ ", fin_dst_less_5=" + fin_dst_less_5 + ", fin_dst_less_10=" + fin_dst_less_10 + ", fin_dst_less_20="
				+ fin_dst_less_20 + ", fin_dst_large_20=" + fin_dst_large_20 + ", fin_src_less_5_ratio="
				+ fin_src_less_5_ratio + ", fin_src_less_10_ratio=" + fin_src_less_10_ratio + ", fin_src_less_20_ratio="
				+ fin_src_less_20_ratio + ", fin_src_large_20_ratio=" + fin_src_large_20_ratio
				+ ", fin_dst_less_5_ratio=" + fin_dst_less_5_ratio + ", fin_dst_less_10_ratio=" + fin_dst_less_10_ratio
				+ ", fin_dst_less_20_ratio=" + fin_dst_less_20_ratio + ", fin_dst_large_20_ratio="
				+ fin_dst_large_20_ratio + ", fin_src_top_20=" + fin_src_top_20 + ", fin_dst_top_20=" + fin_dst_top_20
				+ ", udp_num=" + udp_num + ", udp_to_flow_ratio=" + udp_to_flow_ratio + ", udp_src_less_5="
				+ udp_src_less_5 + ", udp_src_less_10=" + udp_src_less_10 + ", udp_src_less_20=" + udp_src_less_20
				+ ", udp_src_large_20=" + udp_src_large_20 + ", udp_dst_less_5=" + udp_dst_less_5 + ", udp_dst_less_10="
				+ udp_dst_less_10 + ", udp_dst_less_20=" + udp_dst_less_20 + ", udp_dst_large_20=" + udp_dst_large_20
				+ ", udp_src_less_5_ratio=" + udp_src_less_5_ratio + ", udp_src_less_10_ratio=" + udp_src_less_10_ratio
				+ ", udp_src_less_20_ratio=" + udp_src_less_20_ratio + ", udp_src_large_20_ratio="
				+ udp_src_large_20_ratio + ", udp_dst_less_5_ratio=" + udp_dst_less_5_ratio + ", udp_dst_less_10_ratio="
				+ udp_dst_less_10_ratio + ", udp_dst_less_20_ratio=" + udp_dst_less_20_ratio
				+ ", udp_dst_large_20_ratio=" + udp_dst_large_20_ratio + ", udp_src_top_20=" + udp_src_top_20
				+ ", udp_dst_top_20=" + udp_dst_top_20 + "]";
	}
	
	
}
