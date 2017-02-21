package com.huirong.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月2日
 */
public class TimeWindowComparator implements Comparator<String>{

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public int compare(String o1, String o2) {
		
		try {
			Date d1 = sdf.parse(o1);
			Date d2 = sdf.parse(o2);
			
			return d1.compareTo(d2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

}
