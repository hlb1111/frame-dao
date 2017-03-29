package com.hu.wxky.frame.test.bean;

import java.util.Date;

import com.hu.wxky.frame.annotation.PrimaryKey;
import com.hu.wxky.frame.bean.BaseBean;
import com.hu.wxky.frame.dao.GenerateKey;

public class UserInfo extends BaseBean {

	private static final long serialVersionUID = -2190047122624232714L;
	@PrimaryKey(generateKey=GenerateKey.ASSIGNED, refObj="idSnowflake")
	private Long id;
	
	private String username;
	
	private String loginAccount;
	
	private String loginPwd;
	
	private Date created;
	
	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
	
}
