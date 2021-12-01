package com.pyramix.swi.webui.utility;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.pyramix.swi.domain.db.ConnectionSetting;
import com.pyramix.swi.persistence.sql.dao.SqlUtilityDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class UtilityListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3979357630984620046L;
	
	private SqlUtilityDao sqlUtilityDao;
	
	private Textbox urlTextbox, usernameTextbox, passwordTextbox, driverTextbox,
		dialectTextbox;
	
	private ConnectionSetting sqlSetting;
	
	public void onCreate$utilityListInfoWin(Event event) throws Exception {
		setSqlSetting(getSqlUtilityDao().findSqlSettingById(new Long(1)));
		
		setSqlSettingInfo();
	}
	
	private void setSqlSettingInfo() {
		urlTextbox.setValue(getSqlSetting().getUrl());
		usernameTextbox.setValue(getSqlSetting().getUsername());
		passwordTextbox.setValue(getSqlSetting().getPassword());
		driverTextbox.setValue(getSqlSetting().getDriverClass());
		dialectTextbox.setValue(getSqlSetting().getDialect());
	}
	
	public ConnectionSetting getSqlSettingInfo() {
		getSqlSetting().setUrl(urlTextbox.getValue());
		getSqlSetting().setUsername(usernameTextbox.getValue());
		getSqlSetting().setPassword(passwordTextbox.getValue());
		getSqlSetting().setDriverClass(driverTextbox.getValue());
		getSqlSetting().setDialect(dialectTextbox.getValue());
		
		return getSqlSetting();
	}

	public void onClick$testConnectionButton(Event event) throws Exception {
		
		try {
			// set 
			Configuration configuration = getSqlUtilityDao().setHibernateConfiguration(getSqlSetting());
			
			// build serviceRegistry
			ServiceRegistry serviceRegistry = sqlUtilityDao.buildServiceRegistry(
					configuration, "hibernate.cfg.sqlserver.xml");
			
			// obtain the sessionFactory
			SessionFactory sessionFactory = sqlUtilityDao.getSessionFactory(
					serviceRegistry);
			
			// open sessionFactory
			Session session = sessionFactory.openSession();

			// display success 
			Messagebox.show("Test Koneksi Berhasil: "+session.isConnected(), 
					"Info", Messagebox.OK,  Messagebox.INFORMATION);			

		} catch (Exception e) {
			Messagebox.show("Koneksi gagal :"+e.getMessage(), 
					"Error", Messagebox.OK,  Messagebox.ERROR);			
		}
	}

	public void onClick$updateButton(Event event) throws Exception {
		try {
			// update
			getSqlUtilityDao().updateSqlSetting(getSqlSettingInfo());
			
			// notify
			Clients.showNotification("Koneksi database sukses tersimpan.");
			
		} catch (Exception e) {
			Messagebox.show("Koneksi database tidak dapat disimpan. "+e.getMessage(), 
					"Error", Messagebox.OK,  Messagebox.ERROR);			
		}
	}
	
	public SqlUtilityDao getSqlUtilityDao() {
		return sqlUtilityDao;
	}

	public void setSqlUtilityDao(SqlUtilityDao sqlUtilityDao) {
		this.sqlUtilityDao = sqlUtilityDao;
	}

	public ConnectionSetting getSqlSetting() {
		return sqlSetting;
	}

	public void setSqlSetting(ConnectionSetting sqlSetting) {
		this.sqlSetting = sqlSetting;
	}
	
}
