package com.hu.wxky.frame.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 数据库字段
 * @author hulb
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnField {
	/**
	 * 该属性数据库字段名称，数据库字段和Bean属性不满足规则：user_name = userName
	 * @return
	 */
	public String value() default "";
}
