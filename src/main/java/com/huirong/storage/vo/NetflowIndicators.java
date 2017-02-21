package com.huirong.storage.vo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月4日
 */
public class NetflowIndicators {

	long packetsCount = 0L;
	long bytesCount = 0L;
	int flowsCount = 0;
	int recordsCount = 0;
	Set<String> activeInnerIp = new HashSet<String>();
	Set<String> activeOutterIp = new HashSet<String>();
	Map<String, Long> bytesPerInnerIp = new HashMap<String, Long>();
	Map<String, Long> bytesPerOutterIp = new HashMap<String, Long>();
	
	// 针对flow部分指标的区间分布统计
	int packetsLessThan500 = 0;
	int pakcetsLessThan1000 = 0;
	int packetsLessThan2000 = 0;
	int packetsLargeThan2000 = 0;
	
	int bytesLessThan20000 = 0;
	int bytesLessThan100000 = 0;
	int bytesLargeThan100000 = 0;
	
	int durationLessThan1 = 0;
	int durationLessThan10 = 0;
	int durationLargeThan10 = 0;
	
	// 用于统计双向指标
	boolean backwardInfoFilled = false;
	boolean forwardInfoFilled = false;
	String timeFrame;
	
	public String getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(String timeFrame) {
		this.timeFrame = timeFrame;
	}

	public boolean isBackwardInfoFilled() {
		return backwardInfoFilled;
	}

	public void setBackwardInfoFilled(boolean backwardInfoFilled) {
		this.backwardInfoFilled = backwardInfoFilled;
	}


	public boolean isForwardInfoFilled() {
		return forwardInfoFilled;
	}


	public void setForwardInfoFilled(boolean forwardInfoFilled) {
		this.forwardInfoFilled = forwardInfoFilled;
	}


	public long getPacketsCount() {
		return packetsCount;
	}


	public void setPacketsCount(long packetsCount) {
		this.packetsCount = packetsCount;
	}


	public long getBytesCount() {
		return bytesCount;
	}


	public void setBytesCount(long bytesCount) {
		this.bytesCount = bytesCount;
	}


	public int getFlowsCount() {
		return flowsCount;
	}


	public void setFlowsCount(int flowsCount) {
		this.flowsCount = flowsCount;
	}


	public int getRecordsCount() {
		return recordsCount;
	}


	public void setRecordsCount(int recordsCount) {
		this.recordsCount = recordsCount;
	}


	public Set<String> getActiveInnerIp() {
		return activeInnerIp;
	}


	public void setActiveInnerIp(Set<String> activeInnerIp) {
		this.activeInnerIp = activeInnerIp;
	}


	public Set<String> getActiveOutterIp() {
		return activeOutterIp;
	}


	public void setActiveOutterIp(Set<String> activeOutterIp) {
		this.activeOutterIp = activeOutterIp;
	}


	public Map<String, Long> getBytesPerInnerIp() {
		return bytesPerInnerIp;
	}


	public void setBytesPerInnerIp(Map<String, Long> bytesPerInnerIp) {
		this.bytesPerInnerIp = bytesPerInnerIp;
	}


	public Map<String, Long> getBytesPerOutterIp() {
		return bytesPerOutterIp;
	}


	public void setBytesPerOutterIp(Map<String, Long> bytesPerOutterIp) {
		this.bytesPerOutterIp = bytesPerOutterIp;
	}


	public int getPacketsLessThan500() {
		return packetsLessThan500;
	}


	public void setPacketsLessThan500(int packetsLessThan500) {
		this.packetsLessThan500 = packetsLessThan500;
	}


	public int getPakcetsLessThan1000() {
		return pakcetsLessThan1000;
	}


	public void setPakcetsLessThan1000(int pakcetsLessThan1000) {
		this.pakcetsLessThan1000 = pakcetsLessThan1000;
	}


	public int getPacketsLessThan2000() {
		return packetsLessThan2000;
	}


	public void setPacketsLessThan2000(int packetsLessThan2000) {
		this.packetsLessThan2000 = packetsLessThan2000;
	}


	public int getPacketsLargeThan2000() {
		return packetsLargeThan2000;
	}


	public void setPacketsLargeThan2000(int packetsLargeThan2000) {
		this.packetsLargeThan2000 = packetsLargeThan2000;
	}


	public int getBytesLessThan20000() {
		return bytesLessThan20000;
	}


	public void setBytesLessThan20000(int bytesLessThan20000) {
		this.bytesLessThan20000 = bytesLessThan20000;
	}


	public int getBytesLessThan100000() {
		return bytesLessThan100000;
	}


	public void setBytesLessThan100000(int bytesLessThan100000) {
		this.bytesLessThan100000 = bytesLessThan100000;
	}


	public int getBytesLargeThan100000() {
		return bytesLargeThan100000;
	}


	public void setBytesLargeThan100000(int bytesLargeThan100000) {
		this.bytesLargeThan100000 = bytesLargeThan100000;
	}


	public int getDurationLessThan1() {
		return durationLessThan1;
	}


	public void setDurationLessThan1(int durationLessThan1) {
		this.durationLessThan1 = durationLessThan1;
	}


	public int getDurationLessThan10() {
		return durationLessThan10;
	}


	public void setDurationLessThan10(int durationLessThan10) {
		this.durationLessThan10 = durationLessThan10;
	}


	public int getDurationLargeThan10() {
		return durationLargeThan10;
	}


	public void setDurationLargeThan10(int durationLargeThan10) {
		this.durationLargeThan10 = durationLargeThan10;
	}


	@Override
	public String toString(){
		return "packetsCount = " + packetsCount + ", bytesCount = " + bytesCount + ", flowsCount = " + flowsCount
				+ ", recordsCount = " + recordsCount + ", activeInnerIp = " + activeInnerIp.size() + ", activeOutterIp = "
				+ activeOutterIp.size() + ", bytesPerInnerIp " + bytesPerInnerIp.size() + ", bytesPerOutterIp = "
				+ bytesPerOutterIp.size() + ", packetsLessThan500 = " + packetsLessThan500 + ", pakcetsLessThan1000 = "
				+ pakcetsLessThan1000 + ", packetsLessThan2000 = " + packetsLessThan2000 + ", packetsLargeThan2000 = "
				+ packetsLargeThan2000 + ", bytesLessThan20000 = " + bytesLessThan20000 + ", bytesLessThan100000 = "
				+ bytesLessThan100000 + ", bytesLargeThan100000 = " + bytesLargeThan100000 + ", durationLessThan1 = "
				+ durationLessThan1 + ", durationLessThan10 = " + durationLessThan10 + ", durationLargeThan10 = "
				+ durationLargeThan10;
	}
}
