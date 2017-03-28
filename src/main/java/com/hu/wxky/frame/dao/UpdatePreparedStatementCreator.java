package com.hu.wxky.frame.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;
/**
 * 更新记录：增加或者修改
 * @author hulb
 *
 */
public class UpdatePreparedStatementCreator implements PreparedStatementCreator {
	private String sql;
	private Object[] arrs;
	private String idName;
	/**
	 * 更新数据库操作
	 * @param sql sql语句
	 * @param params 参数
	 */
	public UpdatePreparedStatementCreator(String sql, Object... params) {
		this(sql, null, params);
	}
	/**
	 * 更新数据库操作
	 * @param sql SQL语句
	 * @param idName 如果是insert into语句是否要取回主键,如果为空，则不能通过getIdValue()来获取主键
	 * @param params 参数
	 */
	public UpdatePreparedStatementCreator(String sql, String idName, Object... params){
		this.sql = sql;
		this.arrs = params;
		this.idName = idName;
	}
	
	public PreparedStatement createPreparedStatement(Connection conn)
			throws SQLException {
		PreparedStatement psst = null;
		if(null==idName){
			psst = conn.prepareStatement(sql);
		}else{
			psst = conn.prepareStatement(sql, new String[]{idName});
		}
		
		if(null!=arrs){
			for(int i=0; i<arrs.length; i++){
				psst.setObject(i+1, arrs[i]);
			}
		}
		return psst;
		
	}

}
