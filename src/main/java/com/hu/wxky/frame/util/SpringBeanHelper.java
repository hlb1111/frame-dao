package com.hu.wxky.frame.util;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
/**
 * 提供对Spring 容器的一些操作方法
 * @author hulb
 *
 */
public class SpringBeanHelper {
	
	private static WebApplicationContext wac;
	
	static {
		wac = ContextLoader.getCurrentWebApplicationContext();
	}
	
	public static void setWebApplicationContext(WebApplicationContext wac) {
		if(null!=wac){
			SpringBeanHelper.wac = wac;
		}
	}
	
	public static WebApplicationContext getWebApplicationContext() {
		return wac;
	}
	
	/**
	 * 获取Spring Bean对象
	 * @param name
	 * @return
	 */
	public static Object getBean(String name){
		return wac.getBean(name);
	}
	
	public static <E> E getBean(Class<E> c, String name) {
		if(null==wac){
			return null;
		}
		return wac.getBean(name, c);
	}
	
	public static <E> E getBean(Class<E> c) {
		if(null==wac){
			return null;
		}
		return wac.getBean(c);
	}
}
