package com.pyramix.swi.persistence;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App_Persistence_VoucherJournal {
	
	@SuppressWarnings("unused")
	private static ApplicationContext ctx;
	
    public static void main( String[] args ) throws Exception {
        System.out.println( "Hello World!" );
        
		ctx = new ClassPathXmlApplicationContext("CommonContext-Dao.xml");

		// VoucherJournalDao voucherJournalDao = (VoucherJournalDao) ctx.getBean("voucherJournalDao");
		
		// VoucherJournal voucherJournal = voucherJournalDao.findVoucherJournalById(new Long(1));
		
		// System.out.println(voucherJournal);

    }
}
