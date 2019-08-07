package com.hu.wxky.frame.test.dao;

import org.springframework.stereotype.Repository;

import com.hu.wxky.frame.bean.PageVo;
import com.hu.wxky.frame.bean.SqlQuery;
import com.hu.wxky.frame.dao.BaseDao;
import com.hu.wxky.frame.dao.SqlPageQuery;
import com.hu.wxky.frame.test.bean.SysUser;

@Repository
public class UserInfoDao extends BaseDao implements IUserInfoDao {

	@Override
	public SysUser getByAccount(String account, String... attrs) {
		StringBuilder sb = attrToSql(SysUser.class, attrs);
		sb.append(WHERE).append("login_account=?");
		return queryOne(SysUser.class, sb.toString(), account);
	}

	@Override
	public void getPage(int status, PageVo<SysUser> page) {
		SqlQuery sqlQuery = new SqlQuery();
		sqlQuery.setSelect("SELECT id, username, login_account, status, created ");
		sqlQuery.setFrom("FROM sys_user ");
		sqlQuery.setWhere("WHERE status=?");
		SqlPageQuery.query(this, SysUser.class, sqlQuery, page, status);
		
	}
	
}
