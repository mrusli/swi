package com.pyramix.swi.webui.customerorder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.inventory.InventoryType;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.receivable.ActivityType;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.persistence.customer.dao.CustomerDao;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.swi.persistence.user.dao.UserDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherSalesDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SuppressedException;
import com.pyramix.swi.webui.faktur.FakturData;
import com.pyramix.swi.webui.inventory.deliveryorder.DeliveryOrderData;
import com.pyramix.swi.webui.suratjalan.SuratJalanData;
import com.pyramix.swi.webui.voucher.VoucherSalesData;

public class CustomerOrderListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8413692917462466856L;

	private CustomerOrderDao customerOrderDao;
	private VoucherSalesDao voucherSalesDao;
	private CompanyDao companyDao;
	private SuratJalanDao suratJalanDao;
	private CustomerDao customerDao;
	private UserDao userDao;
	private CustomerOrderProductDao customerOrderProductDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox customerOrderListbox;
	private Tabbox ordersPeriodTabbox;
	private Combobox ppnTransactionCombobox, customerCombobox;
	private Button batalButton;
	private Datebox startDatebox, endDatebox;
	
	private List<CustomerOrder> customerOrderList;
	// private BigDecimal totalOrderValue;
	private int orderCount;
	private User loginUser;

	private final long SUNTER_ID 		= 2;
	private final long KARAWANG_ID		= 3;
	private final BigDecimal DIVISOR 	= new BigDecimal(1000000);
	private final int WORK_DAY_WEEK		= 6;
	private final int DEFAULT_TAB_INDEX = 1;
	
	private final static Logger log = Logger.getLogger(CustomerOrderListInfoControl.class);
	
	@SuppressWarnings("rawtypes")
	private EventQueue eq;
	
	public void onCreate$customerOrderListInfoWin(Event event) throws Exception {
		// loginUser is User object
		setLoginUser(getUserDao().findUserByUsername(getLoginUsername()));

		formTitleLabel.setValue("Customer Order");
		
		customerOrderListbox.setEmptyMessage("Tidak ada");
		
		ordersPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);

		// datebox locale
		startDatebox.setLocale(getLocale());
		startDatebox.setFormat(getLongDateFormat());
		endDatebox.setLocale(getLocale());
		endDatebox.setFormat(getLongDateFormat());
		
		setupPpnTransactionSelection();
		
		boolean checkTransaction = false;
		boolean usePpn = false; // <-- non applicable
		
		listBySelection(DEFAULT_TAB_INDEX, checkTransaction, usePpn);
		
		// list all
		// listAllCustomerOrder();

		// load
		// loadCustomerOrderList();
	}

	private void setupPpnTransactionSelection() {
		String[] ppnTransaction = {"Semua", "PPN", "Non-PPN"};
		
		Comboitem comboitem;
		for (String transPpn : ppnTransaction) {
			comboitem = new Comboitem();
			comboitem.setLabel(transPpn.toString());
			comboitem.setParent(ppnTransactionCombobox);
		}
		
		ppnTransactionCombobox.setSelectedIndex(0);
	}

	public void onSelect$ordersPeriodTabbox(Event event) throws Exception {
		boolean checkTransaction = false;
		boolean usePpnSel = false; // <-- non-applicable
		
		// reset to 'semua' transaksi (ppn & non-ppn)
		ppnTransactionCombobox.setSelectedIndex(0);

		int selIndex = ordersPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex, checkTransaction, usePpnSel);
	}

	private void listBySelection(int selIndex, boolean checkTransaction, boolean usePpn) throws Exception {
		Date startDate, endDate;
		
		switch (selIndex) {
		case 0: // Semua			
			// list all
			listAllCustomerOrder(checkTransaction, usePpn);
			
			// find starting date and ending date from customer order
			CustomerOrder customerOrderLatest = getCustomerOrderList().get(0);
			int lastIndex = getCustomerOrderList().size()-1;
			CustomerOrder customerOrderEarliest = getCustomerOrderList().get(lastIndex);
			
			// find all customer in the customer order - all
			listCustomerInCustomerOrder();			

			startDatebox.setValue(customerOrderEarliest.getOrderDate());
			endDatebox.setValue(customerOrderLatest.getOrderDate());
			
			// load
			loadCustomerOrderList();			
			break;
		case 1: // Hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			// list by date
			listCustomerOrderByDate(startDate, endDate, checkTransaction, usePpn);
			
			// find all customer in the customer order - today
			listCustomerInCustomerOrder();
			
			// assign startDate and endDate to date component - disable date component			
			startDatebox.setValue(startDate);
			endDatebox.setValue(endDate);
			
			// load
			loadCustomerOrderList();			
			break;
		case 2: // Minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			// list by date
			listCustomerOrderByDate(startDate, endDate, checkTransaction, usePpn);
			
			// find all customer in the customer order - this week
			listCustomerInCustomerOrder();
			
			// assign startDate and endDate to date component - disable date component			
			startDatebox.setValue(startDate);
			endDatebox.setValue(endDate);
			
			// load
			loadCustomerOrderList();			
			break;
		case 3: // Bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			// list by date
			listCustomerOrderByDate(startDate, endDate, checkTransaction, usePpn);

			// find all customer in the customer order - this month
			listCustomerInCustomerOrder();
			
			// assign startDate and endDate to date component - disable date component			
			startDatebox.setValue(startDate);
			endDatebox.setValue(endDate);
			
			// load
			loadCustomerOrderList();			
			break;
		default:
			break;
		}
	}
	
	private void listAllCustomerOrder(boolean checkTransaction, boolean usePpn) throws Exception {
		boolean listByDescendingOrder = true;
		
		setCustomerOrderList(
				getCustomerOrderDao().findAllCustomerOrder_OrderBy_OrderDate(
						listByDescendingOrder, checkTransaction, usePpn));
		
		orderCount = getCustomerOrderList().size();
		
		batalButton.setDisabled(orderCount==0);
	}

	private void listCustomerOrderByDate(Date startDate, Date endDate, boolean checkTransaction, boolean usePpn) throws Exception {
		boolean listByDescendingOrder = true;
		
		setCustomerOrderList(
				getCustomerOrderDao().findCustomerOrder_By_OrderDate(startDate, endDate, listByDescendingOrder, 
						checkTransaction, usePpn));
		
		orderCount = getCustomerOrderList().size();
		
		batalButton.setDisabled(orderCount==0);
	}
	
	private void listCustomerInCustomerOrder() throws Exception {
		List<Customer> uniqueCustList = new ArrayList<Customer>();
				// 11/01/2022 - can't use the hibernate query due to limitations in DB production
				// getCustomerOrderDao().findUniqueCustomer(checkTransaction, usePpn, startDate, endDate);
		boolean unique = true;
		for (CustomerOrder customerOrder : getCustomerOrderList()) {
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
				for (Customer uniqueCustomer : uniqueCustList) {
					if (uniqueCustomer.getId().compareTo(customer.getId())==0) {
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
		uniqueCustList.sort((o1, o2) ->{
			return o1.getCompanyLegalName().compareTo(o2.getCompanyLegalName());
		});
		loadCustomerCombobox(uniqueCustList);
	}

	private void loadCustomerCombobox(List<Customer> uniqueCustList) {
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
	
	public void onSelect$ppnTransactionCombobox(Event event) throws Exception {
		int selTabIndex = ordersPeriodTabbox.getSelectedIndex();
		int selIndex = ppnTransactionCombobox.getSelectedIndex();
		
		boolean checkTransaction = false;
		boolean usePpn = false; // <-- non-applicable

		switch (selIndex) {
		case 0: // semua transaksi (ppn & non-ppn)
			checkTransaction = false;
			usePpn = false; // <-- non-applicable
			
			listBySelection(selTabIndex, checkTransaction, usePpn);

			break;
		case 1: // transaksi ppn
			checkTransaction = true;
			usePpn = true;
			
			listBySelection(selTabIndex, checkTransaction, usePpn);				
			
			break;
		case 2: // transaksi non-ppn
			checkTransaction = true;
			usePpn = false;
			
			listBySelection(selTabIndex, checkTransaction, usePpn);
			
			break;
		default:
			break;
		}
	}
	
	public void onClick$filterButton(Event event) throws Exception {
		// ppn
		int selIndex = ppnTransactionCombobox.getSelectedIndex();

		boolean checkTransaction = false;
		boolean usePpn = false; // <-- non-applicable

		switch (selIndex) {
			case 0: // semua transaksi (ppn & non-ppn)
				checkTransaction = false;
				usePpn = false; // <-- non-applicable
				
				break;
			case 1: // transaksi ppn
				checkTransaction = true;
				usePpn = true;

				break;
			case 2: // transaksi non-ppn
				checkTransaction = true;
				usePpn = false;

				break;
			default:
				break;
		}
		
		// customer
		Customer selCustomer = customerCombobox.getSelectedItem().getValue();
		
		// date
		Date startDate = startDatebox.getValue();
		Date endDate = endDatebox.getValue();
		
		// query
		boolean listByDescendingOrder = true;
		
		if (selCustomer==null) {
			// user not selecting customer
			setCustomerOrderList(
					getCustomerOrderDao().findAllCustomerOrder_By_Filter(
							listByDescendingOrder, checkTransaction, usePpn, startDate, endDate));			
		} else {
			// user select customer
			setCustomerOrderList(
					getCustomerOrderDao().findSelectedCustomerOrder_By_Filter(
							listByDescendingOrder, checkTransaction, usePpn, selCustomer, startDate, endDate));
		}		
		// display
		loadCustomerOrderList();
	}
	
	public void onClick$resetButton(Event event) throws Exception {
		boolean checkTransaction = false;
		boolean usePpnSel = false; // <-- non-applicable
		
		// reset to 'semua' transaksi (ppn & non-ppn)
		ppnTransactionCombobox.setSelectedIndex(0);

		// rest to 1st
		customerCombobox.setSelectedIndex(0);
		
		int selIndex = ordersPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex, checkTransaction, usePpnSel);

	}
	
	private void loadCustomerOrderList() {
		// set model
		customerOrderListbox.setModel(
				new ListModelList<CustomerOrder>(getCustomerOrderList()));
			
		// render
		customerOrderListbox.setItemRenderer(
				getCustomerOrderListitemRenderer());		
	}
	
	private ListitemRenderer<CustomerOrder> getCustomerOrderListitemRenderer() {

		// totalOrderValue = BigDecimal.ZERO;
		
		return new ListitemRenderer<CustomerOrder>() {
			
			@Override
			public void render(Listitem item, CustomerOrder customerOrder, int index) throws Exception {
				Listcell lc;
								
				// Tgl.
				lc = new Listcell(dateToStringDisplay(
						asLocalDate(customerOrder.getOrderDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// Order-No.
				lc = new Listcell(customerOrder.getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// Customer
				lc = initCustomerDisplay(new Listcell(), customerOrder); 
				lc.setParent(item);				
				
				// Pembayaran
				lc = initPembayaran(new Listcell(), customerOrder);
				lc.setParent(item);
				
				// Total Order
				lc = new Listcell(toLocalFormat(customerOrder.getTotalOrder()));
				lc.setParent(item);
				
				// EmployeeCommissions employeeCommissionsByProxy =
				//	    getEmployeeCommissionsByProxy(customerOrder.getId());
				// lc = new Listcell(employeeCommissionsByProxy==null ?
				//		"-" : employeeCommissionsByProxy.getEmployee().getName());

				// Sales Employee
				Employee employeeSales = getEmployeeSalesByProxy(customerOrder.getId());
				if (employeeSales == null) {
					lc = new Listcell("-");
				} else {
					lc = new Listcell(employeeSales.getName());
				}
				
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);

				// posting - voucher
				lc = initVoucherSalesPosting(new Listcell(), customerOrder);
				lc.setParent(item);

				// Settlement
				lc = initSettlement(new Listcell(), customerOrder);
				lc.setParent(item);				
				
				// Surat Jalan
				lc = initSuratJalan(new Listcell(), customerOrder);
				lc.setParent(item);
				
				// D/O
				lc = initDeliveryOrder(new Listcell(), customerOrder);
				lc.setParent(item);
				
				// Faktur
				lc = initFaktur(new Listcell(), customerOrder);
				lc.setParent(item);				
				
				// edit
				lc = initEditButton(new Listcell(), customerOrder);
				lc.setParent(item);			
				
				// if (customerOrder.getOrderStatus().equals(DocumentStatus.NORMAL)) {
				// 	totalOrderValue = totalOrderValue.add(customerOrder.getTotalOrder());					
				// }
				
				item.setValue(customerOrder);
				
				// if the status of customerOrder is 'BATAL', change the backgroud color to red
				if (customerOrder.getOrderStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");					
				}
			}

			private Listcell initSuratJalan(Listcell listcell, CustomerOrder customerOrder) throws Exception {
				// consist of label and button
				Label 	postingLabel 	= new Label();
				Button 	postingButton 	= new Button();

				if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
					// CustomerOrder BATAL
					
					if (customerOrder.getSuratJalan()==null) {
						postingLabel.setValue("-");
						postingLabel.setWidth("100px");
						postingLabel.setStyle("font-size: 1em; padding-right: 10px");
						postingLabel.setParent(listcell);						
					} else {
						SuratJalan suratJalanByProxy = getSuratJalanByProxy(customerOrder.getId()); 
						DocumentSerialNumber suratJalanNumber = suratJalanByProxy.getSuratJalanNumber();
						
						postingLabel.setValue(suratJalanNumber.getSerialComp());
						postingLabel.setWidth("100px");
						postingLabel.setStyle("font-size: 1em; padding-right: 5px");
						postingLabel.setParent(listcell);
					}
				
				} else if (customerOrder.getSuratJalan()==null) {
					postingLabel.setValue("Belum Dibuat");
					postingLabel.setWidth("100px");
					postingLabel.setStyle("color: red; font-size: 1em; padding-right: 10px");
					postingLabel.setParent(listcell);
					
					postingButton.setLabel("Buat");
					postingButton.setWidth("50px");
					postingButton.setClass("inventoryEditButton");
					postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							SuratJalanData suratJalanData = new SuratJalanData();
							suratJalanData.setCustomerOrder(customerOrder);
							suratJalanData.setSuratJalan(new SuratJalan());
							suratJalanData.setDeliveryOrderRequired(
									customerOrder.isDeliveryOrderRequired());
							suratJalanData.setDeliveryOrderCompany(
									customerOrder.getCompanyToDeliveryOrder());
							suratJalanData.setPageMode(PageMode.NEW);
							suratJalanData.setUserCreate(getLoginUser());
							suratJalanData.setRequestPath(
									Executions.getCurrent().getDesktop().getRequestPath());
							suratJalanData.setNote(customerOrder.getNote());
							
							Map<String, SuratJalanData> arg = 
									Collections.singletonMap("suratJalanData", suratJalanData);
							
							Window suratJalanCreateWin = 
									(Window) Executions.createComponents("/suratjalan/SuratJalanDialog.zul", null, arg);
							
							suratJalanCreateWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

								@SuppressWarnings("unchecked")
								@Override
								public void onEvent(Event event) throws Exception {
									SuratJalan suratJalan = (SuratJalan) event.getData();
																		
									// set
									customerOrder.setSuratJalan(suratJalan);
									
									// update
									getCustomerOrderDao().update(customerOrder);
									
									// load and display
									loadCustomerOrderList();
									
									// notify to update
									eq = EventQueues.lookup("salesInteractive", EventQueues.APPLICATION, true);
									eq.publish(new Event(Events.ON_CHANGE, null, null));
									
								}
							});
							
							suratJalanCreateWin.doModal();
							
						}
					});
					postingButton.setParent(listcell);
					
				} else {
					SuratJalan suratJalanByProxy = getSuratJalanByProxy(customerOrder.getId()); 
					DocumentSerialNumber suratJalanNumber = suratJalanByProxy.getSuratJalanNumber();
					
					postingLabel.setValue(suratJalanNumber.getSerialComp());
					postingLabel.setWidth("100px");
					postingLabel.setStyle("color:blue; font-weight:bold; font-size: 1em; padding-right: 5px");
					postingLabel.setParent(listcell);
					postingLabel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Map<String, SuratJalan> arg = 
									Collections.singletonMap("suratJalan", suratJalanByProxy);
																					
							Window printWindow =
									(Window) Executions.createComponents("/suratjalan/SuratJalanPrint.zul", null, arg);
							
							printWindow.doModal();							
						}
					});
					
					// postingButton.setLabel("Print");
					// postingButton.setWidth("50px");
					// postingButton.setClass("inventoryEditButton");
					// postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					//	@Override
					//	public void onEvent(Event arg0) throws Exception {

					//	}
					// });
					// postingButton.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initSettlement(Listcell listcell, CustomerOrder customerOrder) {
				String settlement = "";
				
				if (customerOrder.getVoucherSales()==null) {
					settlement = "-";
				} else {
					if (customerOrder.getPaymentType().compareTo(PaymentType.tunai)==0) {
						settlement = "Lunas";
					} else {						
						if (customerOrder.isPaymentComplete()) {
							settlement = "Lunas";
						} else {
							settlement = toLocalFormat(customerOrder.getAmountPaid());
						}
					}					
				}
				
				listcell.setLabel(settlement);
				
				return listcell;
			}

			private Listcell initCustomerDisplay(Listcell listcell, CustomerOrder customerOrder) throws Exception {
				Customer customer = getCustomerByProxy(customerOrder.getId());
				
				if (customer==null) {
					listcell.setLabel("tunai");
				} else {
					listcell.setLabel(customer.getCompanyType()+". "+
							customer.getCompanyLegalName());
					listcell.setStyle("white-space: nowrap;");
				}
						
				return listcell;
			}

			private Listcell initPembayaran(Listcell listcell, CustomerOrder customerOrder) {
				String pembayaranInfo = customerOrder.getPaymentType().compareTo(PaymentType.giro)==0 || 
					customerOrder.getPaymentType().compareTo(PaymentType.bank)==0 ?
						customerOrder.getPaymentType().toString()+" - "+customerOrder.getCreditDay()+" Hari" : 
							customerOrder.getPaymentType().toString();
				
				listcell.setLabel(pembayaranInfo);
				
				return listcell;
			}

			private Listcell initVoucherSalesPosting(Listcell listcell, CustomerOrder customerOrder) throws Exception {
				// consist of label and button
				Label 	postingLabel 	= new Label();				
				Button 	postingButton 	= new Button();

				if (customerOrder.getVoucherSales()==null) {
					
					if (customerOrder.getSuratJalan()==null) {
						postingLabel.setValue("-");
						postingLabel.setWidth("100px");
						postingLabel.setStyle("color: red; font-size: 1em; padding-right: 8px");
						postingLabel.setParent(listcell);						
					} else if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
						postingLabel.setValue("-");
						postingLabel.setWidth("100px");
						postingLabel.setParent(listcell);
					} else {
						postingLabel.setValue("Belum Posting");
						postingLabel.setWidth("100px");
						postingLabel.setStyle("color: red; font-size: 1em; padding-right: 8px");
						postingLabel.setParent(listcell);
						
						postingButton.setLabel("Posting");
						postingButton.setWidth("60px");
						postingButton.setClass("inventoryEditButton");
						postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								VoucherSalesData data = new VoucherSalesData();
								data.setPageMode(PageMode.NEW);
								data.setVoucherSales(null);
								data.setCustomerOrder(customerOrder);
								
								Map<String, VoucherSalesData> arg = 
										Collections.singletonMap("voucherSalesData", data);
								
								Window voucherSalesDialogWin = 
										(Window) Executions.createComponents("/voucher/VoucherSalesDialog.zul", null, arg);
								
								// add eventlistener
								voucherSalesDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
									
									@Override
									public void onEvent(Event event) throws Exception {
										// get the data
										VoucherSales voucherSales = (VoucherSales) event.getData();
										
										// set the link
										customerOrder.setVoucherSales(voucherSales);
										
										// create the receivable activities for this customer
										// -- only NON tunai payment type
										if (customerOrder.getPaymentType().compareTo(PaymentType.tunai)!=0) {
											log.info("VoucherSales: "+customerOrder.getPaymentType().toString());
											
											// GET the receivable
											CustomerReceivable receivableFromProxy = 
													getCustomerReceivableFromProxy(customerOrder.getId());
											
											if (receivableFromProxy.getCustomerReceivableActivities().isEmpty()) {
												receivableFromProxy.setCustomerReceivableActivities(
														getReceivableActivityFromVoucherSales(
																new ArrayList<CustomerReceivableActivity>(),voucherSales));																					
											} else {
												List<CustomerReceivableActivity> existingList = 
														receivableFromProxy.getCustomerReceivableActivities();
												receivableFromProxy.setCustomerReceivableActivities(
														getReceivableActivityFromVoucherSales(
																existingList, voucherSales));
											}										
											
											// paymentDueDate --> will be the latest
											LocalDate paymentDueDate = addDate(voucherSales.getJumlahHari(), 
													asLocalDate(voucherSales.getTransactionDate()));
											receivableFromProxy.setLatestDue(asDate(paymentDueDate));
											
											customerOrder.setCustomerReceivable(receivableFromProxy);
										}
										
										// update
										getCustomerOrderDao().update(customerOrder);
										
										// notify to update
										// EventQueue eq = EventQueues.lookup("voucherSales", EventQueues.APPLICATION, true);
										// eq.publish(new Event(Events.ON_CHANGE, null, "data has changed..."));
										
										// load
										loadCustomerOrderList();
									}
									
									private List<CustomerReceivableActivity> getReceivableActivityFromVoucherSales(
											List<CustomerReceivableActivity> receivableActivities, VoucherSales voucherSales) {
										
										CustomerReceivableActivity activity = 
												new CustomerReceivableActivity();
										// `customer_order_id` bigint(20)
										activity.setCustomerOrderId(voucherSales.getCustomerOrder().getId());
										//  `activity_type` int(11) DEFAULT NULL,
										activity.setActivityType(ActivityType.PENJUALAN);
										//  `sales_date` date DEFAULT NULL,
										activity.setSalesDate(voucherSales.getTransactionDate());
										//  `payment_due_date` date DEFAULT NULL,
										activity.setPaymentDueDate(asDate(
												addDate(voucherSales.getJumlahHari(), 
														asLocalDate(voucherSales.getTransactionDate()))));
										//  `sales_description` varchar(255) DEFAULT NULL,
										activity.setSalesDescription(ActivityType.PENJUALAN.toString()+
												" dengan voucher no.: "+
												voucherSales.getVoucherNumber().getSerialComp());
										//  `amount_sales` decimal(19,2) DEFAULT NULL,
										activity.setAmountSales(voucherSales.getTheSumOf());
										//  `amount_sales_ppn` decimal(19,2) DEFAULT NULL,
										activity.setAmountSalesPpn(voucherSales.getPpnAmount());
										//  `payment_date` date DEFAULT NULL,
										activity.setPaymentDate(null);
										//  `payment_description` varchar(255) DEFAULT NULL,
										activity.setPaymentDescription(null);
										//  `overdue_day` int(11) DEFAULT NULL,
										activity.setOverdueDay(0);									
										//  `amount_paid` decimal(19,2) DEFAULT NULL,
										activity.setAmountPaid(BigDecimal.ZERO);
										//  `amount_paid_ppn` decimal(19,2) DEFAULT NULL,  
										activity.setAmountPaidPpn(BigDecimal.ZERO);
										//  `payment_complete` char(1) DEFAULT NULL,
										activity.setPaymentComplete(false);
										//  `remaining_amount` decimal(19,2) DEFAULT NULL,
										activity.setRemainingAmount(BigDecimal.ZERO);
										//
										activity.setReceivableStatus(DocumentStatus.NORMAL);
										
										log.info("Create: "+activity.toString());
										
										// add
										receivableActivities.add(activity);
										
										return receivableActivities;
									}
								});
								
								voucherSalesDialogWin.doModal();							
								
							}
						});
						postingButton.setParent(listcell);
						
					}
					
				} else {
					VoucherSales voucherSalesByProxy = getVoucherSalesByProxy(customerOrder.getId());
					VoucherSerialNumber voucherSerNum = voucherSalesByProxy.getVoucherNumber();
					
					postingLabel.setValue(voucherSerNum.getSerialComp());
					postingLabel.setWidth("100px");
					postingLabel.setStyle("color:blue;font-weight:bold;font-size: 1em; padding-right: 5px");
					postingLabel.setParent(listcell);
					postingLabel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							log.info("Voucher Number Label Clicked...");
							VoucherSalesData data = new VoucherSalesData();
							data.setPageMode(PageMode.VIEW);
							data.setVoucherSales(getVoucherSalesByProxy(customerOrder.getId()));
							data.setCustomerOrder(null);
							
							Map<String, VoucherSalesData> arg = 
									Collections.singletonMap("voucherSalesData", data);
							
							Window voucherSalesDialogWin = 
									(Window) Executions.createComponents("/voucher/VoucherSalesDialog.zul", null, arg);
							
							voucherSalesDialogWin.doModal();													
						}
					});

					// postingButton.setLabel("View");
					// postingButton.setWidth("60px");
					// postingButton.setClass("inventoryEditButton");
					// postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
						
					//	@Override
					//	public void onEvent(Event event) throws Exception {
							
					//	}
					// });
					// postingButton.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initDeliveryOrder(Listcell listcell, CustomerOrder customerOrder) throws Exception {
				SuratJalan suratJalanByProxy = getSuratJalanByProxy(customerOrder.getId());
				if (suratJalanByProxy==null) {
					listcell.setLabel("-");					
				} else {

					if (suratJalanByProxy.getDeliveryOrder()==null) {
						listcell.setLabel("-");
					} else if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
						// CustomerOrder BATAL
						Label postingLabel = new Label();
						
						DeliveryOrder deliveryOrderByProxy = 
								getDeliveryOrderFromSuratJalan_ByProxy(suratJalanByProxy.getId());

						postingLabel.setValue(
								deliveryOrderByProxy.getDeliveryOrderNumber().getSerialComp());
						postingLabel.setStyle("font-size: 1em; padding-right: 5px");
						postingLabel.setParent(listcell);
						
					} else {
						Label postingLabel = new Label();

						DeliveryOrder deliveryOrderByProxy = 
								getDeliveryOrderFromSuratJalan_ByProxy(suratJalanByProxy.getId());

						postingLabel.setValue(
								deliveryOrderByProxy.getDeliveryOrderNumber().getSerialComp());
						postingLabel.setStyle("color:blue; font-weight:bold; font-size: 1em; padding-right: 5px");
						postingLabel.setParent(listcell);
						postingLabel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								DeliveryOrderData deliveryOrderData = new DeliveryOrderData();
								deliveryOrderData.setDeliveryOrder(deliveryOrderByProxy);
								deliveryOrderData.setSuratJalan(suratJalanByProxy);
								
								Map<String, DeliveryOrderData> arg =
										Collections.singletonMap("deliveryOrderData", deliveryOrderData);
								
								Window deliveryOrderPrintWin = 
										(Window) Executions.createComponents("/deliveryorder/DeliveryOrderPrint.zul", null, arg);
								
								deliveryOrderPrintWin.doModal();								
							}
						});
						
						// postingButton.setLabel("Print");
						// postingButton.setClass("inventoryEditButton");
						// postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						//	@Override
						//	public void onEvent(Event arg0) throws Exception {
								
						//	}
						// });
						// postingButton.setParent(listcell);
					}

				}
				
				return listcell;
			}
			
			private Listcell initFaktur(Listcell listcell, CustomerOrder customerOrder) throws Exception {
				SuratJalan suratJalanByProxy = getSuratJalanByProxy(customerOrder.getId());
				if (suratJalanByProxy==null) {
					listcell.setLabel("-");					
				} else if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
					// CustomerOrder BATAL
					
					Label postingLabel = new Label();

					Faktur fakturByProxy =
							getFakturFromSuratJalan_ByProxy(suratJalanByProxy.getId());

					postingLabel.setValue(
							fakturByProxy.getFakturNumber().getSerialComp());
					postingLabel.setStyle("font-size: 1em; padding-right: 5px");
					postingLabel.setParent(listcell);

				} else {

					if (suratJalanByProxy.getFaktur()==null) {
						listcell.setLabel("-");
					} else {
						Label postingLabel = new Label();
						// Button postingButton = new Button();
						
						Faktur fakturByProxy =
								getFakturFromSuratJalan_ByProxy(suratJalanByProxy.getId());

						postingLabel.setValue(
								fakturByProxy.getFakturNumber().getSerialComp());
						postingLabel.setStyle("color:blue; font-weight:bold; font-size: 1em; padding-right: 5px");
						postingLabel.setParent(listcell);
						postingLabel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								FakturData fakturData = new FakturData();
								fakturData.setFaktur(fakturByProxy);
								fakturData.setSuratJalan(suratJalanByProxy);
								
								Map<String, FakturData> arg =
										Collections.singletonMap("fakturData", fakturData);
								
								Window fakturPrintWin = 
										(Window) Executions.createComponents("/faktur/FakturPrint.zul", null, arg);
								
								fakturPrintWin.doModal();								
							}
						});
						
						// postingButton.setLabel("Print");
						// postingButton.setClass("inventoryEditButton");
						// postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						//	@Override
						//	public void onEvent(Event arg0) throws Exception {
								
						//	}

						// });
						// postingButton.setParent(listcell);
					}

				}

				return listcell;
			}
			
			private Listcell initEditButton(Listcell listcell, CustomerOrder customerOrder) {
				Button editButton = new Button();

				if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
					// CustomerOrder BATAL
					editButton.setLabel("View");
					editButton.setClass("inventoryEditButton");
					editButton.addEventListener("onClick", new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {						
							CustomerOrderData data = new CustomerOrderData();
							data.setPageMode(PageMode.VIEW);
							data.setCustomerOrder(customerOrder);
							data.setCustomerOrderDao(getCustomerOrderDao());
							
							Map<String, CustomerOrderData> arg = 
									Collections.singletonMap("orderData", data);

							Window customerOrderDialogWin = (Window) Executions.createComponents(
									"/customerorder/CustomerOrderDialog.zul", null, arg);
							
							customerOrderDialogWin.doModal();
						}
						
					});
				} else {
					editButton.setLabel((customerOrder.getVoucherSales()==null 
							&& customerOrder.getSuratJalan()==null) ?
									"Edit" : "View");
					editButton.setClass("inventoryEditButton");
					editButton.addEventListener("onClick", new EventListener<Event>() {
						
						@Override
						public void onEvent(Event event) throws Exception {						
							CustomerOrderData data = new CustomerOrderData();
							data.setPageMode((customerOrder.getVoucherSales()==null && customerOrder.getSuratJalan()==null) ?
									PageMode.EDIT : PageMode.VIEW);
							data.setCustomerOrder(customerOrder);
							data.setCustomerOrderDao(getCustomerOrderDao());
							
							Map<String, CustomerOrderData> arg = 
									Collections.singletonMap("orderData", data);
							
							log.info("User to view / modify the CustomerOrder...");
							
							Window customerOrderDialogWin = (Window) Executions.createComponents(
									"/customerorder/CustomerOrderDialog.zul", null, arg);
							
							customerOrderDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {
								
								@Override
								public void onEvent(Event event) throws Exception {
									// get the data
									CustomerOrder customerOrder = (CustomerOrder) event.getData();
									log.info("Get the modified CustomerOrder id: "+customerOrder.getId());
									
									// update the inventory
									log.info("Starting to update the inventory (AFTER USER MODIFY the CustomerOrder...");
									for (CustomerOrderProduct product : customerOrder.getCustomerOrderProducts()) {
										
										log.info("CustomerOrderProduct id:"+product.getId());
										
										if (product.getId().compareTo(Long.MIN_VALUE)==0) {
											// user adds another CustomerOrderProduct to CustomerOrder
											log.info("User adds another CustomerOrderProduct...");
											
											if (product.getInventoryPacking().compareTo(InventoryPacking.lembaran)==0) {
												log.info("Inventory Packing is: "+product.getInventoryPacking().toString());
												
												log.info("access to inventory ShtQty from CustomerOrderProduct : "+product.getInventory().getSheetQuantity());
												
												// update shtQty
												updateLembaranShtQtyInventory(product);
												// update weightQty
												updateLembaranWeightQtyInventory(product);
												// update note
												product.getInventory().setNote(
														customerOrder.getDocumentSerialNumber().getSerialComp());
												
												log.info("after deducting inventory ShtQty from CustomerOrderProduct : "+product.getInventory().getSheetQuantity());
											} else {
												log.info("Inventory Packing is: "+product.getInventoryPacking().toString());
												
												// update status
												product.getInventory().setInventoryStatus(InventoryStatus.sold);
												// update note
												product.getInventory().setNote(customerOrder.getDocumentSerialNumber().getSerialComp());											
											}							
											
										} else {
											// existing CustomerOrderProduct
											log.info("CustomerOrderProduct id: "+product.getId()+" - user did not modify existing CustomerOrderProduct. Do nothing.");
											
											// do nothing
										}
										
									}
									
									// location to determine whether D/O is needed or not
									setCustomerOrderRequiredDeliveryOrder(customerOrder);
									
									// receivable account from Customer id
									if (customerOrder.getPaymentType().compareTo(PaymentType.tunai) != 0) {
										log.info("PaymentType changed to : "+customerOrder.getPaymentType().toString());
										log.info("Getting the Receivable Reference from Customer id : "+customerOrder.getCustomer().getId());
										
										CustomerReceivable receivable = 
												getCustomerReceivableFromCustomerByProxy(customerOrder.getCustomer().getId());				
										customerOrder.setCustomerReceivable(receivable);
										
										log.info("Receivable Reference is set to id: "+receivable.getId());
									} else {
										log.info("PaymentType changed to : "+customerOrder.getPaymentType().toString());
										
										customerOrder.setCustomerReceivable(null);
									}
									
									// update
									log.info("Updating Inventory via CustomerOrder id: "+customerOrder.getId());
									
									getCustomerOrderDao().update(customerOrder);
									
									// load
									loadCustomerOrderList();
								}
								
							});						
							
							customerOrderDialogWin.doModal();
						}
					});

				}
				
				
				editButton.setParent(listcell);
				
				return listcell;
			}
			
		};
	}

	public void onAfterRender$customerOrderListbox(Event event) throws Exception {
		// infoResultlabel.setValue("Total: "+orderCount+" orders - Rp."+toLocalFormat(totalOrderValue));
		infoResultlabel.setValue("");
	}
	
	public void onSelect$customerOrderListbox(Event event) throws Exception {
		CustomerOrder customerOrder = customerOrderListbox.getSelectedItem().getValue();
		
		batalButton.setDisabled(customerOrder.getOrderStatus().equals(DocumentStatus.BATAL));
	}
	
	public void onClick$addButton(Event event) throws Exception {
		CustomerOrderData data = new CustomerOrderData();
		data.setPageMode(PageMode.NEW);
		data.setCustomerOrder(null);
		data.setCustomerOrderDao(getCustomerOrderDao());
		
		Map<String, CustomerOrderData> arg = 
				Collections.singletonMap("orderData", data);

		Window customerOrderDialogWin = (Window) Executions.createComponents(
				"/customerorder/CustomerOrderDialog.zul", null, arg);

		customerOrderDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// get the data				
				CustomerOrder customerOrder = (CustomerOrder) event.getData();
				// customer object needed for the note in the inventory sold
				Customer customer = customerOrder.getCustomer();
				
				// update the inventory
				log.info("Updating Inventory using CustomerOrderProduct...");

				for (CustomerOrderProduct product : customerOrder.getCustomerOrderProducts()) {
					
					if (product.getInventoryPacking().compareTo(InventoryPacking.lembaran)==0) {
						log.info("InventoryPacking is: "+product.getInventoryPacking().toString());
												
						// update shtQty
						updateLembaranShtQtyInventory(product);
						// update weightQty
						updateLembaranWeightQtyInventory(product);
						
						// NOTE: Potential Error: when this inventory is updated multiple times,
						// eventually the note is running out of space.  Maximum is 255 characters.
						// -- 24/06/2020 - set to save the latest customer order serial number only.
						// i.e., not concating customer order serial number.
						// String noteInventory = 
						//		product.getInventory().getNote();
						 product.getInventory().setNote(
								//noteInventory==null ? 
								//customerOrder.getDocumentSerialNumber().getSerialComp() :
								//noteInventory+" "+
								customerOrder.getDocumentSerialNumber().getSerialComp());
					} else {
						log.info("InventoryPacking is: "+product.getInventoryPacking().toString());

						product.getInventory().setInventoryStatus(InventoryStatus.sold);
						// product.getInventory().setNote(customerOrder.getDocumentSerialNumber().getSerialComp());
						// 21/01/2022 - change to customer name
						product.getInventory().setNote(
								customer.getCompanyType()+"."+customer.getCompanyLegalName());
						
						log.info("Set Inventory Petian/Coil status to: "+
								product.getInventory().getInventoryStatus().toString());						
					}
					
				}
				
				// location to determine whether D/O is needed or not
				setCustomerOrderRequiredDeliveryOrder(customerOrder);
				
				// receivable account from Customer id
				if (customerOrder.getPaymentType().compareTo(PaymentType.tunai) != 0) {
					CustomerReceivable receivable = 
							getCustomerReceivableFromCustomerByProxy(customerOrder.getCustomer().getId());				
					customerOrder.setCustomerReceivable(receivable);
				}
				
				// assign the current login user
				customerOrder.setUserCreate(getLoginUser());				
				
				// save
				getCustomerOrderDao().save(customerOrder);
				
				// check the selected tab
				int selTabIndex = ordersPeriodTabbox.getSelectedIndex();
				
				// check the selected transaction
				int selTransactionIndex = ppnTransactionCombobox.getSelectedIndex();
				boolean checkTransaction = selTransactionIndex > 0 ? true : false;
				boolean usePpn = selTransactionIndex == 1 ? true : false;
				
				// list
				listBySelection(selTabIndex, checkTransaction, usePpn);
			}
		});
		
		customerOrderDialogWin.doModal();
	}

	private void updateLembaranShtQtyInventory(CustomerOrderProduct product) {
		int shtQtyInventory = product.getInventory().getSheetQuantity();
		int shtQtyProduct = product.getSheetQuantity();
		
		log.info("customerOrderShtQty: "+product.getSheetQuantity()+
				", inventoryId: "+product.getInventory().getId()+
				", inventoryShtQty: "+product.getInventory().getSheetQuantity());
		
		product.getInventory().setSheetQuantity(shtQtyInventory-shtQtyProduct);

		log.info("update InventoryLembaranShtQtyInventory to: "+
				product.getInventory().getSheetQuantity());

	}

	private void updateLembaranWeightQtyInventory(CustomerOrderProduct product) {
		BigDecimal weightQtyInventory = product.getInventory().getWeightQuantity();
		BigDecimal weightQtyProduct = product.getWeightQuantity();

		log.info("customerOrderWeightQty: "+weightQtyProduct+
				", inventoryId: "+product.getInventory().getId()+
				", inventoryWeightQty: "+weightQtyProduct);

		product.getInventory().setWeightQuantity(weightQtyInventory.subtract(weightQtyProduct));

		log.info("update InventoryLembaranWeightQtyInventory to: "+
				product.getInventory().getWeightQuantity());
	}

	@SuppressWarnings("unused")
	private BigDecimal calculateWeightPerSheet(CustomerOrderProduct product) {
		// to calc qty(kg) from the qth(sht)
		InventoryType inventoryType = product.getInventoryCode().getInventoryType();

		BigDecimal density = inventoryType.getDensity();
		BigDecimal thickness = product.getThickness();
		BigDecimal width = product.getWidth();
		BigDecimal length = product.getLength();
		
		return thickness.multiply(width).multiply(length).multiply(density)
				.divide(DIVISOR, RoundingMode.HALF_UP);
	}

	private void setCustomerOrderRequiredDeliveryOrder(CustomerOrder customerOrder) throws Exception {
		CustomerOrderProduct product = customerOrder.getCustomerOrderProducts().get(0);
		InventoryLocation location = product.getInventoryLocation();		
		switch (location) {
		case swi:
			customerOrder.setDeliveryOrderRequired(false);
			customerOrder.setCompanyToDeliveryOrder(null);					
			break;
		case sunter:
			customerOrder.setDeliveryOrderRequired(true);
			Company companySunter = getCompanyDao().findCompanyById(SUNTER_ID);
			customerOrder.setCompanyToDeliveryOrder(companySunter);
			break;
		case karawang:
			customerOrder.setDeliveryOrderRequired(true);
			Company companyKarawang = getCompanyDao().findCompanyById(KARAWANG_ID);
			customerOrder.setCompanyToDeliveryOrder(companyKarawang);
			break;
		default:
			break;
		}				

		log.info("Inventory Location: "+location.toString()+
				" DeliveryOrder Required: "+(customerOrder.isDeliveryOrderRequired() ? "Yes" : "No"));

	}
	
	
	public void onClick$batalButton(Event event) throws Exception {
		Listitem item = customerOrderListbox.getSelectedItem();
		
		if (item==null) {
			throw new SuppressedException("Belum memilih Customer Order untuk dibatalkan.", true);			
		} else {
			CustomerOrder customerOrder = customerOrderListbox.getSelectedItem().getValue();
					
			if (customerOrder.isPaymentComplete()) {
				throw new SuppressedException("Batalkan Settlement terlebih dahulu sebelum membatalkan Customer Order", true);
			}
						
			log.info("Starting CustomerOrder Batal...");
			
			Map<String, CustomerOrder> args = 
					Collections.singletonMap("customerOrder", customerOrder);
			Window customerOrderBatalWin = 
					(Window) Executions.createComponents("/customerorder/CustomerOrderDialogBatal02.zul", null, args);
			
			customerOrderBatalWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					// re-load / re-list
					int selIndex = ordersPeriodTabbox.getSelectedIndex();
					
					listBySelection(selIndex, false, false);
				}
			});
			
			customerOrderBatalWin.doModal();
		}
	}
	
	protected CustomerReceivable getCustomerReceivableFromCustomerByProxy(long id) throws Exception {
		Customer customer = getCustomerDao().findCustomerReceivableByProxy(id);
		
		return customer.getCustomerReceivable();
	}

	protected EmployeeCommissions getEmployeeCommissionsByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findEmployeeCommissionsByProxy(id);
		
		return customerOrder.getEmployeeCommissions();
	}
	
	private Employee getEmployeeSalesByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findEmployeeSalesByProxy(id);
		
		return customerOrder.getEmployeeSales();
	}	
	
	protected DeliveryOrder getDeliveryOrderFromSuratJalan_ByProxy(long id) throws Exception {
		SuratJalan suratJalan = getSuratJalanDao().findDeliveryOrderByProxy(id);
		
		return suratJalan.getDeliveryOrder();
	}	
	
	protected Faktur getFakturFromSuratJalan_ByProxy(long id) throws Exception {
		SuratJalan suratJalan = getSuratJalanDao().findFakturByProxy(id);
		
		return suratJalan.getFaktur();
	}
	
	protected SuratJalan getSuratJalanByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findSuratJalanByProxy(id); 

		return customerOrder.getSuratJalan();
	}	
	
	
	private VoucherSales getVoucherSalesByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findVoucherSalesByProxy(id); 

		return customerOrder.getVoucherSales();
	}
	
	private Customer getCustomerByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findCustomerByProxy(id); 

		return customerOrder.getCustomer();
	}

	private CustomerReceivable getCustomerReceivableFromProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findCustomerReceivableByProxy(id);
		
		return customerOrder.getCustomerReceivable();
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

	public VoucherSalesDao getVoucherSalesDao() {
		return voucherSalesDao;
	}

	public void setVoucherSalesDao(VoucherSalesDao voucherSalesDao) {
		this.voucherSalesDao = voucherSalesDao;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public SuratJalanDao getSuratJalanDao() {
		return suratJalanDao;
	}

	public void setSuratJalanDao(SuratJalanDao suratJalanDao) {
		this.suratJalanDao = suratJalanDao;
	}

	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
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

	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}
}
