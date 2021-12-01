package com.pyramix.swi.webui.employee;

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
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.settings.Settings;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;
import com.pyramix.swi.persistence.settings.dao.SettingsDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class EmployeeListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2944189923866161027L;

	private SettingsDao settingsDao;
	private EmployeeDao employeeDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox employeeListbox;
	
	private Settings settings;
	private Company selectedCompany;
	private List<Employee> employeeList;
	
	private final long DEFAULT_COMPANY_ID	= 1L;	
	
	public void onCreate$employeeListInfoWin(Event event) throws Exception {
		// get the settings -- find out the selected company
		setSettings(
				getSettingsDao().findSettingsById(DEFAULT_COMPANY_ID));
		
		// set the selected company from settings
		setSelectedCompany(
				getSettings().getSelectedCompany());

		// display the company
		formTitleLabel.setValue("Karyawan (Employee) - "+getSelectedCompany().getCompanyType()+". "+
				getSelectedCompany().getCompanyLegalName()+" ("+
				getSelectedCompany().getCompanyDisplayName()+")");
		
		// load and display
		displayEmployeeListInfo();
	}

	private void displayEmployeeListInfo() throws Exception {
		// get all the employees matching the selected company -- sets in the settings
		setEmployeeList(
				getEmployeeDao().findAllEmployeesBySelectedCompany(getSelectedCompany()));

		employeeListbox.setModel(
				new ListModelList<Employee>(getEmployeeList()));
		employeeListbox.setItemRenderer(getEmployeeListitemRenderer());
	}

	private ListitemRenderer<Employee> getEmployeeListitemRenderer() {

		return new ListitemRenderer<Employee>() {
			
			@Override
			public void render(Listitem item, Employee employee, int index) throws Exception {
				Listcell lc;
				
				// Tipe Karyawan
				lc = new Listcell(employee.getEmployeeType().toString());
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(employee.getName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Nama-Login
				lc = new Listcell(employee.getUser()==null ? "-" : employee.getUser().getUser_name());
				lc.setParent(item);
				
				// No.Telp
				lc = new Listcell(employee.getPhone());
				lc.setParent(item);
				
				// Perusahaan -- not displayed -- has been selected previously in Settings
				/*				
				 *	Company companyByProxy = getCompanyByProxy(employee.getId());
				
					lc = new Listcell(companyByProxy.getCompanyType()+". "+
						companyByProxy.getCompanyLegalName()+" ("+
						companyByProxy.getCompanyDisplayName()+")");
					lc.setParent(item);
				 */				
				
				// Aktif
				lc = new Listcell(employee.isActive() ? "Aktif" : "Tidak Aktif");
				lc.setParent(item);
				
				// Terima Komisi
				lc = new Listcell(employee.isCommission() ? "Ya" : "Tidak");
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(employee.getCatatan());
				lc.setParent(item);

				// edit
				lc = initEditButton(new Listcell(), employee);
				lc.setParent(item);
			}

			private Listcell initEditButton(Listcell listcell, Employee employee) {
				Button button = new Button();
				button.setLabel("...");
				button.setSclass("inventoryEditButton");
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						EmployeeData employeeData = new EmployeeData();
						employeeData.setEmployee(employee);
						employeeData.setPageMode(PageMode.EDIT);
						
						Map<String, EmployeeData> args = 
								Collections.singletonMap("employeeData", employeeData);
						
						Window employeeEditWin = 
								(Window) Executions.createComponents("/employee/EmployeeDialog.zul", null, args);
						
						employeeEditWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								getEmployeeDao().update((Employee) event.getData());
								
								// load and display
								displayEmployeeListInfo();
							}
						});
						
						employeeEditWin.doModal();
					}
				});
				button.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$employeeListbox(Event event) throws Exception {
		// count 'Aktif'
		int i=0;
		for (Employee employee : getEmployeeList()) {
			i = i+(employee.isActive()? 1 : 0);
		}
		infoResultlabel.setValue("Total: "+i+" Aktif Karyawan");
		
	}
	
	public void onClick$addButton(Event event) throws Exception {
		EmployeeData employeeData = new EmployeeData();
		employeeData.setEmployee(new Employee());
		// set the selected company -- new employee no need to select the company anymore
		employeeData.getEmployee().setCompany(getSelectedCompany());
		employeeData.setPageMode(PageMode.NEW);
		
		Map<String, EmployeeData> args = 
				Collections.singletonMap("employeeData", employeeData);
		
		Window employeeAddWin = 
				(Window) Executions.createComponents("/employee/EmployeeDialog.zul", null, args);

		employeeAddWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// save
				getEmployeeDao().save((Employee) event.getData());
				
				// create index -- because indexer is set to normal
				getEmployeeDao().createIndexer();
				
				// load and display
				displayEmployeeListInfo();
			}
		});
		
		employeeAddWin.doModal();		
	}
	
/*	
 * 	private Company getCompanyByProxy(long id) throws Exception {
		Employee employee = getEmployeeDao().findCompanyByProxy(id);
		
		return employee.getCompany();
	}	
*/	
	public EmployeeDao getEmployeeDao() {
		return employeeDao;
	}

	public void setEmployeeDao(EmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public SettingsDao getSettingsDao() {
		return settingsDao;
	}

	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public Company getSelectedCompany() {
		return selectedCompany;
	}

	public void setSelectedCompany(Company selectedCompany) {
		this.selectedCompany = selectedCompany;
	}
	
}
