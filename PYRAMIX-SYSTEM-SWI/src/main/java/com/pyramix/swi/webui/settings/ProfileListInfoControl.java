package com.pyramix.swi.webui.settings;

import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.user.dao.UserDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.security.UserSecurityDetails;

public class ProfileListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5381909371461145028L;

	private UserDao userDao;
	
	private Label formTitleLabel, organizationLegalNameLabel, employeeNameLabel,
		userNameLabel;
	private Textbox passwordTextbox;
	
	private UserSecurityDetails userSecurityDetails;
	private User loginUser;
	
	public void onCreate$profileListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("User Profile");
		
		// login user
		setUserSecurityDetails(
				(UserSecurityDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		// user
		setLoginUser(
				getUserSecurityDetails().getLoginUser());
		
		// display
		displayUserProfile();
		
	}

	private void displayUserProfile() throws Exception {
		// Nama Perusahaan
		User userByProxy = getUserDao().findUserCompanyByProxy(getLoginUser().getId());
		organizationLegalNameLabel.setValue(userByProxy.getCompany().getCompanyType()+". "+
				userByProxy.getCompany().getCompanyLegalName()+" ("+
				userByProxy.getCompany().getCompanyDisplayName()+")");
		
		// Nama Karyawan
		Employee employee = getLoginUser().getEmployee();
		employeeNameLabel.setValue(employee.getName());
		
		// Nama-Login
		userNameLabel.setValue(getLoginUser().getUser_name());
		
		// Password
		passwordTextbox.setValue(getLoginUser().getUser_password());
	}

	public void onClick$changePasswordButton(Event event) throws Exception {
		Window editPasswordWin = 
				(Window) Executions.createComponents("/user/UserPasswordDialog.zul", null, null);
		
		editPasswordWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				String encodedPassword = (String) event.getData();
				
				// display the encoded password
				passwordTextbox.setValue(encodedPassword);
				
				// set the encoded password into the user object
				getLoginUser().setUser_password(passwordTextbox.getValue());
				
				// save the user object
				getUserDao().update(getLoginUser());

				// notify user
				Clients.showNotification("Perubahan Password sudah disimpan.", 
						"info", null, "bottom_right", 0);
			}
		});
		
		editPasswordWin.doModal();		
	}
	
	public UserSecurityDetails getUserSecurityDetails() {
		return userSecurityDetails;
	}

	public void setUserSecurityDetails(UserSecurityDetails userSecurityDetails) {
		this.userSecurityDetails = userSecurityDetails;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
