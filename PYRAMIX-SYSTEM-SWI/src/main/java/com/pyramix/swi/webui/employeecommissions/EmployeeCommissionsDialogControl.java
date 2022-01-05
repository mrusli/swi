package com.pyramix.swi.webui.employeecommissions;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.employeecommissions.dao.EmployeeCommissionsDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class EmployeeCommissionsDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4469478911489591266L;

	private EmployeeCommissionsDao employeeCommissionsDao;
	private CustomerOrderDao customerOrderDao;
	
	private Window employeeCommissionsDialogWin;
	private Textbox customerOrderNoTextbox, customerOrderCustomerTextbox,
		totalSalesTextbox, commissionPercentTextbox,
		commissionTotalTextbox, batalTextbox;
	private Datebox customerOrderDatebox, batalDatebox;
	private Combobox employeeNameCombobox;
	private EmployeeCommissions employeeCommissions;
	private Label idLabel, batalStatusLabel;
	private Grid batalGrid;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		// commissions
		setEmployeeCommissions((EmployeeCommissions) arg.get("commissions"));
	}

	public void onCreate$employeeCommissionsDialogWin(Event event) throws Exception {
		employeeCommissionsDialogWin.setTitle("Melihat (View) Komisi Penjualan Karyawan Sales");
		
		// set locale and format for datebox
		customerOrderDatebox.setLocale(getLocale());
		customerOrderDatebox.setFormat(getLongDateFormat());
		// batal
		batalDatebox.setLocale(getLocale());
		batalDatebox.setFormat(getLongDateFormat());
		
		setEmployeeCommissionsInfo();
	}
	
	private void setEmployeeCommissionsInfo() throws Exception {
		idLabel.setValue("id:#"+getEmployeeCommissions().getId());
		
		batalGrid.setVisible(getEmployeeCommissions().getCommissionStatus().equals(DocumentStatus.BATAL));
		batalStatusLabel.setValue(getEmployeeCommissions().getCommissionStatus().toString());
		batalDatebox.setValue(getEmployeeCommissions().getBatalDate());
		batalTextbox.setValue(getEmployeeCommissions().getBatalNote());
		
		EmployeeCommissions employeeCommissionsCustomerOrderByProxy = 
				getEmployeeCommissionsDao().findCustomerOrderByProxy(getEmployeeCommissions().getId());
		CustomerOrder customerOrder = 
				employeeCommissionsCustomerOrderByProxy.getCustomerOrder();
		
		customerOrderNoTextbox.setValue(customerOrder.getDocumentSerialNumber().getSerialComp());
		customerOrderDatebox.setValue(customerOrder.getOrderDate());
		CustomerOrder customerOrderCustomerByProxy =
				getCustomerOrderDao().findCustomerByProxy(customerOrder.getId());
		Customer customer = customerOrderCustomerByProxy.getCustomer();
		customerOrderCustomerTextbox.setValue(customer.getCompanyType()+"."+customer.getCompanyLegalName());
		
		employeeNameCombobox.setValue(getEmployeeCommissions().getEmployee().getName());
		
		totalSalesTextbox.setValue(
				toLocalFormat(customerOrder.getTotalOrder().subtract(customerOrder.getTotalPpn())));
		commissionPercentTextbox.setValue(getFormatedFloatLocal(getEmployeeCommissions().getCommissionPercent()));
		commissionTotalTextbox.setValue(toLocalFormat(BigDecimal.ZERO));
	}

	public void onClick$cancelButton(Event event) throws Exception {
		employeeCommissionsDialogWin.detach();
	}

	public EmployeeCommissions getEmployeeCommissions() {
		return employeeCommissions;
	}

	public void setEmployeeCommissions(EmployeeCommissions employeeCommissions) {
		this.employeeCommissions = employeeCommissions;
	}

	public EmployeeCommissionsDao getEmployeeCommissionsDao() {
		return employeeCommissionsDao;
	}

	public void setEmployeeCommissionsDao(EmployeeCommissionsDao employeeCommissionsDao) {
		this.employeeCommissionsDao = employeeCommissionsDao;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}
}
