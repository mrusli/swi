package com.pyramix.swi.webui.employee;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.kendaraan.Kendaraan;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeGender;
import com.pyramix.swi.domain.organization.EmployeeReligion;
import com.pyramix.swi.domain.organization.EmployeeType;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;
import com.pyramix.swi.persistence.kendaraan.dao.KendaraanDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class EmployeeDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3229052445715045563L;

	private EmployeeDao employeeDao;
	private CompanyDao companyDao;
	private KendaraanDao kendaraanDao;
	
	private Window employeeDialogWin;
	private Combobox employeeTypeCombobox, genderCombobox, religionCombobox, 
		organizationLegalNameCombobox, nomorKendaraanCombobox;
	private Textbox employeeNameTextbox, idKtpNumberTextbox,
		addressTextbox, cityTextbox, postalCodeTextbox, phoneTextbox, emailTextbox,
		noteTextbox, loginUserNameTextbox;
	private Checkbox activeCheckbox, commissionCheckbox, kendaraanCheckbox;
	private Decimalbox commissionPercentDecimalbox;
	private Button createLoginUserButton;
	
	private EmployeeData employeeData;
	private PageMode pageMode;
	private User loginUser;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setEmployeeData(
				(EmployeeData) arg.get("employeeData"));
	}

	public void onCreate$employeeDialogWin(Event event) throws Exception {
		setPageMode(
				getEmployeeData().getPageMode());
		
		// assume no login user -- used PageMode is NEW only
		setLoginUser(null);
		
		// comboboxes: gender, religion, kendaraan, company
		setupComboboxes();
		
		// set the locale for the decimalbox
		commissionPercentDecimalbox.setLocale(getLocale());
		
		// making textbox responds to "Enter" key
		setEmployeeNameTextboxEventListener();
		
		switch (getPageMode()) {
		case EDIT:
			employeeDialogWin.setTitle("Merubah (Edit) Informasi Karyawan");			
			break;
		case NEW:
			employeeDialogWin.setTitle("Menambah (Add) Karyawan Baru");
			break;
		default:
			break;
		}
		
		setupEmployeeInfo();
	}
	
	private void setupComboboxes() throws Exception {
		Comboitem comboitem;
		// employeeTypeCombobox,
		for (EmployeeType employeeType : EmployeeType.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(employeeType.toString());
			comboitem.setValue(employeeType);
			comboitem.setParent(employeeTypeCombobox);
		}
		
		// genderCombobox,
		for (EmployeeGender employeeGender : EmployeeGender.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(employeeGender.toString());
			comboitem.setValue(employeeGender);
			comboitem.setParent(genderCombobox);
		}
		
		// religionCombobox;
		for (EmployeeReligion employeeReligion : EmployeeReligion.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(employeeReligion.toString());
			comboitem.setValue(employeeReligion);
			comboitem.setParent(religionCombobox);			
		}
		
		// organization / company (internal)
		List<Company> companyList = getCompanyDao().findAllCompany();
		for (Company company : companyList) {
			comboitem = new Comboitem();
			comboitem.setLabel(company.getCompanyType()+". "+
					company.getCompanyLegalName()+" ("+
					company.getCompanyDisplayName()+")");
			comboitem.setValue(company);
			comboitem.setParent(organizationLegalNameCombobox);
		}
		
		// kendaraan
		List<Kendaraan> kendaraanList = getKendaraanDao().findAllActiveKendaraan();
		for (Kendaraan kendaraan : kendaraanList) {						
			comboitem = new Comboitem();
			comboitem.setLabel(kendaraan.getNomorPolisi());
			comboitem.setValue(kendaraan);
			comboitem.setParent(nomorKendaraanCombobox);				
		}
	}

	private void setEmployeeNameTextboxEventListener() throws Exception {
		// when user press the 'tab' key to go to the next component
		employeeNameTextbox.addEventListener(Events.ON_BLUR, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Clients.showBusy("Checking for Duplication. Please Wait...");
				
				// if not empty
				if (!employeeNameTextbox.getValue().isEmpty()) {
					Employee employee = getEmployeeDao().findEmployeeByName(employeeNameTextbox.getValue());
					
					// if the name not exist in database
					if (employee==null) {						
						// clear
						Clients.clearBusy();						

					} else {
						// clear
						Clients.clearBusy();
						
						throw new Exception("Nama Karyawan: "+employeeNameTextbox.getValue()+" sudah didaftarkan sebelumnya.");	
					}
				}
			}
		});
	}
	
	private void setupEmployeeInfo() throws Exception {
		Employee employee = getEmployeeData().getEmployee();

		if (employee.getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			employeeTypeCombobox.setSelectedIndex(0);
			genderCombobox.setSelectedIndex(0);
			religionCombobox.setSelectedIndex(0);
			// set the selected company name -- user do not choose
			Company selectedCompany = employee.getCompany();
			List<Comboitem> companyItems = organizationLegalNameCombobox.getItems();
			for (Comboitem comboitem : companyItems) {
				if (comboitem.getValue().equals(selectedCompany)) {
					organizationLegalNameCombobox.setSelectedItem(comboitem);
					
					break;
				}
			}
			// organizationLegalNameCombobox.setSelectedIndex(0);
			employeeNameTextbox.setValue("");
			// organizationLegalNameTextbox.setValue("");
			idKtpNumberTextbox.setValue("");
			addressTextbox.setValue("");
			cityTextbox.setValue("");
			postalCodeTextbox.setValue("");
			phoneTextbox.setValue("");
			emailTextbox.setValue("");
			noteTextbox.setValue("");
			activeCheckbox.setChecked(false);
			
			// commissionCheckbox
			commissionCheckbox.setChecked(false);
			commissionPercentDecimalbox.setDisabled(true);
			commissionPercentDecimalbox.setValue(BigDecimal.ZERO);

		} else {
			// edit
			
			// employeeTypeCombobox, genderCombobox, religionCombobox;
			for (Comboitem item : employeeTypeCombobox.getItems()) {
				if (item.getValue().equals(employee.getEmployeeType())) {
					employeeTypeCombobox.setSelectedItem(item);
				}
			}
			
			for (Comboitem item : genderCombobox.getItems()) {
				if (item.getValue().equals(employee.getGender())) {
					genderCombobox.setSelectedItem(item);
				}
			}
			
			for (Comboitem item : religionCombobox.getItems()) {
				if (item.getValue().equals(employee.getReligion())) {
					religionCombobox.setSelectedItem(item);
				}
			}
			
			Company companyByProxy = getCompanyByProxy(employee.getId());
			long proxyId = companyByProxy.getId();

			for (Comboitem item : organizationLegalNameCombobox.getItems()) {
				Company company = item.getValue();
				long itemId = company.getId();
				// 
				// item.getValue().equals(companyByProxy)
				if (itemId==proxyId) {
					organizationLegalNameCombobox.setSelectedItem(item);
				}
			}
			
			Employee employeeKendaraanByProxy = getEmployeeDao().findEmployeeKenadaraanByProxy(employee.getId());
			List<Kendaraan> kendaraanEmployeeList = employeeKendaraanByProxy.getEmployeeKendaraanList();
			if (!kendaraanEmployeeList.isEmpty()) {
				kendaraanCheckbox.setChecked(true);
				nomorKendaraanCombobox.setDisabled(false);
				
				Kendaraan kendaraanEmployeeByProxy = kendaraanEmployeeList.get(0);
				long proxyKendaraanEmployeeId = kendaraanEmployeeByProxy.getId();
				
				for (Comboitem item : nomorKendaraanCombobox.getItems()) {
					Kendaraan kendaraan = item.getValue();
					long itemId = kendaraan.getId();
					
					if (itemId==proxyKendaraanEmployeeId) {
						nomorKendaraanCombobox.setSelectedItem(item);
					}
				}				
			}
			
			// employeeNameTextbox, organizationLegalNameTextbox, idKtpNumberTextbox,
			employeeNameTextbox.setValue(employee.getName());
			idKtpNumberTextbox.setValue(employee.getKtpNumber());
			
			// addressTextbox, cityTextbox, postalCodeTextbox, phoneTextbox, emailTextbox,
			addressTextbox.setValue(employee.getAddress());
			cityTextbox.setValue(employee.getCity());
			postalCodeTextbox.setValue(employee.getPostalCode());
			phoneTextbox.setValue(employee.getPhone());
			emailTextbox.setValue(employee.getEmail());
			
			// faxTextbox, noteTextbox;
			noteTextbox.setValue(employee.getCatatan());
			
			// activeCheckbox
			activeCheckbox.setChecked(employee.isActive());
			
			// commissionCheckbox
			commissionCheckbox.setChecked(employee.isCommission());
			commissionPercentDecimalbox.setDisabled(!employee.isCommission());
			commissionPercentDecimalbox.setValue(employee.getCommissionPercent());
			
			// user login by proxy
			Employee employeeLoginUserByProxy = 
					getEmployeeDao().findEmployeeLoginUserByProxy(employee.getId());
			User loginUser = employeeLoginUserByProxy.getUser();
			loginUserNameTextbox.setValue(loginUser==null ? 
					"Tidak Ada Nama Login" : loginUser.getUser_name()+(loginUser.isEnabled()? " [AKTIF]" : " [TIDAK AKTIF]"));
		}
		
	}

	public void onClick$commissionCheckbox(Event event) throws Exception {
		commissionPercentDecimalbox.setDisabled(!commissionCheckbox.isChecked());		
	}
	
	public void onClick$kendaraanCheckbox(Event event) throws Exception {
		nomorKendaraanCombobox.setDisabled(!kendaraanCheckbox.isChecked());
	}
	
	public void onClick$activeCheckbox(Event event) throws Exception {
		if (getPageMode().compareTo(PageMode.EDIT)==0) {
			// user login by proxy
			Employee employee = getEmployeeData().getEmployee();
			// Employee employeeLoginUserByProxy = 
			// 		getEmployeeDao().findEmployeeLoginUserByProxy(employee.getId());
			User loginUser = employee.getUser();
			
			// if active is unchecked, user login WILL BE automatically disabled 
			// when user click the save button
			if (activeCheckbox.isChecked()) {
				// aktif			
				loginUserNameTextbox.setValue(loginUser==null ? 
						"Tidak Ada Nama Login" : loginUser.getUser_name()+" [AKTIF]");
			} else {
				// tidak aktif
				loginUserNameTextbox.setValue(loginUser==null ? 
						"Tidak Ada Nama Login" : loginUser.getUser_name()+" [TIDAK AKTIF]");			
			}
			
			// automatically update receiving commission checkbox
			// if the employee is receiving commission
			if (commissionCheckbox.isChecked()) {
				// employee receives commission
				commissionCheckbox.setChecked(activeCheckbox.isChecked());
				commissionPercentDecimalbox.setDisabled(!activeCheckbox.isChecked());
			} else {
				// employee not receiving commission
				
				// -- do nothing
			}
		} else {
			// allow user to create a new login name
			createLoginUserButton.setVisible(activeCheckbox.isChecked());
		}
		
	}
	
	public void onClick$createLoginUserButton(Event event) throws Exception {
		User empUser = new User();
		empUser.setUser_name(createUserName(employeeNameTextbox.getValue()));
		empUser.setReal_name(employeeNameTextbox.getValue());
		empUser.setEmail(emailTextbox.getValue());
		empUser.setEmployee(getEmployeeData().getEmployee());
		
		// default company for this employee
		Company selectedCompany = getEmployeeData().getEmployee().getCompany();
		empUser.setCompany(selectedCompany);
		
		Map<String, User> args = 
				Collections.singletonMap("user", empUser);
		
		Window userCreateWin = (Window) Executions.createComponents("/user/UserDialog.zul", null, args);

		userCreateWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				User user = (User) event.getData();
				
				// assign to the loginUser
				setLoginUser(user);
				
			}
		});
		
		userCreateWin.doModal();
	}

	/**
	 * Grab the first 'word' in a string, seperated by a space
	 * ref: https://stackoverflow.com/questions/5067942/what-is-the-best-way-to-extract-the-first-word-from-a-string-in-java
	 * 
	 * @param realName
	 * @return
	 * @throws Exception
	 */
	private String createUserName(String realName) throws Exception {
		if (realName.isEmpty()) {
			throw new Exception("Tidak Ada Nama untuk dibuatkan Nama Login");
		} else {
			String arr[] = realName.split(" ", 2);

			return arr[0];
		}
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		if (employeeNameTextbox.getValue().isEmpty()) {
			throw new Exception("Nama karyawan baru belum diisi.");
		}
		
		Employee userModEmployee = getEmployeeData().getEmployee();
		
		userModEmployee.setEmployeeType(employeeTypeCombobox.getSelectedItem().getValue());
		userModEmployee.setCompany(organizationLegalNameCombobox.getSelectedItem().getValue());
		userModEmployee.setGender(genderCombobox.getSelectedItem().getValue());
		userModEmployee.setReligion(religionCombobox.getSelectedItem().getValue());
		userModEmployee.setName(employeeNameTextbox.getValue());
		userModEmployee.setKtpNumber(idKtpNumberTextbox.getValue());
		userModEmployee.setAddress(addressTextbox.getValue());
		userModEmployee.setCity(cityTextbox.getValue());
		userModEmployee.setPostalCode(postalCodeTextbox.getValue());
		userModEmployee.setPhone(phoneTextbox.getValue());
		userModEmployee.setEmail(emailTextbox.getValue());
		userModEmployee.setCatatan(noteTextbox.getValue());
		userModEmployee.setActive(activeCheckbox.isChecked());
		userModEmployee.setCommission(commissionCheckbox.isChecked());
		userModEmployee.setCommissionPercent(commissionPercentDecimalbox.getValue());

		if (getPageMode().compareTo(PageMode.EDIT)==0) {
			// enable / disable the login
			// user login by proxy
			// Employee employeeLoginUserByProxy = 
			//		getEmployeeDao().findEmployeeLoginUserByProxy(userModEmployee.getId());
			User existingLoginUser = userModEmployee.getUser();
			
			if (existingLoginUser==null) {
				// no login user -- do nothing
				
			} else {
				// login user exist -- disable / enable the login
				existingLoginUser.setEnabled(activeCheckbox.isChecked());
				
				// if the login user not exist
				if (userModEmployee.getUser()==null) {
					userModEmployee.setUser(existingLoginUser);					
				}
			}			
		} else {
			// assuming the login user has been created and assigned to loginUser
			userModEmployee.setUser(getLoginUser());
		}
		
		List<Kendaraan> userModKendaraanList = new ArrayList<Kendaraan>();
		if (kendaraanCheckbox.isChecked()) {
			if (nomorKendaraanCombobox.getSelectedItem()!=null) {
				userModKendaraanList.add(nomorKendaraanCombobox.getSelectedItem().getValue());				
			} else {
				Messagebox.show("Tidak memilih kendaraan. Pilih Kendaraan untuk Karyawan ini sebelum klik Simpan.",
					    "Error", 
					    Messagebox.OK,  
					    Messagebox.ERROR);
				// abort the save
				return;
			}
		}
		
		userModEmployee.setEmployeeKendaraanList(userModKendaraanList);
		
		Events.sendEvent(getPageMode().compareTo(PageMode.EDIT)==0 ?
				Events.ON_CHANGE : Events.ON_OK, employeeDialogWin, userModEmployee);
		
		employeeDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		employeeDialogWin.detach();
	}

	private Company getCompanyByProxy(long id) throws Exception {
		Employee employee = getEmployeeDao().findCompanyByProxy(id);
		
		return employee.getCompany();
	}	
	
	public EmployeeData getEmployeeData() {
		return employeeData;
	}

	public void setEmployeeData(EmployeeData employeeData) {
		this.employeeData = employeeData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public EmployeeDao getEmployeeDao() {
		return employeeDao;
	}

	public void setEmployeeDao(EmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public KendaraanDao getKendaraanDao() {
		return kendaraanDao;
	}

	public void setKendaraanDao(KendaraanDao kendaraanDao) {
		this.kendaraanDao = kendaraanDao;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
	
}
