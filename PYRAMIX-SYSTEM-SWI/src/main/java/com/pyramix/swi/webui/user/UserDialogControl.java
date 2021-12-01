package com.pyramix.swi.webui.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.domain.user.UserRole;
import com.pyramix.swi.persistence.userrole.dao.UserRoleDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class UserDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7964407205812630704L;

	private UserRoleDao userRoleDao;
	
	private Window userDialogWin;
	private Textbox userNameTextbox, namaTextbox, emailTextbox, 
		passwordTextbox, employeeNameTextbox;
	private Datebox createDateDatebox, lastEditedDatebox;
	private Listbox existingRoleListbox, availableRoleListbox;
	private Checkbox enabledCheckbox;
	private Button employeeButton;
	private Grid employeeSelectionGrid;
	
	private User user;
	private Employee selectedEmployee;
	
	private final String EMPLOYEE_LISTINFO_REQUEST_PATH = "/employee/EmployeeListInfo.zul";
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setUser((User) arg.get("user"));
	}

	public void onCreate$userDialogWin(Event event) throws Exception {
		// request path from:
		// 	/employee/EmployeeListInfo.zul - to create a new user
		//	/user/UserListInfo.zul - to create a new user and to edit an existing user
		String requestPath = Executions.getCurrent().getDesktop().getRequestPath();
		
		if (getUser().getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			userDialogWin.setTitle("Membuat (Create) User Login - "+
					getUser().getCompany().getCompanyType()+". "+
					getUser().getCompany().getCompanyLegalName()+" ("+
					getUser().getCompany().getCompanyDisplayName()+")");
			
			// if this is from /employee/EmployeeListInfo.zul - to create a new user
			if (requestPath.matches(EMPLOYEE_LISTINFO_REQUEST_PATH)) {
				
				// no need to select employee
				employeeSelectionGrid.setVisible(false);
			} else {
				
				// must select employee to create user
				employeeSelectionGrid.setVisible(true);
			}
			
		} else {
			// edit
			userDialogWin.setTitle("Merubah (Edit) User");
			// indicate the employee name
			employeeNameTextbox.setValue(getUser().getEmployee().getName());
			// disable the button for employee name selection
			employeeButton.setDisabled(true);
			
		}
		
		// if user is created from EmployeeDialog, selectedEmployee will have a value
		// if user is created from UserListInfo, selectedEmployee will be null
		setSelectedEmployee(getUser().getEmployee());
		
		createDateDatebox.setLocale(getLocale());
		createDateDatebox.setFormat(getLongDateFormat());
		lastEditedDatebox.setLocale(getLocale());
		lastEditedDatebox.setFormat(getLongDateFormat());
		
		displayUserInfo();
	}
	
	private void displayUserInfo() throws Exception {
		if (getUser().getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			userNameTextbox.setValue(getUser().getUser_name());
			namaTextbox.setValue(getUser().getReal_name());
			enabledCheckbox.setChecked(true);
			emailTextbox.setValue(getUser().getEmail());
			createDateDatebox.setValue(asDate(getLocalDate()));
			lastEditedDatebox.setValue(asDate(getLocalDate()));

			passwordTextbox.setValue("");
			
			// init
			getUser().setUser_roles(new HashSet<UserRole>());
			
			// display
			displayUserRoleInfo(getUser().getUser_roles());
			displayAvailableRoleInfo(getUser().getUser_roles());
			
		} else {
			// edit
			userNameTextbox.setValue(getUser().getUser_name());
			namaTextbox.setValue(getUser().getReal_name());
			enabledCheckbox.setChecked(getUser().isEnabled());
			emailTextbox.setValue(getUser().getEmail());
			
			createDateDatebox.setValue(getUser().getCreateDate());
			lastEditedDatebox.setValue(asDate(getLocalDate()));
			
			passwordTextbox.setValue(getUser().getUser_password());
			
			displayUserRoleInfo(getUser().getUser_roles());
			displayAvailableRoleInfo(getUser().getUser_roles());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void displayUserRoleInfo(Set<UserRole> user_roles) {
		existingRoleListbox.setModel(new ListModelList(user_roles));
		existingRoleListbox.setItemRenderer(getExistingRoleListitemRenderer());
	}
	
	private ListitemRenderer<UserRole> getExistingRoleListitemRenderer() {
		
		return new ListitemRenderer<UserRole>() {
			
			@Override
			public void render(Listitem item, UserRole userRole, int index) throws Exception {
				Listcell lc;
				
				// checkbox
				lc = initCheckbox(new Listcell(), userRole);
				lc.setParent(item);
				
				// Role
				lc = new Listcell(userRole.getRole_name());
				lc.setParent(item);
				
				item.setValue(userRole);
			}

			private Listcell initCheckbox(Listcell listcell, UserRole userRole) {
				Checkbox checkbox = new Checkbox();
				checkbox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	private void displayAvailableRoleInfo(Set<UserRole> user_roles) throws Exception {
		Set<UserRole> userRoleSet = new HashSet<>(getUserRoleDao().findAllUserRole());
		
		userRoleSet.removeAll(user_roles);
		
		availableRoleListbox.setModel(new ListModelList<>(userRoleSet));
		availableRoleListbox.setItemRenderer(getAvailableRoleListitemRenderer());
		
	}

	private ListitemRenderer<UserRole> getAvailableRoleListitemRenderer() {

		return new ListitemRenderer<UserRole>() {
			
			@Override
			public void render(Listitem item, UserRole userRole, int index) throws Exception {
				Listcell lc;
				
				// checkbox
				lc = initCheckbox(new Listcell(), userRole);
				lc.setParent(item);

				// Role
				lc = new Listcell(userRole.getRole_name());
				lc.setParent(item);

				item.setValue(userRole);
			}

			private Listcell initCheckbox(Listcell listcell, UserRole userRole) {
				Checkbox checkbox = new Checkbox();
				checkbox.setParent(listcell);				
				
				return listcell;
			}
		};
	}

	public void onClick$employeeButton(Event event) throws Exception {
		Map<String, Company> args = 
				Collections.singletonMap("company", getUser().getCompany());
		
		Window employeeListDialogWin = 
				(Window) Executions.createComponents("/employee/EmployeeListDialog.zul", null, args);

		employeeListDialogWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				setSelectedEmployee((Employee) event.getData());
				
				employeeNameTextbox.setValue(getSelectedEmployee().getName());
				userNameTextbox.setValue(createUserName(getSelectedEmployee().getName()));
				namaTextbox.setValue(getSelectedEmployee().getName());
				emailTextbox.setValue(getSelectedEmployee().getEmail());
			}

			private String createUserName(String realName) throws Exception {
				if (realName.isEmpty()) {
					throw new Exception("Tidak Ada Nama untuk dibuatkan Nama Login");
				} else {
					String arr[] = realName.split(" ", 2);

					return arr[0];
				}
			}
		});
		
		employeeListDialogWin.doModal();
	}
	
	public void onClick$editPasswordButton(Event event) throws Exception {
		Window editPasswordWin = (Window) Executions.createComponents("/user/UserPasswordDialog.zul", null, null);
		
		editPasswordWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				String encodedPassword = (String) event.getData();
				
				passwordTextbox.setValue(encodedPassword);
			}
		});
		
		editPasswordWin.doModal();		
	}
	
	public void onClick$addUserRoleButton(Event event) throws Exception {
		// availableRoleListbox - check which of the roles is/are checked - setup an empty set to collect the checked role
		Set<UserRole> addedRole = new HashSet<UserRole>();

		for (Listitem item : availableRoleListbox.getItems()) {
			Checkbox checkbox = (Checkbox) item.getChildren().get(0).getFirstChild();
			
			if (checkbox.isChecked()) {
				addedRole.add(item.getValue());
			}
		}
		
		// add
		addToExistingListbox(getUser(), addedRole);

		// remove		
		removeFromAvailableListbox(getUser(), addedRole);
		
	}
	
	private void addToExistingListbox(User selUser, Set<UserRole> addedRole) {
		Set<UserRole> existingRole = selUser.getUser_roles();
		
		// union
		existingRole.addAll(addedRole);
		
		// list
		existingRoleListbox.setModel(new ListModelList<UserRole>(existingRole));
		existingRoleListbox.setItemRenderer(getExistingRoleListitemRenderer());		
	}

	private void removeFromAvailableListbox(User selUser, Set<UserRole> addedRole) throws Exception {
		Set<UserRole> availableRoleSet = new HashSet<UserRole>(getUserRoleDao().findAllUserRole());
		
		availableRoleSet.removeAll(selUser.getUser_roles());
		
		availableRoleSet.removeAll(addedRole);
		
		// list
		availableRoleListbox.setModel(new ListModelList<UserRole>(availableRoleSet));
		availableRoleListbox.setItemRenderer(getAvailableRoleListitemRenderer());
		
	}

	public void onClick$removeUserRoleButton(Event event) throws Exception {
		// existingRoleListbox - check which of the roles is/are checked - setup an empty set to collect the checked role
		Set<UserRole> removedRole = new HashSet<UserRole>();
		
		for (Listitem item : existingRoleListbox.getItems()) {
			Checkbox checkbox = (Checkbox) item.getChildren().get(0).getFirstChild();
			
			if (checkbox.isChecked()) {
				removedRole.add(item.getValue());
			}
		}
		
		// remove
		removeFromExistingListbox(getUser(), removedRole);
		
		/* 
		 * availableRoleListbox - add the set into this listbox
		 */
		
		// get the initial roles
		Set<UserRole> initialRoleSet = getUser().getUser_roles();
		
		// remove the selected role
		initialRoleSet.removeAll(removedRole);
		
		// get all roles
		Set<UserRole> availableRoleSet = new HashSet<UserRole>(getUserRoleDao().findAllUserRole());
		
		// remove the remaining in the selected role
		availableRoleSet.removeAll(initialRoleSet);
		
		availableRoleListbox.setModel(new ListModelList<UserRole>(availableRoleSet));
		availableRoleListbox.setItemRenderer(getAvailableRoleListitemRenderer());
		
	}
	
	private void removeFromExistingListbox(User selUser, Set<UserRole> removedRole) {
		Set<UserRole> existingRoleSet = selUser.getUser_roles();
		
		existingRoleSet.removeAll(removedRole);
		
		existingRoleListbox.setModel(new ListModelList<UserRole>(existingRoleSet));
		existingRoleListbox.setItemRenderer(getExistingRoleListitemRenderer());
	}

	public void onClick$saveButton(Event event) throws Exception {
		if (getSelectedEmployee()==null) {
			// user not selected employee
			throw new Exception("Karyawan belum dipilih.");
		}
		if (passwordTextbox.getValue().isEmpty()) {
			// password not set
			throw new Exception("Password belum diberikan untuk login user.");
		}
		if (getUser().getUser_roles().isEmpty()) {
			// no user role selected
			throw new Exception("User Role belum dipilih untuk login user.");
		}
		
		User userMod = getUserModifiedValue();
		
		Events.sendEvent(Events.ON_OK, userDialogWin, userMod);
		
		userDialogWin.detach();		
	}
	
	private User getUserModifiedValue() {
		User userMod = getUser();

		userMod.setUser_name(userNameTextbox.getValue());
		userMod.setReal_name(namaTextbox.getValue());
		userMod.setEnabled(enabledCheckbox.isChecked());
		userMod.setEmail(emailTextbox.getValue());
		userMod.setCreateDate(createDateDatebox.getValue());
		userMod.setLastEditDate(lastEditedDatebox.getValue());
		userMod.setUser_password(passwordTextbox.getValue());
		userMod.setUser_roles(getUserModifiedUserRoles(userMod));
		userMod.setEmployee(getSelectedEmployee());
		userMod.setCompany(getUser().getCompany());
		
		// update employee info
		userMod.getEmployee().setUser(userMod);
		
		return userMod;
	}

	private Set<UserRole> getUserModifiedUserRoles(User userMod) {
		Set<UserRole> modifiedUserRole = userMod.getUser_roles();
		
		// set to empty
		modifiedUserRole.clear();
		
		for (Listitem item : existingRoleListbox.getItems()) {
			modifiedUserRole.add(item.getValue());
		}
		
		return modifiedUserRole;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		userDialogWin.detach();
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserRoleDao getUserRoleDao() {
		return userRoleDao;
	}

	public void setUserRoleDao(UserRoleDao userRoleDao) {
		this.userRoleDao = userRoleDao;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	
	
}
