package com.hu.wxky.frame.bean;

import java.beans.PropertyDescriptor;
/**
 * Bean 对象的属性描述
 * @author hulb
 *
 */
public class BeanProperty extends BaseBean {

	private static final long serialVersionUID = 6077184922390700441L;
	
	private String columnName;
	private String proertyName;
	private PropertyDescriptor pd;
	private boolean isBlob;
	private boolean isClob;
	
	public boolean isClob() {
		return isClob;
	}
	public void setClob(boolean isClob) {
		this.isClob = isClob;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getProertyName() {
		return proertyName;
	}
	public void setProertyName(String proertyName) {
		this.proertyName = proertyName;
	}
	public PropertyDescriptor getPd() {
		return pd;
	}
	public void setPd(PropertyDescriptor pd) {
		this.pd = pd;
	}
	public boolean isBlob() {
		return isBlob;
	}
	public void setBlob(boolean isBlob) {
		this.isBlob = isBlob;
	}
	@Override
	public boolean equals(Object arg0) {
		if(null==arg0){
			return false;
		}
		if(arg0 instanceof BeanProperty){
			BeanProperty bp = (BeanProperty)arg0;
			return this.proertyName.equals(bp.getProertyName()) && this.columnName.equals(bp.getColumnName());
		}else{
			return false;
		}
	}
	@Override
	public int hashCode() {
		return this.proertyName.hashCode() + this.columnName.hashCode();
	}
	
	
}
