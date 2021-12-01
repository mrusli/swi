package com.pyramix.swi.webui.suratjalan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.customerorder.ProductType;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderProduct;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderType;
import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.faktur.FakturProduct;
import com.pyramix.swi.domain.faktur.FakturType;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.suratjalan.SuratJalanProduct;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

/**
 * SuratJalanDialogControl - for SuratJalanDialog.zul
 * 
 * This is a controller for the dialog to CREATE (new), EDIT, or VIEW a
 * SuratJalan.  Note the followings when modifying this dialog:
 * 
 * 1. This dialog is called from CustomerOrderListInfo.zul when a CustomerOrder
 * 	  has been created.  The call is to CREATE a new SuratJalan.
 * 2. This dialog is also called from SuratJalanListInfo.zul from
 *    the 'addButton'.  The call also CREATE a new SuratJalan BUT without using
 *    the data from the CustomerOrder.  Its purpose is to provide additional
 *    support for user when a manual process of creating a SuratJalan is needed.
 * 3. The following defines the type of SuratJalan:
 *      Type 0 (penjualan) - case #1 above - customer order related
 *      Type 1 (manual)    - case #2 above - manual / nothing related 
 
 * @author rusli
 *
 */
public class SuratJalanDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2421273792028594787L;

	private CustomerOrderDao customerOrderDao;
	private CustomerOrderProductDao customerOrderProductDao;
	private SerialNumberGenerator serialNumberGenerator;
	private SuratJalanDao suratJalanDao;
	private EmployeeDao employeeDao;
	
	private Window suratJalanDialogWin;
	private Textbox orderNoTextbox, customerTextbox, noteTextbox;
	// private Combobox pembayaranCombobox, salesPersonCombobox;
	private Datebox orderDatebox, deliveryDatebox;
	// private Intbox jumlahHariIntbox;
	private Listbox productListbox;
	private Button customerButton, resetProductButton, addProductButton, 
		saveButton, cancelButton, printButton;
	private Label infoSuratJalanProductlabel;
	private Checkbox selectCustomerCheckbox;
	
	private SuratJalanData suratJalanData;
	private PageMode pageMode;
	private SuratJalan suratJalan;
	private String requestPath;
	private ListModelList<SuratJalanProduct> SuratJalanProductListModelList;
	
	private static final DocumentStatus STATUS = DocumentStatus.NORMAL;
	private static final String SURAT_JALAN_LISTINFO_REQUESTPATH = "/suratjalan/SuratJalanListInfo.zul";

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setSuratJalanData(
				(SuratJalanData) arg.get("suratJalanData"));		
	}

	public void onCreate$suratJalanDialogWin(Event event) throws Exception {
		// date format
		orderDatebox.setFormat(getLongDateFormat());
		orderDatebox.setLocale(getLocale());
		deliveryDatebox.setFormat(getLongDateFormat());
		deliveryDatebox.setLocale(getLocale());
		
		// surat jalan -- no payment setting
		// set pembayaran - payment type for the combobox
		// setPaymentTypeCombobox();
		
		// surat jalan -- no transaction from sales employee
		// set sales employee
		// setupComboboxSalesEmployee();
		
		// set page mode
		setPageMode(
				getSuratJalanData().getPageMode());
		
		switch (getPageMode()) {
		case EDIT:
			suratJalanDialogWin.setTitle("Merubah (Edit) Surat Jalan"); 			
			break;
		case NEW:
			suratJalanDialogWin.setTitle("Membuat (Add) Surat Jalan"); 			
			break;
		case VIEW:
			suratJalanDialogWin.setTitle("Informasi Surat Jalan");
			break;
		default:
			break;
		}
		
		if (getSuratJalanData().getRequestPath().compareTo(SURAT_JALAN_LISTINFO_REQUESTPATH)==0) {
			// from SuratJalanListInfo.zul
			setSuratJalan(getSuratJalanData().getSuratJalan());
			
			displaySuratJalanStandardInfo();
			
			setStandardReadInfo();
		} else {
			// from CustomerOrderListInfo.zul
			setSuratJalan(getPageMode().compareTo(PageMode.NEW)==0 ?
					customerOrderToSuratJalan(getSuratJalanData().getCustomerOrder(), getSuratJalanData().getSuratJalan()) :
						getSuratJalanData().getSuratJalan());

			displaySuratJalanInfo();

			setReadOnly();
		}
		
		
	}
	
/*
 * // surat jalan -- no payment setting
 * // set pembayaran - payment type for the combobox
 * 	
 * private void setPaymentTypeCombobox() {
		for (PaymentType paymentType : PaymentType.values()) {
			Comboitem comboitem = new Comboitem();
			
			comboitem.setLabel(paymentType.toString());
			comboitem.setValue(paymentType);
			comboitem.setParent(pembayaranCombobox);		
		}
	}

	// surat jalan -- no transaction from sales employee
	// set sales employee

	private void setupComboboxSalesEmployee() throws Exception {
		boolean descendingOrderEmployeeName = false;
		List<Employee> commissionEmployeeList = 
				getEmployeeDao().findAllEmployees_Receiving_Commission(descendingOrderEmployeeName);
		
		Comboitem comboitem;
		for (Employee employee : commissionEmployeeList) {
			comboitem = new Comboitem();
			comboitem.setLabel(employee.getName());
			comboitem.setValue(employee);
			comboitem.setParent(salesPersonCombobox);
		}		
	}
	
	public void onSelect$pembayaranCombobox(Event event) throws Exception {
		if (pembayaranCombobox.getSelectedIndex()>0) {
			// set enable
			jumlahHariIntbox.setValue(0);
			jumlahHariIntbox.setDisabled(false);
			
			// enable selection of customer
			customerTextbox.setValue("");
			customerTextbox.setDisabled(false);
			customerButton.setDisabled(false);			
		} else {
			// make disable
			jumlahHariIntbox.setValue(0);
			jumlahHariIntbox.setDisabled(true);

			// set customer to null -- since pembayaran is 'tunai'
			customerTextbox.setValue("tunai");
			customerTextbox.setAttribute("customer", null);
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
		}
	}
*/		

	public void onCheck$selectCustomerCheckbox(Event event) throws Exception {
		if (selectCustomerCheckbox.isChecked()) {
			selectCustomerCheckbox.setLabel("Tunai");
			customerTextbox.setValue("tunai");
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
		} else {
			selectCustomerCheckbox.setLabel("Pilih Customer");
			customerTextbox.setValue("");
			customerTextbox.setDisabled(false);
			customerButton.setDisabled(false);
		}
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
	
	
	private SuratJalan customerOrderToSuratJalan(CustomerOrder customerOrder, SuratJalan suratJalan) throws Exception {
		
		Date defaultDate = asDate(getLocalDate());
		
		suratJalan.setSuratJalanDate(defaultDate);
		suratJalan.setDeliveryDate(defaultDate);
		suratJalan.setPaymentType(customerOrder.getPaymentType());
		suratJalan.setJumlahHari(customerOrder.getCreditDay());
		
		// by proxy
		EmployeeCommissions employeeCommissionsByProxy = 
				getCustomerOrder_EmployeeCommissionsByProxy(customerOrder.getId());
		suratJalan.setEmployeeCommissions(employeeCommissionsByProxy);
		suratJalan.setUsePpn(customerOrder.isUsePpn());
		suratJalan.setTotalOrder(customerOrder.getTotalOrder());
		suratJalan.setTotalPpn(customerOrder.getTotalPpn());
		
		// by proxy
		Customer customerByProxy = getCustomerOrder_CustomerByProxy(customerOrder.getId()); 
		suratJalan.setCustomer(customerByProxy);
		
		// detail products
		suratJalan.setSuratJalanProducts(createSuratJalanProducts(customerOrder));
		
		return suratJalan;
	}
	
	private List<SuratJalanProduct> createSuratJalanProducts(CustomerOrder customerOrder) throws Exception {
		List<SuratJalanProduct> suratJalanProductList = new ArrayList<SuratJalanProduct>();
		
		for (CustomerOrderProduct product : customerOrder.getCustomerOrderProducts()) {
			SuratJalanProduct suratJalanProduct = new SuratJalanProduct();
			
			suratJalanProduct.setSheetQuantity(product.getSheetQuantity());
			suratJalanProduct.setWeightQuantity(product.getWeightQuantity());
			InventoryCode inventoryCodeByProxy = getCustomerOrderProduct_InventoryCodeByProxy(product.getId());
			suratJalanProduct.setInventoryCode(inventoryCodeByProxy);
			suratJalanProduct.setDescription(product.getDescription());
			suratJalanProduct.setInventoryMarking(product.getMarking());
			suratJalanProduct.setByKg(product.isByKg());
			//
			suratJalanProduct.setProductType(product.getProductType());
			suratJalanProduct.setInventoryPacking(product.getInventoryPacking());
			suratJalanProduct.setInventoryLocation(product.getInventoryLocation());

		 	// needed for faktur
			suratJalanProduct.setSellingPrice(product.getSellingPrice());
			suratJalanProduct.setSellingSubTotal(product.getSellingSubTotal());
			suratJalanProduct.setSellingDiscount(product.isDiscount());
			suratJalanProduct.setSellingDiscountPercent(product.getDiscountPercent());
			suratJalanProduct.setSellingUsePpn(product.isPpn());
			suratJalanProduct.setSellingPpnPercent(product.getPpnPercent());

			suratJalanProductList.add(suratJalanProduct);
		}
		
		return suratJalanProductList;
	}

	private void displaySuratJalanInfo() throws Exception {
		SuratJalan suratJalan = getSuratJalan();
		
		if (suratJalan.getId()==Long.MIN_VALUE) {
			// new
			orderNoTextbox.setValue("");
			orderDatebox.setValue(suratJalan.getSuratJalanDate());
			customerTextbox.setValue(suratJalan.getCustomer()==null ?
					"tunai" :
					suratJalan.getCustomer().getCompanyType()+". "+
					suratJalan.getCustomer().getCompanyLegalName());
			// pembayaranCombobox.setValue(suratJalan.getPaymentType().toString());
			deliveryDatebox.setValue(asDate(getLocalDate()));
			// usePpnCheckbox.setChecked(suratJalan.isUsePpn());
			// jumlahHariIntbox.setValue(suratJalan.getJumlahHari());
			noteTextbox.setValue(getSuratJalanData().getNote());
			// salesPersonCombobox.setValue(suratJalan.getEmployeeCommissions()==null ? " " :
			//		suratJalan.getEmployeeCommissions().getEmployee().getName());
			
			productListbox.setModel(
					new ListModelList<SuratJalanProduct>(suratJalan.getSuratJalanProducts()));
			productListbox.setItemRenderer(getSuratJalanProductListitemRenderer());
		} else {
			// edit
			orderNoTextbox.setValue(suratJalan.getSuratJalanNumber().getSerialComp());
			orderDatebox.setValue(suratJalan.getSuratJalanDate());
			// customer by proxy
			Customer customerByProxy = getSuratJalan_CustomerByProxy(suratJalan.getId());
			customerTextbox.setValue(customerByProxy==null ?
					"tunai" :
					customerByProxy.getCompanyType()+". "+
					customerByProxy.getCompanyLegalName());
			// pembayaranCombobox.setValue(suratJalan.getPaymentType().toString());
			deliveryDatebox.setValue(suratJalan.getDeliveryDate());
			// usePpnCheckbox.setChecked(suratJalan.isUsePpn());
			// jumlahHariIntbox.setValue(suratJalan.getJumlahHari());
			noteTextbox.setValue(suratJalan.getNote());
			// employeeCommissions by proxy
			// EmployeeCommissions employeeCommissionsByProxy = getSuratJalan_EmployeeCommissionsByProxy(suratJalan.getId());
			// salesPersonCombobox.setValue(employeeCommissionsByProxy==null ? " " :
			//		employeeCommissionsByProxy.getEmployee().getName());
			
			productListbox.setModel(
					new ListModelList<SuratJalanProduct>(suratJalan.getSuratJalanProducts()));
			productListbox.setItemRenderer(getSuratJalanProductListitemRenderer());			
		}
		
	}

	private void displaySuratJalanStandardInfo() throws Exception {
		if (getSuratJalan().getId()==Long.MIN_VALUE) {
			// new
			orderNoTextbox.setValue("");
			orderDatebox.setValue(asDate(getLocalDate()));
			// defaulted to 'tunai'
			// pembayaranCombobox.setSelectedIndex(0);
			customerTextbox.setValue("tunai");
			customerTextbox.setAttribute("customer", null);
			// jumlahHariIntbox.setValue(0);
			// usePpnCheckbox.setChecked(false);			
			deliveryDatebox.setValue(asDate(getLocalDate()));
			noteTextbox.setValue("");
			// salesPersonCombobox.setSelectedIndex(0);
			
			// create a new list for the product
			getSuratJalan().setSuratJalanProducts(
					new ArrayList<SuratJalanProduct>());
			// create ListModelList with SuratJalanProduct
			// NOTE: in order to add item into the list,
			// MUST add into the ListModelList
			setSuratJalanProductListModelList(
					new ListModelList<SuratJalanProduct>());
			productListbox.setModel(
					getSuratJalanProductListModelList());
			productListbox.setItemRenderer(getSuratJalanProductListitemRenderer());
		} else {
			// edit / view
			orderNoTextbox.setValue(getSuratJalan().getSuratJalanNumber().getSerialComp());
			orderDatebox.setValue(getSuratJalan().getSuratJalanDate());
			// customer by proxy
			Customer customerByProxy = getSuratJalan_CustomerByProxy(getSuratJalan().getId());
			customerTextbox.setValue(customerByProxy==null ?
					"tunai" :
					customerByProxy.getCompanyType()+". "+
					customerByProxy.getCompanyLegalName());
			// pembayaranCombobox.setValue(getSuratJalan().getPaymentType().toString());
			deliveryDatebox.setValue(getSuratJalan().getDeliveryDate());
			// jumlahHariIntbox.setValue(getSuratJalan().getJumlahHari());
			// usePpnCheckbox.setChecked(getSuratJalan().isUsePpn());
			noteTextbox.setValue(getSuratJalan().getNote());
			// employeeCommissions by proxy
			// EmployeeCommissions employeeCommissionsByProxy = getSuratJalan_EmployeeCommissionsByProxy(getSuratJalan().getId());
			// salesPersonCombobox.setValue(employeeCommissionsByProxy==null ? " " :
			//		employeeCommissionsByProxy.getEmployee().getName());
			
			productListbox.setModel(
					new ListModelList<SuratJalanProduct>(suratJalan.getSuratJalanProducts()));
			productListbox.setItemRenderer(getSuratJalanProductListitemRenderer());						
		}
	}
	
	private ListitemRenderer<SuratJalanProduct> getSuratJalanProductListitemRenderer() {

		return new ListitemRenderer<SuratJalanProduct>() {
			
			@Override
			public void render(Listitem item, SuratJalanProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(String.valueOf(index+1)+".");
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = initQtySht(new Listcell(), product);
				lc.setParent(item);
				
				// Qty (Kg)
				lc = initQtyKg(new Listcell(), product);
				lc.setParent(item);
				
				// Kode
				lc = initKode(new Listcell(), product);
				lc.setParent(item);
				
				// Spesifikasi
				lc = initSpesifikasi(new Listcell(), product);
				lc.setParent(item);
				
				// No.Coil
				lc = initNoCoil(new Listcell(), product);
				lc.setParent(item);
				
				item.setValue(product);
			}
			
			private Listcell initQtySht(Listcell listcell, SuratJalanProduct product) {
				Intbox intbox = new Intbox();
				intbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						product.getSheetQuantity() : product.getP_sheetQuantity());
				intbox.setWidth("50px");
				intbox.setParent(listcell);
				
				return listcell;
			}			
			
			private Listcell initQtyKg(Listcell listcell, SuratJalanProduct product) {
				Decimalbox decimalbox = new Decimalbox();
				decimalbox.setLocale(getLocale());
				decimalbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						product.getWeightQuantity() : product.getP_weightQuantity());
				decimalbox.setWidth("70px");
				decimalbox.setParent(listcell);

				return listcell;
			}
			
			private Listcell initKode(Listcell listcell, SuratJalanProduct product) {
				Textbox textbox = new Textbox();
				// need to check for null, because adding a 'manual' surat jalan
				// not required to select from inventory
				textbox.setValue(product.getInventoryCode()==null ?
						product.getP_inventoryCode() : product.getInventoryCode().getProductCode());
				textbox.setWidth("110px");
				textbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initSpesifikasi(Listcell listcell, SuratJalanProduct product) {
				Textbox textbox = new Textbox();
				textbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						product.getDescription() : product.getP_description());
				textbox.setWidth("190px");
				textbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initNoCoil(Listcell listcell, SuratJalanProduct product) {
				Textbox textbox = new Textbox();
				textbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						product.getInventoryMarking() : product.getP_inventoryMarking());
				textbox.setWidth("90px");
				textbox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$productListbox(Event event) throws Exception {
		
		infoSuratJalanProductlabel.setValue("SuratJalan: "+productListbox.getItemCount()+" items");
	}
	
	public void onClick$resetProductButton(Event event) throws Exception {
		// reset the surat_jalan_product pinventory_marking, pdescription, psheet_quantity, 
		// pweight_quantity, pinventory_code to its original value
		
		// PageMode is EDIT
		List<SuratJalanProduct> userModSuratJalanProducts = getSuratJalan().getSuratJalanProducts();
		
		for (int i = 0; i < productListbox.getItemCount(); i++) {
		 	Listitem item = productListbox.getItemAtIndex(i);
		 	
		 	Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
		 	Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
		 	Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
		 	Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
		 	Textbox nocoilTextbox = (Textbox) item.getChildren().get(5).getFirstChild();

		 	SuratJalanProduct product = userModSuratJalanProducts.get(i);
		 	qtyShtIntbox.setValue(product.getSheetQuantity());
		 	qtyWeightDecimalbox.setValue(product.getWeightQuantity());
		 	kodeTextbox.setValue(product.getInventoryCode().getProductCode());
		 	spesifikasiTextbox.setValue(product.getDescription());
		 	nocoilTextbox.setValue(product.getInventoryMarking());
		}
	}
	
	public void onClick$addProductButton(Event event) throws Exception {
		
		// NEW
		SuratJalanProduct suratJalanProduct = new SuratJalanProduct();
		
		suratJalanProduct.setSheetQuantity(0);
		suratJalanProduct.setWeightQuantity(BigDecimal.ZERO);
		suratJalanProduct.setInventoryCode(null);
		suratJalanProduct.setDescription("");
		suratJalanProduct.setByKg(false);
		suratJalanProduct.setInventoryMarking("");
		
		getSuratJalanProductListModelList().add(suratJalanProduct);
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		SuratJalan userModSuratJalan = getSuratJalan();
		
		userModSuratJalan.setSuratJalanDate(orderDatebox.getValue());
		userModSuratJalan.setDeliveryDate(deliveryDatebox.getValue());
		userModSuratJalan.setNote(noteTextbox.getValue());
		userModSuratJalan.setSuratJalanStatus(STATUS);
		
		if (getPageMode().compareTo(PageMode.NEW)==0) {
			// new
			
			if (getSuratJalanData().getRequestPath().compareTo(SURAT_JALAN_LISTINFO_REQUESTPATH)==0) {
				// set to 'tunai' as default payment type
				userModSuratJalan.setPaymentType(PaymentType.tunai);
				userModSuratJalan.setJumlahHari(0);
				userModSuratJalan.setUsePpn(false);
				userModSuratJalan.setTotalOrder(BigDecimal.ZERO);
				userModSuratJalan.setTotalPpn(BigDecimal.ZERO);
				userModSuratJalan.setEmployeeCommissions(null);
				userModSuratJalan.setCustomer((Customer) customerTextbox.getAttribute("customer"));
				// from SuratJalanListInfo.zul
				userModSuratJalan.setSuratJalanNumber(
						createDocumentSerialNumber(DocumentType.NON_PPN_SURATJALAN, orderDatebox.getValue()));
				
				// no DO associated with this SuratJalan
				userModSuratJalan.setDeliveryOrder(null);
				
				// no faktur associated with this SuratJalan
				userModSuratJalan.setFaktur(null);

				// create date
				userModSuratJalan.setCreateDate(asDate(getLocalDate()));
				// modified datetime
				userModSuratJalan.setModifiedDate(asDateTime(getLocalDateTime()));
				userModSuratJalan.setCheckDate(asDateTime(getLocalDateTime()));
				
				// user create
				userModSuratJalan.setUserCreate(getSuratJalanData().getUserCreate());
				
				// get all products
				userModSuratJalan.setSuratJalanProducts(getStandardUserModSuratJalanProducts());
			} else {
				// from CustomerOrderListInfo.zul
				userModSuratJalan.setSuratJalanProducts(getUserModSuratJalanProducts());
				
				// check use ppn or not?
				boolean usePpn = userModSuratJalan.isUsePpn();
				
				userModSuratJalan.setSuratJalanNumber(usePpn ?
						createDocumentSerialNumber(DocumentType.SURATAJALAN, orderDatebox.getValue()) :
							createDocumentSerialNumber(DocumentType.NON_PPN_SURATJALAN, orderDatebox.getValue()));
				
				// requires checking of the product location in CustomerOrder whether to issue D/O
				// customerOrder.isDeliveryOrderRequired()
				// customerOrder.getCompany();
				userModSuratJalan.setDeliveryOrder(getSuratJalanData().isDeliveryOrderRequired() ?
						createDeliveryOrder(new DeliveryOrder(), getSuratJalanData().getDeliveryOrderCompany()) :
							null);
				
				// by default -- faktur will be created
				userModSuratJalan.setFaktur(createFaktur(new Faktur()));		
								
				// modified datetime
				userModSuratJalan.setModifiedDate(asDateTime(getLocalDateTime()));
				userModSuratJalan.setCheckDate(asDateTime(getLocalDateTime()));
			}
		} else {
			// edit
			userModSuratJalan.setNote(noteTextbox.getValue());
			
			// modified datetime
			userModSuratJalan.setModifiedDate(asDateTime(getLocalDateTime()));
			userModSuratJalan.setCheckDate(asDateTime(getLocalDateTime()));
		}
				
		Events.sendEvent(Events.ON_OK, suratJalanDialogWin, userModSuratJalan);
		
		suratJalanDialogWin.detach();
	}

	private List<SuratJalanProduct> getStandardUserModSuratJalanProducts() {
		List<SuratJalanProduct> userModSuratJalanProducts = getSuratJalan().getSuratJalanProducts();

		for (int i = 0; i < productListbox.getItemCount(); i++) {
		 	Listitem item = productListbox.getItemAtIndex(i);
		 	
		 	Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
		 	Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
		 	Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
		 	Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
		 	Textbox nocoilTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
		 	
		 	SuratJalanProduct product = item.getValue();

		 	product.setSheetQuantity(qtyShtIntbox.getValue());
			product.setWeightQuantity(qtyWeightDecimalbox.getValue());
			// InventoryCode not selected -- set to null
			// -- set pinventoryCode with string value from the kodeTextbox
			product.setInventoryCode(null);
			product.setP_inventoryCode(kodeTextbox.getValue());
			product.setDescription(spesifikasiTextbox.getValue());
			product.setInventoryMarking(nocoilTextbox.getValue());
			// not created from CustomerOrder
			// -- do not know this info
			product.setByKg(false);
			// since it's created manually
			// -- set to 'barang' ProductType
			product.setProductType(ProductType.barang);
			// -- set to 'lembaran' InventoryPacking
			product.setInventoryPacking(InventoryPacking.lembaran);
			// -- set to 'swi' InventoryLocation
			product.setInventoryLocation(InventoryLocation.swi);
		 	// not associated with faktur
			// -- set to '0'
			product.setSellingPrice(BigDecimal.ZERO);
			product.setSellingSubTotal(BigDecimal.ZERO);
			product.setSellingDiscount(false);
			product.setSellingDiscountPercent(BigDecimal.ZERO);
			product.setSellingUsePpn(false);
			product.setSellingPpnPercent(BigDecimal.ZERO);
			// for printing
			product.setP_inventoryMarking(nocoilTextbox.getValue());
			product.setP_description(spesifikasiTextbox.getValue());
			product.setP_sheetQuantity(qtyShtIntbox.getValue());
			product.setP_weightQuantity(qtyWeightDecimalbox.getValue());
			
			userModSuratJalanProducts.add(product);
		}
		
		return userModSuratJalanProducts;
	}	
	
	private List<SuratJalanProduct> getUserModSuratJalanProducts() {
		List<SuratJalanProduct> userModSuratJalanProducts = getSuratJalan().getSuratJalanProducts();
		
		for (int i = 0; i < productListbox.getItemCount(); i++) {
		 	Listitem item = productListbox.getItemAtIndex(i);
		 	
		 	Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
		 	Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
		 	Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
		 	Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
		 	Textbox nocoilTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
		 	
		 	SuratJalanProduct product = userModSuratJalanProducts.get(i);
		 	product.setP_sheetQuantity(qtyShtIntbox.getValue());
		 	product.setP_weightQuantity(qtyWeightDecimalbox.getValue());
		 	product.setP_inventoryCode(kodeTextbox.getValue());
		 	product.setP_description(spesifikasiTextbox.getValue());
		 	product.setP_inventoryMarking(nocoilTextbox.getValue());
		 	
		}
		
		return userModSuratJalanProducts;
	}

	private DeliveryOrder createDeliveryOrder(DeliveryOrder deliveryOrder, Company company) throws Exception {
		deliveryOrder.setCompany(company);
		deliveryOrder.setDeliveryOrderType(DeliveryOrderType.penjualan);
		deliveryOrder.setDeliveryOrderDate(orderDatebox.getValue());
		deliveryOrder.setCompany(company);
		deliveryOrder.setDeliveryOrderNumber(
				createDocumentSerialNumber(DocumentType.DELIVERY_ORDER, orderDatebox.getValue()));
		deliveryOrder.setDeliveryOrderProducts(getUserModDeliveryOrderProducts());
		deliveryOrder.setDeliveryOrderStatus(STATUS);
		// refer to 'new' SuratJalan
		deliveryOrder.setSuratJalan(getSuratJalanData().getSuratJalan());
		// create date
		deliveryOrder.setCreateDate(asDate(getLocalDate()));
		deliveryOrder.setModifiedDate(asDateTime(getLocalDateTime()));
		deliveryOrder.setCheckDate(asDateTime(getLocalDateTime()));
		// user create
		deliveryOrder.setUserCreate(getSuratJalanData().getUserCreate());
		
		return deliveryOrder;
	}

	private List<DeliveryOrderProduct> getUserModDeliveryOrderProducts() {
		List<DeliveryOrderProduct> userModDeliveryOrderProducts = new ArrayList<DeliveryOrderProduct>();
		
		for (int i = 0; i < productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);

		 	Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
		 	Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
		 	Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
		 	Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
		 	Textbox nocoilTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
			
		 	DeliveryOrderProduct product = new DeliveryOrderProduct();
			product.setP_sheetQuantity(qtyShtIntbox.getValue());
			product.setP_weightQuantity(qtyWeightDecimalbox.getValue());
			product.setP_inventoryCode(kodeTextbox.getValue());
			product.setP_description(spesifikasiTextbox.getValue());
			product.setP_marking(nocoilTextbox.getValue());
			
			SuratJalanProduct suratJalanProduct = item.getValue();
			product.setSheetQuantity(suratJalanProduct.getSheetQuantity());			
			product.setWeightQuantity(suratJalanProduct.getWeightQuantity());
			product.setInventoryCode(suratJalanProduct.getInventoryCode());
			product.setDescription(suratJalanProduct.getDescription());
			product.setMarking(suratJalanProduct.getInventoryMarking());
			
			userModDeliveryOrderProducts.add(product);
		}
		
		return userModDeliveryOrderProducts;
	}

	private Faktur createFaktur(Faktur faktur) throws Exception {		
		faktur.setFakturDate(orderDatebox.getValue());
		faktur.setFakturType(FakturType.penjualan);
		faktur.setPaymentType(getSuratJalan().getPaymentType());
		faktur.setJumlahHari(getSuratJalan().getJumlahHari());
		faktur.setUsePpn(getSuratJalan().isUsePpn());
		faktur.setTotalOrder(getSuratJalan().getTotalOrder());
		faktur.setTotalPpn(getSuratJalan().getTotalPpn());
		faktur.setNote(getSuratJalan().getNote());
		faktur.setCustomer(getSuratJalan().getCustomer());
		faktur.setFakturStatus(STATUS);
		// refer to 'new' SuratJalan
		faktur.setSuratJalan(getSuratJalanData().getSuratJalan());
		// create date
		faktur.setCreateDate(asDate(getLocalDate()));
		faktur.setModifiedDate(asDateTime(getLocalDateTime()));
		faktur.setCheckDate(asDateTime(getLocalDateTime()));
		// user create
		faktur.setUserCreate(getSuratJalanData().getUserCreate());
		
		boolean usePpn = getSuratJalan().isUsePpn();
		
		faktur.setFakturNumber(usePpn ?
				createDocumentSerialNumber(DocumentType.FAKTUR, orderDatebox.getValue()) :
					createDocumentSerialNumber(DocumentType.NON_PPN_FAKTUR, orderDatebox.getValue()));
		
		faktur.setFakturProducts(getUserModFakturProducts());
		
		return faktur;
	}	
	
	
	private List<FakturProduct> getUserModFakturProducts() {
		List<FakturProduct> userModFakturProducts = new ArrayList<FakturProduct>();
		
		for (int i = 0; i < productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);

		 	Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
		 	Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
		 	Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
		 	Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
		 	Textbox nocoilTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
			
			FakturProduct product = new FakturProduct();
			product.setP_sheetQuantity(qtyShtIntbox.getValue());
			product.setP_weightQuantity(qtyWeightDecimalbox.getValue());
			product.setP_inventoryCode(kodeTextbox.getValue());
			product.setP_description(spesifikasiTextbox.getValue());
			product.setP_inventoryMarking(nocoilTextbox.getValue());
			
			SuratJalanProduct suratJalanProduct = item.getValue();
			product.setSheetQuantity(suratJalanProduct.getSheetQuantity());
			product.setWeightQuantity(suratJalanProduct.getWeightQuantity());
			product.setInventoryCode(suratJalanProduct.getInventoryCode());
			product.setDescription(suratJalanProduct.getDescription());
			product.setInventoryMarking(suratJalanProduct.getInventoryMarking());
			
			//
			product.setByKg(suratJalanProduct.isByKg());
			product.setUnitPrice(suratJalanProduct.getSellingPrice());
			product.setP_unitPrice(suratJalanProduct.getSellingPrice());
			product.setSubTotal(suratJalanProduct.getSellingSubTotal());
			product.setP_subTotal(suratJalanProduct.getSellingSubTotal());
			product.setDiscount(suratJalanProduct.isSellingDiscount());
			product.setDiscountPercent(suratJalanProduct.getSellingDiscountPercent());
			product.setUsePpn(suratJalanProduct.isSellingUsePpn());
			product.setPpnPercent(suratJalanProduct.getSellingPpnPercent());
			//
			product.setProductType(suratJalanProduct.getProductType());
			product.setInventoryPacking(suratJalanProduct.getInventoryPacking());
			product.setInventoryLocation(suratJalanProduct.getInventoryLocation());
			
			userModFakturProducts.add(product);
		}		
		
		return userModFakturProducts;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		suratJalanDialogWin.detach();
	}

	public void onClick$printButton(Event event) throws Exception {
		Map<String, SuratJalan> arg = 
				Collections.singletonMap("suratJalan", getSuratJalan());
																
		Window printWindow =
				(Window) Executions.createComponents("/suratjalan/SuratJalanPrint.zul", null, arg);
		
		printWindow.doModal();
	}
	
/*	
 *  private EmployeeCommissions getSuratJalan_EmployeeCommissionsByProxy(long id) throws Exception {
		SuratJalan suratJalan = getSuratJalanDao().findEmployeeCommissionsByProxy(id);
		
		return suratJalan.getEmployeeCommissions();
	}	
*/	
	
	private Customer getSuratJalan_CustomerByProxy(long id) throws Exception {
		SuratJalan suratJalan = getSuratJalanDao().findCustomerByProxy(id);
		
		return suratJalan.getCustomer();
	}	
		
	private EmployeeCommissions getCustomerOrder_EmployeeCommissionsByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findEmployeeCommissionsByProxy(id);
		
		return customerOrder.getEmployeeCommissions();
	}	
	
	private Customer getCustomerOrder_CustomerByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findCustomerByProxy(id);
		
		return customerOrder.getCustomer();
	}

	private InventoryCode getCustomerOrderProduct_InventoryCodeByProxy(long id) throws Exception {
		CustomerOrderProduct customerOrderProduct = getCustomerOrderProductDao().findInventoryCodeByProxy(id);
		
		return customerOrderProduct.getInventoryCode();
	}	

	private void setReadOnly() {
		switch (getPageMode()) {
		case EDIT:
			orderDatebox.setDisabled(true);
			deliveryDatebox.setDisabled(true);
			// usePpnCheckbox.setDisabled(true);
			noteTextbox.setDisabled(false);

			resetProductButton.setVisible(true);
			addProductButton.setVisible(false);
			
			saveButton.setVisible(true);
			printButton.setVisible(false);
			cancelButton.setVisible(true);
			break;
		case NEW:
			orderDatebox.setDisabled(false);
			deliveryDatebox.setDisabled(false);
			// usePpnCheckbox.setDisabled(false);
			noteTextbox.setDisabled(false);

			resetProductButton.setVisible(true);
			addProductButton.setVisible(false);

			saveButton.setVisible(true);
			printButton.setVisible(false);
			cancelButton.setVisible(true);
			break;
		case VIEW:
			orderDatebox.setDisabled(true);
			deliveryDatebox.setDisabled(true);
			noteTextbox.setDisabled(true);

			resetProductButton.setVisible(false);
			addProductButton.setVisible(false);

			saveButton.setVisible(false);
			printButton.setVisible(true);
			cancelButton.setVisible(true);
			break;
		default:
			break;
		}
		
	}
	
	private void setStandardReadInfo() {
		switch (getPageMode()) {
		case NEW:
			orderDatebox.setDisabled(false);
			// pembayaran defaulted to 'tunai'
			// --> customer, jumlahHari set to disabled
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
			// jumlahHariIntbox.setDisabled(true);
			// user can select the type of payment
			// pembayaranCombobox.setDisabled(false);
			selectCustomerCheckbox.setVisible(true);
			selectCustomerCheckbox.setDisabled(false);
			
			deliveryDatebox.setDisabled(false);
			// salesPersonCombobox.setDisabled(false);
			noteTextbox.setDisabled(false);
			
			resetProductButton.setVisible(false);
			addProductButton.setVisible(true);
			
			saveButton.setVisible(true);
			printButton.setVisible(false);
			cancelButton.setVisible(true);
			
			break;
		case EDIT:
			orderDatebox.setDisabled(true);
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
			// jumlahHariIntbox.setDisabled(true);
			// pembayaranCombobox.setDisabled(true);
			selectCustomerCheckbox.setVisible(true);
			selectCustomerCheckbox.setDisabled(false);

			deliveryDatebox.setDisabled(true);
			// salesPersonCombobox.setDisabled(true);
			noteTextbox.setDisabled(false);

			resetProductButton.setVisible(true);
			addProductButton.setVisible(false);

			saveButton.setVisible(true);
			printButton.setVisible(true);
			cancelButton.setVisible(true);

			break;
		case VIEW:
			orderDatebox.setDisabled(true);
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
			// jumlahHariIntbox.setDisabled(true);
			// pembayaranCombobox.setDisabled(true);
			selectCustomerCheckbox.setVisible(true);
			selectCustomerCheckbox.setDisabled(true);

			deliveryDatebox.setDisabled(true);
			// salesPersonCombobox.setDisabled(true);
			noteTextbox.setDisabled(true);

			resetProductButton.setVisible(false);
			addProductButton.setVisible(false);
			
			saveButton.setVisible(false);
			printButton.setVisible(false);
			cancelButton.setVisible(true);

			break;
		default:
			break;
		}		
	}
	
	
	private DocumentSerialNumber createDocumentSerialNumber(DocumentType documentType, Date currentDate) throws Exception {
		int serialNum =	getSerialNumberGenerator().getSerialNumber(documentType, currentDate);
		
		DocumentSerialNumber documentSerNum = new DocumentSerialNumber();
		documentSerNum.setDocumentType(documentType);
		documentSerNum.setSerialDate(currentDate);
		documentSerNum.setSerialNo(serialNum);
		documentSerNum.setSerialComp(formatSerialComp(
				documentType.toCode(documentType.getValue()), currentDate, serialNum));
		
		return documentSerNum;
	}	
	
	/**
	 * @return the suratJalanData
	 */
	public SuratJalanData getSuratJalanData() {
		return suratJalanData;
	}

	/**
	 * @param suratJalanData the suratJalanData to set
	 */
	public void setSuratJalanData(SuratJalanData suratJalanData) {
		this.suratJalanData = suratJalanData;
	}

	/**
	 * @return the pageMode
	 */
	public PageMode getPageMode() {
		return pageMode;
	}

	/**
	 * @param pageMode the pageMode to set
	 */
	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	/**
	 * @return the suratJalan
	 */
	public SuratJalan getSuratJalan() {
		return suratJalan;
	}

	/**
	 * @param suratJalan the suratJalan to set
	 */
	public void setSuratJalan(SuratJalan suratJalan) {
		this.suratJalan = suratJalan;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public SuratJalanDao getSuratJalanDao() {
		return suratJalanDao;
	}

	public void setSuratJalanDao(SuratJalanDao suratJalanDao) {
		this.suratJalanDao = suratJalanDao;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public ListModelList<SuratJalanProduct> getSuratJalanProductListModelList() {
		return SuratJalanProductListModelList;
	}

	public void setSuratJalanProductListModelList(ListModelList<SuratJalanProduct> suratJalanProductListModelList) {
		SuratJalanProductListModelList = suratJalanProductListModelList;
	}

	public EmployeeDao getEmployeeDao() {
		return employeeDao;
	}

	public void setEmployeeDao(EmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}
}
