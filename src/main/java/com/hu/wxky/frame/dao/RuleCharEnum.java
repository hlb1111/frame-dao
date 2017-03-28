package com.hu.wxky.frame.dao;

public enum RuleCharEnum {
	/**
	 * 相同
	 */
	EQU,
	/**
	 * 表小写
	 */
	LOW,
	/**
	 * 表大写
	 */
	UPP,
	/**
	 * 默认：java类、属性名中遇到大写字母时转换DB表、字段名加入前缀""字符(DBSpe="_"时，abCd成为ab_Cd;AbCd成为Ab_Cd)
	 */
	DBSpe
}
