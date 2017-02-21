package com.huirong.biz;

import backtype.storm.tuple.Tuple;

/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月14日
 */
public interface TupleFilter {

	public boolean drop(Tuple t);
}
