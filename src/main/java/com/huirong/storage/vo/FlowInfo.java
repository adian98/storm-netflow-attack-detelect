package com.huirong.storage.vo;

import com.huirong.util.FlowDirectionEnum;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月4日
 */
public class FlowInfo {

	String innerIp;
	String innerPort;

	String outterIp;

	String outterPort;

	FlowDirectionEnum direction;

	public FlowInfo(String inip, String inport, String outip, String outport, FlowDirectionEnum direction){
		this.innerIp = inip;
		this.innerPort = inport;
		this.outterIp = outip;
		this.outterPort = outport;
		this.direction = direction;
	}

	public FlowDirectionEnum getDirection() {
		return direction;
	}

	public String getInnerIp() {
		return innerIp;
	}

	public String getInnerPort() {
		return innerPort;
	}

	public String getOutterIp() {
		return outterIp;
	}

	public String getOutterPort() {
		return outterPort;
	}

	public void setDirection(FlowDirectionEnum direction) {
		this.direction = direction;
	}
	public void setInnerIp(String innerIp) {
		this.innerIp = innerIp;
	}
	public void setInnerPort(String innerPort) {
		this.innerPort = innerPort;
	}
	public void setOutterIp(String outterIp) {
		this.outterIp = outterIp;
	}
	
	public void setOutterPort(String outterPort) {
		this.outterPort = outterPort;
	}
	
	@Override
	public String toString(){
		return "innerIp = " + innerIp + ", innerPort = " + innerPort + ", outterIp = " + outterIp + ", outterPort = "
				+ outterPort + ", direction = " + this.direction;
	}
}
