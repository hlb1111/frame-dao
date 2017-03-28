package com.hu.wxky.frame.dao;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Rule {
	protected static final Logger log = Logger.getLogger(Rule.class);
	/**
	 * 默认：DB表、字段命名与java类、属性命名大小写相同
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
		try {
			InputStream fis = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("conf/config.properties");
			Properties properties = new Properties();
			properties.load(fis);
			// 读取配置的上面的几个属性值
			String loadDBChar = properties.getProperty("DBChar");
			String loadDBSpe = properties.getProperty("DBSpe");
			String loadUppOpen = properties.getProperty("uppOmit");
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

		} catch (Exception e) {
			log.error("加载rule.properties文件失败", e);
		}

	}

	private Rule() {
	}

}
