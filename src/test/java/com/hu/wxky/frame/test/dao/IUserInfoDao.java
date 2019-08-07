package com.hu.wxky.frame.test.dao;

import com.hu.wxky.frame.bean.PageVo;
import com.hu.wxky.frame.dao.IBaseDao;
import com.hu.wxky.frame.test.bean.SysUser;

public interface IUserInfoDao extends IBaseDao {
	
	public SysUser getByAccount(String account, String...attrs);
	/**
	 * 分页查询
	 * @param status
	 * @param page 分页信息
	 */
	public void getPage(int status, PageVo<SysUser> page);
	
	
}
