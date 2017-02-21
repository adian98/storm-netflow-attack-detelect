package com.huirong.util.script;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author yaoxin   
 * 
 * 2016年1月29日
 */
public class HDFSUploader {

	public static void main(String[] args) {
		try {
			String in = args[0];
			String out = args[1];
			
			File[] ls = new File(in).listFiles();
			
			for(File file : ls){
				
				if(file.isFile() && file.getName().substring(0, 6).equals("nfcapd")){
					String tmpOutputDir = in + file.getName();
					System.out.println(tmpOutputDir);
					String cmd2 = " hdfs dfs -put " + tmpOutputDir + " " + out;
					Process process2 = Runtime.getRuntime().exec(new String[]{"bash","-c", cmd2});
					process2.waitFor();
				}
			}
			System.out.println("done.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
