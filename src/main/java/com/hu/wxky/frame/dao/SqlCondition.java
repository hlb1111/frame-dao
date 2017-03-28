package com.hu.wxky.frame.dao;

public class SqlCondition {
	/**
	 * 字段名
	 */
	private String name;
	/**
	 * 条件值
	 */
	private Object value;
	/**
	 * 条件：=、 >、 <、 like、 not、 is not、 
	 */
	private String condition;
	
	public SqlCondition(){}
	public SqlCondition(String name,  String condition, Object value){
		this.name = name;
		this.value = value;
		this.condition = condition;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	
}
