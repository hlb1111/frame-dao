package com.hu.wxky.frame.service;

import java.io.Serializable;
import java.util.List;

import com.hu.wxky.frame.bean.PageVo;

public interface IBaseService {
	/**
	 * 保存新增的Bean对象
	 * @param bean
	 */
	public <E> void saveBean(E bean);
	/**
	 * 更新Bean对象
	 * @param bean
	 */
	public <E> void updateBean(E bean);
	/**
	 * 根据主键ID查询对象
	 * @param cls
	 * @param id
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
	 * 根据主键ID删除Bean对象
	 * @param id
	 */
	public <E> void deleteById(Class<E> cls, Serializable id);
	/**
	 * 根据主键ID获取Bean对象
	 * @param id
	 * @return
	 */
	public <E> E getBean(Class<E> cls, Serializable id);
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
	
	public <E> int queryCount(E obj);
	
	public void saveOrUpdate(Object bean);
	/**
	 * 根据非空字段分页查询
	 * 
	 * @param sellerPoints
	 * @param oriPage
	 * @return
	 */
	<E> PageVo<E> queryPage(E obj, PageVo<E> oriPage);
	
}
