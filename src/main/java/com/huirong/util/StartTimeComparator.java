package com.huirong.util;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.huirong.storage.vo.TimeDurationPair;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年12月1日
 */
public class StartTimeComparator implements Comparator<TimeDurationPair>{

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public int compare(TimeDurationPair o1, TimeDurationPair o2) {
		try {
			Date d1 = sdf.parse(o1.getTime());
			Date d2 = sdf.parse(o2.getTime());
			
			return d1.compareTo(d2);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

}
