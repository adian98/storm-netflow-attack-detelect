package com.huirong.util;

import java.util.Comparator;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月3日
 */
public class TopIpComparator implements Comparator<String>{

	public int compare(String o1, String o2) {
		int num1 = Integer.parseInt((o1.split("#"))[1]);
		int num2 = Integer.parseInt((o2.split("#"))[1]);
		
		return num2 - num1;
	}

}
