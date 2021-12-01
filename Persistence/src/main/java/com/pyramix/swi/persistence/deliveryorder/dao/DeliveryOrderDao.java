package com.pyramix.swi.persistence.deliveryorder.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderProduct;

public interface DeliveryOrderDao {

	public DeliveryOrder findDeliveryOrderById(Long id) throws Exception;
	
	public List<DeliveryOrder> findAllDeliveryOrder() throws Exception;
	
	public Long save(DeliveryOrder deliveryOrder) throws Exception;
	
	public void update(DeliveryOrder deliveryOrder) throws Exception;
	
	public DeliveryOrder findDeliveryOrderProductsByProxy(Long id) throws Exception;
	
	public DeliveryOrderProduct findInventoryCodeByProxy(Long id) throws Exception;

	public DeliveryOrder findCompanyByProxy(long id) throws Exception;

	/**
	 * select all DeliverOrder order by the DeliveryOrder date.  Use the desc parameter
	 * to list the DeliveryOrder either ascending (true) or descending (false).
	 * 
	 * @param desc - true / false for ascending or descending - descdending the latest first
	 * @param locationId - long value indicating the index of the company location - zero means all locations
	 * @return {@link List}<DeliveryOrder>
	 * @throws Exception
	 * 
	 */
	public List<DeliveryOrder> findAllDeliveryOrder_OrderBy_DeliveryOrderDate(boolean desc, long locationId) throws Exception;
	
	
	/**
	 * select all DeliveryOrder by the DeliveryOrder date using the startDate and endDate parameter.  Use 
	 * the desc parameter to list the DeliveryOrder either ascending (true) or descending (false).
	 * 
	 * @param startDate - start date (for today's date, use the same date as the endDate)
	 * @param endDate - end date
	 * @param desc - true / false for ascending or descending - descdending the latest first
	 * @param locationId - long value indicating the index of the company location - zero means all locations
	 * @return {@link List}<DeliveryOrder>
	 * @throws Exception
	 */
	public List<DeliveryOrder> findAllDeliveryOrder_By_DeliveryOrderDate(Date startDate, Date endDate, boolean desc, long locationId) throws Exception;

	/**
	 * SuratJalan is fetched Lazy, need to initialize the suratJalan property
	 * 
	 * @param id
	 * @return DeliveryOrder
	 * @throws Exception
	 */
	public DeliveryOrder findSuratJalanByProxy(long id) throws Exception;

	/**
	 * User create is fetched lazy, need to initialize the userCreate property
	 * 
	 * @param deliveryOrder
	 * @return DeliveryOrder
	 */
	public DeliveryOrder findUserCreateByProxy(long id) throws Exception;

}
