package com.pyramix.swi.webui.customerorder;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.persistence.employee.dao.EmployeeDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;
import com.pyramix.swi.webui.inventory.InventoryData;
import com.pyramix.swi.webui.inventory.InventoryListInfoType;

public class CustomerOrderDialogControl extends GFCBaseController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4487991631023244308L;

	private SerialNumberGenerator serialNumberGenerator;
	private EmployeeDao employeeDao;
	private CustomerOrderProductDao customerOrderProductDao;
	
	private Window customerOrderDialogWin;
	private Datebox orderDatebox, pembatalanCustomerOrderDatebox;
	private Listbox productListbox;
	private Combobox pembayaranCombobox, salesPersonCombobox;
	private Intbox jumlahHariIntbox;
	private Textbox orderNoTextbox, customerTextbox, totalOrderTextbox, 
		noteTextbox, subTotalTextbox, ppnTextbox, totalTextbox,
		pembatalanCatatanCustomerOrderTextbox;
	private Button customerButton, addInventoryButton,
		checkButton, saveButton, cancelButton;
	private Checkbox usePpn;
	private Label infoOrderlabel, customerOrderStatusLabel;
	private Grid customerOrderBatalGrid;
		
	private CustomerOrderData customerOrderData;
	private PageMode pageMode;
	private List<CustomerOrderProduct> customerOrderProductList;
	// must use this because the totalOrder componetn is a TEXTBOX
	// -- changes in the textBox reflects this value
	private BigDecimal totalOrderValue 	= BigDecimal.ZERO;
	private BigDecimal accSubTotal 		= BigDecimal.ZERO;
	private List<Inventory> selectedInventoryList;
	private int productCount;

	private final DocumentType DEFAULT_DOCUMENT_TYPE 	= DocumentType.CUSTOMER_ORDER;
	private final DocumentType NON_PPN_ORDER 			= DocumentType.NON_PPN_ORDER;
	private final DocumentStatus STATUS					= DocumentStatus.NORMAL;
	
	private final BigDecimal PPN = new BigDecimal(0.11);
	
	private final Logger log = Logger.getLogger(CustomerOrderDialogControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerOrderData(
				(CustomerOrderData) Executions.getCurrent().getArg().get("orderData"));
	}

	public void onCreate$customerOrderDialogWin(Event event) throws Exception {
		// set datebox format
		orderDatebox.setLocale(getLocale());
		orderDatebox.setFormat(getLongDateFormat());
		// pembatalan
		pembatalanCustomerOrderDatebox.setLocale(getLocale());
		pembatalanCustomerOrderDatebox.setFormat(getLongDateFormat());
		
		// init selectedInventoryList
		setSelectedInventoryList(new ArrayList<Inventory>());
		
		// payment type
		setupComboboxPaymentType();
		
		setPageMode(getCustomerOrderData().getPageMode());

		setupComboboxSalesEmployee();
		
		setReadOnly(getPageMode());
		
		switch (getPageMode()) {
		case EDIT:
			customerOrderDialogWin.setTitle("Merubah (Edit) Customer Order");
			break;
		case NEW:
			customerOrderDialogWin.setTitle("Membuat (Tambah) Customer Order");
			break;
		case VIEW:
			customerOrderDialogWin.setTitle("Melihat (View) Customer Order");
			break;
		default:
			break;
		}
		
		// set the CustomerOrder info
		setCustomerOrderInfo();
		
		// init the product list
		setCustomerOrderProductList(
			getPageMode().compareTo(PageMode.EDIT)==0 || getPageMode().compareTo(PageMode.VIEW)==0 ?
				getCustomerOrderData().getCustomerOrder().getCustomerOrderProducts() :
					new ArrayList<CustomerOrderProduct>());
		
		// set productCount
		productCount = getCustomerOrderProductList().size();
		
		// load the product list
		loadAllCustomerOrderProduct();
		
	}

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

	private void setupComboboxPaymentType() {
		for (PaymentType type : PaymentType.values()) {
			Comboitem comboitem = new Comboitem();
			
			comboitem.setLabel(type.toString());
			comboitem.setValue(type);
			comboitem.setParent(pembayaranCombobox);
		}
		
		// pembayaranCombobox.setSelectedIndex(0);
		// customerTextbox.setValue("TUNAI");
	}

	private void setCustomerOrderInfo() throws Exception {
		if (getPageMode().compareTo(PageMode.EDIT)==0) {
			// EDIT load the info from the listinfo listitem
			CustomerOrder order = getCustomerOrderData().getCustomerOrder();
			
			// generated orderNo
			orderNoTextbox.setValue(order.getDocumentSerialNumber().getSerialComp());

			orderDatebox.setValue(order.getOrderDate());

			Customer proxyCustomer = getCustomerByProxy(order.getId());
			customerTextbox.setValue(proxyCustomer==null ?
					"tunai" : proxyCustomer.getCompanyType()+". "+
					proxyCustomer.getCompanyLegalName());
			customerTextbox.setAttribute("customer", proxyCustomer);			
			
			for (Comboitem item : pembayaranCombobox.getItems()) {
				if (order.getPaymentType().equals(item.getValue())) {
					pembayaranCombobox.setSelectedItem(item);
				}
			}
			
			jumlahHariIntbox.setValue(order.getCreditDay());
			jumlahHariIntbox.setDisabled(
				!(order.getPaymentType().compareTo(PaymentType.bank)==0 || 
					order.getPaymentType().compareTo(PaymentType.giro)==0));
			
			usePpn.setChecked(order.isUsePpn());
			
			totalOrderTextbox.setValue(toLocalFormat(order.getTotalOrder()));
			setTotalOrderValue(order.getTotalOrder());
			
			noteTextbox.setValue(order.getNote());
			
/*			EmployeeCommissions employeeCommissions = getEmployeeCommissionsByProxy(order.getId());
			if (employeeCommissions==null) {
				salesPersonCombobox.setValue("");
			} else {
				long empCommId = employeeCommissions.getEmployee().getId();
				
				for (Comboitem item : salesPersonCombobox.getItems()) {
					Employee employee = item.getValue();
					long empId = employee.getId();
					
					if (empCommId==empId) {
						salesPersonCombobox.setSelectedItem(item);
					}
				}
			}
*/			
			// salesPersonCombobox
			Employee employeeSales = getEmployeeSalesByProxy(order.getId());
			if (employeeSales == null) {
				salesPersonCombobox.setValue("");
			} else {
				
				long employeeSalesId = employeeSales.getId();
				
				for (Comboitem item : salesPersonCombobox.getItems()) {
					Employee employee = item.getValue();
					long employeeId = employee.getId();
					
					if (employeeSalesId==employeeId) {
						salesPersonCombobox.setSelectedItem(item);
						
						break;
					}
				}
								
			}
		
		} else if (getPageMode().compareTo(PageMode.VIEW)==0) {
			// VIEW - load the info from the listinfo listitem
			CustomerOrder order = getCustomerOrderData().getCustomerOrder();

			customerOrderBatalGrid.setVisible(order.getOrderStatus().equals(DocumentStatus.BATAL));
			customerOrderStatusLabel.setValue(order.getOrderStatus().toString());
			pembatalanCustomerOrderDatebox.setValue(order.getBatalDate());
			pembatalanCatatanCustomerOrderTextbox.setValue(order.getBatalNote());
			
			// generated orderNo
			orderNoTextbox.setValue(order.getDocumentSerialNumber().getSerialComp());

			orderDatebox.setValue(order.getOrderDate());

			Customer proxyCustomer = getCustomerByProxy(order.getId());
			customerTextbox.setValue(proxyCustomer==null ?
					"tunai" : proxyCustomer.getCompanyType()+". "+
					proxyCustomer.getCompanyLegalName());
			customerTextbox.setAttribute("customer", proxyCustomer);			
			
			// for (Comboitem item : pembayaranCombobox.getItems()) {
			//	if (order.getPaymentType().equals(item.getValue())) {
			//		pembayaranCombobox.setSelectedItem(item);
			//	}
			// }
			
			pembayaranCombobox.setText(order.getPaymentType().toString());
			
			jumlahHariIntbox.setValue(order.getCreditDay());
			
			usePpn.setChecked(order.isUsePpn());
			
			totalOrderTextbox.setValue(toLocalFormat(order.getTotalOrder()));
			setTotalOrderValue(order.getTotalOrder());
			
			noteTextbox.setValue(order.getNote());
			
/*			EmployeeCommissions employeeCommissions = getEmployeeCommissionsByProxy(order.getId());
			salesPersonCombobox.setValue(employeeCommissions.getEmployee().getName());

			if (employeeCommissions==null) {
				salesPersonCombobox.setValue("");
			} else {
				long empCommId = employeeCommissions.getEmployee().getId();
				
				for (Comboitem item : salesPersonCombobox.getItems()) {
					Employee employee = item.getValue();
					long empId = employee.getId();
					
					if (empCommId==empId) {
						salesPersonCombobox.setSelectedItem(item);
					}
				}
			}
*/
			// salesPersonCombobox
			Employee employeeSales = getEmployeeSalesByProxy(order.getId());
			if (employeeSales == null) {
				salesPersonCombobox.setValue("");
			} else {
				
				long employeeSalesId = employeeSales.getId();
				
				for (Comboitem item : salesPersonCombobox.getItems()) {
					Employee employee = item.getValue();
					long employeeId = employee.getId();
					
					if (employeeSalesId==employeeId) {
						salesPersonCombobox.setSelectedItem(item);
						
						break;
					}
				}
			}
			
		} else {
			// NEW : set to default
			orderDatebox.setValue(asDate(getLocalDate()));
			totalOrderTextbox.setValue(toLocalFormat(getTotalOrderValue()));
		}
		
	}

	public void onClick$totalOrderButton(Event event) throws Exception {
		CustomerOrder order = getPageMode().compareTo(PageMode.EDIT)==0 || getPageMode().compareTo(PageMode.VIEW)==0 ?
				getCustomerOrderData().getCustomerOrder() : null;
						
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", order==null ? null : order.getTotalOrder());		
				
		Window totalOrderDialogWin = 
				(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
		
		totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				totalOrderTextbox.setValue(toLocalFormat((BigDecimal) event.getData()));
				setTotalOrderValue((BigDecimal) event.getData());				
			}
		});
		
		totalOrderDialogWin.doModal();
	}
	
	private void loadAllCustomerOrderProduct() {
		
		productListbox.setModel(
				new ListModelList<CustomerOrderProduct>(getCustomerOrderProductList()));
		productListbox.setItemRenderer(
				getCustomerOrderProductListitemRenderer());
		
	}

	private ListitemRenderer<CustomerOrderProduct> getCustomerOrderProductListitemRenderer() {
		
		return new ListitemRenderer<CustomerOrderProduct>() {
			
			@Override
			public void render(Listitem item, CustomerOrderProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.Coil
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
				// Kode		
				if (product.getId().compareTo(Long.MIN_VALUE)==0) {
					lc = new Listcell(product.getInventoryCode().getProductCode());
				} else {
					CustomerOrderProduct productByProxy = getInventoryCodeByProxy(product.getId());
					
					lc = new Listcell(productByProxy.getInventoryCode().getProductCode());
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
						// getFormatedDecimal(product.getWeightQuantity()));
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = new Listcell(getFormatedInteger(product.getSheetQuantity()));
				lc.setParent(item);
				
				// Unit
				lc = new Listcell(product.isByKg() ? "Kg" : "Sht/Line");
				lc.setParent(item);
				
				// Harga
				// System.out.println(product.getSellingPrice());
				lc = new Listcell(toLocalFormatWithDecimal(product.getSellingPrice()));
						//toLocalFormat(product.getSellingPrice()));
				lc.setParent(item);
				
				// SubTotal
				BigDecimal subTotal = product.isByKg() ? product.getWeightQuantity().multiply(product.getSellingPrice()) :
					product.getSellingPrice().multiply(new BigDecimal(product.getSheetQuantity()));				
				lc = new Listcell(toLocalFormat(subTotal));
				lc.setParent(item);
				
				// edit
				lc = initEdit(new Listcell(), product, index);
				lc.setParent(item);

				// set value
				item.setValue(product);				
				
				if (product.getId().compareTo(Long.MIN_VALUE)!=0) {
					// NOT new
					accSubTotal = accSubTotal.add(subTotal);
				}
			}

			private Listcell initEdit(Listcell listcell, CustomerOrderProduct product, int index) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				// NOTE: 16/06/2021 - We stop the user from modifying existing customerOrderProduct
				// because we NEED to update the inventory -- becoming too complex
				// 
				// ONLY new CustomerOrderProduct allowed to edit
				editButton.setDisabled((getPageMode().compareTo(PageMode.VIEW)==0) ||
						(product.getId().compareTo(Long.MIN_VALUE)!=0));
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						log.info("CustomerOrderProduct id: "+product.getId());
						
						Map<String, CustomerOrderProduct> arg = 
								Collections.singletonMap("customerOrderProduct", product);
						Window productEditWin = 
								(Window) Executions.createComponents("/customerorder/CustomerOrderProductDialog.zul", null, arg);
						
						productEditWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								CustomerOrderProductData productData = 
										(CustomerOrderProductData) event.getData();
								
								productDataToCustomerOrder(productData, index);
								
								// after user make changes to the item
								// updateProductTotalInfo();
							}
							
							private void productDataToCustomerOrder(CustomerOrderProductData productData, int index) {
								Listitem item = productListbox.getItemAtIndex(index);
								
								CustomerOrderProduct productVal = item.getValue();
								
								// No.Coil
								Listcell lc00 = (Listcell) item.getChildren().get(0);
								lc00.setLabel(productData.getCoilNo());
								
								productVal.setMarking(productData.getCoilNo());
								
								// Kode
								Listcell lc01 = (Listcell) item.getChildren().get(1);
								lc01.setLabel(productData.getKode());
								
								productVal.setUserModInventoryCode(productData.getKode());
								
								// Spesifikasi
								Listcell lc02 = (Listcell) item.getChildren().get(2);
								lc02.setLabel(productData.getSpesifikasi());
								
								productVal.setDescription(productData.getSpesifikasi());
								
								// packing --> no change
								
								// Qty (Kg)
								Listcell lc04 = (Listcell) item.getChildren().get(4);
								lc04.setLabel(toLocalFormatWithDecimal(productData.getQtyKg()));
								//getFormatedDecimal(productData.getQtyKg()));
								
								productVal.setWeightQuantity(productData.getQtyKg());
								
								// Qty (Sht/Line)
								Listcell lc05 = (Listcell) item.getChildren().get(5);
								lc05.setLabel(getFormatedInteger(productData.getQtySht()));
								
								productVal.setSheetQuantity(productData.getQtySht());
								
								// Unit
								Listcell lc06 = (Listcell) item.getChildren().get(6);
								lc06.setLabel(productData.isByKg() ? "Kg" : "Sht/Line");
								
								productVal.setByKg(productData.isByKg());
								
								// Harga (Rp.)
								Listcell lc07 = (Listcell) item.getChildren().get(7);
								lc07.setLabel(toLocalFormatWithDecimal(productData.getHarga()));
								//getFormatedDecimal(productData.getHarga()));
								//toLocalFormat(productData.getHarga()));
								
								productVal.setSellingPrice(productData.getHarga());
								
								// SubTotal (Rp.)
								Listcell lc08 = (Listcell) item.getChildren().get(8);
								lc08.setLabel(toLocalFormat(productData.getSubTotal()));
								
								// System.out.println(productData.getSubTotal().setScale(0, RoundingMode.HALF_EVEN));
								
								productVal.setSellingSubTotal(productData.getSubTotal().setScale(0, RoundingMode.HALF_EVEN));
								
								// other attributes -- not used at the moment
								productVal.setProductType(ProductType.barang);
								productVal.setDiscount(false);
								productVal.setDiscountPercent(BigDecimal.ZERO);
								// check for PPN in the CustomerOrder
								// -- set to false here (in CustomerOrder maybe true)
								productVal.setPpn(false);
								productVal.setPpnPercent(BigDecimal.ZERO); 								
								
							}
						});
						
						productEditWin.doModal();
						
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}
	
	public void onAfterRender$productListbox(Event event) throws Exception {
		if (getPageMode().compareTo(PageMode.NEW)!=0) {
			updateProductTotalInfo();
		}
		
		infoOrderlabel.setValue("Pembelian: "+productCount+" items");
	}
	
	public void updateProductTotalInfo() {
		log.info("updateProductTotalInfo()");
		log.info("SubTotal: "+toLocalFormat(accSubTotal));
		log.info("Per 01 April 2022 PPN is 11%: "+PPN);		

		BigDecimal ppnValue 	= usePpn.isChecked() ? 
				accSubTotal.multiply(PPN) : BigDecimal.ZERO;
				// accSubTotal.multiply(new BigDecimal(0.1)) : BigDecimal.ZERO;
		log.info("PPN 11%: "+toLocalFormat(ppnValue));
		
		BigDecimal totalOrder 	= accSubTotal.add(ppnValue);
		log.info("Total: "+toLocalFormat(totalOrder));
		
		subTotalTextbox.setValue(toLocalFormat(accSubTotal));
		ppnTextbox.setValue(toLocalFormat(ppnValue));
		totalTextbox.setValue(toLocalFormat(totalOrder));
		
		totalOrderTextbox.setValue(toLocalFormat(totalOrder));
		setTotalOrderValue(totalOrder);
	}
	
	public void onSelect$pembayaranCombobox(Event event) throws Exception {
		PaymentType selType = 
				pembayaranCombobox.getSelectedItem().getValue();
		
		if (selType.compareTo(PaymentType.tunai)==0) {
			jumlahHariIntbox.setDisabled(true);
			jumlahHariIntbox.setValue(0);
			
			if (customerTextbox.getValue().isEmpty()) {
				customerTextbox.setValue("tunai");
				customerTextbox.setAttribute("customer", null);				
			}
			
		} else {
			jumlahHariIntbox.setDisabled(false);
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
	
	public void onClick$addInventoryButton(Event event) throws Exception {
		InventoryData inventoryData = new InventoryData();
		inventoryData.setInventoryListInfoType(InventoryListInfoType.CustomerOrder);
		inventoryData.setSelectedInventoryList(getSelectedInventoryList());
		
		Map<String, InventoryData> argument =
				Collections.singletonMap("inventoryData", inventoryData);

		Window inventoryDialogWin = 
				(Window) Executions.createComponents("/inventory/InventoryListInfoDialog.zul", null, argument);

		inventoryDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				// get the selected inventory
				Inventory inventory = (Inventory) event.getData();

				// need to check whether the subsequent products are in the same location as the 1st product
				// if not, raise exception
				if (getCustomerOrderProductList().isEmpty()) {
					getSelectedInventoryList().add(inventory);
					
					// add to the list
					getCustomerOrderProductList().add(
							inventoryToCustomerOrderProduct(inventory));					
				} else {
					// check products are in the same location as the 1st product
					CustomerOrderProduct product = getCustomerOrderProductList().get(0);

					if (product.getInventoryLocation().compareTo(inventory.getInventoryLocation())==0) {
						getSelectedInventoryList().add(inventory);
						
						// add to the list
						getCustomerOrderProductList().add(
								inventoryToCustomerOrderProduct(inventory));											
					} else {
						throw new Exception("Lokasi inventory tidak sama.  "
								+ "Pilih Inventory selanjutnya dengan lokasi yang sama dengan yang sebelumnya.");
					}
				}
				// set to productCount
				productCount = getCustomerOrderProductList().size();
				
				// load the list into listbox
				loadAllCustomerOrderProduct();
			}
		});
		
		inventoryDialogWin.doModal();
	}
	
	protected CustomerOrderProduct inventoryToCustomerOrderProduct(Inventory inventory) {
		CustomerOrderProduct product = new CustomerOrderProduct();
		product.setMarking(inventory.getMarking());
		product.setInventoryCode(inventory.getInventoryCode());
		// NOT defined in Inventory -- must define when using -- to avoid ambiguity
		String description = getFormatedFloatLocal(inventory.getThickness())+" x "+ 
				// String.valueOf(inventory.getThickness())+" x "+
				getFormatedFloatLocal(inventory.getWidth())+" x "+
				// String.valueOf(inventory.getWidth())+" x "+
				(inventory.getLength().compareTo(BigDecimal.ZERO)==0 ?
						"Coil" :
						getFormatedFloatLocal(inventory.getLength()));
						// getFormatedDecimal(inventory.getLength()));
		
		// -- needed for weight calculation
		product.setThickness(inventory.getThickness());
		product.setWidth(inventory.getWidth());
		product.setLength(inventory.getLength());
		// -- end of needed...
		product.setDescription(description);
		product.setInventoryPacking(inventory.getInventoryPacking());
		product.setWeightQuantity(inventory.getWeightQuantity());
		product.setSheetQuantity(inventory.getSheetQuantity());
		product.setByKg(inventory.getInventoryPacking().compareTo(InventoryPacking.coil)==0);
		product.setSellingPrice(BigDecimal.ZERO);
		product.setInventoryLocation(inventory.getInventoryLocation());
		product.setPpn(usePpn.isChecked());
		
		// set to Inventory - join table - customer_order_product_join_inventory
		product.setInventory(inventory);
		
		return product;
	}

/*
 *  THIS IS WRONG !!! When you remove the customerOrderProduct, you must update
 *  the inventory as well.
 *  
 * 	public void onClick$removeInventoryButton(Event event) throws Exception {
		// init selectedInventoryList
		setSelectedInventoryList(new ArrayList<Inventory>());

		// init the product list
		setCustomerOrderProductList(
					new ArrayList<CustomerOrderProduct>());
	
		loadAllCustomerOrderProduct();
	
		// reset accSubTotal
		accSubTotal = BigDecimal.ZERO;
		
		// reset productCount
		productCount = 0;
		
		updateProductTotalInfo();
	}
*/		
	public void onClick$checkButton(Event event) throws Exception {
		if (customerTextbox.getValue().isEmpty()) {
			throw new Exception("Nama Customer Harus Diisi sebelum Disimpan.");
		}

		if (pembayaranCombobox.getSelectedItem()==null) {
			throw new Exception("Pembayaran Harus Diisi sebelum Disimpan.");
		}
		
		if ((pembayaranCombobox.getSelectedItem().getValue().equals(PaymentType.bank)) ||
				(pembayaranCombobox.getSelectedItem().getValue().equals(PaymentType.giro))) {
			
			if (jumlahHariIntbox.getValue()==0) {
				throw new Exception("Jumlah Hari Harus Diisi sebelum Disimpan.");
			}
			
		}
		
		if (salesPersonCombobox.getSelectedItem()==null) {
			throw new Exception("Sales Harus Diisi sebelum Disimpan.");
		}
		
		if (getCustomerOrderProductList().isEmpty()) {
			throw new Exception("Produk Harus Diisi sebelum Disimpan.");
		}
		
		accSubTotal = BigDecimal.ZERO;

		for (int i = 0; i < productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);
			
			CustomerOrderProduct product = item.getValue();
	
			if (product.getSellingSubTotal()==null) {
				throw new Exception("Klik Edit di setiap item untuk mendapatkan subtotal.");
			}
			
			accSubTotal = accSubTotal.add(product.getSellingSubTotal().setScale(0, RoundingMode.HALF_EVEN));			
		}
		
		subTotalTextbox.setValue(toLocalFormat(accSubTotal));
		subTotalTextbox.setAttribute("subTotal", accSubTotal);

		log.info("SubTotal: "+toLocalFormat(accSubTotal));
		log.info("Per 01 April 2022 PPN is 11%: "+PPN);
		
		BigDecimal ppnValue = usePpn.isChecked() ?
				accSubTotal.multiply(PPN) : BigDecimal.ZERO;
				// accSubTotal.multiply(new BigDecimal(0.1)) : BigDecimal.ZERO;	
		ppnTextbox.setValue(toLocalFormat(ppnValue));
		ppnTextbox.setAttribute("ppnTotal", ppnValue.setScale(0, RoundingMode.HALF_EVEN));
		
		log.info("PPN: "+toLocalFormat(ppnValue));
		// System.out.println(ppnValue.setScale(0, RoundingMode.HALF_EVEN));
		
		BigDecimal totalValue = accSubTotal.add(ppnValue);
		totalTextbox.setValue(toLocalFormat(totalValue));
		totalTextbox.setAttribute("orderTotal", totalValue.setScale(0, RoundingMode.HALF_EVEN));
		
		log.info("Total: "+toLocalFormat(totalValue));
		// System.out.println(totalValue.setScale(0, RoundingMode.HALF_EVEN));
		
		totalOrderTextbox.setValue(toLocalFormat(totalValue));
		
		for (int i = 0; i < productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);
			
			Button editButton = (Button) item.getChildren().get(9).getFirstChild();
			editButton.setDisabled(true);
		}
		
		usePpn.setDisabled(true);
		addInventoryButton.setDisabled(true);
		// removeInventoryButton.setDisabled(true);
		
		saveButton.setDisabled(false);
		checkButton.setDisabled(true);
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		
		if (getPageMode().compareTo(PageMode.EDIT)==0) {
			// edit
			log.info("Modifying existing CustomerOrder...");
			
			CustomerOrder existingOrder = getCustomerOrderData().getCustomerOrder();
			
			existingOrder.setOrderDate(orderDatebox.getValue());
			existingOrder.setPaymentType(pembayaranCombobox.getSelectedItem().getValue());
			existingOrder.setCreditDay(jumlahHariIntbox.getValue());
			existingOrder.setUsePpn(usePpn.isChecked());
			existingOrder.setTotalPpn((BigDecimal) ppnTextbox.getAttribute("ppnTotal"));
			existingOrder.setTotalOrder((BigDecimal) totalTextbox.getAttribute("orderTotal"));			
			existingOrder.setNote(noteTextbox.getValue());
			existingOrder.setCustomer((Customer) customerTextbox.getAttribute("customer"));
			EmployeeCommissions employeeCommissionsByProxy = 
					getEmployeeCommissionsByProxy(existingOrder.getId());
			existingOrder.setEmployeeCommissions(updateEmployeeReceivingCommission(
					employeeCommissionsByProxy,	salesPersonCombobox.getSelectedItem().getValue()));
			existingOrder.setEmployeeSales(salesPersonCombobox.getSelectedItem().getValue());
			// modified datetime
			existingOrder.setModifiedDate(asDateTime(getLocalDateTime()));
			
			// update the product as well
			existingOrder.setCustomerOrderProducts(getCustomerOrderProductList());
			
			// send event
			Events.sendEvent(Events.ON_OK, customerOrderDialogWin, existingOrder);
		} else { 
			// new
			log.info("Creating new CustomerOrder...");
			
			CustomerOrder customerOrder = new CustomerOrder();
			
			customerOrder.setDocumentSerialNumber(usePpn.isChecked() ?
					createDocumentSerialNumber(DEFAULT_DOCUMENT_TYPE, orderDatebox.getValue()):
						createDocumentSerialNumber(NON_PPN_ORDER, orderDatebox.getValue()));
			customerOrder.setOrderDate(orderDatebox.getValue());
			customerOrder.setPaymentType(pembayaranCombobox.getSelectedItem().getValue());
			customerOrder.setCreditDay(jumlahHariIntbox.getValue());
			customerOrder.setUsePpn(usePpn.isChecked());
			customerOrder.setTotalPpn((BigDecimal) ppnTextbox.getAttribute("ppnTotal"));				
			customerOrder.setTotalOrder((BigDecimal) totalTextbox.getAttribute("orderTotal"));
			customerOrder.setNote(noteTextbox.getValue());
			customerOrder.setCustomer((Customer) customerTextbox.getAttribute("customer"));
			customerOrder.setEmployeeCommissions(getEmployeeReceivingCommission(
					new EmployeeCommissions(), salesPersonCombobox.getSelectedItem().getValue(), customerOrder));
			customerOrder.setEmployeeSales(salesPersonCombobox.getSelectedItem().getValue());
			// createDate
			customerOrder.setCreateDate(asDate(getLocalDate()));
			customerOrder.setModifiedDate(asDateTime(getLocalDateTime()));
			customerOrder.setCheckDate(asDateTime(getLocalDateTime()));
			// assign the product inventory
			customerOrder.setCustomerOrderProducts(getCustomerOrderProductList());
			customerOrder.setAmountPaid(BigDecimal.ZERO);
			customerOrder.setOrderStatus(STATUS);
			// send event
			Events.sendEvent(Events.ON_OK, customerOrderDialogWin, customerOrder);
		}
		
		customerOrderDialogWin.detach();
	}
	
	private EmployeeCommissions updateEmployeeReceivingCommission(EmployeeCommissions employeeCommissions, Employee employee) {
		BigDecimal commissionAmount = BigDecimal.ZERO;
		
		employeeCommissions.setCommissionAmount(commissionAmount);
		employeeCommissions.setCommissionPercent(employee.getCommissionPercent());
		employeeCommissions.setEmployee(employee);
		
		return employeeCommissions;
	}

	private EmployeeCommissions getEmployeeReceivingCommission(EmployeeCommissions employeeCommissions, Employee employee, CustomerOrder customerOrder) throws Exception {
		BigDecimal commissionAmount = BigDecimal.ZERO;
		
		employeeCommissions.setCommissionAmount(commissionAmount);
		employeeCommissions.setCommissionPercent(employee.getCommissionPercent());
		employeeCommissions.setCustomerOrder(customerOrder);			
		employeeCommissions.setEmployee(employee);
		employeeCommissions.setCommissionStatus(STATUS);
		
		return employeeCommissions;
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

	private void setReadOnly(PageMode pageMode) {
		
		switch (pageMode) {
		case EDIT:
			// disable --> false
			orderDatebox.setDisabled(false);
			productListbox.setDisabled(false);
			pembayaranCombobox.setDisabled(false);
			jumlahHariIntbox.setDisabled(false);
			customerButton.setDisabled(false);
			usePpn.setDisabled(false);
			noteTextbox.setDisabled(false);
			salesPersonCombobox.setDisabled(false);
			addInventoryButton.setDisabled(false);
			// removeInventoryButton.setDisabled(false);
			
			productListbox.setDisabled(false);
			
			// saveButton - buttonSave --> disable --> false
			saveButton.setVisible(true);
			// cancelButton - buttonCancel --> label --> Cancel
			cancelButton.setLabel("Cancel");
			//
			checkButton.setVisible(true);

			break;
		case NEW:
			orderDatebox.setDisabled(false);
			productListbox.setDisabled(false);
			pembayaranCombobox.setDisabled(false);
			jumlahHariIntbox.setDisabled(false);
			customerButton.setDisabled(false);
			usePpn.setDisabled(false);
			noteTextbox.setDisabled(false);
			salesPersonCombobox.setDisabled(false);
			addInventoryButton.setDisabled(false);
			// removeInventoryButton.setDisabled(false);
			
			productListbox.setDisabled(false);
			
			// saveButton - buttonSave --> disable --> false
			saveButton.setVisible(true);
			// cancelButton - buttonCancel --> label --> Cancel
			cancelButton.setLabel("Cancel");
			//
			checkButton.setVisible(true);
			
			break;
		case VIEW:
			// disable --> true
			orderDatebox.setDisabled(true);
			productListbox.setDisabled(true);
			pembayaranCombobox.setDisabled(true);
			jumlahHariIntbox.setDisabled(true);
			customerButton.setDisabled(true);
			usePpn.setDisabled(true);
			noteTextbox.setDisabled(true);
			salesPersonCombobox.setDisabled(true);
			addInventoryButton.setDisabled(true);
			// removeInventoryButton.setDisabled(true);
			
			productListbox.setDisabled(true);
			
			// saveButton - buttonSave --> disable --> true
			saveButton.setVisible(false);
			// cancelButton - buttonCancel --> label --> Tutup
			cancelButton.setLabel("Tutup");
			//
			checkButton.setVisible(false);

			break;
		default:
			break;
		}		
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		customerOrderDialogWin.detach();
	}

	protected CustomerOrderProduct getInventoryCodeByProxy(long id) throws Exception {
		CustomerOrderProduct orderProduct = getCustomerOrderProductDao().findInventoryCodeByProxy(id);
		
		return orderProduct;
	}
	
	
	private Customer getCustomerByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderData().getCustomerOrderDao().findCustomerByProxy(id);
			
		return customerOrder.getCustomer();
	}	
		
	private Employee getEmployeeSalesByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderData().getCustomerOrderDao().findEmployeeSalesByProxy(id);
		
		return customerOrder.getEmployeeSales();
	}
	
	private EmployeeCommissions getEmployeeCommissionsByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderData().getCustomerOrderDao().findEmployeeCommissionsByProxy(id);
		
		return customerOrder.getEmployeeCommissions();
	}
	
	public CustomerOrderData getCustomerOrderData() {
		return customerOrderData;
	}

	public void setCustomerOrderData(CustomerOrderData customerOrderData) {
		this.customerOrderData = customerOrderData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public List<CustomerOrderProduct> getCustomerOrderProductList() {
		return customerOrderProductList;
	}

	public void setCustomerOrderProductList(List<CustomerOrderProduct> customerOrderProductList) {
		this.customerOrderProductList = customerOrderProductList;
	}

	public BigDecimal getTotalOrderValue() {
		return totalOrderValue;
	}

	public void setTotalOrderValue(BigDecimal totalOrderValue) {
		this.totalOrderValue = totalOrderValue;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public EmployeeDao getEmployeeDao() {
		return employeeDao;
	}

	public void setEmployeeDao(EmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}

	/**
	 * @return the customerOrderProductDao
	 */
	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	/**
	 * @param customerOrderProductDao the customerOrderProductDao to set
	 */
	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}

	/**
	 * @return the selectedInventoryList
	 */
	public List<Inventory> getSelectedInventoryList() {
		return selectedInventoryList;
	}

	/**
	 * @param selectedInventoryList the selectedInventoryList to set
	 */
	public void setSelectedInventoryList(List<Inventory> selectedInventoryList) {
		this.selectedInventoryList = selectedInventoryList;
	}

}
