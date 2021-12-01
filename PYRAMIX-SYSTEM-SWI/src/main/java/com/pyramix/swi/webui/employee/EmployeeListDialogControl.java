package com.pyramix.swi.webui.employee;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class EmployeeListDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4047362573320760711L;

	private EmployeeDao employeeDao;
	
	private Window employeeListDialog;
	private Textbox searchEmployeeTextbox;
	private Listbox employeeListbox;
	
	private List<Employee> employeeList;
	private Company selectedCompany;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setSelectedCompany((
				Company) arg.get("company"));
	}
	
	public void onCreate$employeeListDialog(Event event) throws Exception {
		employeeListDialog.setTitle("Pilih Karyawan");

		// making textbox responds to "Enter" key
		setSearchTextboxEventListener();
		
		listActiveEmployee();
	}
	
	private void setSearchTextboxEventListener() {
		searchEmployeeTextbox.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				searchEmployee();
				
			}
		});
	}

	protected void searchEmployee() throws WrongValueException, Exception {
		// index
		// getEmployeeDao().createIndexer();
		
		try {
			setEmployeeList(
					getEmployeeDao().findActiveEmployee(searchEmployeeTextbox.getValue(), getSelectedCompany()));
			
			employeeListbox.setModel(
					new ListModelList<Employee>(getEmployeeList()));
			
			employeeListbox.setItemRenderer(getEmployeeListitemRenderer());
			
		} catch (Exception e) {
			listActiveEmployee();
		}
		
	}

	private void listActiveEmployee() throws Exception {
		try {
			setEmployeeList(
					getEmployeeDao().findActiveEmployee_OrderBy_EmployeeName(false, getSelectedCompany()));
			
			employeeListbox.setModel(
					new ListModelList<Employee>(getEmployeeList()));
			
			employeeListbox.setItemRenderer(getEmployeeListitemRenderer());			
			
		} catch (Exception e) {
			Messagebox.show("Daftar Karyawan gagal. "+e.getMessage(), 
					"Error", Messagebox.OK,  Messagebox.ERROR);			
		}
		
	}

	private ListitemRenderer<Employee> getEmployeeListitemRenderer() {

		return new ListitemRenderer<Employee>() {
			
			@Override
			public void render(Listitem item, Employee employee, int index) throws Exception {
				Listcell lc;

				lc = new Listcell(employee.getName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
		
				item.setValue(employee);
				
			}
		};
	}

	public void onClick$selectButton(Event event) throws Exception {
		if (employeeListbox.getSelectedItem()==null) {
			throw new Exception("Pilih nama karyawan sebelum klik tombol");
		}
		
		Employee selEmployee = employeeListbox.getSelectedItem().getValue();
		
		Events.sendEvent(Events.ON_SELECT, employeeListDialog, selEmployee);
		
		employeeListDialog.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		employeeListDialog.detach();
	}

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

	public Company getSelectedCompany() {
		return selectedCompany;
	}

	public void setSelectedCompany(Company selectedCompany) {
		this.selectedCompany = selectedCompany;
	}
	
}
