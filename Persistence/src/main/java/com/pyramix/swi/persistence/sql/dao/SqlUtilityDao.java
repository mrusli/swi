package com.pyramix.swi.persistence.sql.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.pyramix.swi.domain.db.ConnectionSetting;
import com.pyramix.swi.domain.mysql.MySqlInventory;
import com.pyramix.swi.domain.sqlserver.SqlInventory;

public interface SqlUtilityDao {

	// configuration and db-connection settings
	
	public Configuration setHibernateConfiguration(
			ConnectionSetting sqlSetting);
	
	public ServiceRegistry buildServiceRegistry(
			Configuration hibernateConfig, String hibernateConfigXML);
	
	public SessionFactory getSessionFactory(
			ServiceRegistry serviceRegistry);
	
	public void updateSqlSetting(ConnectionSetting sqlSetting) throws Exception;

	public void connect();

	// sql server table operations
	
	public SqlInventory findSqlInventoryById(
			SessionFactory sessionFactory, int id);
		
	public List<SqlInventory> findAllSqlInventory(
			SessionFactory sessionFactory) throws Exception;
	
	public ConnectionSetting findSqlSettingById(Long id) throws Exception;

	public List<SqlInventory> findSqlInventory_ByShape(
			SessionFactory sessionFactory, String shape) throws Exception;
	
	// mysql table operations
	
	public MySqlInventory findMySqlInventoryById(
			SessionFactory sessionFactory, Long id);
	
	public List<MySqlInventory> findAllMySqlInventoryByStatus(
			SessionFactory sessionFactory, int status);

	public List<MySqlInventory> findAllMySqlInventoryByPacking(
			SessionFactory sessionFactory, Long packing);
}
