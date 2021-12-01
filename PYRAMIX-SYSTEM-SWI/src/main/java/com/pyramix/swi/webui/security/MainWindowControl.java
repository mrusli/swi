package com.pyramix.swi.webui.security;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;

import com.pyramix.swi.domain.user.UserRole;
import com.pyramix.swi.persistence.userrole.dao.UserRoleDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class MainWindowControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6125532679695713737L;

	private UserRoleDao userRoleDao;
	
	private Label pageTitleLabel, productVersionLabel, loginUserInformationLabel;
	private Hlayout userManagerHlayout, userCheckerHlayout, userUserHlayout;
	
	private UserSecurityDetails userSecurityDetails;

	private final long ROLE_MANAGER_ID 	= 1L;
	private final long ROLE_CHECKER_ID 	= 2L;
	// private final long ROLE_USER_ID		= 3L;
	
	public void onCreate$mainWindow(Event event) throws Exception {
		pageTitleLabel.setValue("Inventory and Accounting System");
		
		// web app properties:
		// 0 : version number
        List<String> propList = getWebAppProperties();
        productVersionLabel.setValue("Version: "+propList.get(0)+" Build No.: "+propList.get(1));
                
		setUserSecurityDetails(
				(UserSecurityDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		
		loginUserInformationLabel.setValue("Informasi Login: "+getUserSecurityDetails().getLoginUser().getReal_name()+
				"["+getUserSecurityDetails().getLoginUser().getUser_name()+"]");
		
		// UserRole loginUserRole =
		//		getUserSecurityDetails().getLoginUser().getUser_roles().iterator().next();
		
		UserRole userRoleManager = getUserRoleDao().findUserRoleById(ROLE_MANAGER_ID);
		UserRole userRoleChecker = getUserRoleDao().findUserRoleById(ROLE_CHECKER_ID);
		// UserRole userRoleUser = getUserRoleDao().findUserRoleById(ROLE_USER_ID);
		
		Set<UserRole> userRoles = getUserSecurityDetails().getLoginUser().getUser_roles();
		// System.out.println(userRoles);
		// System.out.println(userRoles.contains(userRoleManager));
		
		/*
		 * ROLE_MANAGER will be able to Create and Post Voucher
		 * ROLE_USER will be able to Create Voucher ONLY
		 * ROLE_CHECKER is the same as ROLE_MANAGER - but will not be able to see the voucher created by ROLE_MANAGER
		 */
		
		if (userRoles.contains(userRoleManager)) {
			// System.out.println("ROLE_MANAGER");
			
			userManagerHlayout.setVisible(true);
			userCheckerHlayout.setVisible(false);
			userUserHlayout.setVisible(false);			
		} else if (userRoles.contains(userRoleChecker)) {
			// System.out.println("ROLE_CHECKER");
			
			userManagerHlayout.setVisible(false);
			userCheckerHlayout.setVisible(true);
			userUserHlayout.setVisible(false);
		} else {
			// System.out.println("ROLE_USER");
			
			userManagerHlayout.setVisible(false);
			userCheckerHlayout.setVisible(false);
			userUserHlayout.setVisible(true);			
		}
		
		/* 
			if (loginUserRole.getRole_name().matches("ROLE_MANAGER")) {
				
				
			} else if (loginUserRole.getRole_name().matches("ROLE_CHECKER")) {
	
				
			} else if (loginUserRole.getRole_name().matches("ROLE_USER")) {
	
				
			}
		 */
		
	}

	private List<String> getWebAppProperties() {
		// List of string values that will be returned
		List<String> propertyList = new ArrayList<String>();
		
		Properties prop = new Properties();
		
		try {
			InputStream inputStream = null;

			String filename = "version.properties";
			
			inputStream = getClass().getClassLoader().getResourceAsStream(filename);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("Property File not found.");
			}
			// load a properties file
			prop.load(inputStream);			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		propertyList.add(prop.getProperty("build.version"));
		propertyList.add(prop.getProperty("build.timestamp"));
		propertyList.add(prop.getProperty("build.name"));
		
		return propertyList;
	}
		
	public UserSecurityDetails getUserSecurityDetails() {
		return userSecurityDetails;
	}

	public void setUserSecurityDetails(UserSecurityDetails userSecurityDetails) {
		this.userSecurityDetails = userSecurityDetails;
	}

	public UserRoleDao getUserRoleDao() {
		return userRoleDao;
	}

	public void setUserRoleDao(UserRoleDao userRoleDao) {
		this.userRoleDao = userRoleDao;
	}

	
}
