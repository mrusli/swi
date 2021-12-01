package com.pyramix.swi.persistence.sql.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import com.pyramix.swi.domain.db.ConnectionSetting;
import com.pyramix.swi.domain.mysql.MySqlInventory;
import com.pyramix.swi.domain.sqlserver.SqlInventory;
import com.pyramix.swi.persistence.sql.dao.SqlUtilityDao;

public class SqlUtilityHibernate implements SqlUtilityDao {

	private SessionFactory sessionFactory;
	
	@Override
	public Configuration setHibernateConfiguration(ConnectionSetting sqlSetting) {
		Configuration hibernateConfig = new Configuration();
		
		hibernateConfig.setProperty("hibernate.connection.url", 
				sqlSetting.getUrl());
		hibernateConfig.setProperty("hibernate.connection.username",
				sqlSetting.getUsername());
		hibernateConfig.setProperty("hibernate.connection.password",
				sqlSetting.getPassword());
		hibernateConfig.setProperty("hibernate.connection.driver_class",
				sqlSetting.getDriverClass());
		hibernateConfig.setProperty("hibernate.dialect",
				sqlSetting.getDialect());
		hibernateConfig.setProperty("hibernate.show_sql",
				sqlSetting.getShowSql());
		hibernateConfig.setProperty("hibernate.connection.pool_size",
				sqlSetting.getPoolSize());
		
		return hibernateConfig;
	}
	
	@Override
	public ServiceRegistry buildServiceRegistry(Configuration hibernateConfig, String hibernateConfigXML) {

		StandardServiceRegistryBuilder ssr = new StandardServiceRegistryBuilder();
		ssr.applySettings(hibernateConfig.getProperties());
		ssr.configure(hibernateConfigXML);

		return ssr.build();
	}
	
	@Override
	public SessionFactory getSessionFactory(ServiceRegistry serviceRegistry) {
		// build metadata
		Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build(); 

		// build sessionfatory
		return metadata.getSessionFactoryBuilder().build();
	}

	public ConnectionSetting findSqlSettingById(Long id) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(ConnectionSetting.class);
		
		try {
		
			return (ConnectionSetting) criteria.add(Restrictions.idEq(id)).uniqueResult();
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
	
	public void updateSqlSetting(ConnectionSetting sqlSetting) throws Exception {
		Session session = getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		
		try {
			session.update(sqlSetting);
			
			tx.commit();
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}	
	
	@Override
	public void connect() {
		Configuration hibernateConfig = new Configuration();
		
		// set different properties
		hibernateConfig.setProperty("hibernate.connection.url", 
				"jdbc:jtds:sqlserver://192.168.100.13;databaseName=SWI");
		hibernateConfig.setProperty("hibernate.connection.username",
				"sa");
		hibernateConfig.setProperty("hibernate.connection.password",
				"sa");
		hibernateConfig.setProperty("hibernate.connection.driver_class",
				"net.sourceforge.jtds.jdbc.Driver");
		hibernateConfig.setProperty("hibernate.dialect",
				"org.hibernate.dialect.SQLServerDialect");
		hibernateConfig.setProperty("hibernate.show_sql",
				"false");
		hibernateConfig.setProperty("hibernate.connection.pool_size",
				"50");

		StandardServiceRegistryBuilder ssr = new StandardServiceRegistryBuilder();
		ssr.applySettings(hibernateConfig.getProperties());
		ssr.configure("hibernate.cfg.sqlserver.xml");

		ServiceRegistry serviceRegistry = ssr.build();
        
		Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
		
		SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
		
		Session session = sessionFactory.openSession();
		
		// SqlInventory sqlInventory = session.get(SqlInventory.class, 404); 
		
		// System.out.println(sqlInventory.getCode());
		
		session.close();
	}	
	
	// --- ms sql table operations --- //
	
	@SuppressWarnings("unchecked")
	@Override
	public SqlInventory findSqlInventoryById(SessionFactory sessionFactory, int id) {
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(SqlInventory.class);
		criteria.add(Restrictions.eq("noUrut", id));
		
		try {
			List<SqlInventory> sqlInventoryList = new ArrayList<SqlInventory>(criteria.list());

			return sqlInventoryList.isEmpty() ? null : sqlInventoryList.get(0);
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SqlInventory> findAllSqlInventory(SessionFactory sessionFactory) throws Exception {
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(SqlInventory.class);
		criteria.add(Restrictions.eq("status", " "));
		// criteria.setMaxResults(50);
		
		try {
			
			return new ArrayList<SqlInventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SqlInventory> findSqlInventory_ByShape(SessionFactory sessionFactory, String shape) throws Exception {
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(SqlInventory.class);
		criteria.add(Restrictions.eq("shape", shape));
		criteria.add(Restrictions.eq("status", " "));
		// criteria.setMaxResults(50);
		
		try {
			
			return new ArrayList<SqlInventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}	
	
	// mysql table operations
	
	public MySqlInventory findMySqlInventoryById(SessionFactory sessionFactory, Long id) {
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(MySqlInventory.class);
		
		try {
			
			return (MySqlInventory) criteria.add(Restrictions.idEq(id)).uniqueResult();
			
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MySqlInventory> findAllMySqlInventoryByStatus(SessionFactory sessionFactory, int status) {
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(MySqlInventory.class);
		criteria.add(Restrictions.eq("inventoryStatus", status));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<MySqlInventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MySqlInventory> findAllMySqlInventoryByPacking(SessionFactory sessionFactory, Long packing) {
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(MySqlInventory.class);
		criteria.add(Restrictions.eq("inventoryStatus", 3));
		criteria.add(Restrictions.eq("inventoryPacking", packing));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<MySqlInventory>(criteria.list());
			
		} finally {
			session.close();
		}
	}
	
	
	// --- get/set --- //
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
