package com.hu.wxky.frame.dao;
/**
 * SQL参数验证
 * @author hulb
 *
 */
public class SqlParamValidate {
	public static final String[] INJ_STR= {"and", "exec", "insert", "select", "delete", 
		"update", "count", "*", "%", "chr", "mid", "master", "truncate", "char", "declare",
		";", "or", "-", "+", ",", "union", "'"};
	/**
	 * 判断是否有SQL注入
	 * @param param
	 * @return true=有注入；false=没有注入
	 */
	public static boolean sqlInj(String param) {
		if(null==param){
			return false;
		}
		param = param.toLowerCase();
		for (int i = 0; i < INJ_STR.length; i++) {
			if (param.indexOf(INJ_STR[i]) >= 0) {
				return true;
			}
		}
		return false;
	}
}
