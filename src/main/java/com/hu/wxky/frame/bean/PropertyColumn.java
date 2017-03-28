package com.hu.wxky.frame.bean;

public class PropertyColumn {
	
	private String proName;
	private String colName;
	/**是否持久化**/
	private boolean persistent = true;
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public boolean getPersistent() {
		return persistent;
	}
	public boolean isPersistent() {
		return persistent;
	}
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	
	
	
	
}
