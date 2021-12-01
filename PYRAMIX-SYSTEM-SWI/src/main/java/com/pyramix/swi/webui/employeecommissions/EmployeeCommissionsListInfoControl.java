package com.pyramix.swi.webui.employeecommissions;

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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;
import com.pyramix.swi.persistence.employeecommissions.dao.EmployeeCommissionsDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class EmployeeCommissionsListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 603564937806302855L;

	private EmployeeCommissionsDao employeeCommissionsDao;
	private CustomerOrderDao customerOrderDao;
	private EmployeeDao employeeDao;
	
	private Label formTitleLabel;
	private Listbox employeeCommissionsListbox;
	private Tabbox employeeCommissionsTabbox;
	
	private List<EmployeeCommissions> employeeCommissionsList;
	private List<Employee> employeeList;
	
	public void onCreate$employeeCommissionsListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Komisi Penjualan Karyawan");
		
		// list sales employee (employee receiving commissions) 
		// -- order by name ascending order (set descending to false)
		boolean descendingOrderEmployeeName = false;
		
		setEmployeeList(
				getEmployeeDao().findAllEmployees_Receiving_Commission(descendingOrderEmployeeName));
		
		setupSalesEmployeeNameTabbox();
		
		// list EmployeeCommissions Order By CustomerOrderDate in descending order
		boolean decendingOrder = true;
		
		// load
		setEmployeeCommissionsList(
				// getEmployeeCommissionsDao().findAllEmployeeCommissions());
				getEmployeeCommissionsDao().findAllEmployeeCommissions_OrderBy_CustomerOrderDate(decendingOrder));
		// list
		employeeCommissionsListInfo();
	}

	private void employeeCommissionsListInfo() {
		employeeCommissionsListbox.setModel(
				new ListModelList<EmployeeCommissions>(getEmployeeCommissionsList()));
		employeeCommissionsListbox.setItemRenderer(
				getEmployeeCommissionsListitemRenderer());
	}

	private void setupSalesEmployeeNameTabbox() {
		Tabs tabs = new Tabs();
		Tab tab;
		
		tab = new Tab("Semua");
		tab.setParent(tabs);
		
		for (Employee employee : getEmployeeList()) {
			tab = new Tab(employee.getName());
			tab.setValue(employee);
			tab.setParent(tabs);
		}
				
		tabs.setParent(employeeCommissionsTabbox);
		
		// set to 'semua'
		employeeCommissionsTabbox.setSelectedIndex(0);
	}

	public void onSelect$employeeCommissionsTabbox(Event event) throws Exception {
		Employee employee = employeeCommissionsTabbox.getSelectedTab().getValue();

		// list EmployeeCommissions Order By CustomerOrderDate in descending order
		boolean decendingOrder = true;

		if (employee == null) {
			setEmployeeCommissionsList(
					// getEmployeeCommissionsDao().findAllEmployeeCommissions());
					getEmployeeCommissionsDao().findAllEmployeeCommissions_OrderBy_CustomerOrderDate(decendingOrder));			
		} else {	
			setEmployeeCommissionsList(
					getEmployeeCommissionsDao().findAllEmployeeCommissions_By_EmployeeId(
							employee.getId(), decendingOrder));			
		}
		
		// list
		employeeCommissionsListInfo();		
	}
	
	private ListitemRenderer<EmployeeCommissions> getEmployeeCommissionsListitemRenderer() {

		return new ListitemRenderer<EmployeeCommissions>() {
			
			@Override
			public void render(Listitem item, EmployeeCommissions commissions, int index) throws Exception {
				Listcell lc;
				
				// Nama Karyawan
				lc = new Listcell(commissions.getEmployee().getName());
				lc.setParent(item);
				
				CustomerOrder orderByProxy = getCustomerOrderByProxy(commissions.getId());
				
				// No.Customer Order
				lc = new Listcell(orderByProxy.getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// Tgl.
				lc = new Listcell(dateToStringDisplay(asLocalDate(orderByProxy.getOrderDate()), getLongDateFormat()));
				lc.setParent(item);				
				
				Customer customerByProxy = getCustomerByProxy(orderByProxy.getId());

				// Customer
				lc = new Listcell(customerByProxy == null ? "tunai" :
						customerByProxy.getCompanyType()+". "+
						customerByProxy.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Total Order
				lc = new Listcell(toLocalFormat(orderByProxy.getTotalOrder()));
				lc.setParent(item);
				
				// Komisi (%)
				lc = new Listcell(
						getFormatedFloatLocal(commissions.getCommissionPercent()));
						// toLocalFormatWithDecimal(commissions.getCommissionPercent()));
				lc.setParent(item);
				
				// Komisi (Rp.)
				lc = new Listcell(toLocalFormat(commissions.getCommissionAmount()));
				lc.setParent(item);
				
				// edit
				lc = initEditButton(new Listcell(), commissions);
				lc.setParent(item);
				
				// if the status of employeeCommissions is 'BATAL', change the backgroud color to red
				if (commissions.getCommissionStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");					
				}
				
			}

			private Listcell initEditButton(Listcell listcell, EmployeeCommissions commissions) {
				Button button = new Button();
				button.setLabel("...");
				button.setSclass("inventoryEditButton");
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						EmployeeCommissionsData commissionsData = new EmployeeCommissionsData();
						commissionsData.setEmployeeCommissions(commissions);
						commissionsData.setPageMode(PageMode.EDIT);
						
						Map<String, EmployeeCommissionsData> arg = 
								Collections.singletonMap("", commissionsData);
						
						Window commissionsEditWin = 
								(Window) Executions.createComponents(
										"/employeecommissions/EmployeeCommissionsDialog.zul", null, arg);
						
						commissionsEditWin.doModal();
					}
					
				});
				button.setParent(listcell);
				
				return listcell;
			}
		};
	}

	protected Customer getCustomerByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findCustomerByProxy(id);
		
		return customerOrder.getCustomer();
	}

	protected CustomerOrder getCustomerOrderByProxy(long id) throws Exception {
		EmployeeCommissions employeeCommissions = getEmployeeCommissionsDao().findCustomerOrderByProxy(id);
		
		return employeeCommissions.getCustomerOrder();
	}

	public EmployeeCommissionsDao getEmployeeCommissionsDao() {
		return employeeCommissionsDao;
	}

	public void setEmployeeCommissionsDao(EmployeeCommissionsDao employeeCommissionsDao) {
		this.employeeCommissionsDao = employeeCommissionsDao;
	}

	public List<EmployeeCommissions> getEmployeeCommissionsList() {
		return employeeCommissionsList;
	}

	public void setEmployeeCommissionsList(List<EmployeeCommissions> employeeCommissionsList) {
		this.employeeCommissionsList = employeeCommissionsList;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
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
}
