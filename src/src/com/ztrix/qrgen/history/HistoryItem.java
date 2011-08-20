package com.ztrix.qrgen.history;

public class HistoryItem{	
	private long timestamp;
	private String data;
	
	public HistoryItem(long t,String d){
		timestamp=t;
		data=d;
	}
	
	public long getTimeStamp(){
		return timestamp;
	}
	
	public String getText(){
		return data;
	}
}