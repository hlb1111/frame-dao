package com.hu.wxky.frame.dao;

import com.hu.wxky.frame.util.PropertyConfigurer;
import com.hu.wxky.frame.util.SpringBeanHelper;

public class Rule {
	/**
	 * 默认：DB表、字段命名与java类、属性命名大小写
	 */
	public static RuleCharEnum DBChar = RuleCharEnum.LOW;
	/**
	 * 默认：java类、属性名中遇到大写字母时转换DB表、字段名加入前缀""字符(DBSpe="_"时，abCd成为ab_Cd;AbCd成为Ab_Cd)
	 */
	public static String DBSpe = "_";
	/**
	 * 默认：遇到全大写字母字符串时是否忽略转换规则：忽略 (不忽略时遇到QQ这样的字符串属性，可能会因为被转换成Q+DBSpe+Q这样的字符串)
	 */
	public static Boolean uppOmit = Boolean.TRUE;

	static {
		PropertyConfigurer pc = SpringBeanHelper.getBean(PropertyConfigurer.class);
		if(null!=pc){
			String loadDBChar = pc.getProperty("DBChar");
			String loadDBSpe = pc.getProperty("DBSpe");
			String loadUppOpen = pc.getProperty("uppOmit");
			if (loadDBChar != null
					&& ("EQU".equalsIgnoreCase(loadDBChar)
							|| "LOW".equalsIgnoreCase(loadDBChar) || "UPP"
								.equalsIgnoreCase(loadDBChar))) {
				DBChar = Enum.valueOf(RuleCharEnum.class, loadDBChar.trim()
						.toUpperCase());
			}
			if (loadDBSpe != null) {
				DBSpe = loadDBSpe.trim();
			}
			if (loadUppOpen != null
					&& ("true".equalsIgnoreCase(loadUppOpen) || "false"
							.equalsIgnoreCase(loadUppOpen))) {
				uppOmit = new Boolean(loadUppOpen);
			}
		}
	}

	private Rule() {
	}

}
