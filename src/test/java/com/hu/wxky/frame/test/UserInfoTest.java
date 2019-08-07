package com.hu.wxky.frame.test;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hu.wxky.frame.bean.PageVo;
import com.hu.wxky.frame.test.bean.SysUser;
import com.hu.wxky.frame.test.dao.IUserInfoDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring-config.xml")
public class UserInfoTest extends AbstractJUnit4SpringContextTests {
	
	
	@Resource IUserInfoDao userInfoDao;
	
	//@Test
	public void saveTest(){
		long t1 = System.currentTimeMillis();
		for(int i=0; i<1000; i++){
			SysUser user = new SysUser();
			user.setCreated(new Date());
			user.setLoginAccount("test" + new Random().nextInt(100000));
			user.setUsername("王完"+ new Random().nextInt(100000));
			user.setLoginPassword("g"+ new Random().nextInt(100000));
			user.setStatus(1);
			userInfoDao.save(user);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("cost times: "+(t2-t1)+"ms");
	}
	@Test
	public void queryOneTest(){
		long t1 = System.currentTimeMillis();
		String account = "root";
		for(int i=0; i<5; i++){
			SysUser info = userInfoDao.getByAccount(account, "id", "username", "loginPwd", "status");
			System.out.println(info);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("cost times: "+(t2-t1)+"ms");
		
	}
	@Test
	public void updateTest(){
		SysUser info = userInfoDao.getById(SysUser.class, 1);
		
		//更新某些字段
		SysUser tmp = new SysUser();
		tmp.setId(info.getId());
		tmp.setStatus(-1);
		userInfoDao.update(tmp);
	}
	@Test
	public void queryByObj(){
		SysUser where = new SysUser();
		where.setStatus(1);
		where.setLoginAccount("root");
		List<SysUser> list = userInfoDao.query(where);
		System.out.println(list.size());
	}
	@Test
	public void getPage(){
		PageVo<SysUser> page = new PageVo<SysUser>();
		userInfoDao.getPage(1, page);
		List<SysUser> first = page.getList();
		System.out.println(first);
		
		page.setPage(2);
		userInfoDao.getPage(1, page);
		List<SysUser> two = page.getList();
		System.out.println(two);
		
	}
}
