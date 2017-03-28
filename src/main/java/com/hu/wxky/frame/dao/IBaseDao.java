package com.hu.wxky.frame.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.hu.wxky.frame.bean.PageVo;
/**
 * 系统基础数据库操作，封装了新增、修改、删除、等常见操作。
 * @author hulb
 *
 */
public interface IBaseDao {
	
	public static final String SELECT = "SELECT";
	public static final String SELECT_COUNT = "SELECT COUNT(*) FROM ";
	public static final String SELECT_FROM = "SELECT * FROM ";
	public static final String DELETE_FROM = "DELETE FROM ";
	public static final String SET = " SET ";
	public static final String UPDATE = "UPDATE ";
	public static final String VALUES_BRACKET = " VALUES (";
	public static final String INSERT_INTO = "INSERT INTO ";
	public static final String SHOW_SQL_TEMPLATE = "%s [success=%s, cost_times=%d ms]";
	public static final String SQL_LAST_ID = "SELECT last_insert_id()";
	public static final String COMMA = ", "; 
	public static final String Q_M = "? ";
	public static final String AND = " AND ";
	public static final String WHERE = " WHERE ";
	public static final String FOR_UPDATE = " FOR UPDATE";
	public static final String EQUALS = "=?";
	
	public void commit();
	
	/**
	 * 保存对象，新增数据到数据库
	 * @param bean
	 */
	public void save(Object bean); 
	/**
	 * 保存集合到数据库
	 * @param list
	 */
	public void saveList(Collection<?> list);
	/**
	 * 批量提交
	 * @param list
	 */
	public void saveListBatch(Collection<?> list);
	
	/**
	 * 保存集合到数据库
	 * @param list
	 * @param autoCommit 是否每保存100个对象就自动提交事务
	 */
	public void saveList(Collection<?> list, boolean autoCommit);
	/**
	 * 批量提交
	 * @param list
	 * @param autoCommit
	 */
	public void saveListBatch(Collection<?> list, boolean autoCommit);
	/**
	 * 更新对象，更新对象到数据库
	 * @param bean
	 */
	public int update(Object bean) ;
	/**
	 * 更新集合，更新集合到数据库
	 * @param bean
	 */
	public void updateList(Collection<?> list) ;
	/**
	 * 删除某个对象
	 * @param bean
	 */
	public int delete(Object bean) ;
	/**
	 * 根据类名和id删除数据库记录
	 * @param cls 需要封装的对象Bean
	 * @param id
	 */
	public <E> int delete(Class<E> cls, Serializable id) ;
	/**
	 * 新增或者修改对象
	 * @param bean
	 */
	public void saveOrUpdate(Object bean);
	/**
	 * 新增或者修改
	 * @param list
	 */
	public void saveOrUpdate(Collection<?> list);
	
	/**
	 * 根据主键获取对象
	 * @param cls 需要封装的对象Bean
	 * @param id
	 * @param propertyNames 如果属性不能空，则只获取指定属性的字段
	 * @return
	 */
	public <E> E getById(Class<E> cls, Serializable id, String... propertyNames);
	/**
	 * 根据主键获取对象，可以锁定该行记录
	 * @param cls 需要封装的对象Bean
	 * @param isLock 是否要锁定该记录
	 * @param id 主键id
	 * @param propertyNames 如果属性不能空，则只获取指定属性的字段
	 * @return
	 */
	public <E> E getById(Class<E> cls, boolean isLock, Serializable id, String... propertyNames);
	/**
	 * 根据对象属性查询单个对象
	 * @param cls 需要封装的对象Bean
	 * @param propName 对象属性名
	 * @param value 值
	 * @return
	 */
	public <E> E getOneByProperty(Class<E> cls, String propName, Serializable value);
	/**
	 * 根据某个字段查询单个对象
	 * @param cls 需要封装的对象Bean
	 * @param columnName 数据库表字段名称
	 * @param value 值
	 * @return
	 */
	public <E> E getOneByColumn(Class<E> cls, String columnName, Serializable value);
	/**
	 * 根据对象属性查询对象集合
	 * @param cls 需要封装的对象Bean
	 * @param propName 对象属性名
	 * @param value 值
	 * @return
	 */
	public <E> List<E> getListByProperty(Class<E> cls, String propName, Serializable value);
	/**
	 * 根据某个字段查询对象集合
	 * @param cls 需要封装的对象Bean
	 * @param columnName 数据库表字段名称
	 * @param propValue 值
	 * @return
	 */
	public <E> List<E> getListByColumn(Class<E> cls, String columnName, Serializable value);
	/**
	 * 获取一张表所有记录
	 * @param cls 表所对应的Bean
	 * @param attrs Bean对象的属性 如果为空，则Bean对应的所有属性，否则，只有指定属性有值
	 * @return
	 */
	public <E> List<E> getAll(Class<E> cls, String...attrs);
	/**
	 * 根据条件进行查询
	 * @param cls 需要封装的对象Bean
	 * @param where
	 * @return
	 */
	public <E> List<E> getListByWhere(Class<E> cls, SqlWhere where);
	/**
	 * 根据Sql语句、 条件进行查询
	 * @param cls  需要封装的对象Bean
	 * @param sql  SQL语句 不包含where条件
	 * @param where where条件
	 * @return
	 */
	public <E> List<E> getListByWhere(Class<E> cls, String sql, SqlWhere where);
	
	/**
	 * 查询Int
	 * @param sql
	 * @param where
	 * @return
	 */
	public Integer getIntByWhere(String sql, SqlWhere where);
	
	/**
	 * 自定义SQL查询，返回定义的类对象集合
	 * @param cls 需要封装的对象Bean
	 * @param sql
	 * @param params
	 * @return
	 */
	public <E> List<E> query(Class<E> cls, 
			String sql, Object... params);
	/**
	 * 自定义SQL查询 查询单条记录 返回Class所映射的对象
	 * @param cls 需要封装的对象Bean
	 * @param sql
	 * @param params
	 * @return
	 */
	public <E> E queryOne(Class<E> cls, 
			String sql, Object... params) ;
	/**
	 * 针对Bean对象查询数据库<br/>
	 * <pre>
	 * Order order = new Order();
	 * order.setState("pending");
	 * order.setWebOrderId("2394829342");
	 * List<Order> list = query(conn, order);
	 * 返回的List结果集包含：select * from order where state=‘pending’ and web_order_id='2394829342'
	 * </pre>
	 * @param obj
	 * @return
	 */
	public <E> List<E> query(E obj);
	
	public List<String> queryString(String sql, Object... params);
	
	public <E> E queryObject(Class<E> requiredType, String sql, Object... params) ;
	
	public <E> E queryObject(Class<E> requiredType, String sql, SqlWhere where);
	/**
	 * 查询单挑记录，返回map结果 字段名->值
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryForMap(String sql, Object... params);
	/**
	 * 查询List 集合 返回键值对
	 * @param sql
	 * @param params
	 * @return
	 * @author hulb
	 * @date 2016年7月28日
	 */
	public List<Map<String, Object>> queryListMap(String sql, Object... params);
	
	public List<Integer> queryInteger(String sql, Object... params);
	/**
	 * 原生查询，没有属性到字段的映射
	 * @param cls
	 * @param sql
	 * @param params
	 * @return
	 */
	public <E> List<E> queryList(Class<E> requiredType, String sql, Object... params);
	/**
	 * 原生查询，没有属性到字段的映射
	 * @param cls
	 * @param sql
	 * @param params
	 * @return
	 */
	public <E> E queryOneObject(Class<E> requiredType, String sql, Object... params);

	public int updateSql(String sql, Object... params);

	public <E> ClassSwitchTable<E> getByBeanName(Class<E> cls);
	/***
	 * 获取最后一次insert操作的自动生成的ID值  支付MySQL
	 * @return
	 */
	public Integer getLastId();
	
	/***
	 * 获取最后一次insert操作的自动生成的ID值  支付SqlServer
	 * @return
	 */
	public Integer getLastIdSqlServer();
	/**
	 * 根据非空字段分页查询
	 * 
	 * @param sellerPoints
	 * @param oriPage
	 * @return
	 */
	<E> PageVo<E> queryPage(E obj, PageVo<E> oriPage);

	List<Long> queryLong(String sql, Object... params);

	int deleteByWhereNotNull(Object bean);
	/**
	 * 设置某些自动为null
	 * @param cls 需要操作的对象对应的表名 
	 * @param id 主键
	 * @param attrs 对象属性
	 * @return
	 */
	public <E> int updateColumnToNull(Class<E> cls, Serializable id, String...attrs);

	public <E> Integer queryCount(E obj);
	/**
	 * 查询数据：参数是以别名的形式出现：<br/>
	 * <code>
	 * select * from t1 where username=:name and passwd=:passwd <br/>
	 * insert into test(name) values(:name)<br/>
	 * delete from test where name=:name<br/>
	 * </code>
	 * @param cls
	 * @param sql 
	 * @param params 键值对 key要和sql里的参数一一对应
	 * @return
	 * @author hulb
	 * @date 2016年7月28日
	 */
	public <E> List<E> query2(Class<E> cls, String sql, Map<String, Object> params);
	
	/**
	 * 查询数据：参数是以别名的形式出现：<br/>
	 * <code>
	 * select * from t1 where username=:name and passwd=:passwd <br/>
	 * insert into test(name) values(:name)<br/>
	 * delete from test where name=:name<br/>
	 * </code>
	 * @param cls
	 * @param sql 
	 * @param params 键值对 key要和sql里的参数一一对应
	 * @return
	 * @author hulb
	 * @date 2016年7月28日
	 */
	public <E> E queryOne2(Class<E> cls, String sql, Map<String, Object> params);
	
	/**
	 * 查询数据：参数是以别名的形式出现：<br/>
	 * <code>
	 * select * from t1 where username=:name and passwd=:passwd <br/>
	 * insert into test(name) values(:name)<br/>
	 * delete from test where name=:name<br/>
	 * </code>
	 * @param sql 
	 * @param params 键值对 key要和sql里的参数一一对应
	 * @return
	 * @author hulb
	 * @date 2016年7月28日
	 */
	public List<Map<String, Object>> queryListMap2(String sql, Map<String, Object> params);
}
