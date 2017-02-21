package com.huirong.util;

import java.text.ParseException;
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
public class EndTimeComparator implements Comparator<TimeDurationPair>{

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public int compare(TimeDurationPair arg0, TimeDurationPair arg1) {
		try {
			Date d1 = sdf.parse(arg0.getEndTimeStr());
			Date d2 = sdf.parse(arg1.getEndTimeStr());
			
			return d1.compareTo(d2);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
}
