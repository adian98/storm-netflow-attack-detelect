package com.huirong.biz.impl;

import java.util.HashSet;

/**
 * Created by nanhuirong on 16-7-15.
 */
public class ApplicationsCatchObj {
    private String time;
    private long bytes;
    private long packets;
    private long links;
    private HashSet<ApplicationsEvent> applicationsEvents = new HashSet<ApplicationsEvent>();

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getPackets() {
        return packets;
    }

    public void setPackets(long packets) {
        this.packets = packets;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public long getLinks() {
        return links;
    }

    public void setLinks(long links) {
        this.links = links;
    }

    public HashSet<ApplicationsEvent> getApplicationsEvents() {
        return applicationsEvents;
    }

    public void setApplicationsEvents(HashSet<ApplicationsEvent> applicationsEvents) {
        this.applicationsEvents = applicationsEvents;
    }

}
