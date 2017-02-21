package com.huirong.util.script;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年12月1日
 */
public class FileEmitter {
	public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		String inDir = args[0];
		String outDir = args[1];
		
		FileEmitter fe = new FileEmitter();
		File[] ls = new File(inDir).listFiles();
		List<File> tmp = new ArrayList<File>();
		for(File file : ls){
			if(file.isFile() && file.getName().substring(0, 6).equals("nfcapd")){
				tmp.add(file);
			}
		}
		
		File[] target = tmp.toArray(new File[0]);
		Arrays.sort(target, fe.new NfComparator());
		
		for(File f : target){
			String cmd = "mv " + inDir + f.getName() + " " + outDir + f.getName();
			
			Process process = Runtime.getRuntime().exec(new String[]{"bash","-c",cmd});
			process.waitFor();
			
			Thread.sleep(1000);
		}
		
	}
	
	class NfComparator implements Comparator<File>{

		public int compare(File arg0, File arg1) {
			try {
				String date1 = arg0.getName().substring(7, 19);
				String date2 = arg1.getName().substring(7, 19);
				
				return sdf.parse(date1).compareTo(sdf.parse(date2));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return 0;
		}
		
	}

}
