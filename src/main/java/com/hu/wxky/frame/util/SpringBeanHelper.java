package com.hu.wxky.frame.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
/**
 * 提供对Spring 容器的一些操作方法
 * @author hulb
 *
 */
public class SpringBeanHelper {
	private static final Logger logger = LoggerFactory.getLogger(SpringBeanHelper.class);
	private static ApplicationContext wac;
	
	public static void setApplicationContext(ApplicationContext wac) {
		if(null!=wac){
			SpringBeanHelper.wac = wac;
		}
	}
	
	public static ApplicationContext getApplicationContext() {
		if(null==wac){
			//如果不是web环境，需要在配置文件里配置ApplicationContextProvider
			wac = ApplicationContextProvider.getApplicationContext();
			if(null==wac){
				wac = ContextLoader.getCurrentWebApplicationContext();
			}
		}
		return wac;
	}
	
	/**
	 * 获取Spring Bean对象
	 * @param name
	 * @return
	 */
	public static Object getBean(String name){
		ApplicationContext ac = getApplicationContext();
		if(null==ac){
			return null;
		}
		try{
			return ac.getBean(name);
		}catch(Exception e){
			logger.debug(e.toString());
			return null;
		}
	}
	
	public static <E> E getBean(Class<E> c, String name) {
		ApplicationContext ac = getApplicationContext();
		if(null==ac){
			return null;
		}
		try{
			return ac.getBean(name, c);
		}catch(Exception e){
			logger.debug(e.toString());
			return null;
		}
	}
	
	public static <E> E getBean(Class<E> c) {
		ApplicationContext ac = getApplicationContext();
		if(null==ac){
			return null;
		}
		try{
			return ac.getBean(c);
		}catch(Exception e){
			logger.debug(e.toString());
			return null;
		}
	}
}
