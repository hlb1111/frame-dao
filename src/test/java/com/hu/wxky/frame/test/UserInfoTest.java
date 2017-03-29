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
import com.hu.wxky.frame.test.bean.UserInfo;
import com.hu.wxky.frame.test.dao.IUserInfoDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring-config.xml")
public class UserInfoTest extends AbstractJUnit4SpringContextTests {
	
	
	@Resource IUserInfoDao userInfoDao;
	
	//@Test
	public void saveTest(){
		long t1 = System.currentTimeMillis();
		for(int i=0; i<1000; i++){
			UserInfo user = new UserInfo();
			user.setCreated(new Date());
			user.setLoginAccount("test" + new Random().nextInt(100000));
			user.setUsername("王完"+ new Random().nextInt(100000));
			user.setLoginPwd("g"+ new Random().nextInt(100000));
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
			UserInfo info = userInfoDao.getByAccount(account, "id", "username", "loginPwd", "status");
			System.out.println(info);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("cost times: "+(t2-t1)+"ms");
		
	}
	@Test
	public void updateTest(){
		UserInfo info = userInfoDao.getById(UserInfo.class, 296575103221764096L);
		
		//更新某些字段
		UserInfo tmp = new UserInfo();
		tmp.setId(info.getId());
		tmp.setStatus(-1);
		userInfoDao.update(tmp);
	}
	@Test
	public void queryByObj(){
		UserInfo where = new UserInfo();
		where.setStatus(1);
		where.setLoginAccount("root");
		List<UserInfo> list = userInfoDao.query(where);
		System.out.println(list.size());
	}
	@Test
	public void getPage(){
		PageVo<UserInfo> page = new PageVo<UserInfo>();
		userInfoDao.getPage(1, page);
		List<UserInfo> first = page.getList();
		System.out.println(first);
		
		page.setPage(2);
		userInfoDao.getPage(1, page);
		List<UserInfo> two = page.getList();
		System.out.println(two);
		
	}
}
