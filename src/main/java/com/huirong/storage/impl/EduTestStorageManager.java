package com.huirong.storage.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.huirong.biz.impl.ApplicationsCatchObj;
import com.huirong.storage.vo.AttackEvent;
import com.huirong.storage.vo.BlacklistIpSession;
import com.huirong.util.DBConnector;
import com.huirong.storage.StorageManager;
import com.huirong.storage.vo.DDoSMetricsVO;
import com.huirong.storage.vo.UnidirectionalSessionRecord;

/**
 * 
 * 在MySQL上存储测试数据
 * 
 * @author yaoxin   
 * 
 * 2015年12月13日
 */
public class EduTestStorageManager implements StorageManager{
	@Override
	public void addApplicationsMetrics(ApplicationsCatchObj catchObj, String port) {

	}

	@Override
	public void addApplicationsEvents(ApplicationsCatchObj catchObj, String port) {

	}

	public final static String LOAD_BLACKLIST = "select ip from edu_black_list";
	public final static String ADD_BACKWARD_SESSION_RECORD = 
			"insert into edu_unidirectional_backward_session_record(start_time, end_time, duration, protocol, srcIp, srcPort,"
			+ "dstIp, dstPort, packets, bytes, bps, bpp, pps, insert_time) values ";
	public final static String ADD_FORWARD_SESSION_RECORD = 
			"insert into edu_unidirectional_forward_session_record(start_time, end_time, duration, protocol, srcIp, srcPort,"
			+ "dstIp, dstPort, packets, bytes, bps, bpp, pps, insert_time) values ";
	public final static String ADD_DDOS_METRICS = "insert into edu_realtime_ddos_metrics(time_window, record_creation_num, ntp_num, "
			+ "ntp_to_flow_ratio, ntp_to_udp_ratio, ntp_src_top_20, ntp_dst_top_20, syn_num, syn_to_flow_ratio, syn_to_tcp_ratio, "
			+ "syn_src_less_5, syn_src_less_10, syn_src_less_20, syn_src_large_20, syn_dst_less_5, syn_dst_less_10, "
			+ "syn_dst_less_20, syn_dst_large_20, syn_src_less_5_ratio, syn_src_less_10_ratio, syn_src_less_20_ratio, syn_src_large_20_ratio, "
			+ "syn_dst_less_5_ratio, syn_dst_less_10_ratio, syn_dst_less_20_ratio, syn_dst_large_20_ratio, syn_src_top_20, "
			+ "syn_dst_top_20, ack_num, ack_to_flow_ratio, ack_to_tcp_ratio, ack_src_less_5, ack_src_less_10, ack_src_less_20, "
			+ "ack_src_large_20, ack_dst_less_5, ack_dst_less_10, ack_dst_less_20, ack_dst_large_20, ack_src_less_5_ratio, "
			+ "ack_src_less_10_ratio, ack_src_less_20_ratio, ack_src_large_20_ratio, ack_dst_less_5_ratio, ack_dst_less_10_ratio, "
			+ "ack_dst_less_20_ratio, ack_dst_large_20_ratio, ack_src_top_20, ack_dst_top_20, fin_num, fin_to_flow_ratio, "
			+ "fin_to_tcp_ratio, fin_src_less_5, fin_src_less_10, fin_src_less_20, fin_src_large_20, fin_dst_less_5, fin_dst_less_10, "
			+ "fin_dst_less_20, fin_dst_large_20, fin_src_less_5_ratio, fin_src_less_10_ratio, fin_src_less_20_ratio, fin_src_large_20_ratio, "
			+ "fin_dst_less_5_ratio, fin_dst_less_10_ratio, fin_dst_less_20_ratio, fin_dst_large_20_ratio, fin_src_top_20, fin_dst_top_20, "
			+ "udp_num, udp_to_flow_ratio, udp_src_less_5, udp_src_less_10, udp_src_less_20, udp_src_large_20, udp_dst_less_5, "
			+ "udp_dst_less_10, udp_dst_less_20, udp_dst_large_20, udp_src_less_5_ratio, udp_src_less_10_ratio, udp_src_less_20_ratio, "
			+ "udp_src_large_20_ratio, udp_dst_less_5_ratio, udp_dst_less_10_ratio, udp_dst_less_20_ratio, udp_dst_large_20_ratio, "
			+ "udp_src_top_20, udp_dst_top_20, icmp_num, icmp_to_flow_ratio, icmp_src_less_5, icmp_src_less_10, icmp_src_less_20, "
			+ "icmp_src_large_20, icmp_dst_less_5, icmp_dst_less_10, icmp_dst_less_20, icmp_dst_large_20, icmp_src_less_5_ratio, "
			+ "icmp_src_less_10_ratio, icmp_src_less_20_ratio, icmp_src_large_20_ratio, icmp_dst_less_5_ratio, icmp_dst_less_10_ratio, "
			+ "icmp_dst_less_20_ratio, icmp_dst_large_20_ratio, icmp_src_top_20, icmp_dst_top_20, inval_flag_num, inval_flag_to_flow_ratio, "
			+ "inval_flag_to_tcp_ratio, inval_flag_src_less_5, inval_flag_src_less_10, inval_flag_src_less_20, inval_flag_src_large_20, "
			+ "inval_flag_dst_less_5, inval_flag_dst_less_10, inval_flag_dst_less_20, inval_flag_dst_large_20, inval_flag_src_less_5_ratio, "
			+ "inval_flag_src_less_10_ratio, inval_flag_src_less_20_ratio, inval_flag_src_large_20_ratio, inval_flag_dst_less_5_ratio, "
			+ "inval_flag_dst_less_10_ratio, inval_flag_dst_less_20_ratio, inval_flag_dst_large_20_ratio, inval_flag_src_top_20, "
			+ "inval_flag_dst_top_20) values";
	
	public final static String ADD_ATTACK_EVENTS = "insert into edu_attack_event(date, srcIp, srcPort, dstIp, dstPort, protocal, "
			+ "flag, typeDescription, typeCode, insertTime) values ";

	public Set<String> getBlacklist() {
		Set<String> ret = new HashSet<String>();
		
		try {
			Connection conn = DBConnector.getConn();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(LOAD_BLACKLIST);
			
			if(rs != null){
				while(rs.next()){
					ret.add(rs.getString(1));
				}
			}
			DBConnector.closeConn(conn, statement, rs);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.write(LOAD_BLACKLIST + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return ret;
	}

	public void addBlacklistIpSession(List<BlacklistIpSession> ls) {
		StringBuilder sb = new StringBuilder();
		try {
			Connection conn = DBConnector.getConn();
			Statement statement = conn.createStatement();
			sb.append("insert into edu_blacklist_ip_session(ip, m_date, duration, protocol, srcIp, srcPort, dstIp, dstPort,"
					+ "flags, tos, packets, bytes, pps, bps, bytespp, flows) values ");
			
			for(BlacklistIpSession r : ls){
				sb.append("('" + r.getIp() + "','" + r.getRecord().getDate() + "'," + r.getRecord().getDuration() + ",'" + r.getRecord().getProtocol() + "','" + r.getRecord().getSrcIp() + 
						"','" + r.getRecord().getSrcPort() + "','" + r.getRecord().getDstIp() + "','" + r.getRecord().getDstPort() + 
						"','" + r.getRecord().getFlags() + "','" + r.getRecord().getTos() + "'," + r.getRecord().getPackets() + "," + r.getRecord().getBytes() + 
						"," + r.getRecord().getPps() + "," + r.getRecord().getBps() + "," + r.getRecord().getBytespp() + "," + r.getRecord().getFlows() + "),");
			}
			
			if(sb.lastIndexOf(",") == sb.length() - 1)
				sb.deleteCharAt(sb.length() - 1);
			
			statement.execute(sb.toString());
			
			DBConnector.closeConn(conn, statement, null);
			
		} catch (Exception e) {
			// TODO: handle exception
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.write(sb.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}


	public void addBackwardSessionRecord(List<UnidirectionalSessionRecord> ls) {
		StringBuilder sb = new StringBuilder();
		try {
			Connection conn = DBConnector.getConn();
			Statement statement = conn.createStatement();
			sb.append(ADD_BACKWARD_SESSION_RECORD);
			
			for(UnidirectionalSessionRecord u : ls){
				sb.append("(")
				  .append("'" + u.getStartTime() + "',")
				  .append("'" + u.getEndTime() + "',")
				  .append(u.getDuration() + ",")
				  .append("'" + u.getProtocol() + "',")
				  .append("'" + u.getSrcIp() + "',")
				  .append("'" + u.getSrcPort() + "',")
				  .append("'" + u.getDstIp() + "',")
				  .append("'" + u.getDstPort() + "',")
				  .append(u.getPackets() + ",")
				  .append(u.getBytes() + ",")
				  .append(u.getBps() + ",")
				  .append(u.getBpp() + ",")
				  .append(u.getPps() + ", now()),");
			}
			
			if(sb.lastIndexOf(",") == sb.length() - 1)
				sb.deleteCharAt(sb.length() - 1);
			
			statement.execute(sb.toString());
			
			DBConnector.closeConn(conn, statement, null);
		} catch (Exception e) {
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.write(sb.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void addForwardSessionRecord(List<UnidirectionalSessionRecord> ls) {
		StringBuilder sb = new StringBuilder();
		try {
			Connection conn = DBConnector.getConn();
			Statement statement = conn.createStatement();
			sb.append(ADD_FORWARD_SESSION_RECORD);
			
			for(UnidirectionalSessionRecord u : ls){
				sb.append("(")
				  .append("'" + u.getStartTime() + "',")
				  .append("'" + u.getEndTime() + "',")
				  .append(u.getDuration() + ",")
				  .append("'" + u.getProtocol() + "',")
				  .append("'" + u.getSrcIp() + "',")
				  .append("'" + u.getSrcPort() + "',")
				  .append("'" + u.getDstIp() + "',")
				  .append("'" + u.getDstPort() + "',")
				  .append(u.getPackets() + ",")
				  .append(u.getBytes() + ",")
				  .append(u.getBps() + ",")
				  .append(u.getBpp() + ",")
				  .append(u.getPps() + ", now()),");
			}
			
			if(sb.lastIndexOf(",") == sb.length() - 1)
				sb.deleteCharAt(sb.length() - 1);
			
			statement.execute(sb.toString());
			
			DBConnector.closeConn(conn, statement, null);
		} catch (Exception e) {
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.write(sb.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void addDDoSMetrics(DDoSMetricsVO vo) {
		StringBuilder sb = new StringBuilder();
		try {
			Connection conn = DBConnector.getConn();
			Statement statement = conn.createStatement();
			sb.append(ADD_DDOS_METRICS)
			  .append("(")
			  .append("'" + vo.getTimeWindow() + "',")
			  .append(vo.getRecord_creation_num() + ",")
			  .append(vo.getNtp_num() + ",")
			  .append(vo.getNtp_to_flow_ratio() + ",")
			  .append(vo.getNtp_to_udp_ratio() + ",")
			  .append("'" + vo.getNtp_src_top_20() + "',")
			  .append("'" + vo.getNtp_dst_top_20() + "',")
			  .append(vo.getSyn_num() + ",")
			  .append(vo.getSyn_to_flow_ratio() + ",")
			  .append(vo.getSyn_to_tcp_ratio() + ",")
			  .append(vo.getSyn_src_less_5() + ",")
			  .append(vo.getSyn_src_less_10() + ",")
			  .append(vo.getSyn_src_less_20() + ",")
			  .append(vo.getSyn_src_large_20() + ",")
			  .append(vo.getSyn_dst_less_5() + ",")
			  .append(vo.getSyn_dst_less_10() + ",")
			  .append(vo.getSyn_dst_less_20() + ",")
			  .append(vo.getSyn_dst_large_20() + ",")
			  .append(vo.getSyn_src_less_5_ratio() + ",")
			  .append(vo.getSyn_src_less_10_ratio() + ",")
			  .append(vo.getSyn_src_less_20_ratio() + ",")
			  .append(vo.getSyn_src_large_20_ratio() + ",")
			  .append(vo.getSyn_dst_less_5_ratio() + ",")
			  .append(vo.getSyn_dst_less_10_ratio() + ",")
			  .append(vo.getSyn_dst_less_20_ratio() + ",")
			  .append(vo.getSyn_dst_large_20_ratio() + ",")
			  .append("'" + vo.getSyn_src_top_20() + "',")
			  .append("'" + vo.getSyn_dst_top_20() + "',")
			  .append(vo.getAck_num() + ",")
			  .append(vo.getAck_to_flow_ratio() + ",")
			  .append(vo.getAck_to_tcp_ratio() + ",")
			  .append(vo.getAck_src_less_5() + ",")
			  .append(vo.getAck_src_less_10() + ",")
			  .append(vo.getAck_src_less_20() + ",")
			  .append(vo.getAck_src_large_20() + ",")
			  .append(vo.getAck_dst_less_5() + ",")
			  .append(vo.getAck_dst_less_10() + ",")
			  .append(vo.getAck_dst_less_20() + ",")
			  .append(vo.getAck_dst_large_20() + ",")
			  .append(vo.getAck_src_less_5_ratio() + ",")
			  .append(vo.getAck_src_less_10_ratio() + ",")
			  .append(vo.getAck_src_less_20_ratio() + ",")
			  .append(vo.getAck_src_large_20_ratio() + ",")
			  .append(vo.getAck_dst_less_5_ratio() + ",")
			  .append(vo.getAck_dst_less_10_ratio() + ",")
			  .append(vo.getAck_dst_less_20_ratio() + ",")
			  .append(vo.getAck_dst_large_20_ratio() + ",")
			  .append("'" + vo.getAck_src_top_20() + "',")
			  .append("'" + vo.getAck_dst_top_20() + "',")
			  .append(vo.getFin_num() + ",")
			  .append(vo.getFin_to_flow_ratio() + ",")
			  .append(vo.getFin_to_tcp_ratio() + ",")
			  .append(vo.getFin_src_less_5() + ",")
			  .append(vo.getFin_src_less_10() + ",")
			  .append(vo.getFin_src_less_20() + ",")
			  .append(vo.getFin_src_large_20() + ",")
			  .append(vo.getFin_dst_less_5() + ",")
			  .append(vo.getFin_dst_less_10() + ",")
			  .append(vo.getFin_dst_less_20() + ",")
			  .append(vo.getFin_dst_large_20() + ",")
			  .append(vo.getFin_src_less_5_ratio() + ",")
			  .append(vo.getFin_src_less_10_ratio() + ",")
			  .append(vo.getFin_src_less_20_ratio() + ",")
			  .append(vo.getFin_src_large_20_ratio() + ",")
			  .append(vo.getFin_dst_less_5_ratio() + ",")
			  .append(vo.getFin_dst_less_10_ratio() + ",")
			  .append(vo.getFin_dst_less_20_ratio() + ",")
			  .append(vo.getFin_dst_large_20_ratio() + ",")
			  .append("'" + vo.getFin_src_top_20() + "',")
			  .append("'" + vo.getFin_dst_top_20() + "',")
			  .append(vo.getUdp_num() + ",")
			  .append(vo.getUdp_to_flow_ratio() + ",")
			  .append(vo.getUdp_src_less_5() + ",")
			  .append(vo.getUdp_src_less_10() + ",")
			  .append(vo.getUdp_src_less_20() + ",")
			  .append(vo.getUdp_src_large_20() + ",")
			  .append(vo.getUdp_dst_less_5() + ",")
			  .append(vo.getUdp_dst_less_10() + ",")
			  .append(vo.getUdp_dst_less_20() + ",")
			  .append(vo.getUdp_dst_large_20() + ",")
			  .append(vo.getUdp_src_less_5_ratio() + ",")
			  .append(vo.getUdp_src_less_10_ratio() + ",")
			  .append(vo.getUdp_src_less_20_ratio() + ",")
			  .append(vo.getUdp_src_large_20_ratio() + ",")
			  .append(vo.getUdp_dst_less_5_ratio() + ",")
			  .append(vo.getUdp_dst_less_10_ratio() + ",")
			  .append(vo.getUdp_dst_less_20_ratio() + ",")
			  .append(vo.getUdp_dst_large_20_ratio() + ",")
			  .append("'" + vo.getUdp_src_top_20() + "',")
			  .append("'" + vo.getUdp_dst_top_20() + "',")
			  .append(vo.getIcmp_num() + ",")
			  .append(vo.getIcmp_to_flow_ratio() + ",")
			  .append(vo.getIcmp_src_less_5() + ",")
			  .append(vo.getIcmp_src_less_10() + ",")
			  .append(vo.getIcmp_src_less_20() + ",")
			  .append(vo.getIcmp_src_large_20() + ",")
			  .append(vo.getIcmp_dst_less_5() + ",")
			  .append(vo.getIcmp_dst_less_10() + ",")
			  .append(vo.getIcmp_dst_less_20() + ",")
			  .append(vo.getIcmp_dst_large_20() + ",")
			  .append(vo.getIcmp_src_less_5_ratio() + ",")
			  .append(vo.getIcmp_src_less_10_ratio() + ",")
			  .append(vo.getIcmp_src_less_20_ratio() + ",")
			  .append(vo.getIcmp_src_large_20_ratio() + ",")
			  .append(vo.getIcmp_dst_less_5_ratio() + ",")
			  .append(vo.getIcmp_dst_less_10_ratio() + ",")
			  .append(vo.getIcmp_dst_less_20_ratio() + ",")
			  .append(vo.getIcmp_dst_large_20_ratio() + ",")
			  .append("'" + vo.getIcmp_src_top_20() + "',")
			  .append("'" + vo.getIcmp_dst_top_20() + "',")
			  .append(vo.getInval_flag_num() + ",")
			  .append(vo.getInval_flag_to_flow_ratio() + ",")
			  .append(vo.getInval_flag_to_tcp_ratio() + ",")
			  .append(vo.getInval_flag_src_less_5() + ",")
			  .append(vo.getInval_flag_src_less_10() + ",")
			  .append(vo.getInval_flag_src_less_20() + ",")
			  .append(vo.getInval_flag_src_large_20() + ",")
			  .append(vo.getInval_flag_dst_less_5() + ",")
			  .append(vo.getInval_flag_dst_less_10() + ",")
			  .append(vo.getInval_flag_dst_less_20() + ",")
			  .append(vo.getInval_flag_dst_large_20() + ",")
			  .append(vo.getInval_flag_src_less_5_ratio() + ",")
			  .append(vo.getInval_flag_src_less_10_ratio() + ",")
			  .append(vo.getInval_flag_src_less_20_ratio() + ",")
			  .append(vo.getInval_flag_src_large_20_ratio() + ",")
			  .append(vo.getInval_flag_dst_less_5_ratio() + ",")
			  .append(vo.getInval_flag_dst_less_10_ratio() + ",")
			  .append(vo.getInval_flag_dst_less_20_ratio() + ",")
			  .append(vo.getInval_flag_dst_large_20_ratio() + ",")
			  .append("'" + vo.getInval_flag_src_top_20() + "',")
			  .append("'" + vo.getInval_flag_dst_top_20() + "'")
			  .append(")");
			
			statement.execute(sb.toString());
			
			DBConnector.closeConn(conn, statement, null);
		} catch (Exception e) {
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.write(sb.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

	public void addAttackEvents(List<AttackEvent> ls) {
		StringBuilder sb = new StringBuilder();
		try {
			
			if(ls != null && ls.size() > 0){
				
				Connection conn = DBConnector.getConn();
				Statement statement = conn.createStatement();
				sb.append(ADD_ATTACK_EVENTS);
				
				for(AttackEvent u : ls){
					sb.append("(")
					  .append("'" + u.getDate() + "',")
					  .append("'" + u.getSrcIp() + "',")
					  .append("'" + u.getSrcPort() + "',")
					  .append("'" + u.getDstIp() + "',")
					  .append("'" + u.getDstPort() + "',")
					  .append("'" + u.getProtocal() + "',")
					  .append("'" + u.getFlag() + "',")
					  .append("'" + u.getType().getDescription() + "',")
					  .append(u.getType().getCode() + ", now()),");
				}
				
				if(sb.lastIndexOf(",") == sb.length() - 1)
					sb.deleteCharAt(sb.length() - 1);
				
				statement.execute(sb.toString());
				
				DBConnector.closeConn(conn, statement, null);
			}
			
			
		} catch (Exception e) {
			try {
				StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        e.printStackTrace(pw);
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
				bw.write(sw.toString() + "\n");
				bw.write(sb.toString() + "\n");
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}