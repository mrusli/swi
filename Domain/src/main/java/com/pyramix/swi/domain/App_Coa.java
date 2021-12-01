package com.pyramix.swi.domain;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;

public class App_Coa {

	public static void main(String[] args) {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
		
		Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
		
		SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
		
		Session session = sessionFactory.openSession();

		Coa_01_AccountType accountType = session.get(Coa_01_AccountType.class, new Long(5));

		System.out.println(accountType.getAccountTypeName());
	}

}
