package com.huirong.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import com.huirong.MainTopology;
import com.huirong.biz.BusinessLogic;
import com.huirong.storage.impl.TjutStorageManager;
import com.huirong.util.NetflowSource;
import com.huirong.biz.impl.Applications;
import com.huirong.storage.StorageManager;
import com.huirong.storage.impl.EduStorageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by nanhuirong on 16-7-17.
 */
public class ApplicationBolt implements IRichBolt {
    private static final long serialVersionUID = 1160564987892748177L;
    OutputCollector outputCollector;
    private List<BusinessLogic> biz = new ArrayList<BusinessLogic>();
    private NetflowSource dataSource;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        String ds = (String) map.get(MainTopology.DATA_SOURCE);
        StorageManager storageManager = null;
        if (ds.equalsIgnoreCase("tjut")){
            this.dataSource = NetflowSource.TJUT;
            storageManager = new TjutStorageManager();
        }else if (ds.equalsIgnoreCase("edu")){
            this.dataSource = NetflowSource.EDU;
            storageManager = new EduStorageManager();
        }

        //添加需要执行的业务逻辑
        biz.add(new Applications());
        for (BusinessLogic bl: this.biz){
            bl.prepare(map, this.dataSource, storageManager, Arrays.asList(NetflowParserBolt.TIME_FRAME_FIELD, NetflowParserBolt.OBJ_FIELD));
        }
    }

    @Override
    public void execute(Tuple tuple) {
        for (BusinessLogic bl: this.biz){
            bl.execute(tuple, outputCollector);
        }
        outputCollector.ack(tuple);
    }

    @Override
    public void cleanup() {
        for (BusinessLogic bl: this.biz){
            bl.cleanup();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
