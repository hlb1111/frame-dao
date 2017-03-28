package com.hu.wxky.frame.dao;

import java.util.ArrayList;
import java.util.List;

import com.hu.wxky.frame.bean.PageVo;
import com.hu.wxky.frame.bean.SqlQuery;


public class SqlPageQuery {
	
	private static String SELECT_COUNT = " SELECT COUNT(1) ";
	private static String SELECT_ONE = " SELECT 1 ";
	
	public static <T> PageVo<T> query(IBaseDao baseDao, Class<T> cls, String select, 
			String from, String where, PageVo<T> page, Object... params) {
		SqlQuery sqlQuery = new SqlQuery();
		sqlQuery.setSelect(select);
		sqlQuery.setFrom(from);
		sqlQuery.setWhere(where);
		
		return query(baseDao, cls, sqlQuery, page, params);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> PageVo<T> query(IBaseDao baseDao, Class cls, SqlQuery sqlQuery, PageVo<T> page, Object... params) {
		if (null == baseDao || null == sqlQuery || null == sqlQuery.getSelect() || null == sqlQuery.getFrom() || null == page) {
			return null;
		}
		StringBuilder sb = new StringBuilder(sqlQuery.getFrom());
		if (null != sqlQuery.getWhere()) {
			sb.append(sqlQuery.getWhere());
		}
		
		if(page.getAllCounts()<1){
			int all = queryCount(baseDao, cls, sqlQuery, params);
			page.setAllCounts(all);
		}
		
		if (page.getAllCounts() > 0) {

			if (null != sqlQuery.getGroup()) {
				sb.append(" GROUP BY ").append(sqlQuery.getGroup());
				if (null != sqlQuery.getHaving()) {
					sb.append(" HAVING ").append(sqlQuery.getHaving());
				}
			}
			
			if(null != page.getOrder()) {
				sb.append(" ORDER BY ").append(page.getOrder());
			}
			sb.append(" LIMIT ").append(page.getStart()).append(", ").append(page.getSize());

			List<T> list = baseDao.query(cls, sqlQuery.getSelect() + sb.toString(), params);
			page.setList(list);
		} else {
			page.setList(new ArrayList<T>());
		}
		page.setSuccess(true);
		return page;
	}
	
	public static <T> int queryCount(IBaseDao baseDao, Class<T> cls, SqlQuery sqlQuery, Object... params) {
		if(null == sqlQuery || null == sqlQuery.getFrom()) {
			return -1;
		}
		
		StringBuilder sb = new StringBuilder(sqlQuery.getFrom());
		if (null != sqlQuery.getWhere()) {
			sb.append(sqlQuery.getWhere());
		}
		
		if (null != sqlQuery.getGroup()) {
			sb.append(" GROUP BY ").append(sqlQuery.getGroup());
			if (null != sqlQuery.getHaving()) {
				sb.append(" HAVING ").append(sqlQuery.getHaving());
			}
			String sqlString = SELECT_COUNT + " from ( " + SELECT_ONE + " " + sb.toString() + " ) tmp";
			return baseDao.queryObject(Integer.class, sqlString, params);
		} else {
			return baseDao.queryObject(Integer.class, SELECT_COUNT + sb.toString(), params);
		}
	}
}
