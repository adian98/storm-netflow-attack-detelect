package com.huirong.storage.vo;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月4日
 */
public class RealtimeIndicatorsVO {

	private Integer id;
	private String timeFrame;
	private Double bps;
	private String activeInnerIps;
	private String activeOutterIps;
	private Double historyAccessorRatio;
	private String top5InnerIps;    // 这里偷懒了, 直接把最终结果拼成字符串
	private String top5OutterIps;
	private Integer recordCreationNum;
	private Integer bpp;
	private Integer pps;
	private Integer ppf;
	private Integer bpf;
	private Integer packetsLessThan500;
	private Integer pakcetsLessThan1000;
	private Integer packetsLessThan2000;
	private Integer packetsLargeThan2000;
	private Integer bytesLessThan20000;
	private Integer bytesLessThan100000;
	private Integer bytesLargeThan100000;
	private Integer durationLessThan1;
	private Integer durationLessThan10;
	private Integer durationLargeThan10;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTimeFrame() {
		return timeFrame;
	}
	public void setTimeFrame(String timeFrame) {
		this.timeFrame = timeFrame;
	}
	public Double getBps() {
		return bps;
	}
	public void setBps(Double bps) {
		this.bps = bps;
	}
	public String getActiveInnerIps() {
		return activeInnerIps;
	}
	public void setActiveInnerIps(String activeInnerIps) {
		this.activeInnerIps = activeInnerIps;
	}
	public String getActiveOutterIps() {
		return activeOutterIps;
	}
	public void setActiveOutterIps(String activeOutterIps) {
		this.activeOutterIps = activeOutterIps;
	}
	public Double getHistoryAccessorRatio() {
		return historyAccessorRatio;
	}
	public void setHistoryAccessorRatio(Double historyAccessorRatio) {
		this.historyAccessorRatio = historyAccessorRatio;
	}
	public String getTop5InnerIps() {
		return top5InnerIps;
	}
	public void setTop5InnerIps(String top5InnerIps) {
		this.top5InnerIps = top5InnerIps;
	}
	public String getTop5OutterIps() {
		return top5OutterIps;
	}
	public void setTop5OutterIps(String top5OutterIps) {
		this.top5OutterIps = top5OutterIps;
	}
	public Integer getRecordCreationNum() {
		return recordCreationNum;
	}
	public void setRecordCreationNum(Integer recordCreationNum) {
		this.recordCreationNum = recordCreationNum;
	}
	public Integer getBpp() {
		return bpp;
	}
	public void setBpp(Integer bpp) {
		this.bpp = bpp;
	}
	public Integer getPps() {
		return pps;
	}
	public void setPps(Integer pps) {
		this.pps = pps;
	}
	public Integer getPpf() {
		return ppf;
	}
	public void setPpf(Integer ppf) {
		this.ppf = ppf;
	}
	public Integer getBpf() {
		return bpf;
	}
	public void setBpf(Integer bpf) {
		this.bpf = bpf;
	}
	public Integer getPacketsLessThan500() {
		return packetsLessThan500;
	}
	public void setPacketsLessThan500(Integer packetsLessThan500) {
		this.packetsLessThan500 = packetsLessThan500;
	}
	public Integer getPakcetsLessThan1000() {
		return pakcetsLessThan1000;
	}
	public void setPakcetsLessThan1000(Integer pakcetsLessThan1000) {
		this.pakcetsLessThan1000 = pakcetsLessThan1000;
	}
	public Integer getPacketsLessThan2000() {
		return packetsLessThan2000;
	}
	public void setPacketsLessThan2000(Integer packetsLessThan2000) {
		this.packetsLessThan2000 = packetsLessThan2000;
	}
	public Integer getPacketsLargeThan2000() {
		return packetsLargeThan2000;
	}
	public void setPacketsLargeThan2000(Integer packetsLargeThan2000) {
		this.packetsLargeThan2000 = packetsLargeThan2000;
	}
	public Integer getBytesLessThan20000() {
		return bytesLessThan20000;
	}
	public void setBytesLessThan20000(Integer bytesLessThan20000) {
		this.bytesLessThan20000 = bytesLessThan20000;
	}
	public Integer getBytesLessThan100000() {
		return bytesLessThan100000;
	}
	public void setBytesLessThan100000(Integer bytesLessThan100000) {
		this.bytesLessThan100000 = bytesLessThan100000;
	}
	public Integer getBytesLargeThan100000() {
		return bytesLargeThan100000;
	}
	public void setBytesLargeThan100000(Integer bytesLargeThan100000) {
		this.bytesLargeThan100000 = bytesLargeThan100000;
	}
	public Integer getDurationLessThan1() {
		return durationLessThan1;
	}
	public void setDurationLessThan1(Integer durationLessThan1) {
		this.durationLessThan1 = durationLessThan1;
	}
	public Integer getDurationLessThan10() {
		return durationLessThan10;
	}
	public void setDurationLessThan10(Integer durationLessThan10) {
		this.durationLessThan10 = durationLessThan10;
	}
	public Integer getDurationLargeThan10() {
		return durationLargeThan10;
	}
	public void setDurationLargeThan10(Integer durationLargeThan10) {
		this.durationLargeThan10 = durationLargeThan10;
	}
	
	
	
}
