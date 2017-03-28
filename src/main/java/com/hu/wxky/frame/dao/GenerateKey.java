package com.hu.wxky.frame.dao;
/**
 * 主键生成方式 
 * @author hulb
 *
 */
public enum GenerateKey {
	/**
	 * 自增长性，主要用于MySql、Sql Server、DB2中主键生成机制 
	 */
	IDENTITY,
	
	/**
	 * 用户自己定义 ，必须配置：com.hu.wxky.frame.dao.IdCreator
	 */
	ASSIGNED,
	
	
}
