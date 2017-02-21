package com.huirong.biz.impl;

/**
 * Created by nanhuirong on 16-7-15.
 */
public class ApplicationsEvent {
    private String time;
    private String srcIp;
    private String srcPort;
    private String dstIp;
    private String dstPort;
    private String protocol;
    private int packets;
    private long bytes;

    public ApplicationsEvent(String time, String srcIp, String srcPort, String dstIp, String dstPort, String protocol, int packets, long bytes) {
        this.time = time;
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
        this.protocol = protocol;
        this.bytes = bytes;
        this.packets = packets;
    }

    @Override
    public int hashCode() {
        return this.time.hashCode() +  this.srcIp.hashCode()  + this.dstIp.hashCode()  + this.protocol.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        ApplicationsEvent event = (ApplicationsEvent)obj;
        if (this.time.equals(event.time) &&this.srcIp.equals(event.srcIp)  && this.dstIp.equals(event.dstIp) && this.protocol.equals(event.protocol)){
            return true;
        }
        else
            return false;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
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
}
