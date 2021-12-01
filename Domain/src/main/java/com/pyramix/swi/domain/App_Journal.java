package com.pyramix.swi.domain;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.domain.coa.Coa_02_AccountGroup;
import com.pyramix.swi.domain.coa.Coa_03_SubAccount01;
import com.pyramix.swi.domain.coa.Coa_04_SubAccount02;
import com.pyramix.swi.domain.coa.Coa_05_Master;
import com.pyramix.swi.domain.voucher.VoucherJournal;
import com.pyramix.swi.domain.voucher.VoucherJournalDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;

public class App_Journal {

	public static void main(String[] args) {
		System.out.println( "Hello World!" );
		
		Configuration configObj = new Configuration();
		configObj.configure("hibernate.cfg.xml");
		
		ServiceRegistry serviceRegistryObj = 
				new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();
		
		Metadata metadata = new MetadataSources(serviceRegistryObj)
				.addAnnotatedClass(VoucherJournal.class)
				.addAnnotatedClass(VoucherSerialNumber.class)
				.addAnnotatedClass(VoucherJournalDebitCredit.class)
				.addAnnotatedClass(Coa_05_Master.class)
				.addAnnotatedClass(Coa_04_SubAccount02.class)
				.addAnnotatedClass(Coa_03_SubAccount01.class)
				.addAnnotatedClass(Coa_02_AccountGroup.class)
				.addAnnotatedClass(Coa_01_AccountType.class)
				.addResource("hibernate.cfg.xml")
				.getMetadataBuilder()
				.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
				.build();
		
		SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();	
		
		Session session = sessionFactory.openSession();
		
		VoucherJournal voucherJournal = session.get(VoucherJournal.class, new Long(1));
		
		System.out.println(voucherJournal.toString());
		
		session.close();
	}
}
