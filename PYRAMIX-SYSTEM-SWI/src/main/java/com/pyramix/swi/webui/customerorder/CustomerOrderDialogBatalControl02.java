package com.pyramix.swi.webui.customerorder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderProduct;
import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.faktur.FakturProduct;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.receivable.ActivityType;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.suratjalan.SuratJalanProduct;
import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.domain.voucher.VoucherSalesDebitCredit;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.persistence.deliveryorder.dao.DeliveryOrderDao;
import com.pyramix.swi.persistence.employeecommissions.dao.EmployeeCommissionsDao;
import com.pyramix.swi.persistence.faktur.dao.FakturDao;
import com.pyramix.swi.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherSalesDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SuppressedException;

public class CustomerOrderDialogBatalControl02 extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8829937887913261244L;

	private CustomerOrderDao customerOrderDao;
	private CustomerOrderProductDao customerOrderProductDao;
	private EmployeeCommissionsDao employeeCommissionsDao;
	private SuratJalanDao suratJalanDao;
	private DeliveryOrderDao deliveryOrderDao;
	private FakturDao fakturDao;
	private VoucherSalesDao voucherSalesDao;
	private CustomerReceivableDao customerReceivableDao;
	private GeneralLedgerDao generalLedgerDao;
	private InventoryDao inventoryDao;
	
	private Window customerOrderDialongBatalWin;
	private Tabbox customerOrderBatalTabbox;
	private Tab customerOrderTab, employeeCommissionTab, inventoryTab, suratJalanTab,
		deliveryOrderTab, fakturTab, voucherSalesTab, glTab, piutangTab;
	private Combobox statusCustomerOrderCombobox, statusCommissionCombobox, statusInventoryCombobox,
		statusSuratJalanCombobox, statusDeliveryOrderCombobox, statusFakturCombobox, 
		statusVoucherSalesCombobox, statusGLCombobox, statusPiutangCombobox, 
		customerOrderPembayaranCombobox, customerOrderSalesPersonCombobox, employeeNameCommissionsCombobox,
		locationDeliveryOrderCombobox, pembayaranFakturCombobox, pembayaranVoucherSalesCombobox,
		voucherSalesStatusCombobox;
	private Textbox pembatalanCatatanCustomerOrderTextbox, customerOrderNoTextbox, customerOrderCustomerTextbox, 
		totalOrderCustomerOrderTextbox, customerOrderNoteTextbox, subTotalCustomerOrderTextbox, 
		ppnCustomerOrderTextbox, totalCustomerOrderTextbox, pembatalanCatatanCommissionTextbox,
		customerOrderCommissionsNoTextbox, customerOrderCustomerCommissionsTextbox, totalSalesCommissionsTextbox,
		commissionPercentCommissionsTextbox, commissionTotalCommissionsTextbox, pembatalanCatatanInventoryTextbox,
		pembatalanCatatanSuratJalanTextbox, orderNoSuratJalanTextbox, customerSuratJalanTextbox,
		noteSuratJalanTextbox, pembatalanCatatanDeliveryOrderTextbox, deliveryOrderNoTextbox, 
		noteDeliveryOrderTextbox, pembatalanCatatanFakturTextbox, fakturNoTextbox, customerFakturTextbox,
		noteFakturTextbox, subTotalFakturTextbox, ppnFakturTextbox, totalFakturTextbox,
		pembatalanCatatanVoucherSalesTextbox, voucherSalesNoCompTextbox, voucherSalesNoPostTextbox,
		customerVoucherSalesTextbox, theSumOfVoucherSalesTextbox, ppnAmountVoucherSalesTextbox,
		referenceVoucherSalesTextbox, descriptionVoucherSalesTextbox, pembatalanCatatanGLTextbox,
		glPostingNumberTextbox, glVoucherNumberTextbox, glDescriptionTextbox, glReferenceTextbox,
		pembatalanCatatanPiutangTextbox; 
	private Datebox pembatalanCustomerOrderDatebox, customerOrderDatebox, pembatalanCommissionDatebox,
		customerOrderCommissionsDatebox, pembatalanInventoryDatebox, pembatalanSuratJalanDatebox, deliveryDatebox, 
		orderSuratJalanDatebox, deliverySuratJalanDatebox, pembatalanDeliveryOrderDatebox, pembatalanFakturDatebox,
		fakturDatebox, pembatalanVoucherSalesDatebox, transactionVoucherSalesDatebox, pembatalanGLDatebox,
		glPostingDatebox, pembatalanPiutangDatebox;
	private Label customerOrderIdLabel, commissionIdLabel, suratJalanIdLabel, deliveryOrderIdLabel,
		fakturIdLabel, voucherSalesIdLabel, voucherSalesTypeLabel, glIdLabel, glVoucherTypeLabel;
	private Intbox customerOrderJumlahHariIntbox, jumlahHariFakturIntbox, jumlahHariVoucherSalesIntbox;
	private Checkbox customerOrderUsePpn, ppnFakturCheckbox, usePpnVoucherSalesCheckbox;
	private Listbox customerOrderDetailListbox, inventoryListbox, productSuratJalanListbox, 
		productDeliveryOrderListbox, productFakturListbox, voucherSalesDbcrListbox, glListbox,
		piutangListbox;
	private Listfooter totalVoucherSalesDebitListfooter, totalVoucherSalesCreditListfooter;
	
	private CustomerOrder customerOrder;
	
	private static final Logger log = Logger.getLogger(CustomerOrderDialogBatalControl02.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerOrder(
				(CustomerOrder) arg.get("customerOrder"));		
	}
	
	public void onCreate$customerOrderDialongBatalWin(Event event) throws Exception {
		customerOrderDialongBatalWin.setTitle("Pembatalan Customer Order");
		
		// Customer-Order
		pembatalanCustomerOrderDatebox.setLocale(getLocale());
		pembatalanCustomerOrderDatebox.setFormat(getLongDateFormat());
		customerOrderDatebox.setLocale(getLocale());
		customerOrderDatebox.setFormat(getLongDateFormat());
		// Komisi
		pembatalanCommissionDatebox.setLocale(getLocale());
		pembatalanCommissionDatebox.setFormat(getLongDateFormat());
		customerOrderCommissionsDatebox.setLocale(getLocale());
		customerOrderCommissionsDatebox.setFormat(getLongDateFormat());
		// Inventory
		pembatalanInventoryDatebox.setLocale(getLocale());
		pembatalanInventoryDatebox.setFormat(getLongDateFormat());
		// Surat-Jalan
		pembatalanSuratJalanDatebox.setLocale(getLocale());
		pembatalanSuratJalanDatebox.setFormat(getLongDateFormat());
		orderSuratJalanDatebox.setLocale(getLocale());
		orderSuratJalanDatebox.setFormat(getLongDateFormat());
		deliverySuratJalanDatebox.setLocale(getLocale());
		deliverySuratJalanDatebox.setFormat(getLongDateFormat());
		// Delivery-Order
		pembatalanDeliveryOrderDatebox.setLocale(getLocale());
		pembatalanDeliveryOrderDatebox.setFormat(getLongDateFormat());
		deliveryDatebox.setLocale(getLocale());
		deliveryDatebox.setFormat(getLongDateFormat());
		// Faktur
		pembatalanFakturDatebox.setLocale(getLocale());
		pembatalanFakturDatebox.setFormat(getLongDateFormat());
		fakturDatebox.setLocale(getLocale());
		fakturDatebox.setFormat(getLongDateFormat());
		// Voucher-Sales
		pembatalanVoucherSalesDatebox.setLocale(getLocale()); 
		pembatalanVoucherSalesDatebox.setFormat(getLongDateFormat());
		transactionVoucherSalesDatebox.setLocale(getLocale());
		transactionVoucherSalesDatebox.setFormat(getLongDateFormat());
		// GL
		pembatalanGLDatebox.setLocale(getLocale());
		pembatalanGLDatebox.setFormat(getLongDateFormat());
		glPostingDatebox.setLocale(getLocale());
		glPostingDatebox.setFormat(getLongDateFormat());
		// Piutang
		pembatalanPiutangDatebox.setLocale(getLocale());
		pembatalanPiutangDatebox.setFormat(getLongDateFormat());
		
		customerOrderTab();
		
		employeeCommissionTab();
		
		inventoryTab();
		
		suratJalanTab();
		
		deliveryOrderTab();
		
		fakturTab();
		
		voucherSalesTab();
		
		glTab();
		
		piutangTab();
	}

	/*
	 * 
	 * Customer Order
	 * 
	 */
	private void customerOrderTab() throws Exception {
		customerOrderTab.setVisible(true);
		log.info(getCustomerOrder().toString());
				
		// statusCustomerOrderCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusCustomerOrderCombobox);
		}
		// set selected - NORMAL
		statusCustomerOrderCombobox.setSelectedIndex(0);
		pembatalanCustomerOrderDatebox.setValue(asDate(getLocalDate()));
		customerOrderIdLabel.setValue("id:#"+getCustomerOrder().getId());

		customerOrderNoTextbox.setValue(getCustomerOrder().getDocumentSerialNumber().getSerialComp());
		customerOrderDatebox.setValue(getCustomerOrder().getOrderDate());
		CustomerOrder customerOrderCustomerByProxy = 
				getCustomerOrderDao().findCustomerByProxy(getCustomerOrder().getId());
		Customer customer = customerOrderCustomerByProxy.getCustomer();
		if (customer!=null) {
			customerOrderCustomerTextbox.setValue(customer.getCompanyType()+"."+customer.getCompanyLegalName());			
		} else {
			customerOrderCustomerTextbox.setValue("tunai");
		}
		customerOrderPembayaranCombobox.setValue(getCustomerOrder().getPaymentType().toString());
		customerOrderJumlahHariIntbox.setValue(getCustomerOrder().getCreditDay());
		customerOrderUsePpn.setChecked(getCustomerOrder().isUsePpn());
		totalOrderCustomerOrderTextbox.setValue(toLocalFormat(getCustomerOrder().getTotalOrder()));
		customerOrderNoteTextbox.setValue(getCustomerOrder().getNote());
		CustomerOrder customerOrderCommissionsByProxy =
				getCustomerOrderDao().findEmployeeCommissionsByProxy(getCustomerOrder().getId());
		Employee employee = customerOrderCommissionsByProxy.getEmployeeSales();
		customerOrderSalesPersonCombobox.setValue(employee.getName());
		// display the details
		displayCustomerOrderDetails(getCustomerOrder().getCustomerOrderProducts());
	}

	private void displayCustomerOrderDetails(List<CustomerOrderProduct> customerOrderProducts) {
		customerOrderDetailListbox.setModel(
				new ListModelList<CustomerOrderProduct>(customerOrderProducts));
		customerOrderDetailListbox.setItemRenderer(getCustomerOrderDetailItemRenderer());		
	}

	private ListitemRenderer<CustomerOrderProduct> getCustomerOrderDetailItemRenderer() {

		return new ListitemRenderer<CustomerOrderProduct>() {
			
			@Override
			public void render(Listitem item, CustomerOrderProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.Coil
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
				CustomerOrderProduct productInventoryByProxy =
						getCustomerOrderProductDao().findInventoryByProxy(product.getId());
				Inventory inventory = productInventoryByProxy.getInventory();				
				
				// Kode		
				if (product.getId().compareTo(Long.MIN_VALUE)==0) {
					lc = new Listcell(product.getInventoryCode().getProductCode());
				} else {					
					lc = new Listcell(inventory.getInventoryCode().getProductCode());
				}
				lc.setParent(item);
				
				// Spesifikasi
				lc = new Listcell(product.getDescription());
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(product.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell(getFormatedFloatLocal(product.getWeightQuantity()));
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(product.getSheetQuantity()));
				lc.setParent(item);
				
				// Unit
				lc = new Listcell(product.isByKg() ? "Kg" : "Sht/Line");
				lc.setParent(item);
				
				// Harga
				lc = new Listcell(toLocalFormatWithDecimal(product.getSellingPrice()));
				lc.setParent(item);
				
				// SubTotal
				BigDecimal subTotal = product.isByKg() ? product.getWeightQuantity().multiply(product.getSellingPrice()) :
						product.getSellingPrice().multiply(new BigDecimal(product.getSheetQuantity()));				
				lc = new Listcell(toLocalFormat(subTotal));
				lc.setParent(item);

				// inventory id
				lc = new Listcell("id:#"+inventory.getId());
				lc.setParent(item);
				
				item.setValue(product);					
			}
		};
	}

	public void onAfterRender$customerOrderDetailListbox(Event event) throws Exception {
		// calc subTotal
		BigDecimal customerOrderSubTotal = BigDecimal.ZERO;
		for (CustomerOrderProduct product : getCustomerOrder().getCustomerOrderProducts()) {
			BigDecimal subTotal = product.isByKg() ? product.getWeightQuantity().multiply(product.getSellingPrice()) :
				product.getSellingPrice().multiply(new BigDecimal(product.getSheetQuantity()));
			customerOrderSubTotal = customerOrderSubTotal.add(subTotal);
		}
		// calc ppn (if any)
		BigDecimal customerOrderPpn = customerOrderUsePpn.isChecked() ? 
				customerOrderSubTotal.multiply(new BigDecimal(0.1)) : BigDecimal.ZERO;
		// display
		subTotalCustomerOrderTextbox.setValue(toLocalFormat(customerOrderSubTotal));
		ppnCustomerOrderTextbox.setValue(toLocalFormat(customerOrderPpn));
		totalCustomerOrderTextbox.setValue(toLocalFormat(customerOrderSubTotal.add(customerOrderPpn)));
	}	
	
	public void onSelect$statusCustomerOrderCombobox(Event event) throws Exception {
		DocumentStatus status = statusCustomerOrderCombobox.getSelectedItem().getValue();
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusCustomerOrderCombobox.setStyle("color: red;");
			
		} else {
			statusCustomerOrderCombobox.setStyle("color: black;");
			
		}
	}
	
	/*
	 * 
	 * Komisi
	 * 
	 */
	private void employeeCommissionTab() throws Exception {
		employeeCommissionTab.setVisible(true);

		CustomerOrder customerOrderCommissionByProxy =
				getCustomerOrderDao().findEmployeeCommissionsByProxy(getCustomerOrder().getId());
		EmployeeCommissions employeeCommissions = customerOrderCommissionByProxy.getEmployeeCommissions();
		log.info(employeeCommissions.toString());
		
		// statusCommissionCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusCommissionCombobox);
		}
		// set selected - NORMAL
		statusCommissionCombobox.setSelectedIndex(0);
		pembatalanCommissionDatebox.setValue(asDate(getLocalDate()));
		commissionIdLabel.setValue("id:#"+employeeCommissions.getId());
		
		EmployeeCommissions employeeCommissionsCustomerOrderByProxy =
				getEmployeeCommissionsDao().findCustomerOrderByProxy(employeeCommissions.getId());
		CustomerOrder customerOrder =
				employeeCommissionsCustomerOrderByProxy.getCustomerOrder();
		customerOrderCommissionsNoTextbox.setValue(customerOrder.getDocumentSerialNumber().getSerialComp());
		customerOrderCommissionsDatebox.setValue(customerOrder.getOrderDate());
		CustomerOrder customerOrderCustomerByProxy =
				getCustomerOrderDao().findCustomerByProxy(customerOrder.getId());
		Customer customer = customerOrderCustomerByProxy.getCustomer();
		if (customer!=null) {
			customerOrderCustomerCommissionsTextbox.setValue(customer.getCompanyType()+"."+
					customer.getCompanyLegalName());			
		} else {
			customerOrderCustomerCommissionsTextbox.setValue("tunai");
		}

		employeeNameCommissionsCombobox.setValue(employeeCommissions.getEmployee().getName());
		
		totalSalesCommissionsTextbox.setValue(toLocalFormat(customerOrder.getTotalOrder().subtract(customerOrder.getTotalPpn())));
		commissionPercentCommissionsTextbox.setValue(getFormatedFloatLocal(employeeCommissions.getCommissionPercent()));
		commissionTotalCommissionsTextbox.setValue(toLocalFormat(BigDecimal.ZERO));
	}
	
	public void onSelect$statusCommissionCombobox(Event event) throws Exception {
		DocumentStatus status = statusCommissionCombobox.getSelectedItem().getValue();
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusCommissionCombobox.setStyle("color: red;");
			String userEntry = pembatalanCatatanCommissionTextbox.getValue();
			pembatalanCatatanCommissionTextbox.setValue(userEntry+
					" Pembatalan Customer Order No: "+getCustomerOrder().getDocumentSerialNumber().getSerialComp());
		} else {
			statusCommissionCombobox.setStyle("color: black;");
			pembatalanCatatanCommissionTextbox.setValue("");
		}
		
	}

	/*
	 * 
	 * Inventory
	 * 
	 */
	private void inventoryTab() throws Exception {
		inventoryTab.setVisible(true);
		
		List<CustomerOrderProduct> customerOrderProducts = getCustomerOrder().getCustomerOrderProducts();
		List<Inventory> inventoryList = new ArrayList<Inventory>();
		for (CustomerOrderProduct product : customerOrderProducts) {
			CustomerOrderProduct productInventoryByProxy = 
					getCustomerOrderProductDao().findInventoryByProxy(product.getId());
			Inventory inventory = productInventoryByProxy.getInventory();
			log.info(inventory.toString());			
			
			inventoryList.add(inventory);
		}
		
		// statusInventoryCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusInventoryCombobox);
		}
		// set selected - NORMAL
		statusInventoryCombobox.setSelectedIndex(0);
		pembatalanInventoryDatebox.setValue(asDate(getLocalDate()));
		
		displayInventory(inventoryList);
	}

	private void displayInventory(List<Inventory> inventoryList) {
		inventoryListbox.setModel(new ListModelList<Inventory>(inventoryList));
		inventoryListbox.setItemRenderer(getInventoryItemRenderer());		
	}

	private ListitemRenderer<Inventory> getInventoryItemRenderer() {

		return new ListitemRenderer<Inventory>() {
			
			@Override
			public void render(Listitem item, Inventory inventory, int index) throws Exception {
				Listcell lc;
				
				//	Status
				lc = new Listcell(inventory.getInventoryStatus().toString());
				lc.setParent(item);
				
				//	Kode
				lc = new Listcell(inventory.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				//	Spesifikasi
				String specs = getFormatedFloatLocal(inventory.getThickness())+" x "+
						getFormatedFloatLocal(inventory.getWidth())+" x "+
						(inventory.getLength().compareTo(BigDecimal.ZERO)==0 ?
								"Coil" : getFormatedFloatLocal(inventory.getLength()));
				lc = new Listcell(specs);
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				//	Pre-Qty
				lc = new Listcell(getFormatedInteger(inventory.getSheetQuantity()));
				lc.setParent(item);
				
				//	Pre-Qty(Kg)
				lc = new Listcell(getFormatedFloatLocal(inventory.getWeightQuantity()));
				lc.setParent(item);
				
				//	Post-Qty
				log.info("post-qty: "+inventory.getPostSheetQuantity());
				lc = new Listcell(getFormatedInteger(inventory.getPostSheetQuantity()));
				lc.setParent(item);
				
				//	Post-Qty(Kg)
				log.info("post-qtyKg: "+inventory.getPostWeightQuantity());
				if (inventory.getPostWeightQuantity()==null) {
					lc = new Listcell(getFormatedFloatLocal(BigDecimal.ZERO));					
				} else {
					lc = new Listcell(getFormatedFloatLocal(inventory.getPostWeightQuantity()));
				}
				lc.setParent(item);
				
				//	Packing
				lc = new Listcell(inventory.getInventoryPacking().toString());
				lc.setParent(item);
				
				//	Lokasi
				lc = new Listcell(inventory.getInventoryLocation().toString());
				lc.setParent(item);
				
				//	No.Coil
				lc = new Listcell(inventory.getMarking());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				//	inventory #id
				lc = new Listcell("id:#"+inventory.getId());
				lc.setParent(item);
				
				item.setValue(inventory);
			}
		};
	}

	public void onSelect$statusInventoryCombobox(Event event) throws Exception {
		DocumentStatus status = statusInventoryCombobox.getSelectedItem().getValue();
		List<Inventory> inventoryList = new ArrayList<Inventory>();
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusInventoryCombobox.setStyle("color: red;");
			String userEntry = pembatalanCatatanInventoryTextbox.getValue();
			pembatalanCatatanInventoryTextbox.setValue(userEntry +
					" Pembatalan Customer Order No: "+getCustomerOrder().getDocumentSerialNumber().getSerialComp());
			List<CustomerOrderProduct> customerOrderProducts = getCustomerOrder().getCustomerOrderProducts();
			for (CustomerOrderProduct product : customerOrderProducts) {
				Inventory inventory = null;
				if (product.getInventoryPacking().equals(InventoryPacking.lembaran)) {
					int orderQty = product.getSheetQuantity();
					BigDecimal orderQtyKg = product.getWeightQuantity();
					
					CustomerOrderProduct productInventoryByProxy = 
							getCustomerOrderProductDao().findInventoryByProxy(product.getId());
					inventory = productInventoryByProxy.getInventory();
					
					inventory.setPostSheetQuantity(orderQty+inventory.getSheetQuantity());
					inventory.setPostWeightQuantity(orderQtyKg.add(inventory.getWeightQuantity()));
					
				} else {
					CustomerOrderProduct productInventoryByProxy = 
							getCustomerOrderProductDao().findInventoryByProxy(product.getId());
					inventory = productInventoryByProxy.getInventory();
					// reset from 'sold' to 'ready'
					inventory.setInventoryStatus(InventoryStatus.ready);						
				}
				inventoryList.add(inventory);
			}
		} else {
			statusInventoryCombobox.setStyle("color: black;");
			pembatalanCatatanInventoryTextbox.setValue("");
			
			List<CustomerOrderProduct> customerOrderProducts = getCustomerOrder().getCustomerOrderProducts();
			for (CustomerOrderProduct product : customerOrderProducts) {
				Inventory inventory = null;
				if (product.getInventoryPacking().equals(InventoryPacking.lembaran)) {
					CustomerOrderProduct productInventoryByProxy = 
							getCustomerOrderProductDao().findInventoryByProxy(product.getId());
					inventory = productInventoryByProxy.getInventory();
					
					inventory.setPostSheetQuantity(0);
					inventory.setPostWeightQuantity(BigDecimal.ZERO);
				} else {
					CustomerOrderProduct productInventoryByProxy = 
							getCustomerOrderProductDao().findInventoryByProxy(product.getId());
					inventory = productInventoryByProxy.getInventory();
					// reset from 'ready' to 'sold'
					inventory.setInventoryStatus(InventoryStatus.sold);											
				}
				inventoryList.add(inventory);
			}
		}
		
		displayInventory(inventoryList);
	}
	
	/*
	 * 
	 * Surat Jalan
	 * 
	 */
	private void suratJalanTab() throws Exception {
		boolean visible = getCustomerOrder().getSuratJalan()!=null;
		suratJalanTab.setVisible(visible);
		log.info("suratJalanTab: visible: "+visible);
		if (!visible) {
			return;
		}
		CustomerOrder customerOrderSuratJalanByProxy =
				getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
		SuratJalan suratJalan = customerOrderSuratJalanByProxy.getSuratJalan();
		log.info(suratJalan.toString());
		// statusSuratJalanCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusSuratJalanCombobox);
		}
		// set selected - NORMAL
		statusSuratJalanCombobox.setSelectedIndex(0);
		pembatalanSuratJalanDatebox.setValue(asDate(getLocalDate()));
		
		suratJalanIdLabel.setValue("id:#"+suratJalan.getId());
		orderNoSuratJalanTextbox.setValue(suratJalan.getSuratJalanNumber().getSerialComp());
		orderSuratJalanDatebox.setValue(suratJalan.getSuratJalanDate());
		SuratJalan suratJalanCustomerByProxy =
				getSuratJalanDao().findCustomerByProxy(suratJalan.getId());
		Customer customer = suratJalanCustomerByProxy.getCustomer();
		customerSuratJalanTextbox.setValue(customer.getCompanyType()+"."+
				customer.getCompanyLegalName());
		deliverySuratJalanDatebox.setValue(suratJalan.getDeliveryDate());
		noteSuratJalanTextbox.setValue(suratJalan.getNote());
		
		displaySuratJalanDetails(suratJalan.getSuratJalanProducts());
	}

	private void displaySuratJalanDetails(List<SuratJalanProduct> suratJalanProducts) {
		productSuratJalanListbox.setModel(
				new ListModelList<SuratJalanProduct>(suratJalanProducts));
		productSuratJalanListbox.setItemRenderer(getSuratJalanDetailItemRenderer());
		
	}

	private ListitemRenderer<SuratJalanProduct> getSuratJalanDetailItemRenderer() {

		return new ListitemRenderer<SuratJalanProduct>() {
			
			@Override
			public void render(Listitem item, SuratJalanProduct product, int index) throws Exception {
				Listcell lc;
				
				//	No.
				lc = new Listcell(String.valueOf(index+1)+".");
				lc.setParent(item);

				//	Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(product.getP_sheetQuantity()));
				lc.setParent(item);
				
				//	Qty (Kg)
				lc = new Listcell(getFormatedFloatLocal(product.getP_weightQuantity()));
				lc.setParent(item);
				
				//	Kode
				lc = new Listcell(product.getP_inventoryCode());
				lc.setParent(item);
				
				//	Spesifikasi
				lc = new Listcell(product.getP_description());
				lc.setParent(item);
				
				//	No.Coil
				lc = new Listcell(product.getP_inventoryMarking());
				lc.setParent(item);
				
				lc.setValue(product);
			}
		};
	}

	public void onSelect$statusSuratJalanCombobox(Event event) throws Exception {
		DocumentStatus status = statusSuratJalanCombobox.getSelectedItem().getValue();
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusSuratJalanCombobox.setStyle("color: red;");
			String userEntry = pembatalanCatatanSuratJalanTextbox.getValue();
			pembatalanCatatanSuratJalanTextbox.setValue(userEntry+
					" Pembatalan Customer Order No: "+getCustomerOrder().getDocumentSerialNumber().getSerialComp());
		} else {
			statusSuratJalanCombobox.setStyle("color: black;");
			pembatalanCatatanSuratJalanTextbox.setValue("");
		}
	}
	
	/*
	 * 
	 * Delivery Order
	 * 
	 */
	private void deliveryOrderTab() throws Exception {
		// check from SuratJalan
		boolean visible = getCustomerOrder().getSuratJalan()!=null;
		deliveryOrderTab.setVisible(visible);
		log.info("deliveryOrderTab: visible: "+visible);
		if (!visible) {
			return;
		}

		CustomerOrder customerOrderSuratJalanByProxy =
				getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
		SuratJalan suratJalan = customerOrderSuratJalanByProxy.getSuratJalan();
		visible = suratJalan.getDeliveryOrder()!=null;
		deliveryOrderTab.setVisible(visible);
		log.info("deliveryOrderTab: visible: "+visible);
		if (!visible) {
			return;
		}
		
		SuratJalan suratJalanDeliveryOrderByProxy = 
				getSuratJalanDao().findDeliveryOrderByProxy(suratJalan.getId());
		DeliveryOrder deliveryOrder = suratJalanDeliveryOrderByProxy.getDeliveryOrder();
		log.info(deliveryOrder.toString());
		
		// statusDeliveryOrderCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusDeliveryOrderCombobox);
		}
		// set selected - NORMAL
		statusDeliveryOrderCombobox.setSelectedIndex(0);
		pembatalanDeliveryOrderDatebox.setValue(asDate(getLocalDate()));
		deliveryOrderIdLabel.setValue("id:#"+deliveryOrder.getId());
		
		deliveryOrderNoTextbox.setValue(deliveryOrder.getDeliveryOrderNumber().getSerialComp());
		deliveryDatebox.setValue(deliveryOrder.getDeliveryOrderDate());
		DeliveryOrder deliveryOrderCompanyByProxy =
				getDeliveryOrderDao().findCompanyByProxy(deliveryOrder.getId());
		Company company = deliveryOrderCompanyByProxy.getCompany();
		locationDeliveryOrderCombobox.setValue("["+company.getCompanyDisplayName()+"] "+
				company.getCompanyType()+"."+
				company.getCompanyLegalName());
		noteDeliveryOrderTextbox.setValue(deliveryOrder.getNote());
		
		displayDeliveryOrderDetails(deliveryOrder.getDeliveryOrderProducts());
	}

	private void displayDeliveryOrderDetails(List<DeliveryOrderProduct> deliveryOrderProducts) {
		productDeliveryOrderListbox.setModel(new ListModelList<DeliveryOrderProduct>(deliveryOrderProducts));
		productDeliveryOrderListbox.setItemRenderer(getDeliveryOrderDetailItemRenderer());
	}

	private ListitemRenderer<DeliveryOrderProduct> getDeliveryOrderDetailItemRenderer() {

		return new ListitemRenderer<DeliveryOrderProduct>() {
			
			@Override
			public void render(Listitem item, DeliveryOrderProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(String.valueOf(index+1)+".");
				lc.setParent(item);

				//	Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(product.getP_sheetQuantity()));
				lc.setParent(item);				
				
				//	Qty (Kg)
				lc = new Listcell(getFormatedFloatLocal(product.getP_weightQuantity()));
				lc.setParent(item);				
				
				//	Kode
				lc = new Listcell(product.getP_inventoryCode());
				lc.setParent(item);				
				
				//	Spesifikasi
				lc = new Listcell(product.getP_description());
				lc.setParent(item);				
				
				//	No.Coil
				lc = new Listcell(product.getP_marking());
				lc.setParent(item);

				item.setValue(product);
			}
		};
	}

	public void onSelect$statusDeliveryOrderCombobox(Event event) throws Exception {
		DocumentStatus status = statusDeliveryOrderCombobox.getSelectedItem().getValue();
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusDeliveryOrderCombobox.setStyle("color: red;");
			String userEntry = pembatalanCatatanDeliveryOrderTextbox.getValue();
			pembatalanCatatanDeliveryOrderTextbox.setValue(userEntry +
					" Pembatalan Customer Order No:"+getCustomerOrder().getDocumentSerialNumber().getSerialComp());
		} else {
			statusDeliveryOrderCombobox.setStyle("color: black;");
			pembatalanCatatanDeliveryOrderTextbox.setValue("");
		}
	}
	
	/*
	 * 
	 * Faktur
	 * 
	 */
	private void fakturTab() throws Exception {
		// check from SuratJalan
		boolean visible = getCustomerOrder().getSuratJalan()!=null;
		fakturTab.setVisible(visible);
		log.info("fakturTab: visible: "+visible);
		if (!visible) {
			return;
		}
		
		CustomerOrder customerOrderSuratJalanByProxy =
				getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
		SuratJalan suratJalan = customerOrderSuratJalanByProxy.getSuratJalan();
		visible = suratJalan.getFaktur()!=null;
		fakturTab.setVisible(visible);
		log.info("fakturTab: visible: "+visible);
		if (!visible) {
			return;
		}
		
		SuratJalan suratJalanFakturByProxy =
				getSuratJalanDao().findFakturByProxy(suratJalan.getId());
		Faktur faktur = suratJalanFakturByProxy.getFaktur();
		log.info(faktur);
		
		// statusFakturCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusFakturCombobox);
		}
		// set selected - NORMAL
		statusFakturCombobox.setSelectedIndex(0);
		pembatalanFakturDatebox.setValue(asDate(getLocalDate()));
		fakturIdLabel.setValue("id:#"+faktur.getId());
		
		fakturNoTextbox.setValue(faktur.getFakturNumber().getSerialComp());
		fakturDatebox.setValue(faktur.getFakturDate());
		Faktur fakturCustomerByProxy = 
				getFakturDao().findCustomerByProxy(faktur.getId());
		Customer customer = fakturCustomerByProxy.getCustomer();
		customerFakturTextbox.setValue(customer.getCompanyType()+"."+
				customer.getCompanyLegalName());
		pembayaranFakturCombobox.setValue(faktur.getPaymentType().toString());
		jumlahHariFakturIntbox.setValue(faktur.getJumlahHari());
		ppnFakturCheckbox.setChecked(faktur.isUsePpn());
		noteFakturTextbox.setValue(faktur.getNote());
		
		displayFakturDetails(faktur.getFakturProducts());
	}

	private void displayFakturDetails(List<FakturProduct> fakturProducts) {
		productFakturListbox.setModel(new ListModelList<FakturProduct>(fakturProducts));
		productFakturListbox.setItemRenderer(getFakturDetailItemRenderer());
	}

	private ListitemRenderer<FakturProduct> getFakturDetailItemRenderer() {

		return new ListitemRenderer<FakturProduct>() {
			
			@Override
			public void render(Listitem item, FakturProduct product, int index) throws Exception {
				Listcell lc;
				
				//	No.
				lc = new Listcell((index+1)+".");
				lc.setParent(item);				
				
				//	Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(product.getP_sheetQuantity()));
				lc.setParent(item);
				
				//	Qty (Kg)
				lc = new Listcell(getFormatedFloatLocal(product.getP_weightQuantity()));
				lc.setParent(item);
				
				//	Kode
				lc = new Listcell(product.getP_inventoryCode());
				lc.setParent(item);
				
				//	No.Coil
				lc = new Listcell(product.getP_inventoryMarking());
				lc.setParent(item);
				
				//	Spesifikasi
				lc = new Listcell(product.getP_description());
				lc.setParent(item);
				
				//	Unit
				lc = new Listcell(product.isByKg()? "Kg" : "Sht");
				lc.setParent(item);
				
				//	Harga (Rp.)
				lc = new Listcell(toLocalFormat(product.getP_unitPrice()));
				lc.setParent(item);
				
				//	SubTotal (Rp.)
				lc = new Listcell(toLocalFormat(product.getP_subTotal()));
				lc.setParent(item);
				
				item.setValue(product);
			}
		};
	}

	public void onAfterRender$productFakturListbox(Event event) throws Exception {
		CustomerOrder customerOrderSuratJalanByProxy =
				getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
		SuratJalan suratJalan = customerOrderSuratJalanByProxy.getSuratJalan();
		SuratJalan suratJalanFakturByProxy =
				getSuratJalanDao().findFakturByProxy(suratJalan.getId());
		Faktur faktur = suratJalanFakturByProxy.getFaktur();

		BigDecimal subTotal = BigDecimal.ZERO;
		for (FakturProduct product : faktur.getFakturProducts()) {
			subTotal = subTotal.add(product.getP_subTotal());
		}
		BigDecimal ppn = faktur.isUsePpn() ? 
				(subTotal.multiply(new BigDecimal(0.1))) : BigDecimal.ZERO;
		BigDecimal total = subTotal.add(ppn);
		
		subTotalFakturTextbox.setValue(toLocalFormat(subTotal));
		ppnFakturTextbox.setValue(toLocalFormat(ppn));
		totalFakturTextbox.setValue(toLocalFormat(total));
	}
	
	public void onSelect$statusFakturCombobox(Event event) throws Exception {
		DocumentStatus status = statusFakturCombobox.getSelectedItem().getValue();
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusFakturCombobox.setStyle("color: red;");
			String userEntry = pembatalanCatatanFakturTextbox.getValue();
			pembatalanCatatanFakturTextbox.setValue(userEntry +
					" Pembatalan Customer Order No: "+getCustomerOrder().getDocumentSerialNumber().getSerialComp());
		} else {
			statusFakturCombobox.setStyle("color: black;");
			pembatalanCatatanFakturTextbox.setValue("");
		}
	}
	
	/*
	 * 
	 * VoucherSales
	 * 
	 */
	private void voucherSalesTab() throws Exception {
		boolean visible = getCustomerOrder().getVoucherSales()!=null;
		voucherSalesTab.setVisible(visible);
		log.info("VoucherSalesTab: visible: "+visible);
		if (!visible) {
			return;
		}
		
		CustomerOrder customerOrderVoucherSalesByProxy =
				getCustomerOrderDao().findVoucherSalesByProxy(getCustomerOrder().getId());
		VoucherSales voucherSales = customerOrderVoucherSalesByProxy.getVoucherSales();
		log.info(voucherSales.toString());
		
		// statusVoucherSalesCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusVoucherSalesCombobox);
		}
		// set selected - NORMAL
		statusVoucherSalesCombobox.setSelectedIndex(0);
		pembatalanVoucherSalesDatebox.setValue(asDate(getLocalDate()));
		voucherSalesIdLabel.setValue("id:#"+voucherSales.getId());
		
		voucherSalesNoCompTextbox.setValue(voucherSales.getVoucherNumber().getSerialComp());
		voucherSalesNoPostTextbox.setValue(voucherSales.getPostingVoucherNumber().getSerialComp());
		transactionVoucherSalesDatebox.setValue(voucherSales.getTransactionDate());
		pembayaranVoucherSalesCombobox.setValue(voucherSales.getPaymentType().toString());
		jumlahHariVoucherSalesIntbox.setValue(voucherSales.getJumlahHari());
		voucherSalesTypeLabel.setValue(voucherSales.getVoucherType().toString());
		voucherSalesStatusCombobox.setValue(voucherSales.getFlowStatus().toString());
		VoucherSales voucherSalesCustomerByProxy = 
				getVoucherSalesDao().findCustomerByProxy(voucherSales.getId());
		Customer customer = voucherSalesCustomerByProxy.getCustomer();
		customerVoucherSalesTextbox.setValue(customer.getCompanyType()+"."+
				customer.getCompanyLegalName());
		theSumOfVoucherSalesTextbox.setValue(toLocalFormat(voucherSales.getTheSumOf()));
		usePpnVoucherSalesCheckbox.setChecked(voucherSales.isUsePpn());
		ppnAmountVoucherSalesTextbox.setValue(toLocalFormat(voucherSales.getPpnAmount()));
		referenceVoucherSalesTextbox.setValue(voucherSales.getDocumentRef());
		descriptionVoucherSalesTextbox.setValue(voucherSales.getTransactionDescription());
		
		displayVoucherSalesDebitCredit(voucherSales.getVoucherSalesDebitCredits());
	}

	private void displayVoucherSalesDebitCredit(List<VoucherSalesDebitCredit> voucherSalesDebitCredits) {
		voucherSalesDbcrListbox.setModel(
				new ListModelList<VoucherSalesDebitCredit>(voucherSalesDebitCredits));
		voucherSalesDbcrListbox.setItemRenderer(getVoucherSalesDebitCreditItemRenderer());
	}

	private ListitemRenderer<VoucherSalesDebitCredit> getVoucherSalesDebitCreditItemRenderer() {
		
		return new ListitemRenderer<VoucherSalesDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherSalesDebitCredit dbcr, int index) throws Exception {
				Listcell lc;
				
				//	No Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				//	Nama Akun
				lc = new Listcell(dbcr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				//	Keterangan
				lc = new Listcell(dbcr.getDbcrDescription());
				lc.setParent(item);
				
				//	Debit
				lc = new Listcell(toLocalFormat(dbcr.getDebitAmount()));
				lc.setParent(item);
				
				//	Kredit
				lc = new Listcell(toLocalFormat(dbcr.getCreditAmount()));
				lc.setParent(item);
				
				item.setValue(dbcr);				
			}
		};
	}

	public void onAfterRender$voucherSalesDbcrListbox(Event event) throws Exception {
		BigDecimal totalDebit = BigDecimal.ZERO;
		BigDecimal totalCredit = BigDecimal.ZERO;

		for (Listitem item : voucherSalesDbcrListbox.getItems()) {
			VoucherSalesDebitCredit dbcr = item.getValue();
			
			totalDebit = totalDebit.add(dbcr.getDebitAmount());
			totalCredit = totalCredit.add(dbcr.getCreditAmount());
		}
		
		totalVoucherSalesDebitListfooter.setLabel(toLocalFormat(totalDebit)); 
		totalVoucherSalesCreditListfooter.setLabel(toLocalFormat(totalCredit));
	}

	public void onSelect$statusVoucherSalesCombobox(Event event) throws Exception {
		DocumentStatus status = statusVoucherSalesCombobox.getSelectedItem().getValue();
	
		CustomerOrder customerOrderVoucherSalesByProxy =
				getCustomerOrderDao().findVoucherSalesByProxy(getCustomerOrder().getId());
		VoucherSales voucherSales = customerOrderVoucherSalesByProxy.getVoucherSales();
		
		List<VoucherSalesDebitCredit> reverseDbCrList = null;
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusVoucherSalesCombobox.setStyle("color: red;");
			String userEntry = pembatalanCatatanVoucherSalesTextbox.getValue();
			pembatalanCatatanVoucherSalesTextbox.setValue(userEntry +
					" Pembatalan Customer Order No: "+getCustomerOrder().getDocumentSerialNumber().getSerialComp());
			reverseDbCrList = 
					new ArrayList<VoucherSalesDebitCredit>(voucherSales.getVoucherSalesDebitCredits());
			
			for (VoucherSalesDebitCredit dbcr : voucherSales.getVoucherSalesDebitCredits()) {
				
				// String dbcrDescription = dbcr.getDbcrDescription() +
				//		" (Pembatalan Customer Order No:"+getCustomerOrder().getDocumentSerialNumber().getSerialComp()+")";
				
				VoucherSalesDebitCredit reverseDbCr = new VoucherSalesDebitCredit();
				reverseDbCr.setMasterCoa(dbcr.getMasterCoa());
				reverseDbCr.setDbcrDescription(pembatalanCatatanVoucherSalesTextbox.getValue());
				
				if (dbcr.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
					reverseDbCr.setDebitAmount(dbcr.getCreditAmount());
					reverseDbCr.setCreditAmount(BigDecimal.ZERO); 					
				}
				if (dbcr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
					reverseDbCr.setDebitAmount(BigDecimal.ZERO);
					reverseDbCr.setCreditAmount(dbcr.getDebitAmount()); 
				}
				
				reverseDbCrList.add(reverseDbCr);				
			}
			
			reverseDbCrList.sort((o1, o2) -> {
				return Long.compare(o1.getMasterCoa().getId(), o2.getMasterCoa().getId());
			});			
			
		} else {
			statusVoucherSalesCombobox.setStyle("color: black;");
			pembatalanCatatanVoucherSalesTextbox.setValue("");
			reverseDbCrList = 
					new ArrayList<VoucherSalesDebitCredit>(voucherSales.getVoucherSalesDebitCredits());			
		}
		
		displayVoucherSalesDebitCredit(reverseDbCrList);
	}
		
	/*
	 * 
	 * General Ledger
	 * 
	 */
	private void glTab() throws Exception {
		CustomerOrder customerOrderVoucherSalesByProxy =
				getCustomerOrderDao().findVoucherSalesByProxy(getCustomerOrder().getId());
		VoucherSales voucherSales = customerOrderVoucherSalesByProxy.getVoucherSales();		
		VoucherSales voucherSalesGLByProxy = 
				getVoucherSalesDao().findGeneralLedgerByProxy(voucherSales.getId());
		List<GeneralLedger> glList = voucherSalesGLByProxy.getGeneralLedgers();
		boolean visible = glList != null;
		glTab.setVisible(visible);
		glList.forEach(gl -> {
			log.info(gl.toString());
		});
		
		// statusGLCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusGLCombobox);
		}
		// set selected - NORMAL
		statusGLCombobox.setSelectedIndex(0);
		pembatalanGLDatebox.setValue(asDate(getLocalDate()));
		
		GeneralLedger glMetaInfo = glList.get(0);
		
		glIdLabel.setValue("id:#"+glMetaInfo.getId());
		glPostingDatebox.setValue(glMetaInfo.getPostingDate());
		glPostingNumberTextbox.setValue(glMetaInfo.getPostingVoucherNumber().getSerialComp());
		glVoucherNumberTextbox.setValue(glMetaInfo.getVoucherNumber().getSerialComp());
		glVoucherTypeLabel.setValue(glMetaInfo.getVoucherType().toString());
		glDescriptionTextbox.setValue(glMetaInfo.getTransactionDescription());
		glReferenceTextbox.setValue(glMetaInfo.getDocumentRef());
		
		displayGeneralLedgerDetails(glList);
	}	
	
	private void displayGeneralLedgerDetails(List<GeneralLedger> glList) {
		glListbox.setModel(new ListModelList<GeneralLedger>(glList));
		glListbox.setItemRenderer(getGeneralLedgerDetailItemRenderer());
	}

	private ListitemRenderer<GeneralLedger> getGeneralLedgerDetailItemRenderer() {

		return new ListitemRenderer<GeneralLedger>() {
			
			@Override
			public void render(Listitem item, GeneralLedger gl, int index) throws Exception {
				Listcell lc;
				
				//	No Akun
				lc = new Listcell(gl.getMasterCoa().getMasterCoaComp());
				lc.setParent(item);
				
				//	Nama Akun
				lc = new Listcell(gl.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				//	Keterangan
				lc = new Listcell(gl.getDbcrDescription());
				lc.setParent(item);
				
				//	Debit
				lc = new Listcell(toLocalFormat(gl.getDebitAmount()));
				lc.setParent(item);
				
				//	Kredit
				lc = new Listcell(toLocalFormat(gl.getCreditAmount()));
				lc.setParent(item);
				
				item.setValue(gl);				
			}
		};
	}

	public void onSelect$statusGLCombobox(Event event) throws Exception {
		DocumentStatus status = statusGLCombobox.getSelectedItem().getValue();

		CustomerOrder customerOrderVoucherSalesByProxy =
				getCustomerOrderDao().findVoucherSalesByProxy(getCustomerOrder().getId());
		VoucherSales voucherSales = customerOrderVoucherSalesByProxy.getVoucherSales();		
		VoucherSales voucherSalesGLByProxy = 
				getVoucherSalesDao().findGeneralLedgerByProxy(voucherSales.getId());
		List<GeneralLedger> glList = voucherSalesGLByProxy.getGeneralLedgers();		
		List<GeneralLedger> reverseDbCrList = null;
		
		if (status.equals(DocumentStatus.BATAL)) {
			statusGLCombobox.setStyle("color: red;");
			String userEntry = pembatalanCatatanGLTextbox.getValue();
			pembatalanCatatanGLTextbox.setValue(userEntry +
					" Pembatalan Customer Order No: "+getCustomerOrder().getDocumentSerialNumber().getSerialComp());
			reverseDbCrList = 
					new ArrayList<GeneralLedger>(glList);

			for (GeneralLedger gl : glList) {
				// String dbcrDescription = gl.getDbcrDescription() +
				//		" (Pembatalan Customer Order No: "+getCustomerOrder().getDocumentSerialNumber().getSerialComp()+")";
				
				GeneralLedger reverseGL = new GeneralLedger();
				reverseGL.setPostingDate(gl.getPostingDate());
				reverseGL.setPostingVoucherNumber(gl.getPostingVoucherNumber());
				reverseGL.setVoucherNumber(gl.getVoucherNumber());
				reverseGL.setVoucherType(gl.getVoucherType());
				reverseGL.setTransactionDescription(gl.getTransactionDescription());
				reverseGL.setDocumentRef(gl.getDocumentRef());
				reverseGL.setTransactionDate(gl.getTransactionDate());
				
				reverseGL.setMasterCoa(gl.getMasterCoa());
				reverseGL.setDbcrDescription(pembatalanCatatanGLTextbox.getValue());
				
				if (gl.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
					reverseGL.setDebitAmount(gl.getCreditAmount());
					reverseGL.setCreditAmount(BigDecimal.ZERO);
				} else {
					reverseGL.setDebitAmount(BigDecimal.ZERO);
					reverseGL.setCreditAmount(gl.getDebitAmount());
				}
			
				reverseDbCrList.add(reverseGL);				
			}
			
		} else {
			statusGLCombobox.setStyle("color: black;");
			pembatalanCatatanGLTextbox.setValue("");
			reverseDbCrList = 
					new ArrayList<GeneralLedger>(glList);			
		}
		// sort by MasterCOA
		reverseDbCrList.sort((o1, o2) -> {
			return Long.compare(o1.getMasterCoa().getId(), o2.getMasterCoa().getId());
		});
		// display
		displayGeneralLedgerDetails(reverseDbCrList);
	}
	
	/*
	 * 
	 * Piutang
	 * 
	 */
	private void piutangTab() throws Exception {
		CustomerOrder customerOrderReceivableByProxy =
				getCustomerOrderDao().findCustomerReceivableByProxy(getCustomerOrder().getId());
		CustomerReceivable customerReceivable = 
				customerOrderReceivableByProxy.getCustomerReceivable();
		boolean visible = customerReceivable != null;
		piutangTab.setVisible(visible);
		log.info("PiutangTab: visible: "+visible);
		// 'tunai' payment - no customerReceivable
		if (!visible) {
			return;
		}
		
		List<CustomerReceivableActivity> receivableActivityList =
				customerReceivable.getCustomerReceivableActivities();
		// receivableActivity Holder - receivableActivity matching customerOrder
		List<CustomerReceivableActivity> activityHolderList =
				new ArrayList<CustomerReceivableActivity>();
		
		for (CustomerReceivableActivity activity : receivableActivityList) {
			if (getCustomerOrder().getId().compareTo(activity.getCustomerOrderId())==0) {
				activityHolderList.add(activity);
			}
		}
		
		activityHolderList.forEach(activity -> {
			log.info(activity.toString());
		});
		
		// statusPiutangCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusPiutangCombobox);
		}
		// set selected - NORMAL
		statusPiutangCombobox.setSelectedIndex(0);
		pembatalanPiutangDatebox.setValue(asDate(getLocalDate()));
		
		displayCustomerReceivableActivityList(activityHolderList);
	}

	private void displayCustomerReceivableActivityList(List<CustomerReceivableActivity> activityHolderList) {
		piutangListbox.setModel(new ListModelList<CustomerReceivableActivity>(activityHolderList));
		piutangListbox.setItemRenderer(getCustomerReceivableActivityItemRenderer());	
	}

	private ListitemRenderer<CustomerReceivableActivity> getCustomerReceivableActivityItemRenderer() {

		return new ListitemRenderer<CustomerReceivableActivity>() {
			
			@Override
			public void render(Listitem item, CustomerReceivableActivity activity, int index) throws Exception {
				Listcell lc;
				
				Label label;
				Vbox vbox;
				
				CustomerOrder customerOrder =
						getCustomerOrderDao().findCustomerOrderById(activity.getCustomerOrderId());
				List<CustomerOrderProduct> productList =
						customerOrder.getCustomerOrderProducts();
				
				// Order-No.
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(customerOrder.getDocumentSerialNumber().getSerialComp());
				label.setParent(vbox);
				//
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: product qty(sht)
					label = new Label();
					label.setValue("Qty[Sht] ");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: product qty (sht)
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(Integer.toString(product.getSheetQuantity()));
						label.setStyle("float: right;");
						label.setParent(vbox);					
					}
				}

				// Tgl.Order
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(dateToStringDisplay(
						asLocalDate(customerOrder.getOrderDate()), getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: product qty(kg)
					label = new Label();
					label.setValue("Qty[Kg]");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: product qty(kg)
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormatWithDecimal(product.getWeightQuantity()));
						label.setStyle("float: right;");
						label.setParent(vbox);
					}
				}
				
				// Tgl.Jatuh-Tempo
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				LocalDate paymentDueDate = addDate(customerOrder.getCreditDay(), 
						asLocalDate(customerOrder.getOrderDate()));
				label.setValue(dateToStringDisplay(paymentDueDate, getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: kode
					label = new Label();
					label.setValue("Kode");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: kode
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(product.getUserModInventoryCode());
						label.setStyle("white-space:nowrap;");
						label.setParent(vbox);					
					}
				}
				
				// Total-Order
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getAmountSales()));
				label.setStyle("float: right;");
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: spesifikasi
					label = new Label();
					label.setValue("Spesifikasi");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: spesifikasi
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(product.getDescription());
						label.setParent(vbox);
					}
				}
				
				// Tgl.Pembayaran
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getPaymentDate()==null? " - " :
						dateToStringDisplay(asLocalDate(activity.getPaymentDate()), getShortDateFormat()));
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: harga(rp.)
					label = new Label();
					label.setValue("Harga(Rp.)");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					// detail: harga
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormat(product.getSellingPrice())+(product.isByKg()? "/KG":"/SHT"));
						label.setStyle("float:right;");
						label.setParent(vbox);
					}
					// subTotal
					label = new Label();
					label.setValue("SubTotal(Rp.)");
					label.setParent(vbox);
					// ppn
					label = new Label();
					label.setValue("PPN 10%");
					label.setParent(vbox);					
					// total
					label = new Label();
					label.setValue("Total(Rp.)");
					label.setParent(vbox);
				}
				
				// Pembayaran
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getAmountPaid()));
				label.setStyle("float:right;");
				label.setParent(vbox);
				if (activity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
					// title: subtotal
					label = new Label();
					label.setValue("SubTotal(Rp.)");
					label.setStyle("border-bottom: 2px dotted red;");
					label.setParent(vbox);
					BigDecimal subTotal = BigDecimal.ZERO;
					BigDecimal ppn = BigDecimal.ZERO;
					for (CustomerOrderProduct product : productList) {
						label = new Label();
						label.setValue(toLocalFormat(product.getSellingSubTotal()));
						label.setStyle("float:right;");
						label.setParent(vbox);
						
						subTotal = subTotal.add(product.getSellingSubTotal());
					}
					// subTotal
					label = new Label();
					label.setStyle("border-top: 2px dotted red;float:right;");
					label.setValue(toLocalFormat(subTotal)); // "999.999.999,-"
					label.setParent(vbox);
					ppn = subTotal.multiply(new BigDecimal(0.1));
					// ppn
					label = new Label();
					label.setValue(customerOrder.isUsePpn()? toLocalFormat(ppn):"-");
					label.setStyle("float:right");
					label.setParent(vbox);					
					// total
					label = new Label();
					label.setValue(customerOrder.isUsePpn()? toLocalFormat(subTotal.add(ppn)):toLocalFormat(subTotal));
					label.setStyle("float:right");
					label.setParent(vbox);
				}
				
				// Sisa
				lc = new Listcell();
				lc.setStyle("vertical-align:top;float:right;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				label.setValue(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getRemainingAmount()));
				label.setParent(vbox);
				
				
				// Status
				lc = new Listcell();
				lc.setStyle("vertical-align:top;");
				lc.setParent(item);
				vbox = new Vbox();
				vbox.setParent(lc);
				label = new Label();
				if (activity.getReceivableStatus().equals(DocumentStatus.BATAL)) {
					label.setValue("BATAL");
					label.setStyle("color:red;");
				} else {
					label.setValue(activity.isPaymentComplete() ? "Lunas" : " - ");
				}
				label.setParent(vbox);
				
				// activityId
				lc = new Listcell("id:#"+activity.getId());
				lc.setParent(item);
				
				item.setValue(activity);
			}
		};
	}

	public void onSelect$statusPiutangCombobox(Event event) throws Exception {
		DocumentStatus status = statusPiutangCombobox.getSelectedItem().getValue();

		CustomerOrder customerOrderReceivableByProxy =
				getCustomerOrderDao().findCustomerReceivableByProxy(getCustomerOrder().getId());
		CustomerReceivable customerReceivable = 
				customerOrderReceivableByProxy.getCustomerReceivable();
		List<CustomerReceivableActivity> receivableActivityList =
				customerReceivable.getCustomerReceivableActivities();
		// receivableActivity Holder - receivableActivity matching customerOrder
		List<CustomerReceivableActivity> activityHolderList =
				new ArrayList<CustomerReceivableActivity>();
		
		for (CustomerReceivableActivity activity : receivableActivityList) {
			if (getCustomerOrder().getId().compareTo(activity.getCustomerOrderId())==0) {
				activityHolderList.add(activity);
			}
		}

		if (status.equals(DocumentStatus.BATAL)) {
			statusPiutangCombobox.setStyle("color: red;");
			String userEntry = pembatalanCatatanPiutangTextbox.getValue();
			pembatalanCatatanPiutangTextbox.setValue(userEntry +
					" Pembatalan Customer Order No: "+getCustomerOrder().getDocumentSerialNumber().getSerialComp());
			for (CustomerReceivableActivity activity : activityHolderList) {
				activity.setReceivableStatus(DocumentStatus.BATAL);
			}

		} else {
			statusPiutangCombobox.setStyle("color: black;");
			pembatalanCatatanPiutangTextbox.setValue("");
			for (CustomerReceivableActivity activity : activityHolderList) {
				activity.setReceivableStatus(DocumentStatus.NORMAL);
			}

		}
		// display
		displayCustomerReceivableActivityList(activityHolderList);
	}
	
	public void onClick$saveBatalButton(Event event) throws Exception {
		Tabs tabs = customerOrderBatalTabbox.getTabs();
		List<Tab> tabList = tabs.getChildren();
		List<Tab> activeTabs = new ArrayList<Tab>();
		for (Tab tab : tabList) {
			if (tab.isVisible()) {
				activeTabs.add(tab);
			}
		}
		activeTabs.forEach(tab -> log.info(tab.getId()));
		
		boolean confirm = false;
		for (Tab tab : activeTabs) {
			if (tab.getId().compareTo("customerOrderTab")==0) {
				// check statusCustomerOrderCombobox
				confirm = statusCustomerOrderCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("employeeCommissionTab")==0) {
				// statusCommissionCombobox
				confirm = statusCommissionCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("inventoryTab")==0) {
				// statusInventoryCombobox
				confirm = statusInventoryCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("suratJalanTab")==0) {
				// statusSuratJalanCombobox
				confirm = statusSuratJalanCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("deliveryOrderTab")==0) {
				// statusDeliveryOrderCombobox
				confirm = statusDeliveryOrderCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("fakturTab")==0) {
				// statusFakturCombobox
				confirm = statusFakturCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("voucherSalesTab")==0) {
				// statusVoucherSalesCombobox
				confirm = statusVoucherSalesCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("glTab")==0) {
				// statusGLCombobox
				confirm = statusGLCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("piutangTab")==0) {
				// statusPiutangCombobox
				confirm = statusPiutangCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			}
			tab.setAttribute("confirm", confirm);
		}
		activeTabs.forEach(tab -> log.info(tab.getAttribute("confirm")));
		
		// check whether all tabs are confirmed -- if not confirmed throw exception
		for (Tab tab : activeTabs) {
			if (tab.getAttribute("confirm")==null) {
				continue;
			}			
			if (!(boolean) tab.getAttribute("confirm")) {
				throw new SuppressedException(tab.getLabel()+" NOT set to 'BATAL'", true);
			}
		}
		
		// update
		updatePembatalan(activeTabs);
		
		Events.sendEvent(Events.ON_OK, customerOrderDialongBatalWin, null);
		
		customerOrderDialongBatalWin.detach();
	}
	
	private void updatePembatalan(List<Tab> activeTabs) throws Exception {
		for (Tab tab : activeTabs) {
			if (tab.getId().compareTo("customerOrderTab")==0) {
				// customerOrder
				CustomerOrder modCustomerOrder = getModifiedCustomerOrder();
				getCustomerOrderDao().update(modCustomerOrder);
				// verify
				long modCustomerId = modCustomerOrder.getId();
				modCustomerOrder = getCustomerOrderDao().findCustomerOrderById(modCustomerId);
				log.info("CustomerOrder Id: "+modCustomerOrder.getId()+
					" CustomerOrder Status: "+modCustomerOrder.getOrderStatus().toString()+
					" Batal Date: "+""+
					" Batal Note: "+"");
			} else if (tab.getId().compareTo("employeeCommissionTab")==0) {
				// employeeCommission
				EmployeeCommissions modEmployeeCommissions = getModifiedEmployeeCommissions();
				getEmployeeCommissionsDao().update(modEmployeeCommissions);
				// verify
				long modEmployeeCommissionsId = modEmployeeCommissions.getId();
				modEmployeeCommissions = getEmployeeCommissionsDao().findEmployeeCommissionsById(modEmployeeCommissionsId);
				log.info("Employee Commissions Id: "+modEmployeeCommissions.getId()+
					" Employee Commissions Status: "+modEmployeeCommissions.getCommissionStatus().toString()+
					" Batal Date: "+""+
					" Batal Note: "+"");
			} else if (tab.getId().compareTo("inventoryTab")==0) {
				// inventory
				for (Listitem item : inventoryListbox.getItems()) {
					Inventory modInventory = getModifiedInventory(item.getValue());
					getInventoryDao().update(modInventory);
					// verify
					long modInventoryId = modInventory.getId();
					modInventory = getInventoryDao().findInventoryById(modInventoryId);
					log.info("Inventory Id: "+modInventory.getId()+
						" Inventory Status: "+modInventory.getInventoryStatus().toString()+
						" Quantity Sheet: "+modInventory.getSheetQuantity()+
						" Quantity Kg: "+modInventory.getWeightQuantity());
				}
			} else if (tab.getId().compareTo("suratJalanTab")==0) {
				// suratJalan
				SuratJalan modSuratJalan = getModifiedSuratJalan();
				getSuratJalanDao().update(modSuratJalan);
				// verify
				long modSuratJalanId = modSuratJalan.getId();
				modSuratJalan = getSuratJalanDao().findSuratJalanById(modSuratJalanId);
				log.info("Surat Jalan Id: "+modSuratJalan.getId()+
					" Surat Jalan Status: "+modSuratJalan.getSuratJalanStatus().toString()+
					" Batal Date: "+""+
					" Batal Note: "+"");
			} else if (tab.getId().compareTo("deliveryOrderTab")==0) {
				// deliveryOrder
				DeliveryOrder modDeliveryOrder = getModifiedDeliveryOrder();
				getDeliveryOrderDao().update(modDeliveryOrder);
				// verify
				long modDeliveryOrderId = modDeliveryOrder.getId();
				modDeliveryOrder = getDeliveryOrderDao().findDeliveryOrderById(modDeliveryOrderId);
				log.info("Delivery Order Id: "+modDeliveryOrder.getId()+
					" Delivery Order Status: "+modDeliveryOrder.getDeliveryOrderStatus().toString()+
					" Batal Date: "+""+
					" Batal Note: "+"");
			} else if (tab.getId().compareTo("fakturTab")==0) {
				// faktur
				Faktur modFaktur = getModifiedFaktur();
				getFakturDao().update(modFaktur);
				// verify
				long modFakturId = modFaktur.getId();
				modFaktur = getFakturDao().findFakturById(modFakturId);
				log.info("Faktur Id: "+modFaktur.getId()+
					" Faktur Status: "+modFaktur.getFakturStatus().toString()+
					" Batal Date: "+""+
					" Batal Note: "+"");
			} else if (tab.getId().compareTo("voucherSalesTab")==0) {
				// voucherSales
				VoucherSales modVoucherSales = getModifiedVoucherSales();
				getVoucherSalesDao().update(modVoucherSales);
				// verify
				long modVoucherSalesId = modVoucherSales.getId();
				modVoucherSales = getVoucherSalesDao().findVoucherById(modVoucherSalesId);
				log.info("VoucherSales Id: "+modVoucherSales.getId()+
					" VoucherSales Status: "+modVoucherSales.getVoucherStatus().toString()+
					" Batal Date: "+""+
					" Batal Note: "+"");
				// additonal logs - reversed db/cr in the details
				modVoucherSales.getVoucherSalesDebitCredits().forEach(dbcr ->
					log.info(dbcr.toString()));
			} else if (tab.getId().compareTo("glTab")==0) {
				// gl - from VoucherSales
				VoucherSales modVoucherSalesGeneralLedger = getModifiedVoucherSalesGeneralLedger();
				getVoucherSalesDao().update(modVoucherSalesGeneralLedger);
				// verify
				long modVoucherSalesId = modVoucherSalesGeneralLedger.getId();
				modVoucherSalesGeneralLedger = getVoucherSalesDao().findVoucherById(modVoucherSalesId);
				// GL
				VoucherSales glByProxy =
						getVoucherSalesDao().findGeneralLedgerByProxy(modVoucherSalesGeneralLedger.getId());
				glByProxy.getGeneralLedgers().forEach(gl ->
						log.info(gl.toString()));
			} else if (tab.getId().compareTo("piutangTab")==0) {
				// piutang
				CustomerReceivable receivable = getModifiedCustomerReceivableActivities();
				getCustomerReceivableDao().update(receivable);
				// verify
				long modCustomerReceivableId = receivable.getId();
				receivable = getCustomerReceivableDao().findCustomerReceivableById(modCustomerReceivableId);
				receivable.getCustomerReceivableActivities().forEach(activity ->
						log.info(activity.toString()));
			}
		}
	}

	private CustomerOrder getModifiedCustomerOrder() {
		CustomerOrder modCustomerOrder = getCustomerOrder();
		
		modCustomerOrder.setOrderStatus(statusCustomerOrderCombobox.getSelectedItem().getValue());
		modCustomerOrder.setBatalDate(pembatalanCustomerOrderDatebox.getValue());
		modCustomerOrder.setBatalNote(pembatalanCatatanCustomerOrderTextbox.getValue());
		
		return modCustomerOrder;
	}

	private EmployeeCommissions getModifiedEmployeeCommissions() throws Exception {
		CustomerOrder customerOrderCommissionByProxy =
				getCustomerOrderDao().findEmployeeCommissionsByProxy(getCustomerOrder().getId());
		EmployeeCommissions modEmployeeCommissions = customerOrderCommissionByProxy.getEmployeeCommissions();
 
		modEmployeeCommissions.setCommissionStatus(statusCommissionCombobox.getSelectedItem().getValue());
		modEmployeeCommissions.setBatalDate(pembatalanCommissionDatebox.getValue());
		modEmployeeCommissions.setBatalNote(pembatalanCatatanCommissionTextbox.getValue());
		
		return modEmployeeCommissions;
	}

	private Inventory getModifiedInventory(Inventory modInventory) {
		if (modInventory.getInventoryPacking().equals(InventoryPacking.lembaran)) {
			modInventory.setSheetQuantity(modInventory.getPostSheetQuantity());
			modInventory.setWeightQuantity(modInventory.getPostWeightQuantity());
		} else {
			// do nothing - inventoryStatus set to 'ready'
		}

		modInventory.setNote(dateToStringDB(asLocalDate(pembatalanInventoryDatebox.getValue()))+
				" - "+pembatalanCatatanInventoryTextbox.getValue());

		return modInventory;
	}

	private SuratJalan getModifiedSuratJalan() throws Exception {
		CustomerOrder customerOrderSuratJalanByProxy =
				getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
		SuratJalan modSuratJalan = customerOrderSuratJalanByProxy.getSuratJalan();
		
		modSuratJalan.setSuratJalanStatus(statusSuratJalanCombobox.getSelectedItem().getValue());
		modSuratJalan.setBatalDate(pembatalanSuratJalanDatebox.getValue());
		modSuratJalan.setBatalNote(pembatalanCatatanSuratJalanTextbox.getValue());
		
		return modSuratJalan;
	}

	private DeliveryOrder getModifiedDeliveryOrder() throws Exception {
		CustomerOrder customerOrderSuratJalanByProxy =
				getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
		SuratJalan suratJalan = customerOrderSuratJalanByProxy.getSuratJalan();		
		SuratJalan suratJalanDeliveryOrderByProxy = 
				getSuratJalanDao().findDeliveryOrderByProxy(suratJalan.getId());
		DeliveryOrder modDeliveryOrder = suratJalanDeliveryOrderByProxy.getDeliveryOrder();

		modDeliveryOrder.setDeliveryOrderStatus(statusDeliveryOrderCombobox.getSelectedItem().getValue());
		modDeliveryOrder.setBatalDate(pembatalanDeliveryOrderDatebox.getValue());
		modDeliveryOrder.setNote(pembatalanCatatanDeliveryOrderTextbox.getValue());
		
		return modDeliveryOrder;
	}

	private Faktur getModifiedFaktur() throws Exception {
		CustomerOrder customerOrderSuratJalanByProxy =
				getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
		SuratJalan suratJalan = customerOrderSuratJalanByProxy.getSuratJalan();				
		SuratJalan suratJalanFakturByProxy =
				getSuratJalanDao().findFakturByProxy(suratJalan.getId());
		Faktur modFaktur = suratJalanFakturByProxy.getFaktur();

		modFaktur.setFakturStatus(statusFakturCombobox.getSelectedItem().getValue());
		modFaktur.setBatalDate(pembatalanFakturDatebox.getValue());
		modFaktur.setBatalNote(pembatalanCatatanFakturTextbox.getValue());
		
		return modFaktur;
	}

	private VoucherSales getModifiedVoucherSales() throws Exception {
		CustomerOrder customerOrderVoucherSalesByProxy =
				getCustomerOrderDao().findVoucherSalesByProxy(getCustomerOrder().getId());
		VoucherSales modVoucherSales = customerOrderVoucherSalesByProxy.getVoucherSales();

		modVoucherSales.setVoucherStatus(statusVoucherSalesCombobox.getSelectedItem().getValue());
		modVoucherSales.setPostingDate(pembatalanVoucherSalesDatebox.getValue());
		modVoucherSales.setBatalDate(pembatalanVoucherSalesDatebox.getValue());
		modVoucherSales.setBatalNote(pembatalanCatatanVoucherSalesTextbox.getValue());
		
		// clear the details list
		// read from listbox and add into the details list
		modVoucherSales.getVoucherSalesDebitCredits().clear();
		
		for (Listitem item : voucherSalesDbcrListbox.getItems()) {
			VoucherSalesDebitCredit dbcr = item.getValue();
			
			VoucherSalesDebitCredit reverseDbCr = new VoucherSalesDebitCredit();
			reverseDbCr.setCreditAmount(dbcr.getCreditAmount());
			reverseDbCr.setDebitAmount(dbcr.getDebitAmount());
			reverseDbCr.setDbcrDescription(dbcr.getDbcrDescription());
			reverseDbCr.setMasterCoa(dbcr.getMasterCoa());
			
			modVoucherSales.getVoucherSalesDebitCredits().add(reverseDbCr);
		}
		
		return modVoucherSales;
	}

	private VoucherSales getModifiedVoucherSalesGeneralLedger() throws Exception {
		CustomerOrder customerOrderVoucherSalesByProxy =
				getCustomerOrderDao().findVoucherSalesByProxy(getCustomerOrder().getId());
		VoucherSales voucherSales = customerOrderVoucherSalesByProxy.getVoucherSales();		
		VoucherSales voucherSalesGLByProxy = 
				getVoucherSalesDao().findGeneralLedgerByProxy(voucherSales.getId());
		
		voucherSalesGLByProxy.getGeneralLedgers().clear();
		
		for (Listitem item : glListbox.getItems()) {
			GeneralLedger gl = item.getValue();
			
			GeneralLedger reverseGL = new GeneralLedger();
			reverseGL.setPostingDate(pembatalanGLDatebox.getValue());
			reverseGL.setPostingVoucherNumber(gl.getPostingVoucherNumber());
			reverseGL.setVoucherNumber(gl.getVoucherNumber());
			reverseGL.setVoucherType(gl.getVoucherType());
			reverseGL.setTransactionDescription(gl.getTransactionDescription());
			reverseGL.setDocumentRef(gl.getDocumentRef());
			reverseGL.setTransactionDate(gl.getTransactionDate());
			
			reverseGL.setCreditAmount(gl.getCreditAmount());
			reverseGL.setDebitAmount(gl.getDebitAmount());
			reverseGL.setDbcrDescription(gl.getDbcrDescription());
			reverseGL.setMasterCoa(gl.getMasterCoa());

			voucherSalesGLByProxy.getGeneralLedgers().add(reverseGL);
		}

		return voucherSalesGLByProxy;
	}

	private CustomerReceivable getModifiedCustomerReceivableActivities() throws Exception {
		CustomerOrder customerOrderReceivableByProxy =
				getCustomerOrderDao().findCustomerReceivableByProxy(getCustomerOrder().getId());
		CustomerReceivable customerReceivable = 
				customerOrderReceivableByProxy.getCustomerReceivable();

		for (Listitem item : piutangListbox.getItems()) {
			CustomerReceivableActivity activity = item.getValue();
			
			for (CustomerReceivableActivity receivableActivity : customerReceivable.getCustomerReceivableActivities()) {
				if (activity.getId().compareTo(receivableActivity.getId())==0) {
					receivableActivity.setReceivableStatus(statusPiutangCombobox.getSelectedItem().getValue());
					receivableActivity.setBatalDate(pembatalanPiutangDatebox.getValue());
					receivableActivity.setBatalNote(pembatalanCatatanPiutangTextbox.getValue());
				}
			}
		}
		
		return customerReceivable;
	}

	public void onClick$closeButton(Event event) throws Exception {
	
		customerOrderDialongBatalWin.detach();
	}
	
	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public EmployeeCommissionsDao getEmployeeCommissionsDao() {
		return employeeCommissionsDao;
	}

	public void setEmployeeCommissionsDao(EmployeeCommissionsDao employeeCommissionsDao) {
		this.employeeCommissionsDao = employeeCommissionsDao;
	}

	public SuratJalanDao getSuratJalanDao() {
		return suratJalanDao;
	}

	public void setSuratJalanDao(SuratJalanDao suratJalanDao) {
		this.suratJalanDao = suratJalanDao;
	}

	public DeliveryOrderDao getDeliveryOrderDao() {
		return deliveryOrderDao;
	}

	public void setDeliveryOrderDao(DeliveryOrderDao deliveryOrderDao) {
		this.deliveryOrderDao = deliveryOrderDao;
	}

	public FakturDao getFakturDao() {
		return fakturDao;
	}

	public void setFakturDao(FakturDao fakturDao) {
		this.fakturDao = fakturDao;
	}

	public VoucherSalesDao getVoucherSalesDao() {
		return voucherSalesDao;
	}

	public void setVoucherSalesDao(VoucherSalesDao voucherSalesDao) {
		this.voucherSalesDao = voucherSalesDao;
	}

	public CustomerReceivableDao getCustomerReceivableDao() {
		return customerReceivableDao;
	}

	public void setCustomerReceivableDao(CustomerReceivableDao customerReceivableDao) {
		this.customerReceivableDao = customerReceivableDao;
	}

	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
	
	
}
