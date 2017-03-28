package com.hu.wxky.frame.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SqlWhere implements Cloneable, Serializable{
	private static final long serialVersionUID = 4788279413053875064L;
	private static final char SPACE = ' ';
	//private static final String COMMA = ", "; 
	private static final String Q_M = "?";
	private static final String AND = " AND ";
	private static final String WHERE = " WHERE ";
	//private static final String EQUALS = "=?";
	private static final String ORDER_BY = " ORDER BY ";
	private static final String LIMIT = " LIMIT %d, %d";
	private List<SqlCondition> list;
	private int offset;
	private Integer rows;
	private String orderBy;
	
	public static final String LIKE = "like";
	public static final String EQUAL = "=";
	public static final String LESS_THAN = "<";
	public static final String GREATER_THAN = ">";
	
	public SqlWhere(){
		list = new ArrayList<SqlCondition>();
	}
	/**
	 * 增加条件
	 * @param columnName
	 * @param condition
	 * @param value
	 */
	public SqlWhere append(String columnName, String condition, Object value) {
		list.add(new SqlCondition(columnName, condition, value));
		return this;
	}
	/**
	 * 增加返回的记录数
	 * @param size
	 * @return
	 */
	public SqlWhere limit(int size) {
		this.offset = 0;
		this.rows = size;
		return this;
	}
	/**
	 * 增加返回的记录数
	 * @param offset
	 * @param size
	 * @return
	 */
	public SqlWhere limit(int offset, int size) {
		this.offset = offset;
		this.rows = size;
		return this;
	}
	/**
	 * 增加排序字段
	 * @param str
	 * @return
	 */
	public SqlWhere orderBy(String str) {
		this.orderBy = str;
		return this;
	}
	/**
	 * 增加默认的“等于”条件
	 * @param columnName
	 * @param value
	 */
	public SqlWhere append(String columnName, Object value) {
		append(columnName, EQUAL, value);
		return this;
	}
	/**
	 * 获取封装的where条件
	 * @return
	 */
	public String getWhere() {
		return toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		if(list.size()>0){
			sb.append(WHERE);
			int max = list.size();
			for(int i=0; i<max; i++) {
				SqlCondition we = list.get(i);
				sb.append(we.getName()).append(SPACE).append(we.getCondition()).append(SPACE).append(Q_M);
				if(i<max-1){
					sb.append(AND);
				}
			}
		}
		if(null!=orderBy){
			sb.append(ORDER_BY).append(orderBy);
		}
		if(null!=rows){
			sb.append(String.format(LIMIT, offset, rows));
		}
		return sb.toString();
	}
	
	public SqlWhere clone() {
		SqlWhere where = new SqlWhere();
		where.list = new ArrayList<SqlCondition>(this.list);
		where.offset = this.offset;
		where.orderBy = this.orderBy;
		if(null!=this.rows){
			where.rows = this.rows.intValue();
		}
		return where;
	}
	
	/**
	 * 获取参数值
	 * @return
	 */
	public Object[] getParams() {
		Object[] params = new Object[list.size()];
		for(int i=0; i<list.size(); i++){
			params[i] = list.get(i).getValue();
		}
		return params;
	}
}
