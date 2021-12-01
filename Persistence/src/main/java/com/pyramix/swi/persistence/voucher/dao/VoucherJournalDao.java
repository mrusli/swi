package com.pyramix.swi.persistence.voucher.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.voucher.VoucherJournal;

public interface VoucherJournalDao {

	public VoucherJournal findVoucherJournalById(long id) throws Exception;
	
	public List<VoucherJournal> findAllVoucherJournal() throws Exception;
	
	/**
	 * selects all VoucherJournal order by TransactionDate attribute.
	 * 
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 *  
	 * @return List<VoucherJournal>
	 * 
	 * @throws Exception
	 */
	public List<VoucherJournal> findAllVoucherJournal_OrderBy_TransactionDate(boolean desc) throws Exception;
	
	/**
	 * selects VoucherJournal based on the startDate and endDate of the TransactionDate attribute.
	 * to select VoucherJournal for today, pass the same date for the start and end dates.
	 * 
	 * @param startDate - start date
	 * @param endDate - end date
	 * @param desc - determines the order.  Passing a true value returns
	 * descending order.  Passing a false value returns ascending order.
	 * 
	 * @return List<VoucherJournal>
	 * 
	 * @throws Exception
	 */
	public List<VoucherJournal> findAllVoucherJournal_OrderBy_TransactionDate(Date startDate, Date endDate, boolean desc) throws Exception;
	
	public Long save(VoucherJournal voucherJOurnal) throws Exception;
	
	public void update(VoucherJournal voucherJournal) throws Exception;

	public VoucherJournal getVoucherJournalUserCreateByProxy(Long id) throws Exception;
	
	public VoucherJournal findVoucherJournalGeneralLedgerByProxy(long id) throws Exception;
}
