package com.pyramix.swi.persistence.voucher.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.voucher.Giro;

public interface GiroDao {

	public Giro findGiroById(Long id) throws Exception;
	
	public List<Giro> findAllGiro() throws Exception;

	public Long save(Giro giro) throws Exception;
	
	public void update(Giro giro) throws Exception;

	public Giro findCustomerByProxy(long id) throws Exception;

	public Giro findVoucherGiroReceiptByProxy(long id) throws Exception;

	public List<Giro> findAllGiro_OrderBy_ReceivedDate(boolean desc, boolean paidStatus) throws Exception;
	
	public List<Giro> findAllGiro_OrderBy_GiroDueDate(boolean desc, boolean paidStatus) throws Exception;

	public Giro findVoucherPaymentByProxy(long id) throws Exception;

	public List<Giro> findAllGiro_OrderBy_PaidGiroDate(boolean desc, boolean paidStatus) throws Exception;

	public List<Giro> findGiro_By_DueGiroDate(Date startDate, Date endDate, boolean paidStatus) throws Exception;
}
