package com.hu.wxky.frame.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hu.wxky.frame.dao.GenerateKey;


/**
 * 数据库主键标识
 * @author hulb
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
	/**
	 * 主键生成方式
	 * @return
	 */
	 public GenerateKey generateKey() default GenerateKey.IDENTITY;
	 
	 //public String sequenceName() default "";
	 
	 public String refObj() default "";
}
