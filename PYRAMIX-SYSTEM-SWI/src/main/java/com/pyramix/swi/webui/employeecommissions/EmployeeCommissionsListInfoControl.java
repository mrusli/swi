package com.pyramix.swi.webui.employeecommissions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
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

public class EmployeeCommissionsListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 603564937806302855L;

	private EmployeeCommissionsDao employeeCommissionsDao;
	private CustomerOrderDao customerOrderDao;
	private EmployeeDao employeeDao;
	
	private Label formTitleLabel, totalEmployeeSalesLabel;
	private Listbox employeeCommissionsListbox;
	private Tabbox employeeCommissionsTabbox;
	private Combobox customerCombobox;
	private Datebox startDatebox, endDatebox;
	
	private List<EmployeeCommissions> employeeCommissionsList;
	private List<Employee> employeeList;
	private Date defaultStartDate, defaultEndDate;
	
	public void onCreate$employeeCommissionsListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Penjualan Karyawan Sales");
		
		// datebox
		startDatebox.setLocale(getLocale());
		startDatebox.setFormat(getLongDateFormat());
		endDatebox.setLocale(getLocale());
		endDatebox.setFormat(getLongDateFormat());
		
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
		
		// setup unique customer list
		setupCustomerList();
		// setup date range
		setupDateRange();
		// calc total sales
		setupTotalSales();
		
		defaultStartDate = startDatebox.getValue(); 
		defaultEndDate = endDatebox.getValue();
		
		// list
		employeeCommissionsListInfo();
	}

	private void setupCustomerList() throws Exception {		
		boolean unique = true;
		List<Customer> uniqueCustList = new ArrayList<Customer>();

		for (EmployeeCommissions commissions : getEmployeeCommissionsList()) {
			EmployeeCommissions commissionsCustomerOrderByProxy =
					getEmployeeCommissionsDao().findCustomerOrderByProxy(commissions.getId());
			CustomerOrder customerOrder = 
					commissionsCustomerOrderByProxy.getCustomerOrder();
			CustomerOrder customerOrderCustomerByProxy =
					getCustomerOrderDao().findCustomerByProxy(customerOrder.getId());
			Customer customer = customerOrderCustomerByProxy.getCustomer();
			
			if (uniqueCustList.isEmpty()) {
				// add
				if (customer!=null) {
					uniqueCustList.add(customer);
					unique = false;
				}
			} else {
				unique = true;
				if (customer==null) {
					continue;
				}
				// go through the list
				for (Customer uniqueCust : uniqueCustList) {
					if (uniqueCust.getId().compareTo(customer.getId())==0) {
						unique = false;
						break;
					}
				}
			}
			if (unique) {
				// add
				if (customer!=null) {
					uniqueCustList.add(customer);
				}
			}
		}
		// sort
		uniqueCustList.sort((o1,o2) -> {
			return o1.getCompanyLegalName().compareTo(o2.getCompanyLegalName());
		});
		// check
		customerCombobox.setDisabled(uniqueCustList.isEmpty());
		// clear items
		customerCombobox.getItems().clear();
		// populate customerCombobox
		Comboitem comboitem = null;
		// all customer
		comboitem = new Comboitem();
		comboitem.setLabel("Semua");
		comboitem.setValue(null);
		comboitem.setParent(customerCombobox);
		for (Customer customer : uniqueCustList) {
			comboitem = new Comboitem();
			comboitem.setLabel(customer.getCompanyType()+"."+customer.getCompanyLegalName());
			comboitem.setValue(customer);
			comboitem.setParent(customerCombobox);
		}
		customerCombobox.setSelectedIndex(0);
	}

	private void setupDateRange() {
		Date endDate = getEmployeeCommissionsList().get(0).getCustomerOrder().getOrderDate();
		int lastIndex = getEmployeeCommissionsList().size()-1;
		Date startDate = getEmployeeCommissionsList().get(lastIndex).getCustomerOrder().getOrderDate();
		
		startDatebox.setValue(startDate);
		endDatebox.setValue(endDate);
	}

	private void setupTotalSales() throws Exception {
		BigDecimal totalSales = BigDecimal.ZERO;
		for (EmployeeCommissions commissions : getEmployeeCommissionsList()) {
			EmployeeCommissions commissionsCustomerOrderByProxy =
					getEmployeeCommissionsDao().findCustomerOrderByProxy(commissions.getId());
			CustomerOrder customerOrder = 
					commissionsCustomerOrderByProxy.getCustomerOrder();
			totalSales = totalSales.add(customerOrder.getTotalOrder());
		}
		totalEmployeeSalesLabel.setValue("Total Penjualan (Rp.): "+toLocalFormat(totalSales));
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
		Employee employeeSales = employeeCommissionsTabbox.getSelectedTab().getValue();

		// list EmployeeCommissions Order By CustomerOrderDate in descending order
		boolean decendingOrder = true;

		if (employeeSales == null) {
			setEmployeeCommissionsList(
					// getEmployeeCommissionsDao().findAllEmployeeCommissions());
					getEmployeeCommissionsDao().findAllEmployeeCommissions_OrderBy_CustomerOrderDate(decendingOrder));			
		} else {	
			setEmployeeCommissionsList(
					getEmployeeCommissionsDao().findAllEmployeeCommissions_By_EmployeeId(
							employeeSales.getId(), decendingOrder));			
		}
		// setup unique customer list
		setupCustomerList();
		// setup date range
		setupDateRange();
		// calc total sales
		setupTotalSales();
		
		defaultStartDate = startDatebox.getValue(); 
		defaultEndDate = endDatebox.getValue();

		// list
		employeeCommissionsListInfo();		
	}
	
	public void onSelect$customerCombobox(Event event) throws Exception {
		// reset date range to default
		startDatebox.setValue(defaultStartDate);
		endDatebox.setValue(defaultEndDate);		
	}
	
	public void onClick$filterButton(Event event) throws Exception {
		Employee employeeSales = employeeCommissionsTabbox.getSelectedTab().getValue();

		// list EmployeeCommissions Order By CustomerOrderDate in descending order
		boolean descendingOrder = true;

		if (employeeSales == null) {
			Customer customer = customerCombobox.getSelectedItem().getValue();
			if (customer==null) {
				setEmployeeCommissionsList(
					getEmployeeCommissionsDao().
						findAllEmployeeCommissions_By_Date_OrderBy_CustomerOrderDate(
							descendingOrder, 
							startDatebox.getValue(),
							endDatebox.getValue()));				
				// setup unique customer list
				setupCustomerList();
			} else {
				// list
				setEmployeeCommissionsList(
					getEmployeeCommissionsDao().
						findAllEmployeeCommissions_By_Customer_By_Date_OrderBy_CustomerOrderDate(
							descendingOrder, 
							customer, 
							startDatebox.getValue(), 
							endDatebox.getValue()));								
			}
			// setup date range
			setupDateRange();
			// calc total sales
			setupTotalSales();			
		} else {
			Customer customer = customerCombobox.getSelectedItem().getValue();
			if (customer==null) {
				setEmployeeCommissionsList(
						getEmployeeCommissionsDao().findAllEmployeeCommissions_By_EmployeeId_By_Date(
								employeeSales.getId(), 
								startDatebox.getValue(),
								endDatebox.getValue(),
								descendingOrder));							
				// setup unique customer list
				setupCustomerList();
			} else {
				setEmployeeCommissionsList(
						getEmployeeCommissionsDao().
							findAllEmployeeCommissions_By_EmployeeId_By_Customer_By_Date_OrderBy_CustomerOrderDate(
								employeeSales.getId(),
								descendingOrder, 
								customer, 
								startDatebox.getValue(), 
								endDatebox.getValue()));								
			}
			// setup date range
			setupDateRange();
			// calc total sales
			setupTotalSales();
		}
		// list
		employeeCommissionsListInfo();		

	}
	
	public void onClick$resetButton(Event event) throws Exception {
		Employee employeeSales = employeeCommissionsTabbox.getSelectedTab().getValue();
		
		// list EmployeeCommissions Order By CustomerOrderDate in descending order
		boolean decendingOrder = true;

		if (employeeSales==null) {
			// reset customer to 'Semua'
			customerCombobox.setSelectedIndex(0);
			// reset date to 'default' date
			startDatebox.setValue(defaultStartDate);
			endDatebox.setValue(defaultEndDate);
			setEmployeeCommissionsList(
					getEmployeeCommissionsDao().
						findAllEmployeeCommissions_OrderBy_CustomerOrderDate(decendingOrder));				
			// setup unique customer list
			setupCustomerList();
			// setup date range
			setupDateRange();			
		} else {
			// reset customer to 'Semua'
			customerCombobox.setSelectedIndex(0);
			// reset date to 'default' date
			startDatebox.setValue(defaultStartDate);
			endDatebox.setValue(defaultEndDate);
			setEmployeeCommissionsList(
					getEmployeeCommissionsDao().findAllEmployeeCommissions_By_EmployeeId(
							employeeSales.getId(), decendingOrder));							
			// setup unique customer list
			setupCustomerList();
			// setup date range
			setupDateRange();
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
						Map<String, EmployeeCommissions> arg = 
								Collections.singletonMap("commissions", commissions);
						
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
