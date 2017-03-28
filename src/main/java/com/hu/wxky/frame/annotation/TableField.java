package com.hu.wxky.frame.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 该注解主要用于Bean类对应的数据库表名： 类名<->数据库表名称
 * @author hulb
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableField {
	/**
	 * 该属性数据库字段名称，数据库字段和Bean属性不满足规则：user_name = userName
	 * @return
	 */
	public String value() default "";
}
