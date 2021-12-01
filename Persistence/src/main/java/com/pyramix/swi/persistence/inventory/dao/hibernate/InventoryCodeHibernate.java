package com.pyramix.swi.persistence.inventory.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.inventory.dao.InventoryCodeDao;

public class InventoryCodeHibernate extends DaoHibernate implements InventoryCodeDao {

	@Override
	public InventoryCode findInventoryCodeById(Long id) throws Exception {

		return (InventoryCode) super.findById(InventoryCode.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InventoryCode> findAllInventoryCode() throws Exception {

		return new ArrayList<InventoryCode>(super.findAll(InventoryCode.class));
	}

	@Override
	public Long save(InventoryCode inventoryCode) throws Exception {
		
		return super.save(inventoryCode);
	}

	@Override
	public void update(InventoryCode inventoryCode) throws Exception {

		super.update(inventoryCode);
	}

}
