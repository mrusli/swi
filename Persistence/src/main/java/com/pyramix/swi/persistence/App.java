package com.pyramix.swi.persistence;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.user.dao.UserDao;

/**
 * Hello world!
 *
 */
public class App 
{
	private static ApplicationContext ctx;
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
		ctx = new ClassPathXmlApplicationContext("CommonContext-Dao.xml");

		UserDao userDao = (UserDao) ctx.getBean("userDao");
		
		User user =	userDao.findUserByUsername("judas");
		
		System.out.println(user);

    }
}
