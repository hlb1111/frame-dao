package com.hu.wxky.frame.dao;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import com.hu.wxky.frame.bean.PageVo;
import com.hu.wxky.frame.bean.PropertyColumn;
import com.hu.wxky.frame.bean.SqlQuery;
import com.hu.wxky.frame.dao.mapper.RowBeanMapper;
import com.hu.wxky.frame.dao.mapper.RowListMapper;
import com.hu.wxky.frame.exception.ExceptionConfig;
/**
 * 系统基础数据库操作，封装了新增、修改、删除、等常见操作。
 * @author hulb
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class BaseDao extends JdbcDaoSupport implements IBaseDao {
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	protected static final Map<String, ClassSwitchTable> cstCache = new HashMap<String, 
			ClassSwitchTable>();
	//protected static Boolean showSql;
	protected Logger logger = LoggerFactory.getLogger(getClass());
	//private static final Logger logger = LoggerFactory.getLogger(DbTools.class);
	
	public static final Set<Class<?>> originClass = new HashSet<Class<?>>();
	
	static {
		originClass.add(Integer.class);
		originClass.add(Short.class);
		originClass.add(Byte.class);
		originClass.add(Float.class);
		originClass.add(Double.class);
		originClass.add(Long.class);
		originClass.add(Boolean.class);
		originClass.add(Character.class);
		originClass.add(String.class);
	}
	
	
	/**强制显示日志的最小SQL语句执行时间**/
	protected int watchMinExecTime = 100;
	@Autowired
    @Qualifier("masterDataSource")
    public void setDS(DataSource ds) {
        setDataSource(ds);
    }
	@Autowired(required=false)
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	/*public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		if(null==namedParameterJdbcTemplate){
			namedParameterJdbcTemplate = SpringBeanHelper.getBean(NamedParameterJdbcTemplate.class, 
					"namedParameterJdbcTemplate");
			if(null==namedParameterJdbcTemplate){
				throw new ExceptionConfig("spring bean没有配置NamedParameterJdbcTemplate");
			}
		}
		return namedParameterJdbcTemplate;
	}*/

	/**强制显示日志的最小SQL语句执行时间**/
	public void setWatchMinExecTime(int watchMinExecTime) {
		this.watchMinExecTime = watchMinExecTime;
	}
	@Override
	public void commit() {
		Connection conn = getConnection();
		try{
			boolean autoCommit = conn.getAutoCommit();
			if(!autoCommit){
				conn.commit();
			}
		}catch(SQLException e){
			logger.error("", e);
		}finally {
			DataSourceUtils.releaseConnection(conn, getDataSource());
		}
	}
	
	@Override
	public void save(Object bean) {
		Assert.notNull(bean);
		if(bean.getClass().isAssignableFrom(Collection.class)){
			saveList((Collection)bean);
		}
		ClassSwitchTable ctt = cstCache.get(bean.getClass().getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(bean.getClass());
			cstCache.put(bean.getClass().getName(), ctt);
		}
		if(GenerateKey.ASSIGNED.equals(ctt.getGenerateKey())){
			Map<String, String> classTypeMap = ctt.getClassTypeMap();
			PropertyDescriptor idPd = ctt.getPdByColumn(classTypeMap.get(ctt.getIdName()));
			Object idValue = DbTools.callGetter(bean, idPd);
			if(null==idValue){
				if(null==ctt.getRefObj()){
					throw new ExceptionConfig("主键生成配置有错：" + ctt);
				}
				Object id = ctt.getRefObj().getId();
				DbTools.callSetter(bean, idPd, id);
			}
		}
		StringBuilder sb = new StringBuilder();
		List<Object> paramsList = new ArrayList<Object>();
		beanToSqlInsert(bean, sb, paramsList);
		
		
		String sql = sb.toString();
		Object[] params = paramsList.toArray(); 
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			//自动设置主键
			if(null!=ctt.getGenerateKey() && ctt.getGenerateKey().equals(GenerateKey.IDENTITY)){
				Map<String, String> classTypeMap = ctt.getClassTypeMap();
				KeyHolder generatedKeyHolder = new GeneratedKeyHolder(); 
				getJdbcTemplate().update(new UpdatePreparedStatementCreator(sql, 
						ctt.getIdName(), params), generatedKeyHolder);
				if(null!=generatedKeyHolder.getKey()){
					DbTools.callSetter(bean, 
							ctt.getPdByColumn(
									classTypeMap.get(ctt.getIdName())),
							generatedKeyHolder.getKey());
				}
			}else{
				getJdbcTemplate().update(sql, params);
			}
			success = true;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	@Override
	public void saveList(Collection<?> list) {
		saveList(list, false);
	}
	@Override
	public void saveListBatch(Collection<?> list) {
		saveListBatch(list, false);
	}
	@Override
	public void saveList(Collection<?> list, boolean autoCommit) {
		int index = 0;
		for(Object o : list) {
			index++;
			save(o);
			if(autoCommit && index==100){
				commit();
				index = 0;
			}
		}
	}
	@Override
	public void saveListBatch(Collection<?> list, boolean autoCommit) {
		saveList(list, autoCommit);
	}
	@Override
	public int update(Object bean) {
		Assert.notNull(bean);
		if(bean.getClass().isAssignableFrom(Collection.class)){
			updateList((Collection)bean);
		}
		ClassSwitchTable ctt = cstCache.get(bean.getClass().getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(bean.getClass());
			cstCache.put(bean.getClass().getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(UPDATE).append(ctt.getTableName()).append(SET);
		List<Object> params = new ArrayList<Object>();
		Map<String, String> classTypeMap = ctt.getClassTypeMap();
		//Map<String, PropertyDescriptor> columnsMap = ctt.getColumnsMap();
		Object idValue = DbTools.callGetter(bean, 
				ctt.getPdByColumn(classTypeMap.get(ctt.getIdName())));
		if(null==idValue){
			throw new IllegalArgumentException("更新对象的主键值不能为空"+bean.toString());
		}
		PropertyColumn[] pcArr = ctt.getProColArr();
		int count = 0;
		for(int i=0; i<pcArr.length; i++){
			if(null!=pcArr[i]){
				if(pcArr[i].getProName().equals(ctt.getIdName())){
					continue;
				}
				PropertyDescriptor pd = ctt.getPdByColumn(pcArr[i].getColName());
				Object value = DbTools.callGetter(bean, pd);
				if(null!=value){
					if(count>0){
						sb.append(COMMA);
					}
					sb.append(pcArr[i].getColName()).append(EQUALS);
					params.add(value);
					count++;
				}
			}
		}
		sb.append(WHERE).append(ctt.getClassTypeMap().get(ctt.getIdName())).append(EQUALS);
		params.add(idValue);
		String sql = sb.toString();
		Object[] paramArr = params.toArray(); 
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			int i = getJdbcTemplate().update(sb.toString(), params.toArray());
			success = true;
			return i;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), paramArr);
        }
	}
	@Override
	public void updateList(Collection<?> list) {
		for(Object o : list) {
			update(o);
		}
	}
	@Override
	public int updateSql(String sql, Object... params){
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			int i= getJdbcTemplate().update(sql, params);
			success = true;
			return i;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	@Override
	public void saveOrUpdate(Object bean) {
		Assert.notNull(bean);
		if(bean.getClass().isAssignableFrom(Collection.class)){
			updateList((Collection)bean);
		}
		ClassSwitchTable ctt = cstCache.get(bean.getClass().getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(bean.getClass());
			cstCache.put(bean.getClass().getName(), ctt);
		}
		Map<String, String> classTypeMap = ctt.getClassTypeMap();
		Object idValue = DbTools.callGetter(bean, 
				ctt.getPdByColumn(classTypeMap.get(ctt.getIdName())));
		if(null==idValue){
			save(bean);
		}else{
			update(bean);
		}
	}
	@Override
	public void saveOrUpdate(Collection<?> list) {
		Assert.notNull(list);
		for(Object o : list){
			saveOrUpdate(o);
		}
	}
	@Override
	public int delete(Object bean) {
		Assert.notNull(bean);
		ClassSwitchTable ctt = cstCache.get(bean.getClass().getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(bean.getClass());
			cstCache.put(bean.getClass().getName(), ctt);
		}
		Map<String, String> classTypeMap = ctt.getClassTypeMap();
		
		Object idValue = DbTools.callGetter(bean, ctt.getPdByColumn(classTypeMap.get(ctt.getIdName())));
		if(null==idValue){
			throw new IllegalArgumentException("更新对象的主键值不能为空"+bean.toString());
		}
		StringBuilder sb = new StringBuilder();
		sb.append(DELETE_FROM).append(ctt.getTableName());
		sb.append(WHERE).append(ctt.getClassTypeMap().get(ctt.getIdName())).append(EQUALS);
		String sql = sb.toString();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			int r = getJdbcTemplate().update(sql, idValue);
			success = true;
			return r;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), idValue);
        }
	}
	@Override
	public <E> int delete(Class<E> cls, Serializable id) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(DELETE_FROM).append(ctt.getTableName());
		sb.append(WHERE).append(ctt.getClassTypeMap().get(ctt.getIdName())).append(EQUALS);
		String sql = sb.toString();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			int r = getJdbcTemplate().update(sb.toString(), id);
			success = true;
			return r;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), id);
        }
	}
	@Override
	public <E> E getById(Class<E> cls, Serializable id, String... propertyNames) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		if(propertyNames.length==0){
			sb.append(SELECT_FROM).append(ctt.getTableName());
		}else{
			sb.append("SELECT ");
			for(int i=0; i<propertyNames.length; i++){
				if(i>0){
					sb.append(COMMA);
				}
				sb.append(ctt.getColumnByPropertyName(propertyNames[i]));
			}
			sb.append(" FROM ").append(ctt.getTableName());
		}
		sb.append(WHERE).append(ctt.getClassTypeMap().get(ctt.getIdName())).append(EQUALS);
		String sql = sb.toString();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			E e = getJdbcTemplate().query(sql, new RowBeanMapper<E>(ctt), id);
			success = true;
			return e;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), id);
        }
	}
	@Override
	public <E> E getById(Class<E> cls, boolean isLock, Serializable id, String... propertyNames) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		if(propertyNames.length==0){
			sb.append(SELECT_FROM).append(ctt.getTableName());
		}else{
			sb.append("SELECT ");
			for(int i=0; i<propertyNames.length; i++){
				if(i>0){
					sb.append(COMMA);
				}
				sb.append(ctt.getColumnByPropertyName(propertyNames[i]));
			}
			sb.append(" FROM ").append(ctt.getTableName());
		}
		sb.append(WHERE).append(ctt.getClassTypeMap().get(ctt.getIdName())).append(EQUALS);
		if(isLock){
			sb.append(FOR_UPDATE);
		}
		String sql = sb.toString();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			E e = getJdbcTemplate().query(sql, new RowBeanMapper<E>(ctt), id);
			success = true;
			return e;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), id);
        }
	}
	@Override
	public <E> E getOneByProperty(Class<E> cls, String propName, 
			Serializable propValue) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		String columnName = ctt.getColumnByPropertyName(propName);
		return getOneByColumn(cls, columnName, propValue);
	}
	
	@Override
	public <E> E getOneByColumn(Class<E> cls, String columnName, Serializable value) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_FROM).append(ctt.getTableName());
		sb.append(WHERE).append(columnName).append(EQUALS);
		String sql = sb.toString();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			E e = getJdbcTemplate().query(sb.toString(), 
					new RowBeanMapper<E>(ctt), value);
			success = true;
	        return e;
	    } finally {
	        long t2 = System.currentTimeMillis();
	        DbTools.showSql(sql, success, (int)(t2-t1), value);
	    }
	}

	@Override
	public <E> List<E> getListByColumn(Class<E> cls, String columnName, Serializable value) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_FROM).append(ctt.getTableName());
		sb.append(WHERE).append(columnName).append(EQUALS);
		String sql = sb.toString();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			List<E> list = getJdbcTemplate().query(sql, 
					new RowListMapper(ctt), value);
			success = true;
			return list;
        } finally {
	        long t2 = System.currentTimeMillis();
	        DbTools.showSql(sql, success, (int)(t2-t1), value);
	    }
	}

	@Override
	public <E> List<E> getListByProperty(Class<E> cls, String propName, 
			Serializable propValue) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		String columnName = ctt.getColumnByPropertyName(propName);
		return getListByColumn(cls, columnName, propValue);
	}
	@Override
	public <E> List<E> getAll(Class<E> cls, String...column) {
		//logger.trace(getConnection());
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		if(column.length==0){
			sb.append(SELECT_FROM).append(ctt.getTableName());
		}else{
			sb.append("SELECT ");
			for(int i=0; i<column.length; i++){
				if(i>0){
					sb.append(COMMA);
				}
				sb.append(ctt.getColumnByPropertyName(column[i]));
			}
			sb.append(" FROM ").append(ctt.getTableName());
		}
		String sql = sb.toString();
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
            List<E> list = getJdbcTemplate().query(sql, new RowListMapper(ctt));
            success = true;
            return list;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1));
        }
	}
	@Override
	public <E> List<E> getListByWhere(Class<E> cls, SqlWhere where) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_FROM).append(ctt.getTableName());
		
		sb.append(where.getWhere());
		String sql = sb.toString();
		Object[] paramArr = where.getParams();
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
            List<E> list = getJdbcTemplate().query(sql, new RowListMapper<E>(ctt), paramArr);
            success = true;
            return list;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), paramArr);
        }
	}
	@Override
	public <E> List<E> getListByWhere(Class<E> cls, String sql, SqlWhere where) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		sql = sql + where.getWhere();
		Object[] paramArr = where.getParams();
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			List<E> list = getJdbcTemplate().query(sql, 
					new RowListMapper<E>(ctt), paramArr);
			success = true;
			return list;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), paramArr);
        }
	}
	@Override
	public Integer getIntByWhere(String sql, SqlWhere where) {
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append(where.getWhere());
		sql = sb.toString();
		Object[] paramArr = where.getParams();
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			Integer c = getJdbcTemplate().queryForObject(sql, 
					paramArr, Integer.class);
			success = true;
			return c;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), paramArr);
        }
	}
	@Override
	public <E> List<E> query(Class<E> cls, 
			String sql, Object... params) {

    	if (originClass.contains(cls)) {
    		return queryList(cls, sql, params);
    	}
		
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
        	List<E> list = getJdbcTemplate().query(sql, new RowListMapper(ctt), params);
			success = true;
			return list;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	
	@Override
	public <E> E queryOne(Class<E> cls, 
			String sql, Object... params) {

    	if (originClass.contains(cls)) {
    		return queryObject(cls, sql, params);
    	}
    	
		long t1 = System.currentTimeMillis();
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		boolean success = false;
        try{
        	E e = getJdbcTemplate().query(sql, new RowBeanMapper<E>(ctt), params);
        	success = true;
        	return e;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	@Override
	public <E> E queryOneObject(Class<E> cls, String sql, Object... params) {
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
            E e = getJdbcTemplate().queryForObject(sql, cls, params);
            success = true;
            return e;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	@Override
	public <E> E queryObject(Class<E> requiredType, String sql, SqlWhere where) {
	    long t1 = System.currentTimeMillis();
	    sql = sql + where.getWhere();
		Object[] paramArr = where.getParams();
	    boolean success = false;
	    try {
	        E e = getJdbcTemplate().queryForObject(sql, requiredType, paramArr);
	        success = true;
	        return e;
	    } finally {
	        long t2 = System.currentTimeMillis();
	        DbTools.showSql(sql, success, (int)(t2-t1), paramArr);
	    }
	}
	@Override
	public <E> List<E> queryList(Class<E> cls, String sql, Object... params) {
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
        	List<E> r = getJdbcTemplate().queryForList(sql, cls, params);
            success = true;
            return r;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	@Override
	public List<Map<String, Object>> queryListMap(String sql, Object...params){
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
        	List<Map<String, Object>> r = getJdbcTemplate().queryForList(sql, params);
            success = true;
            return r;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	@Override
	public <E> List<E> query(E obj) {
		if(null==obj){
			throw new NullPointerException("query obj is null");
		}
		ClassSwitchTable ctt = cstCache.get(obj.getClass().getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(obj.getClass());
			cstCache.put(obj.getClass().getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_FROM).append(ctt.getTableName());
		List<Object> params = new ArrayList<Object>();
		//Map<String, PropertyDescriptor> columnsMap = ctt.getColumnsMap();
		PropertyColumn[] pcArr = ctt.getProColArr();
		int count = 0;
		for(int i=0; i<pcArr.length; i++){
			if(null!=pcArr[i]){
				//PropertyDescriptor pd = columnsMap.get(pcArr[i].getColName());
				PropertyDescriptor pd = ctt.getPdByColumn(pcArr[i].getColName());
				Object value = DbTools.callGetter(obj, pd);
				if(null!=value){
					if(count==0){
						sb.append(WHERE);
					}else{
						sb.append(AND);
					}
					sb.append(pcArr[i].getColName()).append(EQUALS);
					params.add(value);
					count++;
				}
			}
		}
		String sql = sb.toString();
        Object[] paramArr = params.toArray();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			List<E> list = getJdbcTemplate().query(sql, 
					new RowListMapper(ctt), paramArr);
			success = true;
			return list;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), paramArr);
        }
	}
	
	public List<Integer> queryInteger(String sql, Object... params) {
	    long t1 = System.currentTimeMillis();
	    boolean success = false;
	    try{
    		List<Integer> list = getJdbcTemplate().queryForList(sql, Integer.class, params);
    		success = true;
    		return list;
	    } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	
	public List<Long> queryLong(String sql, Object... params) {
	    long t1 = System.currentTimeMillis();
	    boolean success = false;
	    try{
    		List<Long> list = getJdbcTemplate().queryForList(sql, Long.class, params);
    		success = true;
    		return list;
	    } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	
	
	
	
	public List<String> queryString(String sql, Object... params) {
	    long t1 = System.currentTimeMillis();
	    boolean success = false;
	    try{
	        List<String> list = getJdbcTemplate().queryForList(sql, String.class, params);
	        success = true;
	        return list;
	    } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	/**
	 * 查询数据
	 * @param requiredType 对象，不支持继承自BaseBean的类,可以是String、Integer、Double等，如果要使用继承自BaseBean的类,请使用queryOne方法
	 * @param sql 查询语句
	 * @param params 参数
	 */
	public <E> E queryObject(Class<E> requiredType, String sql, Object... params) {
	    long t1 = System.currentTimeMillis();
	    boolean success = false;
	    try {
	        E e = getJdbcTemplate().queryForObject(sql, requiredType, params);
	        success = true;
	        return e;
	    } finally {
	        long t2 = System.currentTimeMillis();
	        DbTools.showSql(sql, success, (int)(t2-t1), params);
	    }
	}
	public Map<String, Object> queryForMap(String sql, Object... params){
		long t1 = System.currentTimeMillis();
	    boolean success = false;
	    try {
	    	//Map<String, Object> e = getJdbcTemplate().queryForMap(sql, params);
	    	List<Map<String, Object>> results =   
	                 getJdbcTemplate().query(sql, params,   
	                         new RowMapperResultSetExtractor<Map<String, Object>>(new ColumnMapRowMapper(), 1));  
	    	Map<String, Object> e =  DataAccessUtils.uniqueResult(results);  
	        success = true;
	        return e;
	    } finally {
	        long t2 = System.currentTimeMillis();
	        DbTools.showSql(sql, success, (int)(t2-t1), params);
	    }
	}
	
	public <E> ClassSwitchTable<E> getByBeanName(Class<E> cls) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		return ctt;
	}
	public Integer getLastId() {
		Integer i = queryObject(Integer.class, SQL_LAST_ID);
		return i;
	}
	
	public Integer getLastIdSqlServer() {
		Integer i = queryObject(Integer.class, "SELECT SCOPE_IDENTITY() As id");
		return i;
	}
	
	protected void beanToSqlInsert(Object bean, StringBuilder sb, List<Object> paramsList){
		ClassSwitchTable ctt = cstCache.get(bean.getClass().getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(bean.getClass());
			cstCache.put(bean.getClass().getName(), ctt);
		}
		StringBuilder values = new StringBuilder();
		sb.append(INSERT_INTO).append(ctt.getTableName()).append(" (");
		values.append(VALUES_BRACKET);
		int count = 0;
		PropertyColumn[] pcArr = ctt.getProColArr();
		for(int i=0; i<pcArr.length; i++){
			if(null!=pcArr[i]){
				//在保存时需要忽略的属性
				if(!pcArr[i].getPersistent()){
					continue;
				}
				PropertyDescriptor pd = ctt.getPdByColumn(pcArr[i].getColName());
				if(null==pd){
					logger.warn(pcArr[i].getColName()+"属性设置有误，请检查");
					continue;
				}
				Object value = DbTools.callGetter(bean, pd);
				if(null!=value){
					if(count>0){
						sb.append(COMMA);
						values.append(COMMA);
					}
					sb.append(pcArr[i].getColName());
					values.append(Q_M);
					paramsList.add(value);
					count++;
				}
			}
		}
		values.append(")");
		sb.append(")");
		sb.append(values.toString());
	}
	/**
	 * 根据传入的属性，拼接SQL
	 * @param cls
	 * @param attrs
	 * @return
	 */
	protected StringBuilder attrToSql(Class<?> cls, String...attrs) {
		//logger.trace(cls.getName());
		ClassSwitchTable<?> ctt = getByBeanName(cls);
		StringBuilder sb = new StringBuilder();
		if(attrs.length==0){
			sb.append(SELECT_FROM).append(ctt.getTableName());
		}else{
			sb.append("SELECT ");
			for(int i=0; i<attrs.length; i++){
				if(i>0){
					sb.append(COMMA);
				}
				sb.append(ctt.getColumnByPropertyName(attrs[i]));
			}
			sb.append(" FROM ").append(ctt.getTableName());
		}
		return sb;
	}
	/**
	 * 根据传入的属性，拼接SQL
	 * @param cls
	 * @param tableAlias
	 * @param attrs
	 * @return format select a.column_1, a.column_2 from table_name a
	 */
	protected StringBuilder attrToSql(Class<?> cls, String tableAlias, String...attrs) {
		ClassSwitchTable<?> ctt = getByBeanName(cls);
		StringBuilder sb = new StringBuilder();
		if(attrs.length==0){
			sb.append("SELECT ").append(tableAlias).append(".*").append(" FROM ").append(
					ctt.getTableName()).append(" ").append(tableAlias);
		}else{
			sb.append("SELECT ");
			for(int i=0; i<attrs.length; i++){
				if(i>0){
					sb.append(COMMA);
				}
				sb.append(tableAlias).append(".").append(ctt.getColumnByPropertyName(attrs[i]));
			}
			sb.append(" FROM ").append(ctt.getTableName()).append(" ").append(tableAlias);
		}
		return sb;
	}
	/**
	 * 根据属性获取select的字段拼接
	 * @param cls
	 * @param attrs 如果为null， 则 返回  a.* ; a=tableAlias
	 * @return a.column_1,a.column_2 ; a=tableAlias
	 */
	protected StringBuilder attrToColumn(Class<?> cls, String tableAlias, String...attrs) {
		ClassSwitchTable<?> ctt = getByBeanName(cls);
		StringBuilder sb = new StringBuilder();
		if(attrs.length==0){
			sb.append(" ").append(tableAlias).append("*").append(" ");
		}else{
			for(int i=0; i<attrs.length; i++){
				if(i>0){
					sb.append(COMMA);
				}
				sb.append(tableAlias).append(".").append(ctt.getColumnByPropertyName(attrs[i]));
			}
		}
		return sb;
	}
	
	public <E> PageVo<E> queryPage(E obj, PageVo<E> oriPage) {
		//throw new UnsupportedOperationException("queryPage not support");
		if(null==obj){
			throw new NullPointerException("query obj is null");
		}
		ClassSwitchTable ctt = cstCache.get(obj.getClass().getName());

		if(null==ctt){
			ctt = new ClassSwitchTable(obj.getClass());
			cstCache.put(obj.getClass().getName(), ctt);
		}
		
		List<Object> params = new ArrayList<Object>();
		StringBuilder selectSB = new StringBuilder(" SELECT * ");
		String from = " FROM " + ctt.getTableName();
		StringBuilder whereSB = new StringBuilder();
		PropertyColumn[] pcArr = ctt.getProColArr();
		if (pcArr == null || pcArr.length == 0) {
			throw new RuntimeException(obj.getClass().getName() + " has not property");
		}
		int count = 0;
		for(int i=0; i < pcArr.length; i++){
			//selectSB.append(pcArr[i].getColName()).append(",");
			if(null!=pcArr[i]){
				PropertyDescriptor pd = ctt.getPdByColumn(pcArr[i].getColName());
				Object value = DbTools.callGetter(obj, pd);
				if(null!=value){
					if(count==0){
						whereSB.append(WHERE);
					}else{
						whereSB.append(AND);
					}
					whereSB.append(pcArr[i].getColName()).append(EQUALS);
					params.add(value);
					count++;
				}
			}
		}
		
		SqlQuery sqlQuery = new SqlQuery();
		sqlQuery.setSelect(selectSB.toString());
		sqlQuery.setFrom(from);
		sqlQuery.setWhere(whereSB.toString());
		return SqlPageQuery.query(this, obj.getClass(), sqlQuery, oriPage, params.toArray());
		
	}
	@Override
	public int deleteByWhereNotNull(Object bean) {
		Assert.notNull(bean);
		ClassSwitchTable ctt = cstCache.get(bean.getClass().getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(bean.getClass());
			cstCache.put(bean.getClass().getName(), ctt);
		}

		List<Object> params = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		sb.append(DELETE_FROM).append(ctt.getTableName()).append(WHERE);
		PropertyColumn[] pcArr = ctt.getProColArr();
		int count = 0;
		for(int i=0; i<pcArr.length; i++){
			if(null!=pcArr[i]){

				PropertyDescriptor pd = ctt.getPdByColumn(pcArr[i].getColName());
				Object value = DbTools.callGetter(bean, pd);
				if(null!=value){
					if(count>0){
						sb.append(AND);
					}
					sb.append(pcArr[i].getColName()).append(EQUALS);
					params.add(value);
					count++;
				}
			}
		}
		if (count < 1) {
			throw new IllegalArgumentException("对象全为空"+bean.toString());
		}
		String sql = sb.toString();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			int r = getJdbcTemplate().update(sql, params.toArray());
			success = true;
			return r;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params.toArray());
        }
	}
	
	public <E> int updateColumnToNull(Class<E> cls, Serializable id, String...attrs) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(UPDATE).append(ctt.getTableName()).append(SET);
		for(int i=0; i<attrs.length; i++){
			if(i>0){
				sb.append(COMMA);
			}
			sb.append(ctt.getColumnByPropertyName(attrs[i])).append("=NULL");
		}
		sb.append(WHERE).append(ctt.getClassTypeMap().get(ctt.getIdName())).append(EQUALS);
		String sql = sb.toString();
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
			int i = getJdbcTemplate().update(sql, id);
			success = true;
			return i;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), id);
        }
	}

	public <E> Integer queryCount(E obj) {
		if(null==obj){
			throw new NullPointerException("query obj is null");
		}
		ClassSwitchTable ctt = cstCache.get(obj.getClass().getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(obj.getClass());
			cstCache.put(obj.getClass().getName(), ctt);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_COUNT).append(ctt.getTableName());
		List<Object> params = new ArrayList<Object>();
		//Map<String, PropertyDescriptor> columnsMap = ctt.getColumnsMap();
		PropertyColumn[] pcArr = ctt.getProColArr();
		int count = 0;
		for(int i=0; i<pcArr.length; i++){
			if(null!=pcArr[i]){
				//PropertyDescriptor pd = columnsMap.get(pcArr[i].getColName());
				PropertyDescriptor pd = ctt.getPdByColumn(pcArr[i].getColName());
				Object value = DbTools.callGetter(obj, pd);
				if(null!=value){
					if(count==0){
						sb.append(WHERE);
					}else{
						sb.append(AND);
					}
					sb.append(pcArr[i].getColName()).append(EQUALS);
					params.add(value);
					count++;
				}
			}
		}
		String sql = sb.toString();
        Object[] paramArr = params.toArray();
        long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
            Integer e = getJdbcTemplate().queryForObject(sql, Integer.class, paramArr);
			return e;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), paramArr);
        }
	}

	@Override
	public <E> List<E> query2(Class<E> cls, String sql, Map<String, Object> params) {
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
        	List<E> list = namedParameterJdbcTemplate.query(sql, 
        			params, new RowListMapper(ctt));
			success = true;
			return list;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}

	@Override
	public <E> E queryOne2(Class<E> cls, String sql, Map<String, Object> params) {
		if (originClass.contains(cls)) {
    		return queryObject(cls, sql, params);
    	}
    	
		long t1 = System.currentTimeMillis();
		ClassSwitchTable ctt = cstCache.get(cls.getName());
		if(null==ctt){
			ctt = new ClassSwitchTable(cls);
			cstCache.put(cls.getName(), ctt);
		}
		boolean success = false;
        try{
        	E e = namedParameterJdbcTemplate.query(sql, 
        			params, new RowBeanMapper<E>(ctt));
        	success = true;
        	return e;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}

	@Override
	public List<Map<String, Object>> queryListMap2(String sql, Map<String, Object> params) {
		long t1 = System.currentTimeMillis();
        boolean success = false;
        try{
        	List<Map<String, Object>> r = namedParameterJdbcTemplate.queryForList(sql, params);
            success = true;
            return r;
        } finally {
            long t2 = System.currentTimeMillis();
            DbTools.showSql(sql, success, (int)(t2-t1), params);
        }
	}
	
	
}
