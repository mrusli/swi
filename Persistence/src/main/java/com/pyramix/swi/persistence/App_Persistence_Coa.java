package com.pyramix.swi.persistence;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.persistence.coa.dao.Coa_01_AccountTypeDao;

public class App_Persistence_Coa {

	private static ApplicationContext ctx;
	
	public static void main(String[] args) throws Exception {
		System.out.println("hello world!!!");

		ctx = new ClassPathXmlApplicationContext("CommonContext-Dao.xml");

		Coa_01_AccountTypeDao accountTypeDao = 
        		(Coa_01_AccountTypeDao) ctx.getBean("coa_01_AccountTypeDao");
    	Coa_01_AccountType accType = 
    			accountTypeDao.findCoa_01_AccountTypeById(new Long(1));
    	
    	System.out.println(accType.getAccountTypeName());
	}
}
