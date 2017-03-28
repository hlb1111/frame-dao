package com.hu.wxky.frame.exception;

public class ExceptionConfig extends RuntimeException {

	private static final long serialVersionUID = 8123728549389985703L;
	
	public ExceptionConfig(){super();}
	
	public ExceptionConfig(String msg) {super(msg);}

	public ExceptionConfig(String msg, Throwable e) {super(msg, e);}
	
}
