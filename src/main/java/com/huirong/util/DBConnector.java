package com.huirong.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月3日
 */
public class DBConnector {

	public final static String MYSQL_CLASS_NAME = "com.mysql.jdbc.Driver";
	public final static String JDBC_URL = "jdbc:mysql://202.113.76.229:3306/yaoxin";
	public final static String MYSQL_USER_NAME = "root";
	public final static String MYSQL_PASSWD = "sanyaolingb";
	
	public static Connection getConn(){
		try {
			Class.forName(MYSQL_CLASS_NAME);
			Connection conn = DriverManager.getConnection(JDBC_URL, MYSQL_USER_NAME, MYSQL_PASSWD);
			
			return conn;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/log.txt", true));
				bw.write(e.getMessage());
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
		
		return null;
	}
	
	
	public static void closeConn(Connection conn, Statement s, ResultSet r){
		try {
			if(r != null){
				r.close();
			}
			
			if(s != null)
				s.close();
			
			if(conn != null){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
