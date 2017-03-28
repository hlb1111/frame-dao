package com.hu.wxky.frame.bean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class BaseBean implements Serializable {
	private static final long serialVersionUID = -5616344589209361032L;
	
	protected static final String SEPARATOR = System.getProperty("line.separator");
	protected static final String DOT = ",";
	protected static final String SPACE = " ";
	private static final String TYPE_DESC = "typeDesc";
	private static final String serialVersionID = "serialVersionUID";
	private static final String __equalsCalc = "__equalsCalc";
	private static final String __hashCodeCalc = "__hashCodeCalc";
	private static final String EQUELS = "=";

	public String toString() {
		String s = null;
		try {
			s = propertyString(this);
		} catch (Exception e) {
			s = super.toString();
		}
		return s;
	}
	
	/**
	 * 利用反射机制打印JavaBean的全部属性
	 * @param entityName
	 * @return
	 * @throws Exception
	 */
	public String propertyString(Object entityName) throws Exception {
		Class<?> c = entityName.getClass();
		Field field[] = c.getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Field f : field) {
			if(Modifier.isStatic(f.getModifiers())){
				continue;
			}
			String fName = f.getName();
			if(!fName.equals(TYPE_DESC)&&
					!fName.equals(serialVersionID)&&
					!fName.equals(__equalsCalc)&&
					!fName.equals(__hashCodeCalc)){
				sb.append(f.getName());
				sb.append(EQUELS);
				sb.append(invokeMethod(entityName, f.getName(), null));
				sb.append(DOT);
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * 利用反射机制调用JavaBean的get方法
	 * @param owner
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Object invokeMethod(Object owner, String methodName, Object[] args) {
		if(null == owner){return null;}
		Class<?> ownerClass = owner.getClass();
		methodName = methodName.substring(0, 1).toUpperCase()
				+ methodName.substring(1);
		Method method = null;
		try {
			method = ownerClass.getMethod("get" + methodName);
			if(null != method){
				return method.invoke(owner);
			}else{
				return " can't find 'get" + methodName + "' method";
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return " can't find 'get" + methodName + "' method";
		}
		
	}
}
