package com.hu.wxky.frame.dao;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hu.wxky.frame.annotation.PrimaryKey;
import com.hu.wxky.frame.annotation.TableField;
import com.hu.wxky.frame.bean.BaseBean;
import com.hu.wxky.frame.bean.PropertyColumn;
import com.hu.wxky.frame.dao.id.IdCreator;
import com.hu.wxky.frame.util.ClassUtil;
import com.hu.wxky.frame.util.SpringBeanHelper;



/**
 * 根据类转换为表对应的字段，主要可用于自动生成SQL语句<br/>
 * 提供实体对象的属性和数据库字段的对应Map
 * @author hulb
 *
 */
public class ClassSwitchTable<E> {
	private static final String SYNTAX = "order;option;";
	
	private static final Logger log = LoggerFactory.getLogger(ClassSwitchTable.class);
	private static String baseBeanName = BaseBean.class.getName();
	private static String objectName = Object.class.getName();
	
	/**
	 * Key = 属性名； Value=字段名
	 */
	private Map<String, String> classTypeMap = new HashMap<String, String>();
	private Map<String, PropertyDescriptor> columnsLowerMap = new HashMap<String, PropertyDescriptor>();
	private PropertyColumn[] proColArr;
	/**表名*/
	private String tableName;
	/**ID属性*/
	private String idName;
	/**
	 * ID生成器
	 */
	private IdCreator refObj;
	private GenerateKey generateKey;
	private Class<E> cls;
	
	public ClassSwitchTable(Class<E> cla) {
		super();
		this.cls = cla;
		init(cla);
	}

	/**
	 * 类构造时初始化方法
	 * @param cla
	 */
	private void init(Class<?> cla) {
		//获取数据库表名
		Annotation[] clsAnnoArr = cls.getAnnotations();
		boolean hasTableNameAnno = false;
		for(Annotation a : clsAnnoArr){
			if(a.annotationType().isAssignableFrom(TableField.class)){
				String tbName = ((TableField)a).value();
				if(!StringUtils.isBlank(tbName)){
					tableName = tbName;
					hasTableNameAnno = true;
				}
			}
		}
		//如果Bean类没有包含TableField注解，则根据类名来设置表名
		if(!hasTableNameAnno){
			tableName = DbTools.strClassToDB(cla.getSimpleName());
			if(SYNTAX.indexOf(tableName)>-1){
				tableName = "`"+tableName+"`";
			}
		}
		//解析属性
		
		List<PropertyColumn> pcList = new ArrayList<PropertyColumn>();
		
		//bean类继承最多五级
		for(int i=0; i<5; i++){
			String clsName = cla.getName();
			if(clsName.equals(baseBeanName) || clsName.equals(objectName)){
				break;
			}else{
				Field[] fs = cla.getDeclaredFields();
				pcList.addAll(getAttr(fs));
			}
			cla = cla.getSuperclass();
		}
		
		proColArr = new PropertyColumn[pcList.size()];
		//pcList.toArray(proColArr);
		for(int i=0; i<proColArr.length; i++){
			proColArr[i] = pcList.get(i);
		}
		if(null==generateKey){
			log.warn(cls.getName() + "缺乏主键标识，没有主键，则不能通过Bean对象来持久化数据");
		}
		
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(cls);
		} catch (IntrospectionException e) {
			log.warn(cls.getName()+"获取属性发生异常：", e);
			
		}
		if(beanInfo!=null){
			PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
			for(int i=0; i<props.length; i++) {
				String dbName = classTypeMap.get(props[i].getName());
				if(null!=dbName){
					//columnsMap.put(dbName, props[i]);
					columnsLowerMap.put(dbName.toLowerCase(), props[i]);
				}
			}
		}
	}
	
	private List<PropertyColumn> getAttr(Field[] fs){
		List<PropertyColumn> pcList = new ArrayList<PropertyColumn>();
		for (int i=0; i<fs.length; i++) {
			Field fe = fs[i];
			if(Modifier.isStatic(fe.getModifiers())){
				continue;
			}
			
			if (DbTools.isSampleType(fe) && DbTools.haveGetSet(fe)) {
				String FeName = fe.getName();
				String DBName = DbTools.includeColumnAnnotation(fe);
				if(null==DBName){
					DBName = DbTools.strClassToDB(fe.getName());
				}
				classTypeMap.put(FeName, DBName);
				//columnsMap.put(DBName, FeName);
				PropertyColumn pc = new PropertyColumn();
				pc.setColName(DBName);
				pc.setProName(FeName);
				boolean b = DbTools.includeIgnoreAnnotation(fe);
				if(b){
					pc.setPersistent(false);
				}
				pcList.add(pc);
			}else if(Blob.class.isAssignableFrom(fe.getType())){
				String FeName = fe.getName();
				String DBName = DbTools.includeColumnAnnotation(fe);
				if(null==DBName){
					DBName = DbTools.strClassToDB(fe.getName());
				}
				classTypeMap.put(FeName, DBName);
				//columnsMap.put(DBName, FeName);
				PropertyColumn pc = new PropertyColumn();
				pc.setColName(DBName);
				pc.setProName(FeName);
				pcList.add(pc);
			}else if(Clob.class.isAssignableFrom(fe.getClass())){
				String FeName = fe.getName();
				String DBName = DbTools.includeColumnAnnotation(fe);
				if(null==DBName){
					DBName = DbTools.strClassToDB(fe.getName());
				}
				classTypeMap.put(FeName, DBName);
				//columnsMap.put(DBName, FeName);
				PropertyColumn pc = new PropertyColumn();
				pc.setColName(DBName);
				pc.setProName(FeName);
				pcList.add(pc);
			}
			PrimaryKey pk = (PrimaryKey)ClassUtil.includeJsonAnnotation(fe, PrimaryKey.class);
			if(null!=pk){
				idName = fe.getName();
				if(null!=pk.refObj() && !pk.refObj().trim().isEmpty()){
					refObj = SpringBeanHelper.getBean(IdCreator.class, pk.refObj().trim());
				}
				generateKey = pk.generateKey();	
			}
		}
		return pcList;
	}
	
	

	/**
	 * 获得类属性名->表字段的名称对应map：<属性名,表列名>
	 * @return
	 */
	public Map<String, String> getClassTypeMap() {
		return classTypeMap;
	}

	/**
	 * 获得表字段的名称->类属性名对应map：<表列名,属性对象描述>
	 * @return
	 */
	@Deprecated
	public Map<String, PropertyDescriptor> getColumnsMap() {
		return columnsLowerMap;
	}

	/**
	 * 获得表名
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * 获取主键
	 * @return
	 */
	public String getIdName() {
		return idName;
	}
	
	public String getColumnByPropertyName(String propertyName) {
		return classTypeMap.get(propertyName);
	}
	
	/**
	 * 获取主键的生成方式 
	 * @return
	 */
	public GenerateKey getGenerateKey() {
		return generateKey;
	}
	
	public IdCreator getRefObj() {
		return refObj;
	}

	public Class<E> getCls() {
		return cls;
	}
	/**
	 * 返回Bean类的反射属性名和数据库字段的对应关系，如果Bean的属性是复杂对象，则数组对应的是空值
	 * [{value},{null},{value}]
	 * @return
	 */
	public PropertyColumn[] getProColArr() {
		return proColArr;
	}
	
	public PropertyDescriptor getPdByColumn(String column) {
		return columnsLowerMap.get(column.toLowerCase());
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{tableName=").append(tableName).append(",");
		sb.append("idName=").append(idName).append(",");
		sb.append("classTypeMap=").append(classTypeMap).append(",");
		sb.append("columnsLowerMap=").append(columnsLowerMap).append(",");
		sb.append("generateKey=").append(generateKey).append(",");
		sb.append("refObj=").append(refObj);
		sb.append("}");
		return sb.toString();
	}
}
