package com.hu.wxky.frame.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * 有个类的一些常规方法
 * @author hulb
 *
 */
@SuppressWarnings("rawtypes")
public class ClassUtil {
	protected static final Logger log = Logger.getLogger(ClassUtil.class);
	/**
	 * model类的默认id属性的命名
	 */
	public static final String ID = "id";

	/**
	 * model类的默认get方法前缀
	 */
	public static final String GET = "get";
	/**
	 * model类的默认set方法前缀
	 */
	public static final String SET = "set";

	/**
	 * 根据fe获得它的get方法
	 * @param fe
	 * @return
	 */
	public static Method getGetMethod(Field fe) {
		Method me = null;
		try {
			me = fe.getDeclaringClass().getMethod(
					GET + subFieldName(fe.getName()));
		} catch (Exception e) {
		}
		return me;
	}

	/**
	 * 根据fe获得它的set方法
	 * @param fe
	 * @return
	 */
	public static Method getSetMethod(Field fe) {
		Method me = null;
		try {
			me = fe.getDeclaringClass().getMethod(
					SET + subFieldName(fe.getName()), fe.getType());
		} catch (Exception e) {
		}
		return me;
	}

	/**
	 * 根据属性名获得它的get方法
	 * @param clas 类
	 * @param fieldName 属性名
	 * @return
	 */
	public static Method getGetMethod(Class<?> clas, String fieldName) {
		Method me = null;
		try {
			me = clas.getMethod(GET + subFieldName(fieldName));
		} catch (Exception e) {
		}
		return me;
	}

	/**
	 * 根据属性名获得它的set方法
	 * @param clas  类
	 * @param fieldName  属性名
	 * @return
	 */
	public static Method getSetMethod(Class<?> clas, String fieldName) {
		Method me = null;
		try {
			me = clas.getMethod(SET + subFieldName(fieldName),
					getField(clas, fieldName).getType());
		} catch (Exception e) {
		}
		return me;
	}

	/**
	 * 根据属性名获得属性
	 * @param clas
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class<?> clas, String fieldName) {
		Field f = null;
		try {
			f = clas.getDeclaredField(fieldName);
		} catch (Exception e) {
		}
		return f;
	}
	
	/**
	 * 获得obj对象的fieldName属性值
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Object getObject(Object obj, String fieldName) {
		try {
			return getGetMethod(obj.getClass(), fieldName).invoke(obj);
		} catch (Exception e) {
			return null;
		}
	}
	public static void setObject(Object obj, String fieldName, Object value){
		try{
			getSetMethod(obj.getClass(), fieldName).invoke(obj, value);
		}catch(Exception e){
			log.equals(e);
		}
	}
	/**
	 * 判断是否 包含JsonField注解，如果包含JsonField注解，
	 * 则认为是需要转换为json的字段，并且提前jsonField的name属性。
	 * 否则返回null，即表示不需要转换为json
	 * @param f
	 * @return
	 */
	public static Annotation includeJsonAnnotation(Field f, Class cla){
		Annotation[] annotations = f.getAnnotations();
		if(annotations.length<1){
			return null;
		}
		for(Annotation a : annotations){
			if(a.annotationType().isAssignableFrom(cla)){
				return a;
			}
		}
		return null;
	}
	/**
	 * 判断类型是否日期型
	 * @param cla
	 * @return
	 */
	
	public static Boolean isDateType(Class cla){
		if(java.util.Date.class.isAssignableFrom(cla)){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	/**
	 * 转换fieldName的首字母到大写
	 * @param fieldName
	 * @return
	 */
	private static String subFieldName(String fieldName) {
		if (fieldName.length() > 1) {
			fieldName = fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
		} else {
			fieldName = fieldName.toUpperCase();
		}
		return fieldName;
	}
}
