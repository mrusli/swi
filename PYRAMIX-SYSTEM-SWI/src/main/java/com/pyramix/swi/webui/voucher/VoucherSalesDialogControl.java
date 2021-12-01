package com.pyramix.swi.webui.voucher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.domain.voucher.VoucherSalesDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherStatus;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherSalesDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

public class VoucherSalesDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6721306998273502766L;
	
	private CustomerOrderDao customerOrderDao;
	private VoucherSalesDao voucherSalesDao;
	private Coa_05_MasterDao coa_05_MasterDao;
	private SerialNumberGenerator serialNumberGenerator;
	private CustomerReceivableDao customerReceivableDao;
	
	private Window voucherSalesDialogWin;
	private Grid createFromGrid;
	private Datebox transactionDatebox;
	private Button customerButton, totalOrderButton, ppnAmountButton, saveButton, cancelButton;
	private Combobox customerOrderCombobox, voucherStatusCombobox, pembayaranCombobox;
	private Hlayout dbcrControl;
	private Checkbox createFromCheckbox, usePpnCheckbox;
	private Textbox voucherNoCompTextbox, voucherNoPostTextbox, customerTextbox, 
		descriptionTextbox, referenceTextbox, ppnAmountTextbox, theSumOfTextbox;
	private Label voucherTypeLabel, idLabel, infoDebitCreditlabel;
	private Intbox jumlahHariIntbox;
	private Listbox voucherDbcrListbox;
	private Listfooter totalDebitListfooter, totalCreditListfooter;
	
	private VoucherSalesData voucherSalesData;
	private VoucherSales voucherSales;
	private PageMode pageMode;
	private String requestPath;
	private BigDecimal totalOrderValue;
	private BigDecimal totalPpnValue;
	private List<VoucherSalesDebitCredit> voucherSalesDebitCreditList;
	private int dbcrCount;
	private BigDecimal totalDebitVal, totalCreditVal;
	
	private final static String 		CUSTOMER_ORDER_REQUEST_PATH 	= "/customerorder/CustomerOrderListInfo.zul";
	private final static VoucherType 	CASH_SALES_VOUCHER 				= VoucherType.SALES_CASH;
	private final static VoucherType 	CREDIT_SALES_VOUCHER 			= VoucherType.SALES_CREDIT;
	private final static VoucherStatus	DEFAULT_FLOW_STATUS 			= VoucherStatus.Posted;
	private final static DocumentStatus STATUS							= DocumentStatus.NORMAL; 
	
	private final static Logger log = Logger.getLogger(VoucherSalesDialogControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setVoucherSalesData(
				(VoucherSalesData) Executions.getCurrent().getArg().get("voucherSalesData"));
		
		// who is the caller for this dialog?
		setRequestPath(
				Executions.getCurrent().getDesktop().getRequestPath());
	}

	public void onCreate$voucherSalesDialogWin(Event event) throws Exception {
		transactionDatebox.setLocale(getLocale());
		transactionDatebox.setFormat(getLongDateFormat());
				
		setPageMode(
				getVoucherSalesData().getPageMode());
		
		switch (getPageMode()) {
		case EDIT:
			voucherSalesDialogWin.setTitle("Merubah (Edit) Sales Voucher");
			break;
		case NEW:
			voucherSalesDialogWin.setTitle("Membuat (Tambah) Sales Voucher");
			break;
		case VIEW:
			voucherSalesDialogWin.setTitle("Melihat (View) Sales Voucher");
			break;
		default:
			break;
		}

		createVoucherStatusCombobox();
		
		createPaymentTypeCombobox();
		
		if (getRequestPath().matches(CUSTOMER_ORDER_REQUEST_PATH)) {
			// *** create SalesVoucher from CustomerOrder
			log.info("Create SalesVoucher from: "+CUSTOMER_ORDER_REQUEST_PATH);
			
			// determine true/false
			boolean setting = getPageMode().compareTo(PageMode.NEW)==0;
			
			// caller from CustomerOrder page -- depends on the PageMode
			createFromGrid.setVisible(setting);
			// no option for user to create SalesVoucher manually
			createFromCheckbox.setDisabled(setting);
			// display the CustomerOrder number -- no selection
			customerOrderCombobox.setValue(setting ? 
				getVoucherSalesData().getCustomerOrder().getDocumentSerialNumber().getSerialComp() : "");
			// set to readonly / disable
			customerOrderCombobox.setDisabled(setting);
			
			// voucher status
			voucherStatusCombobox.setValue(DEFAULT_FLOW_STATUS.toString());
			
			// obtain the CustomerOrder data -- convert to VoucherSales
			setVoucherSales(getPageMode().compareTo(PageMode.NEW)==0 ?
					customerOrderToVoucherSales(new VoucherSales(), getVoucherSalesData().getCustomerOrder()) :
						getVoucherSalesData().getVoucherSales());

		} else {
			// *** create SalesVoucher from VoucherSalesDialog
			
			// caller from VoucherSales page -- depends on the PageMode
			createFromGrid.setVisible(getPageMode().compareTo(PageMode.NEW)==0);
			// option for user to create SalesVoucher manually
			createFromCheckbox.setDisabled(false);
			// load comboitems with CustomerOrder number
			loadCustomerOrderSelections();
			// allow users to select
			customerOrderCombobox.setDisabled(false);
			
			// voucher status
			voucherStatusCombobox.setValue(DEFAULT_FLOW_STATUS.toString());
			// voucherStatusCombobox.setDisabled(false);
			
			// obtain the VoucherSales data -- depneds on the PageMode
			setVoucherSales(getPageMode().compareTo(PageMode.NEW)==0 ?
					null : getVoucherSalesData().getVoucherSales());			
		}
		
		setReadOnly();
		
		// load info
		setVoucherSalesInfo();
	}

	private SuratJalan getSuratJalanByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findSuratJalanByProxy(id);
		
		return customerOrder.getSuratJalan();
	}

	private void createVoucherStatusCombobox() {
		Comboitem item;
		
		for (VoucherStatus status : VoucherStatus.values()) {
			item = new Comboitem();
			item.setLabel(status.toString());
			item.setValue(status);
			item.setParent(voucherStatusCombobox);
		}
		
	}

	private void createPaymentTypeCombobox() {
		Comboitem item;
		
		for (PaymentType type : PaymentType.values()) {
			item = new Comboitem();
			item.setLabel(type.toString());
			item.setValue(type);
			item.setParent(pembayaranCombobox);
		}
		
	}

	private void setVoucherSalesInfo() throws Exception {
		// voucherStatusCombobox.setValue(DEFAULT_FLOW_STATUS.toString());
		
		if (getVoucherSales()==null) {
			voucherNoCompTextbox.setValue(null);
			voucherNoPostTextbox.setValue(null);
			transactionDatebox.setValue(asDate(getLocalDate()));
			pembayaranCombobox.setValue("");
			jumlahHariIntbox.setValue(0);
			usePpnCheckbox.setChecked(true);
			voucherTypeLabel.setValue(""); 
			customerTextbox.setValue("");
			theSumOfTextbox.setValue("");
			descriptionTextbox.setValue("");
			referenceTextbox.setValue("");
		} else if (getVoucherSales().getId()==Long.MIN_VALUE) {						
			voucherNoCompTextbox.setValue(null);
			voucherNoPostTextbox.setValue(null);
			
			transactionDatebox.setValue(getVoucherSales().getCreateDate());
			
			for (Comboitem item : pembayaranCombobox.getItems()) {
				if (getVoucherSales().getPaymentType().equals(item.getValue())) {
					pembayaranCombobox.setSelectedItem(item);
				}
			}
			
			jumlahHariIntbox.setValue(getVoucherSales().getJumlahHari());
			voucherTypeLabel.setValue(getVoucherSales().getVoucherType().toString());
			voucherStatusCombobox.setValue(getVoucherSales().getFlowStatus().toString());			
			
			// new VoucherSales -- user selects from CustomerOrder 
			customerTextbox.setValue(getVoucherSales().getCustomer()==null ? "tunai" : 
				getVoucherSales().getCustomer().getCompanyType().toString()+". "+
				getVoucherSales().getCustomer().getCompanyLegalName());
			customerTextbox.setAttribute("customer", getVoucherSales().getCustomer());
			
			theSumOfTextbox.setValue(toLocalFormat(getTotalOrderValue()));
			usePpnCheckbox.setChecked(getVoucherSales().isUsePpn());
			ppnAmountTextbox.setValue(toLocalFormat(getTotalPpnValue()));
			
			referenceTextbox.setValue(getVoucherSales().getDocumentRef());
			descriptionTextbox.setValue(getVoucherSales().getTransactionDescription());

			// USER Clicks Debit/Kredit button to display
			// setVoucherSalesDebitCreditInfo(getVoucherSales().getVoucherSalesDebitCredits());
			
		} else {
			idLabel.setValue(getVoucherSales().getId()==Long.MIN_VALUE ? 
					"" : String.valueOf(getVoucherSales().getId()));
			voucherNoCompTextbox.setValue(getVoucherSales().getVoucherNumber().getSerialComp());
			voucherNoPostTextbox.setValue(null);
			
			transactionDatebox.setValue(getVoucherSales().getCreateDate());
			for (Comboitem item : pembayaranCombobox.getItems()) {
				if (getVoucherSales().getPaymentType().equals(item.getValue())) {
					pembayaranCombobox.setSelectedItem(item);
				}
			}
			jumlahHariIntbox.setValue(getVoucherSales().getJumlahHari());
			voucherTypeLabel.setValue(getVoucherSales().getVoucherType().toString());
			voucherStatusCombobox.setValue(getVoucherSales().getFlowStatus().toString());			

			// existing VoucherSales -- user selects from VoucherSales - must use proxy
			Customer customer = getCustomerFromSalesVoucherByProxy(getVoucherSales().getId());			
			customerTextbox.setValue(getVoucherSales().getCustomer()==null ? "tunai" : 
				customer.getCompanyType().toString()+". "+
				customer.getCompanyLegalName());
			customerTextbox.setAttribute("customer", customer);

			theSumOfTextbox.setValue(toLocalFormat(getVoucherSales().getTheSumOf()));
			setTotalOrderValue(getVoucherSales().getTheSumOf());
			usePpnCheckbox.setChecked(getVoucherSales().isUsePpn());
			ppnAmountTextbox.setValue(toLocalFormat(getVoucherSales().getPpnAmount()));
			setTotalPpnValue(getVoucherSales().getPpnAmount());

			referenceTextbox.setValue(getVoucherSales().getDocumentRef());
			descriptionTextbox.setValue(getVoucherSales().getTransactionDescription());			
			
			// count
			dbcrCount = getVoucherSales().getVoucherSalesDebitCredits().size();
			
			// display
			setVoucherSalesDebitCreditList(getVoucherSales().getVoucherSalesDebitCredits());
			setVoucherSalesDebitCreditInfo(getVoucherSalesDebitCreditList());
		}
	}

	private void setVoucherSalesDebitCreditInfo(List<VoucherSalesDebitCredit> voucherSalesDbCrList) {
		voucherDbcrListbox.setModel(
				new ListModelList<VoucherSalesDebitCredit>(voucherSalesDbCrList));
		voucherDbcrListbox.setItemRenderer(
				getVoucherDbcrListitemRenderer());		
	}
	
	private ListitemRenderer<VoucherSalesDebitCredit> getVoucherDbcrListitemRenderer() {
		
		totalDebitVal = BigDecimal.ZERO; 
		totalCreditVal = BigDecimal.ZERO;
		
		return new ListitemRenderer<VoucherSalesDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherSalesDebitCredit voucherDbCr, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = new Listcell(voucherDbCr.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				// Nama Akun
				lc = new Listcell(voucherDbCr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Keterangan
				lc = new Listcell(voucherDbCr.getDbcrDescription());
				lc.setParent(item);
				
				// Debit
				lc = new Listcell(toLocalFormat(voucherDbCr.getDebitAmount()));
				lc.setParent(item);
				
				// Kredit
				lc = new Listcell(toLocalFormat(voucherDbCr.getCreditAmount()));
				lc.setParent(item);
				
				totalDebitVal = totalDebitVal.add(voucherDbCr.getDebitAmount());
				totalCreditVal = totalCreditVal.add(voucherDbCr.getCreditAmount());
			}
		};
	}	
	
	public void onAfterRender$voucherDbcrListbox(Event event) throws Exception {
		infoDebitCreditlabel.setValue("Debit/Kredit: "+dbcrCount+" items");
		
		totalDebitListfooter.setLabel(toLocalFormat(totalDebitVal)); 
		totalCreditListfooter.setLabel(toLocalFormat(totalCreditVal));
	}
	
	public void onClick$totalOrderButton(Event event) throws Exception {		
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", getVoucherSales()==null ? null : getVoucherSales().getTheSumOf());		
				
		Window totalOrderDialogWin = 
				(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
		
		totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				theSumOfTextbox.setValue(toLocalFormat((BigDecimal) event.getData()));
				setTotalOrderValue((BigDecimal) event.getData());
			}
		});
		
		totalOrderDialogWin.doModal();
		
	}
	
	public void onClick$ppnAmountButton(Event event) throws Exception {
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", getVoucherSales()==null ? null : getVoucherSales().getPpnAmount());		

		Window totalOrderDialogWin = 
				(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
		
		totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				ppnAmountTextbox.setValue(toLocalFormat((BigDecimal) event.getData()));
				setTotalPpnValue((BigDecimal) event.getData());
			}
		});
		
		totalOrderDialogWin.doModal();
	}
	
	
	private VoucherSales customerOrderToVoucherSales(VoucherSales voucherSales, CustomerOrder orderData) throws Exception {
		
		// set to VoucherSales
		voucherSales.setTheSumOf(orderData.getTotalOrder());
		setTotalOrderValue(orderData.getTotalOrder());
		voucherSales.setPpnAmount(orderData.getTotalPpn());
		setTotalPpnValue(orderData.getTotalPpn());
		voucherSales.setTransactionDate(orderData.getOrderDate());
		voucherSales.setPaymentType(orderData.getPaymentType());
		voucherSales.setJumlahHari(orderData.getCreditDay());		
		voucherSales.setUsePpn(orderData.isUsePpn());
		
		voucherSales.setCreateDate(orderData.getOrderDate());
		voucherSales.setModifiedDate(orderData.getOrderDate());
		voucherSales.setCheckDate(orderData.getOrderDate());
		voucherSales.setPostingDate(orderData.getOrderDate());
		
		// status set to Posted
		voucherSales.setFlowStatus(DEFAULT_FLOW_STATUS);
		
		// if tunai -- payment is completed -- Lunas
		voucherSales.setPaymentComplete(orderData.getPaymentType().compareTo(PaymentType.tunai)==0);
		
		// voucher & posting no is filled up before saving
		voucherSales.setPostingVoucherNumber(null);
		voucherSales.setVoucherNumber(null);
		
		// customer 
		Customer customerByProxy = getCustomerFromCustomerOrderByProxy(orderData.getId());
		voucherSales.setCustomer(customerByProxy);				

		// NOTE: Ref and Description -- WILL BE USED in Debit/Credit
		// Surat Jalan is created before VoucherSales
		SuratJalan suratJalanFromProxy = 
				getSuratJalanByProxy(orderData.getId());
		String refSuratJalan = suratJalanFromProxy.getSuratJalanNumber().getSerialComp();
		voucherSales.setDocumentRef("Surat Jalan No."+refSuratJalan);	
		// transactionDescription
		String customerName = customerByProxy == null ? "tunai" : 
				customerByProxy.getCompanyType()+". "+customerByProxy.getCompanyLegalName();
		voucherSales.setTransactionDescription(orderData.getPaymentType().compareTo(PaymentType.tunai) == 0 ?
				"Penjualan tunai" : "Penjualan kredit ke "+customerName);		
		
		// set to join_table -- voucherSales to customerOrder
		voucherSales.setCustomerOrder(orderData);
		
		// voucher type depends on payment type
		voucherSales.setVoucherType(voucherSales.getPaymentType().compareTo(PaymentType.tunai)==0 ?
				CASH_SALES_VOUCHER : CREDIT_SALES_VOUCHER);

		// USER MUST CLICK Debit/Credit button to add the debit/credit accounts
		// voucherSales.setVoucherDebitCreditList(getVoucherDebitCredit(new ArrayList<VoucherDebitCredit>(), orderData));
		
		return voucherSales;
	}

	private void loadCustomerOrderSelections() throws Exception {
		// -- if user clicks button "Tambah" from the VoucherSales page
		List<CustomerOrder> orderList = 
				getCustomerOrderDao().findNonPostingCustomerOrder(); 
		
		Comboitem item;
			
		for (CustomerOrder order : orderList) {
				item = new Comboitem();
				item.setLabel(order.getOrderDate().toString());
				item.setValue(order);
				item.setParent(customerOrderCombobox);
		}
	}

	public void onClick$addDebitCreditButton(Event event) throws Exception {
		/*
		 * - Tunai / Cash : update COA : 
		 * 		DB  Petty Cash  ( COA 1.211.0001 )
		 * 		CR  Penjualan Tunai  ( COA  4.111.0001 )
		 * 
		 * 	 PPN:
		 * 		CR  Penjualan - PPN (COA  4.111.0005)	
		 * 
		 * - Giro / Bank / Kredit / Hutang : update COA :
		 *		DB  Piutang Langganan (COA 1.241.003 )
		 *		CR  Penjualan Kredit  ( COA 4.111.0002 )
		 *
		 *	 PPN:
		 *		CR  Penjualan - PPN (COA  4.111.0005)	
		 */
		// init list
		setVoucherSalesDebitCreditList(new ArrayList<VoucherSalesDebitCredit>());
		
		PaymentType paymentType = pembayaranCombobox.getSelectedItem().getValue();
		boolean usePpn = usePpnCheckbox.isChecked();
		
		// create a new Debit account
		VoucherSalesDebitCredit debitAccount = new VoucherSalesDebitCredit();
		debitAccount.setDebitAmount(getTotalOrderValue());
		debitAccount.setCreditAmount(BigDecimal.ZERO);
		debitAccount.setDbcrDescription(getVoucherSales().getTransactionDescription());
		debitAccount.setMasterCoa(paymentType.compareTo(PaymentType.tunai)==0 ?
			getCoa_05_MasterDao().findCoa_05_MasterById(new Long(4)) : 
				getCoa_05_MasterDao().findCoa_05_MasterById(new Long(44)));

		// create a new Credit account
		VoucherSalesDebitCredit creditAccount = new VoucherSalesDebitCredit();
		creditAccount.setDebitAmount(BigDecimal.ZERO);
		creditAccount.setCreditAmount(usePpn ?		
			getTotalOrderValue().subtract(getTotalPpnValue()) : getTotalOrderValue());
		creditAccount.setDbcrDescription(getVoucherSales().getTransactionDescription());
		creditAccount.setMasterCoa(paymentType.compareTo(PaymentType.tunai)==0 ?
			getCoa_05_MasterDao().findCoa_05_MasterById(new Long(10)) : 
				getCoa_05_MasterDao().findCoa_05_MasterById(new Long(42)));		
		
		// add Debit and Credit objects to the list
		getVoucherSalesDebitCreditList().add(debitAccount);
		getVoucherSalesDebitCreditList().add(creditAccount);
				
		if (usePpn) {
			VoucherSalesDebitCredit creditPpnAccount = new VoucherSalesDebitCredit();
			creditPpnAccount.setDebitAmount(BigDecimal.ZERO);
			creditPpnAccount.setCreditAmount(getTotalPpnValue());
			creditPpnAccount.setDbcrDescription(getVoucherSales().getTransactionDescription());
			creditPpnAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(43)));
					
			getVoucherSalesDebitCreditList().add(creditPpnAccount);
		}
		
		// count
		dbcrCount = getVoucherSalesDebitCreditList().size();
		
		// display
		setVoucherSalesDebitCreditInfo(getVoucherSalesDebitCreditList());
	}
	
	public void onClick$removeDebitCreditButton(Event event) throws Exception {
		// reset
		setVoucherSalesDebitCreditList(null);

		// display
		setVoucherSalesDebitCreditInfo(new ArrayList<VoucherSalesDebitCredit>());
		
		// reset count
		dbcrCount = 0;
	}
	
	public void onCheck$createFromCheckbox(Event event) throws Exception {
		if (createFromCheckbox.isChecked()) {
			// enable the CustomerOrder combobox selection
			customerOrderCombobox.setDisabled(false);
			
		} else {
			// disable the CustomerOrder combobox selection
			customerOrderCombobox.setDisabled(true);
			// clear the combobox value
			customerOrderCombobox.setValue("");
			// reset the VoucherSales
			setVoucherSales(null);
			// load info
			setVoucherSalesInfo();
		
		}
	}
	
	public void onSelect$customerOrderCombobox(Event event) throws Exception {
		CustomerOrder customerOrder = customerOrderCombobox.getSelectedItem().getValue();
		
		setVoucherSales(
				customerOrderToVoucherSales(new VoucherSales(), customerOrder));
		
		// load info
		setVoucherSalesInfo();
	
	}
	
	public void onSelect$pembayaranCombobox(Event event) throws Exception {
		PaymentType paymentType = pembayaranCombobox.getSelectedItem().getValue();
		
		// voucher type depends on payment type
		voucherTypeLabel.setValue(
			paymentType.compareTo(PaymentType.giro)==0 || paymentType.compareTo(PaymentType.bank)==0 ?
				CREDIT_SALES_VOUCHER.toString() : CASH_SALES_VOUCHER.toString());
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		// NOTE: both of these events actually create a new VoucherSales -- the difference is with the link to the CustomerOrder
		if (getVoucherSalesDebitCreditList() == null) {
			throw new Exception("Debit / Kredit Harus Dilengkapi sebelum Disimpan.");
		}		
		
		if (getVoucherSales()==null) {
			// NOTE: this is a NEW VoucherSales
			// -- save NEW VoucherSales - send ON_OK event

			Events.sendEvent(Events.ON_OK, voucherSalesDialogWin, getUserUpdatedVoucherSales(new VoucherSales()));
		} else { 
			// NOTE: this is a VoucherSales from CustomerOrder 
			// -- update VoucherSales - send ON_CHANGE

			// send ON_CHANGE event back to the caller
			Events.sendEvent(Events.ON_CHANGE, voucherSalesDialogWin, getUserUpdatedVoucherSales(getVoucherSales()));
		}
		
		// detach
		voucherSalesDialogWin.detach();
	}
	
	private VoucherSales getUserUpdatedVoucherSales(VoucherSales voucherSales) throws Exception {
		// -- transactionDatebox.setValue(getVoucherSales().getCreateDate());
		// use the transactionDatebox as the default
		Date userDefault = transactionDatebox.getValue();
		voucherSales.setCheckDate(userDefault);
		voucherSales.setCreateDate(userDefault);
		voucherSales.setModifiedDate(asDate(getLocalDate()));
		voucherSales.setFlowStatus(DEFAULT_FLOW_STATUS);
		// set vouchersales from components
		PaymentType paymentType = pembayaranCombobox.getSelectedItem().getValue();			
		voucherSales.setPaymentType(paymentType); 	// <--
		voucherSales.setJumlahHari(jumlahHariIntbox.getValue());		// <--	
		voucherSales.setUsePpn(usePpnCheckbox.isChecked());
		voucherSales.setVoucherType(paymentType.compareTo(
				PaymentType.bank)==0 || paymentType.compareTo(PaymentType.giro)==0 ?
						CREDIT_SALES_VOUCHER : CASH_SALES_VOUCHER);	// <-- depends on the PaymentType
		voucherSales.setCustomer((Customer) customerTextbox.getAttribute("customer")); 	// <--						
		voucherSales.setTheSumOf(getTotalOrderValue());
		voucherSales.setPpnAmount(getTotalPpnValue());
		voucherSales.setTransactionDescription(descriptionTextbox.getValue());	// <--
		voucherSales.setDocumentRef(referenceTextbox.getValue());	// <--
		voucherSales.setVoucherSalesDebitCredits(getVoucherSalesDebitCreditList());
		voucherSales.setVoucherStatus(STATUS);
		
		if (voucherSales.getId().compareTo(Long.MIN_VALUE)==0) {			
			// NOTE: from CustomerOrder

			// generate new voucher numbers
			voucherSales.setVoucherNumber(addVoucherNumber(paymentType.compareTo(PaymentType.tunai)==0 ?
					CASH_SALES_VOUCHER : CREDIT_SALES_VOUCHER, userDefault));

			// posting immediately -- use today's date
			voucherSales.setPostingDate(asDate(getLocalDate()));
			voucherSales.setPostingVoucherNumber(addVoucherNumber(paymentType.compareTo(PaymentType.tunai)==0 ?
					VoucherType.POSTING_SALESCASH : VoucherType.POSTING_SALESCREDIT, asDate(getLocalDate())));
			voucherSales.setGeneralLedgers(createGeneralLedgersFromVoucherSales(voucherSales));
		}
		
		// paid by cash, do we need to create voucher payment?  If yes, what're the debit/credit coa?
		// ANSWER: [Thu 18/01/2018 08:41] Kalo pembelian tunai , tidak perlu voucher pembayaran
		//
		// voucherSales.setVoucherPayment(paymentType.compareTo(PaymentType.tunai)==0 ?
		//		getVoucherPayment(new VoucherPayment()) : null);
		
		return voucherSales;
	}

	private List<GeneralLedger> createGeneralLedgersFromVoucherSales(VoucherSales voucherSales) {
		// use the dbcr to create generalledger
		List<VoucherSalesDebitCredit> dbcrList = getVoucherSalesDebitCreditList();
		// multipe generalledgers are created, so need a new list to pass back to the caller
		List<GeneralLedger> generalLedgerList = new ArrayList<GeneralLedger>();

		for (VoucherSalesDebitCredit dbcr : dbcrList) {
			GeneralLedger gl = new GeneralLedger();
			
			gl.setMasterCoa(dbcr.getMasterCoa());
			// 30/07/2021 - posting date must be the same as the transaction date
			// gl.setPostingDate(asDate(getLocalDate()));
			gl.setPostingDate(voucherSales.getTransactionDate());
			gl.setPostingVoucherNumber(voucherSales.getPostingVoucherNumber());
			gl.setCreditAmount(dbcr.getCreditAmount());
			gl.setDebitAmount(dbcr.getDebitAmount());
			gl.setDbcrDescription(dbcr.getDbcrDescription());
			gl.setTransactionDescription(voucherSales.getTransactionDescription());
			gl.setDocumentRef(voucherSales.getDocumentRef());
			gl.setTransactionDate(voucherSales.getTransactionDate());
			gl.setVoucherType(voucherSales.getVoucherType());
			gl.setVoucherNumber(voucherSales.getVoucherNumber());
						
			// add
			generalLedgerList.add(gl);
		}
		
		return generalLedgerList;
	}

	private VoucherSerialNumber addVoucherNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = getSerialNumberGenerator().getSerialNumber(voucherType, currentDate);  
		
		VoucherSerialNumber voucherSerialNumber = new VoucherSerialNumber();
		voucherSerialNumber.setVoucherType(voucherType);
		voucherSerialNumber.setSerialDate(currentDate);
		voucherSerialNumber.setSerialNo(serialNum);
		voucherSerialNumber.setSerialComp(
				formatSerialComp(voucherType.toCode(voucherType.getValue()), currentDate, serialNum));
		
		return voucherSerialNumber;
	}

	public void onClick$customerButton(Event event) throws Exception {
		Window customerDialogWin = 
				(Window) Executions.createComponents("/customer/CustomerListDialog.zul", null, null);
		
		customerDialogWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Customer selCustomer = (Customer) event.getData();
				
				customerTextbox.setValue(selCustomer.getCompanyType()+". "+
						selCustomer.getCompanyLegalName());
				customerTextbox.setAttribute("customer", selCustomer);
				
			}
		});
		
		customerDialogWin.doModal();		
	}
	
	
	public void onClick$cancelButton(Event event) throws Exception {
		voucherSalesDialogWin.detach();
	}

	private void setReadOnly() {
		if (getPageMode().compareTo(PageMode.EDIT)==0 || getPageMode().compareTo(PageMode.NEW)==0) {
			transactionDatebox.setDisabled(false);
			pembayaranCombobox.setDisabled(false);
			jumlahHariIntbox.setDisabled(false);
			usePpnCheckbox.setDisabled(false);
			// voucherStatusCombobox.setDisabled(false);
			customerButton.setDisabled(false);
			
			totalOrderButton.setDisabled(false);
			ppnAmountButton.setDisabled(false);
			descriptionTextbox.setDisabled(false);
			referenceTextbox.setDisabled(false);
						
			// by default user not allowed to add more db/cr - however, this depends on the createFromCheckbox
			dbcrControl.setVisible(true);
			
			// button
			saveButton.setVisible(true);
			cancelButton.setLabel("Cancel");
		} else {
			transactionDatebox.setDisabled(true);
			pembayaranCombobox.setDisabled(true);
			jumlahHariIntbox.setDisabled(true);
			usePpnCheckbox.setDisabled(true);			
			// voucherStatusCombobox.setDisabled(true);
			customerButton.setDisabled(true);
			
			totalOrderButton.setDisabled(true);
			ppnAmountButton.setDisabled(true);
			descriptionTextbox.setDisabled(true);
			referenceTextbox.setDisabled(true);
			
			// by default user not allowed to add more db/cr - however, this depends on the createFromCheckbox
			dbcrControl.setVisible(false);
			
			// button
			saveButton.setVisible(false);
			cancelButton.setLabel("Tutup");
		}
	}

	private Customer getCustomerFromSalesVoucherByProxy(Long id) throws Exception {
		try {
			VoucherSales voucherSales = getVoucherSalesDao().findCustomerByProxy(id); 

			return voucherSales.getCustomer();
		} catch (Exception e) {
			throw e;
		}
	}
	
	private Customer getCustomerFromCustomerOrderByProxy(Long id) throws Exception {
		try {
			CustomerOrder customerOrder = getCustomerOrderDao().findCustomerByProxy(id); 

			return customerOrder.getCustomer();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public VoucherSalesData getVoucherSalesData() {
		return voucherSalesData;
	}

	public void setVoucherSalesData(VoucherSalesData voucherSalesData) {
		this.voucherSalesData = voucherSalesData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public VoucherSales getVoucherSales() {
		return voucherSales;
	}

	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public VoucherSalesDao getVoucherSalesDao() {
		return voucherSalesDao;
	}

	public void setVoucherSalesDao(VoucherSalesDao voucherSalesDao) {
		this.voucherSalesDao = voucherSalesDao;
	}

	public BigDecimal getTotalOrderValue() {
		return totalOrderValue;
	}

	public void setTotalOrderValue(BigDecimal totalOrderValue) {
		this.totalOrderValue = totalOrderValue;
	}

	public Coa_05_MasterDao getCoa_05_MasterDao() {
		return coa_05_MasterDao;
	}

	public void setCoa_05_MasterDao(Coa_05_MasterDao coa_05_MasterDao) {
		this.coa_05_MasterDao = coa_05_MasterDao;
	}

	public BigDecimal getTotalPpnValue() {
		return totalPpnValue;
	}

	public void setTotalPpnValue(BigDecimal totalPpnValue) {
		this.totalPpnValue = totalPpnValue;
	}

	public List<VoucherSalesDebitCredit> getVoucherSalesDebitCreditList() {
		return voucherSalesDebitCreditList;
	}

	public void setVoucherSalesDebitCreditList(List<VoucherSalesDebitCredit> voucherSalesDebitCreditList) {
		this.voucherSalesDebitCreditList = voucherSalesDebitCreditList;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public CustomerReceivableDao getCustomerReceivableDao() {
		return customerReceivableDao;
	}

	public void setCustomerReceivableDao(CustomerReceivableDao customerReceivableDao) {
		this.customerReceivableDao = customerReceivableDao;
	}
}
