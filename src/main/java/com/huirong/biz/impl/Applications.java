package com.huirong.biz.impl;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import com.huirong.biz.BusinessLogic;
import com.huirong.storage.vo.NetflowRecord;
import com.huirong.util.NetflowSource;
import com.huirong.storage.StorageManager;
import com.huirong.util.TimeWindowComparator;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nanhuirong on 16-7-15.
 * 添加基于应用的过滤规则
 */






public class Applications implements BusinessLogic {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final int WINDOW_SIZE = 3;
    private StorageManager storageManager;
    private NetflowSource source;
    private List<String> tupleFieldList;


    String[] currentTimeWindow = new String[WINDOW_SIZE];
    Map<String, ApplicationsCatchObj> cache = new HashMap<String, ApplicationsCatchObj>();
    int numOfTimeWindow = 0;
    @Override
    public void execute(Tuple tuple, OutputCollector collector) {
        try{
            String timeFram = tuple.getStringByField(this.tupleFieldList.get(0));
            ByteArrayInputStream byteArray = new ByteArrayInputStream(tuple.getBinaryByField(this.tupleFieldList.get(1)));
            ObjectInputStream inputStream = new ObjectInputStream(byteArray);
            NetflowRecord nfRecord = (NetflowRecord) inputStream.readObject();
            inputStream.close();
            process(timeFram, nfRecord);
        }catch (Exception e){
            try{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
                bw.write(sw.toString() + "\n");
            }catch (IOException ioe){
                new RuntimeException(ioe);
            }
        }
    }

    void process(String timeWindow, NetflowRecord nf)throws Exception{
        //如果记录的端口不是特定端口, 不进行任何处理
        if (!(nf.getSrcPort().equals("53") || nf.getDstPort().equals("53")))
            return;
        //发现新窗口
        if (!timeWindowInCacheNow(timeWindow)){
            if (!isLegalTimeFrame(timeWindow, numOfTimeWindow, currentTimeWindow)){
                return;
            }
            ApplicationsCatchObj item = new ApplicationsCatchObj();
            this.cache.put(timeWindow, item);
            if (this.numOfTimeWindow < WINDOW_SIZE){
                this.currentTimeWindow[numOfTimeWindow ++] = timeWindow;
            }else {
                //丢掉最老的时间戳
                String toExpired = this.currentTimeWindow[0];
                this.currentTimeWindow[0] = timeWindow;
                ApplicationsCatchObj applicationsCatchObj = this.cache.get(toExpired);
                expireAttackInfo(toExpired, applicationsCatchObj);

            }
            //对时间窗口进行排序
            if (numOfTimeWindow == WINDOW_SIZE){
                Arrays.sort(this.currentTimeWindow, new TimeWindowComparator());
            }
        }
        //对符合条件的新纪录进行业务处理
        ApplicationsCatchObj catchObj = this.cache.get(timeWindow);
//        catchObj.setTime(timeWindow);
//        catchObj.setBytes(catchObj.getBytes() + nf.getBytes());
//        catchObj.setPackets(catchObj.getPackets() + nf.getPackets());
//        catchObj.setLinks(catchObj.getLinks() + 1);
        ApplicationsEvent event = new ApplicationsEvent(timeWindow, nf.getSrcIp(), nf.getSrcPort(), nf.getDstIp(), nf.getDstPort(),
                nf.getProtocol(), nf.getPackets(), nf.getBytes());
        HashSet<ApplicationsEvent> applicationsEvents = catchObj.getApplicationsEvents();
        applicationsEvents.add(event);
//        if (flag == true){
//            catchObj.setLinks(catchObj.getLinks() + 1);
//        }
//        catchObj.setApplicationsEvents(applicationsEvents);


    }

    //将丢掉的缓存进行存库
    public void expireAttackInfo(String timeWindow, ApplicationsCatchObj applicationsCatchObj){
        HashSet<ApplicationsEvent> applicationsEvents = applicationsCatchObj.getApplicationsEvents();
//        applicationsCatchObj.setTime(applicationsEvents.);
        long bytes = 0;
        int packets = 0;
        long links = 0;
        for (ApplicationsEvent event: applicationsEvents){
            bytes += event.getBytes();
            packets += event.getPackets();
            links += 1;
        }
        applicationsCatchObj.setTime(timeWindow);
        applicationsCatchObj.setBytes(bytes);
        applicationsCatchObj.setPackets(packets);
        applicationsCatchObj.setLinks(links);
        this.storageManager.addApplicationsMetrics(applicationsCatchObj, "53");
        this.storageManager.addApplicationsEvents(applicationsCatchObj, "53");
        this.cache.remove(timeWindow);
    }

    public boolean timeWindowInCacheNow(String timeFrame){
        for (String time: currentTimeWindow){
            if ((time != null) && time.equals(timeFrame)){
                return true;
            }
        }
        return false;
    }

    public boolean isLegalTimeFrame(String timeFrame, int num, String[] currentTimeWindow)throws ParseException{
        if (num < WINDOW_SIZE){
            return true;
        }
        Date d1 = sdf.parse(currentTimeWindow[0]);
        Date d2 = sdf.parse(timeFrame);
        return d1.before(d2);
    }

    @Override
    public void prepare(Map config, NetflowSource from, StorageManager storageManager, List<String> inputFieldList) {
        this.storageManager = storageManager;
        this.source = from;
        this.tupleFieldList = inputFieldList;

    }

    @Override
    public void cleanup() {

    }
}
