package com.hu.wxky.frame.service;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.hu.wxky.frame.bean.PageVo;
import com.hu.wxky.frame.dao.IBaseDao;

public abstract class BaseService implements IBaseService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Transactional
	public <E> void saveBean(E bean) {
		getDao().save(bean);
	}
	@Transactional
	public <E> void updateBean(E bean) {
		getDao().update(bean);
	}
	public <E> E getById(Class<E> cls, Serializable id, String... propertyNames) {
		return getDao().getById(cls, id, propertyNames);
	}
	public <E> E getById(Class<E> cls, boolean isLock, Serializable id, String... propertyNames) {
		return getDao().getById(cls, isLock, id, propertyNames);
	}
	public <E> void deleteById(Class<E> cls, Serializable id) {
		getDao().delete(cls, id);
	}
	
	
	public <E> E getBean(Class<E> cls, Serializable id) {
		return getDao().getById(cls, id);
	}
	
	public <E> List<E> query(E obj) {
		return getDao().query(obj);
	}
	public <E> int queryCount(E obj) {
		return getDao().queryCount(obj);
	}

	@Transactional
	public void saveOrUpdate(Object bean) {
		getDao().saveOrUpdate(bean);
	}
	
	public <E> PageVo<E> queryPage(E obj, PageVo<E> oriPage) {
		return getDao().queryPage(obj, oriPage);
	}

	public abstract IBaseDao getDao();
}
