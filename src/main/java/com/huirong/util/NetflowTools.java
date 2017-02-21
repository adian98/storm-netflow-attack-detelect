package com.huirong.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.huirong.storage.vo.NetflowRecord;
import com.huirong.storage.vo.FlowInfo;


/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月14日
 */
public class NetflowTools {
	
	// tjut ip 段
	public final static Map<String, String> TJUT_IP_SEGMENT ;
	public final static List<String> TJUT_IP_LIST ;
	
	// 天津教育网ip段.(暂时不处理其他地区的教育网数据) 
	public final static Map<String, String> EDU_IP_SEGMENT;
	public final static List<String> EDU_IP_LIST;
	
	static{
		TJUT_IP_SEGMENT = new HashMap<String, String>();
		TJUT_IP_SEGMENT.put("202.113.64", "202.113.79");
		TJUT_IP_SEGMENT.put("59.67.144", "59.67.159");
		
		EDU_IP_SEGMENT = new HashMap<String, String>();
		EDU_IP_SEGMENT.put("58.207.48", "58.207.63");
		EDU_IP_SEGMENT.put("59.67.0", "59.67.191");
		EDU_IP_SEGMENT.put("202.113.0", "202.113.255");
		EDU_IP_SEGMENT.put("211.81.0", "211.81.63");
		
		// 为加快后续查询速度, 把上述手动配置的ip段转化为列表的形式
		TJUT_IP_LIST = new ArrayList<String>();
		for(Entry<String, String> e : TJUT_IP_SEGMENT.entrySet()){
			String begin = e.getKey();
			String end = e.getValue();
			
			String prefix = begin.substring(0, begin.lastIndexOf("."));
			int i = Integer.parseInt(begin.substring(begin.lastIndexOf(".") + 1));
			int until = Integer.parseInt(end.substring(end.lastIndexOf(".") + 1));
			
			for(; i <= until; i ++){
				TJUT_IP_LIST.add(prefix + "." + i);
			}
		}
		
		EDU_IP_LIST = new ArrayList<String>();
		for(Entry<String, String> e : EDU_IP_SEGMENT.entrySet()){
			String begin = e.getKey();
			String end = e.getValue();
			
			String prefix = begin.substring(0, begin.lastIndexOf("."));
			int i = Integer.parseInt(begin.substring(begin.lastIndexOf(".") + 1));
			int until = Integer.parseInt(end.substring(end.lastIndexOf(".") + 1));
			
			for(; i <= until; i ++){
				EDU_IP_LIST.add(prefix + "." + i);
			}
		}
		
	}
	
	public static FlowInfo recogniseEduFlow(NetflowRecord record){
		String dstSeg = record.getDstIp().substring(0, record.getDstIp().lastIndexOf("."));
		return doRecognise(EDU_IP_LIST, dstSeg, record);
	}
	
	public static FlowInfo recogniseTjutFlow(NetflowRecord record){
		String dstSeg = record.getDstIp().substring(0, record.getDstIp().lastIndexOf("."));
		return doRecognise(TJUT_IP_LIST, dstSeg, record);
	}
	
	private static FlowInfo doRecognise(List<String> segList, String dstSeg, NetflowRecord record){
		String innerIp = "";
		String innerPort = "";
		String outterIp = "";
		String outterPort = "";
		FlowDirectionEnum direction;  // 当前flow的方向
		
		if(segList.contains(dstSeg)){
			innerIp = record.getDstIp();
			innerPort = record.getDstPort();
			outterIp = record.getSrcIp();
			outterPort = record.getSrcPort();
			direction = FlowDirectionEnum.IN;
		}
		else {
			innerIp = record.getSrcIp();
			innerPort = record.getSrcPort();
			outterIp = record.getDstIp();
			outterPort = record.getDstPort();
			direction = FlowDirectionEnum.OUT;
		}
		
		return new FlowInfo(innerIp, innerPort, outterIp, outterPort, direction);
	}
	
	public static boolean belongToTjutNetworkSegment(String srcIp, String dstIp){
		String srcSeg = srcIp.substring(0, srcIp.lastIndexOf('.'));
		
		String dstSeg = dstIp.substring(0, dstIp.lastIndexOf('.'));
		
		if(TJUT_IP_LIST.contains(srcSeg) || TJUT_IP_LIST.contains(dstSeg))
			return true;
		
		return false;
	}
	
	public static boolean belongToEduNetworkSegment(String srcIp, String dstIp){
		String srcSeg = srcIp.substring(0, srcIp.lastIndexOf('.'));
		
		String dstSeg = dstIp.substring(0, dstIp.lastIndexOf('.'));
		
		if(EDU_IP_LIST.contains(srcSeg) || EDU_IP_LIST.contains(dstSeg))
			return true;
		
		return false;
	}
	
	public static void main(String[] args) {
		for(String s : TJUT_IP_LIST){
			System.out.println(s);
		}
		
		for(String s : EDU_IP_LIST){
			System.out.println(s);
		}
	}
}
