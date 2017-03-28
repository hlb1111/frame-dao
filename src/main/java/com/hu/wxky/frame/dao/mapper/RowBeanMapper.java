package com.hu.wxky.frame.dao.mapper;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.hu.wxky.frame.dao.ClassSwitchTable;
import com.hu.wxky.frame.dao.DbTools;

public class RowBeanMapper<E> implements ResultSetExtractor<E> {
	private ClassSwitchTable<E> ctt;
	private Class<E> cls;
	
	public RowBeanMapper(Class<E> cls) {
		this.cls = cls;
		ctt = new ClassSwitchTable<E>(cls);
	}
	public RowBeanMapper(ClassSwitchTable<E> ctt) {
		this.ctt = ctt;
		this.cls = ctt.getCls();
	}
	
	public E extractData_old(ResultSet rs) throws SQLException {
		if(rs.next()){
			E bean;
			try {
				bean = this.cls.newInstance();
			} catch (InstantiationException e) {
				throw new SQLException(
		                "Cannot create " + this.cls.getName() + ": " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new SQLException(
		                "Cannot create " + this.cls.getName() + ": " + e.getMessage(), e);
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			for (int col = 1; col <= cols; col++) {
	            String columnName = rsmd.getColumnLabel(col);
	            if (null == columnName || 0 == columnName.length()) {
	                columnName = rsmd.getColumnName(col).toLowerCase();
	            }else{
	            	columnName = columnName.toLowerCase();
	            }
	            PropertyDescriptor pd = ctt.getPdByColumn(columnName);
	            if(null!=pd){
	            	Class<?> propType = pd.getPropertyType();
	                Object value = DbTools.processColumn(rs, columnName, propType);
	                if (propType != null && value == null && propType.isPrimitive()) {
	                    value = DbTools.primitiveDefaults.get(propType);
	                }
	                DbTools.callSetter(bean, pd, value);
	            }
			}
	        return bean;
		}
		return null;
	}

	public E extractData(ResultSet rs) throws SQLException {
		if(rs.next()){
			E bean;
			try {
				bean = this.cls.newInstance();
			} catch (InstantiationException e) {
				throw new SQLException(
		                "Cannot create " + this.cls.getName() + ": " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new SQLException(
		                "Cannot create " + this.cls.getName() + ": " + e.getMessage(), e);
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			for (int col = 1; col <= cols; col++) {
	            String columnName = rsmd.getColumnLabel(col);
	            if (null == columnName || 0 == columnName.length()) {
	                columnName = rsmd.getColumnName(col).toLowerCase();
	            }
	            PropertyDescriptor pd = ctt.getPdByColumn(columnName);
	            if(null!=pd){
	            	Class<?> propType = pd.getPropertyType();
	                Object value = DbTools.processColumn(rs, columnName, propType);
	                if (propType != null && value == null && propType.isPrimitive()) {
	                    value = DbTools.primitiveDefaults.get(propType);
	                }
	                DbTools.callSetter(bean, pd, value);
	            }
			}
	        return bean;
		}
		return null;
	}
}
