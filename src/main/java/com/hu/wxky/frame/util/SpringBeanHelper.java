package com.hu.wxky.frame.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
/**
 * 提供对Spring 容器的一些操作方法
 * @author hulb
 *
 */
public class SpringBeanHelper {
	private static final Logger logger = LoggerFactory.getLogger(SpringBeanHelper.class);
	private static ApplicationContext wac;
	
	static {
		try{
			wac = ContextLoader.getCurrentWebApplicationContext();
		}catch(Exception e){
			logger.debug(e.toString());
		}
		if(null==wac){
			//如果不是web环境，需要在配置文件里配置ApplicationContextProvider
			wac = ApplicationContextProvider.getApplicationContext();
			if(null==wac){
				logger.warn("获取Spring 上下文对象失败");
			}
		}
	}
	
	public static void setWebApplicationContext(WebApplicationContext wac) {
		if(null!=wac){
			SpringBeanHelper.wac = wac;
		}
	}
	
	public static ApplicationContext getWebApplicationContext() {
		return wac;
	}
	
	/**
	 * 获取Spring Bean对象
	 * @param name
	 * @return
	 */
	public static Object getBean(String name){
		if(null==wac){
			return null;
		}
		try{
			return wac.getBean(name);
		}catch(Exception e){
			logger.debug(e.toString());
			return null;
		}
	}
	
	public static <E> E getBean(Class<E> c, String name) {
		if(null==wac){
			return null;
		}
		try{
			return wac.getBean(name, c);
		}catch(Exception e){
			logger.debug(e.toString());
			return null;
		}
	}
	
	public static <E> E getBean(Class<E> c) {
		if(null==wac){
			return null;
		}
		try{
			return wac.getBean(c);
		}catch(Exception e){
			logger.debug(e.toString());
			return null;
		}
	}
}
