package com.pyramix.swi.webui.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
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
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class ReportSalesListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3824902547375760302L;
	
	private CustomerOrderDao customerOrderDao;
	private EmployeeDao employeeDao;
	
	private Label formTitleLabel, timezoneLocaleLabel;
	private Listbox reportSalesListbox;
	private Label infoResultlabel, totalPenjualanTunaiLabel, totalPenjualanKreditLabel,
		totalPenjualanLabel;
	private Datebox todayDatebox, startDatebox, endDatebox;
	private Tabbox reportSalesPeriodTabbox, reportSalesWeekPeriodTabbox;
	private Button printTodayButton, printDateRangeButton;
	private Combobox employeeCommissionedCombobox;
	
	private List<CustomerOrder> customerOrderList;
	// private BigDecimal totalPenjualanKredit = BigDecimal.ZERO;
	// private BigDecimal totalPenjualanTunai	= BigDecimal.ZERO;
	private LocalDate startWeekDate, endWeekDate;
	
	private final int WORK_DAY_WEEK = 6;
	private final Logger log = Logger.getLogger(ReportSalesListInfoControl.class);
	
	@SuppressWarnings("rawtypes")
	private EventQueue eq;

	@SuppressWarnings("unchecked")
	public void onCreate$reportSalesListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Laporan Penjualan");

		eq = EventQueues.lookup("salesInteractive", EventQueues.APPLICATION, true);
		eq.subscribe(new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				// load and display -- default to today (one day only)
				displayReportSales(todayDatebox.getValue(), todayDatebox.getValue());				
			}

		});
		
		todayDatebox.setFormat(getLongDateFormat());
		todayDatebox.setLocale(getLocale());
		todayDatebox.setValue(asDate(getLocalDate()));
		
		startDatebox.setFormat(getLongDateFormat());
		startDatebox.setLocale(getLocale());
		startDatebox.setValue(asDate(getLocalDate()));
		
		endDatebox.setFormat(getLongDateFormat());
		endDatebox.setLocale(getLocale());
		endDatebox.setValue(asDate(addDate(1, getLocalDate())));		
		
		// for the week
		setStartWeekDate(getFirstDateOfTheWeek(asLocalDate(todayDatebox.getValue())));
		setEndWeekDate(getLastDateOfTheWeek(asLocalDate(todayDatebox.getValue()), WORK_DAY_WEEK));
	
		// load commissioned employee and set to 'Semua' all employees
		loadCommissionedEmployee();
		
		// defaulted to 'Hari Ini'
		infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(asLocalDate(todayDatebox.getValue()), getLongDateFormat()));
		
		// display timezone and locale info
		timezoneLocaleLabel.setValue("Time Zone: "+getSystemTimeZone().toString()+" - "+getLocale());
		
		// load and display -- default to today (one day only)
		boolean canPrint = displayReportSales(todayDatebox.getValue(), todayDatebox.getValue());

		printTodayButton.setVisible(canPrint);
	}

	private void loadCommissionedEmployee() throws Exception {
		List<Employee> employeeCommissionedList = 
				getEmployeeDao().findAllEmployees_Receiving_Commission(true);
		
		Comboitem allComboitem, comboitem;
		// all commissioned employee
		allComboitem = new Comboitem();
		allComboitem.setLabel("Semua");
		allComboitem.setValue(null);
		allComboitem.setParent(employeeCommissionedCombobox);
		// commssioned employee
		for (Employee employeeCommissioned : employeeCommissionedList) {
			comboitem = new Comboitem();
			comboitem.setLabel(employeeCommissioned.getName());
			comboitem.setValue(employeeCommissioned);
			comboitem.setParent(employeeCommissionedCombobox);
		}
		
		// set to 'Semua'
		employeeCommissionedCombobox.setSelectedItem(allComboitem);
	}
		
	private boolean displayReportSales(Date startDate, Date endDate) throws Exception {
		if (employeeCommissionedCombobox.getSelectedItem().getValue()==null) {
			log.info("List by All CommissionedEmployee");
			setCustomerOrderList(
					getCustomerOrderDao().findAllCustomerOrder_By_DateRange(startDate, endDate));
			reportSalesListbox.setEmptyMessage("Tidak ada penjualan...");
			reportSalesListbox.setModel(
					new ListModelList<CustomerOrder>(getCustomerOrderList()));
			reportSalesListbox.setItemRenderer(
					getCustomerOrderListitemRenderer());
			
			log.info("Sum of 'Tunai' totalOrder with startDate : "+startDate.toString()+", endDate : "+endDate.toString());
			
			// 'tunai' paymentType sumTotalOrder
			BigDecimal sumPaymentTypeTunai = getCustomerOrderDao().sumTotalOrderPaymentTypeTunai(startDate, endDate);
			
			log.info("'Tunai' totalOrder : "+sumPaymentTypeTunai);
			
			log.info("Sum of 'All' totalOrder with startDate : "+startDate.toString()+", endDate : "+endDate.toString());
			
			// all paymentType
			BigDecimal sumPaymentTypeAll = getCustomerOrderDao().sumTotalOrderPaymentTypeAll(startDate, endDate);
			
			log.info("'All' PaymentType totalOrder : "+sumPaymentTypeAll);
			
			// 'credit' paymentType sumTotalOrder
			BigDecimal sumPaymentTypeCredit = sumPaymentTypeAll.subtract(sumPaymentTypeTunai);
			
			log.info("'Kredit' PaymentType totalOrder : "+sumPaymentTypeCredit);
			
			displayTotalPenjualan(sumPaymentTypeTunai, sumPaymentTypeAll);
		} else {
			// employeeSales
			Employee employeeSales = employeeCommissionedCombobox.getSelectedItem().getValue();
			log.info("List by CommissioneEmployee : "+employeeSales.getName());
			setCustomerOrderList(
					getCustomerOrderDao().findAllCustomerOrder_By_DateRange_By_EmployeeSales(startDate, endDate, employeeSales));
			reportSalesListbox.setEmptyMessage("Tidak ada penjualan...");
			reportSalesListbox.setModel(
					new ListModelList<CustomerOrder>(getCustomerOrderList()));
			reportSalesListbox.setItemRenderer(
					getCustomerOrderListitemRenderer());
			
			log.info("Sum of 'Tunai' totalOrder for employee: "+employeeSales.getName()+", with startDate : "+startDate.toString()+", endDate : "+endDate.toString());
			
			// 'tunai' paymentType sumTotalOrder
			BigDecimal sumPaymentTypeTunai = getCustomerOrderDao().sumTotalOrderPaymentTypeTunai_By_EmployeeSales(startDate, endDate, employeeSales);
			
			log.info("'Tunai' totalOrder : "+sumPaymentTypeTunai);
			
			log.info("Sum of 'All' totalOrder for employee: "+employeeSales.getName()+", with startDate : "+startDate.toString()+", endDate : "+endDate.toString());
			
			// all paymentType
			BigDecimal sumPaymentTypeAll = getCustomerOrderDao().sumTotalOrderPaymentTypeAll_By_EmployeeSales(startDate, endDate, employeeSales);
			
			log.info("'All' PaymentType for employee: "+employeeSales.getName()+", totalOrder : "+sumPaymentTypeAll);
			
			// 'credit' paymentType sumTotalOrder
			BigDecimal sumPaymentTypeCredit = sumPaymentTypeAll.subtract(sumPaymentTypeTunai);
			
			log.info("'Kredit' PaymentType for employee: "+employeeSales.getName()+", totalOrder : "+sumPaymentTypeCredit);
			
			displayTotalPenjualan(sumPaymentTypeTunai, sumPaymentTypeAll);

		}
		
		return !getCustomerOrderList().isEmpty();
	}

	public void onSelect$reportSalesPeriodTabbox(Event event) throws Exception {

		switch (reportSalesPeriodTabbox.getSelectedTab().getIndex()) {
		case 0:
			infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(asLocalDate(todayDatebox.getValue()), getLongDateFormat()));
			// load and display -- default to today (one day only)
			displayReportSales(todayDatebox.getValue(), todayDatebox.getValue());
			break;
		case 1:
			infoResultlabel.setValue("Dari Tanggal: "+dateToStringDisplay(getStartWeekDate(), "dd-MM")+
					" s/d "+dateToStringDisplay(getEndWeekDate(), getShortDateFormat()));			
			// load and display -- default to WORK_DAY_WEEK (one week)
			displayReportSales(asDate(getStartWeekDate()), asDate(getEndWeekDate()));
			break;
		case 2:
			infoResultlabel.setValue("Dari Tanggal: "+dateToStringDisplay(asLocalDate(startDatebox.getValue()), "dd-MM")+
					" s/d "+dateToStringDisplay(asLocalDate(endDatebox.getValue()), getShortDateFormat())+
					" Sales : "+employeeCommissionedCombobox.getSelectedItem().getLabel());
			// load and display --
			displayReportSales(startDatebox.getValue(), endDatebox.getValue());
			break;
		default:
			break;
		}
	}
	
	public void onSelect$reportSalesWeekPeriodTabbox(Event event) throws Exception {
		
		switch (reportSalesWeekPeriodTabbox.getSelectedTab().getIndex()) {
		case 0: // semua
			infoResultlabel.setValue("Dari Tanggal: "+dateToStringDisplay(getStartWeekDate(), "dd-MM")+
					" s/d "+dateToStringDisplay(getEndWeekDate(), getShortDateFormat()));			
			// load and display -- default to WORK_DAY_WEEK (one week)
			displayReportSales(asDate(getStartWeekDate()), asDate(getEndWeekDate()));
			break;
		case 1: // senin
			infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(addDate(0, getStartWeekDate()), getShortDateFormat()));			
			// load and display --
			displayReportSales(asDate(addDate(0, getStartWeekDate())), asDate(addDate(0, getStartWeekDate())));
			break;
		case 2: // selasa
			// load and display --
			infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(addDate(1, getStartWeekDate()), getShortDateFormat()));
			displayReportSales(asDate(addDate(1, getStartWeekDate())), asDate(addDate(1, getStartWeekDate())));
			break;
		case 3: // rabu
			// load and display --
			infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(addDate(2, getStartWeekDate()), getShortDateFormat()));
			displayReportSales(asDate(addDate(2, getStartWeekDate())), asDate(addDate(2, getStartWeekDate())));
			break;
		case 4: // kamis
			// load and display --
			infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(addDate(3, getStartWeekDate()), getShortDateFormat()));
			displayReportSales(asDate(addDate(3, getStartWeekDate())), asDate(addDate(3, getStartWeekDate())));
			break;
		case 5: // jumat
			// load and display --
			infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(addDate(4, getStartWeekDate()), getShortDateFormat()));
			displayReportSales(asDate(addDate(4, getStartWeekDate())), asDate(addDate(4, getStartWeekDate())));
			break;
		case 6: // sabtu
			// load and display --
			infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(addDate(5, getStartWeekDate()), getShortDateFormat()));
			displayReportSales(asDate(addDate(5, getStartWeekDate())), asDate(addDate(5, getStartWeekDate())));
			break;
		default:
			break;
		}
	}
	
	private ListitemRenderer<CustomerOrder> getCustomerOrderListitemRenderer() {
		
		// totalPenjualanKredit = BigDecimal.ZERO;
		
		// totalPenjualanTunai = BigDecimal.ZERO;
		
		return new ListitemRenderer<CustomerOrder>() {
			
			@Override
			public void render(Listitem item, CustomerOrder order, int index) throws Exception {
				Listcell lc;
				
				// Customer Order No.
				lc = new Listcell(order.getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// Tgl.
				lc = new Listcell(dateToStringDisplay(asLocalDate(order.getOrderDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Nama Langganan
				Customer customerByProxy = getCustomerByProxy(order.getId());
				
				lc = new Listcell(customerByProxy==null ? "tunai" : customerByProxy.getCompanyType()+". "+
						customerByProxy.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Nama Sales
				EmployeeCommissions salesByProxy = 
						getEmployeeCommissionsByProxy(order.getId());
				lc = new Listcell(salesByProxy==null ? " " : 
					salesByProxy.getEmployee().getName());
				lc.setParent(item);
								
				// Pembayaran
				lc = new Listcell(order.getPaymentType().compareTo(PaymentType.tunai)==0 ? "tunai" : 
					order.getPaymentType().toString()+" - "+String.valueOf(order.getCreditDay())+" Hari");
				lc.setParent(item);
				
				// PPN (Rp.)
				lc = new Listcell(order.getOrderStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(order.getTotalPpn()));
				lc.setParent(item);
				
				// Nominal (Rp.)
				lc = new Listcell(order.getOrderStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(order.getTotalOrder()));
				lc.setParent(item);				
				
				// details
				lc = initDetails(new Listcell(), order);
				lc.setParent(item);
				
				if (order.getOrderStatus().equals(DocumentStatus.NORMAL)) {
					// accumulate
					if (order.getPaymentType().compareTo(PaymentType.tunai)==0) {
						// totalPenjualanTunai = totalPenjualanTunai.add(order.getTotalOrder());
					} else {
						// totalPenjualanKredit = totalPenjualanKredit.add(order.getTotalOrder());
					}					
				}

				// if the status of customerOrder is 'BATAL', change the backgroud color to red
				if (order.getOrderStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");					
				}				
			}

			private Listcell initDetails(Listcell listcell, CustomerOrder order) {
				Button detailButton = new Button();
				detailButton.setLabel("...");
				detailButton.setSclass("inventoryEditButton");
				detailButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						HashMap<String, Object> arg = new HashMap<String, Object>();
						arg.put("customerOrder", order);
						
						Window detailCustomerOrderWin = 
								(Window) Executions.createComponents("/report/ReportSalesDialog.zul", null, arg);
						
						detailCustomerOrderWin.doModal();
					}
				});
				detailButton.setParent(listcell);
				
				return listcell;
			}
		};
	}
	
	private void displayTotalPenjualan(BigDecimal totalPenjTunai, BigDecimal totalPenjualan) {
		
		totalPenjualanTunaiLabel.setValue(toLocalFormat(totalPenjTunai));
		
		totalPenjualanKreditLabel.setValue(toLocalFormat(totalPenjualan.subtract(totalPenjTunai)));
		
		totalPenjualanLabel.setValue(toLocalFormat(totalPenjualan));
	}
		
	public void onClick$executeTodayButton(Event event) throws Exception {
		infoResultlabel.setValue("Tanggal: "+dateToStringDisplay(asLocalDate(todayDatebox.getValue()), getLongDateFormat()));
		// load and display -- default to today (one day only)
		boolean canPrint = displayReportSales(todayDatebox.getValue(), todayDatebox.getValue());
		
		printTodayButton.setVisible(canPrint);
	}
	
	public void onClick$executeChooseDateButton(Event event) throws Exception {
		infoResultlabel.setValue("Dari Tanggal: "+dateToStringDisplay(asLocalDate(startDatebox.getValue()), "dd-MM")+
				" s/d "+dateToStringDisplay(asLocalDate(endDatebox.getValue()), getShortDateFormat())+
				" Sales : "+employeeCommissionedCombobox.getSelectedItem().getLabel());
		// load and display --
		boolean canPrint = displayReportSales(startDatebox.getValue(), endDatebox.getValue());
		
		printDateRangeButton.setVisible(canPrint);
	}
	
	public void onClick$printTodayButton(Event event) throws Exception {
		ReportSalesData reportSalesData = new ReportSalesData();
		reportSalesData.setCustomerOrders(getCustomerOrderList());
		reportSalesData.setStartDate(todayDatebox.getValue());
		reportSalesData.setEndDate(todayDatebox.getValue());
		
		Map<String, ReportSalesData> arg = 
				Collections.singletonMap("reportSalesData", reportSalesData);
		
		Window reportSalesPrintWin = 
				(Window) Executions.createComponents("/report/print/ReportSalesPrint.zul", null, arg);
		
		reportSalesPrintWin.doModal();
	}
	
	public void onClick$printDateRangeButton(Event event) throws Exception {
		ReportSalesData reportSalesData = new ReportSalesData();
		reportSalesData.setCustomerOrders(getCustomerOrderList());
		reportSalesData.setStartDate(startDatebox.getValue());
		reportSalesData.setEndDate(endDatebox.getValue());
		
		Map<String, ReportSalesData> arg = 
				Collections.singletonMap("reportSalesData", reportSalesData);
		
		Window reportSalesPrintWin = 
				(Window) Executions.createComponents("/report/print/ReportSalesPrint.zul", null, arg);
		
		reportSalesPrintWin.doModal();		
	}
		
	protected EmployeeCommissions getEmployeeCommissionsByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findEmployeeCommissionsByProxy(id);
		
		return customerOrder.getEmployeeCommissions();
	}

	protected Customer getCustomerByProxy(Long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findCustomerByProxy(id);
		
		return customerOrder.getCustomer();
	}
	
	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public List<CustomerOrder> getCustomerOrderList() {
		return customerOrderList;
	}

	public void setCustomerOrderList(List<CustomerOrder> customerOrderList) {
		this.customerOrderList = customerOrderList;
	}

	/**
	 * @return the startWeekDate
	 */
	public LocalDate getStartWeekDate() {
		return startWeekDate;
	}

	/**
	 * @param startWeekDate the startWeekDate to set
	 */
	public void setStartWeekDate(LocalDate startWeekDate) {
		this.startWeekDate = startWeekDate;
	}

	/**
	 * @return the endWeekDate
	 */
	public LocalDate getEndWeekDate() {
		return endWeekDate;
	}

	/**
	 * @param endWeekDate the endWeekDate to set
	 */
	public void setEndWeekDate(LocalDate endWeekDate) {
		this.endWeekDate = endWeekDate;
	}

	public EmployeeDao getEmployeeDao() {
		return employeeDao;
	}

	public void setEmployeeDao(EmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}

}
