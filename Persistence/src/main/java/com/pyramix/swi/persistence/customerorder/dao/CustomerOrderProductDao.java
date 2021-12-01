package com.pyramix.swi.persistence.customerorder.dao;

import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;

public interface CustomerOrderProductDao {

	public CustomerOrderProduct findInventoryCodeByProxy(long id) throws Exception;

	public CustomerOrderProduct findInventoryByProxy(long id) throws Exception;
}
