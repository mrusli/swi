package com.pyramix.swi.persistence;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.user.dao.UserDao;

public class App_Persistence_AuthUser {

	private static ApplicationContext ctx;

	public static void Main(String[] args) {
		System.out.println("hello world!!!");

		ctx = new ClassPathXmlApplicationContext("CommonContext-Dao.xml");

		UserDao userDao = (UserDao) ctx.getBean("userDao");
		
		User user =	userDao.findUserByUsername("rusli");
		
		System.out.println(user);
	}
}
