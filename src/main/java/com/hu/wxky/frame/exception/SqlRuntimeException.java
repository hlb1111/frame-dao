package com.hu.wxky.frame.exception;

public class SqlRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -5407178858302962030L;

	public SqlRuntimeException() {
		super();
	}

	public SqlRuntimeException(String msg) {
		super(msg);
	}

	public SqlRuntimeException(String msg, Throwable e) {
		super(msg, e);
	}
}
