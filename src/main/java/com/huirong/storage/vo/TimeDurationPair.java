package com.huirong.storage.vo;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年12月1日
 */
public class TimeDurationPair {

	private String time;
	private double duration;
	private String endTimeStr;
	
	public String getEndTimeStr() {
		return endTimeStr;
	}
	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
}
