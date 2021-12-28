package com.pyramix.swi.persistence.settlement.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;

public interface SettlementDao {
	
	public Settlement findSettlementById(long id) throws Exception;
	
	public List<Settlement> findAllSettlement() throws Exception;
	
	public long save(Settlement settlement) throws Exception;
	
	public void update(Settlement settlement) throws Exception;
	
	public List<Settlement> findAllSettlement_OrderBy_SettlementDate(boolean desc) throws Exception;

	public List<Settlement> findSettlement_By_SettlementDate(boolean desc, Date startDate, Date endDate) throws Exception;

	public List<Settlement> findAllSettlement_By_Customer_OrderBy_SettlementDate(boolean desc, Customer customer) throws Exception;
		
	public List<Settlement> findSettlement_By_Customer_By_SettlementDate(boolean desc, Customer customer, Date  startDate, Date endDate) throws Exception;	
	
	public Settlement findCustomerByProxy(long id) throws Exception;

	public Settlement findVoucherGiroReceiptByProxy(long id) throws Exception;
	
	public List<SettlementDetail> findSettlementDetail_By_CustomerOrderId(long id) throws Exception;

	public SettlementDetail getSettlementByProxy(long id) throws Exception;

	public Settlement findVoucherPaymentByProxy(long id) throws Exception;

	public Settlement getSettlementUserCreateByProxy(Long id) throws Exception;
	
	public Settlement findCustomerReceivableByProxy(long id) throws Exception;
	
	public SettlementDetail findSettlementFromSettlementDetailByProxy(long id) throws Exception;
}