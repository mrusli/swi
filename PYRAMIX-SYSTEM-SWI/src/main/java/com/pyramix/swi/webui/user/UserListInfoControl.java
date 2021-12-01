package com.pyramix.swi.webui.user;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.settings.Settings;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.settings.dao.SettingsDao;
import com.pyramix.swi.persistence.user.dao.UserDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class UserListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1228304534562291619L;

	private UserDao userDao;
	private SettingsDao settingsDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox userListbox;
	
	private List<User> userList;
	private Company defaultCompany;
	
	private final long DEFAULT_COMPANY_ID	= 1L;	
	
	public void onCreate$userListInfoWin(Event event) throws Exception {
		
		// obtain the settings
		Settings settings = 
				getSettingsDao().findSettingsById(DEFAULT_COMPANY_ID); 
		
		// set defaultCompany according to settings
		setDefaultCompany(
				settings.getSelectedCompany());

		formTitleLabel.setValue("User Login - "+getDefaultCompany().getCompanyType()+". "+
				getDefaultCompany().getCompanyLegalName()+" ("+
				getDefaultCompany().getCompanyDisplayName()+")");
		
		// display user
		displayUserListInfo();
	}
	
	private void displayUserListInfo() throws Exception {
		setUserList(getUserDao().findAllUsersByCompany(getDefaultCompany()));
				
		userListbox.setModel(new ListModelList<User>(getUserList()));
		userListbox.setItemRenderer(getUserListitemRenderer());
	}

	private ListitemRenderer<User> getUserListitemRenderer() {

		return new ListitemRenderer<User>() {
			
			@Override
			public void render(Listitem item, User user, int index) throws Exception {
				Listcell lc;
				
				// User-Name
				lc = new Listcell(user.getUser_name());
				lc.setParent(item);

				// Aktif -- enable / disable this user
				lc = new Listcell(user.isEnabled()? "Aktif" : "Tidak Aktif");
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(user.getReal_name());
				lc.setParent(item);
				
				// Email
				lc = new Listcell(user.getEmail());
				lc.setParent(item);
				
				// Tgl.Dibuat
				lc = new Listcell(dateToStringDisplay(asLocalDate(user.getCreateDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Tgl.Perubahan
				lc = new Listcell(dateToStringDisplay(asLocalDate(user.getLastEditDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// edit
				lc = initEdit(new Listcell(), user);
				lc.setParent(item);
				
			}

			private Listcell initEdit(Listcell listcell, User user) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, User> args = 
								Collections.singletonMap("user", user);

						Window userEditWin = (Window) Executions.createComponents("/user/UserDialog.zul", null, args);
						
						userEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								User user = (User) event.getData();
								
								// update
								getUserDao().update(user);
								
								// display
								displayUserListInfo();
								
							}
						});
						
						userEditWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$userListbox(Event event) throws Exception {
		int i=0;
		for (User user : userList) {
			i = i+(user.isEnabled() ? 1 : 0);
		}
		// info result - Total: 8 Users
		infoResultlabel.setValue("Total: "+i+" Aktif Users");
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$addButton(Event event) throws Exception {
		// display message box: an employee must be associated with a login user.
		// did you create an employee yet?
		Messagebox.show("Karyawan harus dibuat sebelum membuat user login.  Lanjut membuat User Login?", 
				"Question", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener() {

					@Override
					public void onEvent(Event event) throws Exception {
		                if(Messagebox.ON_OK.equals(event.getName())){
		                    displayUserDialog();
		                } else {
		                    // do nothing
		                	// System.out.println("Cancel");
		                }
		 						
					}

					private void displayUserDialog() {
						User user = new User();
						user.setCompany(getDefaultCompany());
						user.setEmployee(null);
						
						Map<String, User> args = 
								Collections.singletonMap("user", user);
						
						Window userCreateWin = (Window) Executions.createComponents("/user/UserDialog.zul", null, args);
						
						userCreateWin.addEventListener(Events.ON_OK, new EventListener<Event>() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								User user = (User) event.getData();
								
								// save
								getUserDao().save(user);
								
								// display user
								displayUserListInfo();
							}
						});
						
						userCreateWin.doModal();						
					}
			
		});
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public SettingsDao getSettingsDao() {
		return settingsDao;
	}

	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

	public Company getDefaultCompany() {
		return defaultCompany;
	}

	public void setDefaultCompany(Company defaultCompany) {
		this.defaultCompany = defaultCompany;
	}
	
}
