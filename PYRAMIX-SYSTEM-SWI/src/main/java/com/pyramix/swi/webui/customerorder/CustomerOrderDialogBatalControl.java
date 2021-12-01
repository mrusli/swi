package com.pyramix.swi.webui.customerorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.domain.voucher.VoucherSalesDebitCredit;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.employeecommissions.dao.EmployeeCommissionsDao;
import com.pyramix.swi.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;
import com.pyramix.swi.persistence.receivableactivity.dao.CustomerReceivableActivityDao;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherSalesDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class CustomerOrderDialogBatalControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8109656303936197157L;

	private CustomerOrderDao customerOrderDao;
	private EmployeeCommissionsDao employeeCommissionsDao;
	private SuratJalanDao suratJalanDao;
	private VoucherSalesDao voucherSalesDao;
	private CustomerReceivableDao customerReceivableDao;
	private CustomerReceivableActivityDao customerReceivableActivityDao;
	private GeneralLedgerDao generalLedgerDao;
	
	private Window customerOrderDialogBatalWin;
	private Tabbox documentBatalTabbox;
	private Datebox batalDatebox;
	
	private CustomerOrder customerOrder;
	private Tabs tabs 			= new Tabs();
	private Tabpanels tabpanels = new Tabpanels();
	
	private CustomerOrder 		customerOrderEmployeeCommissionsByProxy 	= null;
	private CustomerOrder 		customerOrderSuratJalanByProxy 				= null;
	private DeliveryOrder 		deliveryOrderByProxy 						= null;
	private Faktur				fakturByProxy								= null;
	private CustomerOrder 		customerOrderVoucherSalesByProxy 			= null;
	private CustomerOrder 		customerOrderReceivableByProxy 				= null;
	private VoucherSales		voucherSalesGeneralLedgerByProxy			= null;
	
	private List<CustomerReceivableActivity> receivableActivityList = null;
	private Long customerReceivableActivity_id = 0L;
	
	private final String PEMBATALAN_TEXT = "Pembatalan";
	private static final DocumentStatus STATUS = DocumentStatus.BATAL;
	private static final Logger log = Logger.getLogger(CustomerOrderDialogBatalControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerOrder(
				(CustomerOrder) arg.get("customerOrder"));
	}

	public void onCreate$customerOrderDialogBatalWin(Event event) throws Exception {
		customerOrderDialogBatalWin.setTitle("Pembatalan Customer Order");
		
		// set current date for the batal date
		batalDatebox.setValue(asDate(getLocalDate()));
		batalDatebox.setLocale(getLocale());
		batalDatebox.setFormat(getLongDateFormat());
		
		// tabs and tabpanels assigned to Tabbox
		tabs.setParent(documentBatalTabbox);
		tabpanels.setParent(documentBatalTabbox);

		// customerOrder
		customerOrderTab();

		// employeeCommissions
		employeeCommissionsTab();

		// customerOrderProducts
		customerOrderProductsTab();

		// suratJalan / deliveryOrder / faktur
		suratJalanTab();
		
		// voucherSales
		voucherSalesTab();
		
		// receivables
		accountReceivableActivitiesTab();
	}

	private void customerOrderTab() {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;
		
		documentTab = new Tab();
		documentTab.setLabel("Customer-Order");
		documentTab.setParent(tabs);
		
		documentTabPanel = new Tabpanel();
		documentTabPanel.setParent(tabpanels);
		
		getCustomerOrder().setOrderStatus(STATUS);
		
		Label customerOrderLabel = new Label();
		customerOrderLabel.setValue(getCustomerOrder().toString());
		customerOrderLabel.setParent(documentTabPanel);
	}

	private void employeeCommissionsTab() throws Exception {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;
	
		if (getCustomerOrder().getEmployeeCommissions()==null) {
			// do nothing -- commissions not created
		} else {			
			customerOrderEmployeeCommissionsByProxy =
					getCustomerOrderDao().findEmployeeCommissionsByProxy(getCustomerOrder().getId());
			customerOrderEmployeeCommissionsByProxy.getEmployeeCommissions().setCommissionStatus(STATUS);
						
			documentTab = new Tab();
			documentTab.setLabel("Komisi");
			documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			documentTabPanel.setParent(tabpanels);
			
			Label employeeCommissionsLabel = new Label();
			employeeCommissionsLabel.setValue(customerOrderEmployeeCommissionsByProxy.getEmployeeCommissions().toString());
			employeeCommissionsLabel.setParent(documentTabPanel);
		}
	}

	private void customerOrderProductsTab() {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;
		
		if (customerOrder.getCustomerOrderProducts()==null) {
			// do nothing -- if this happens --> you'r in big trouble!!!
		} else {
			documentTab = new Tab();
			documentTab.setLabel("Inventory");
			documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			documentTabPanel.setParent(tabpanels);
						
			List<CustomerOrderProduct> productList =
					customerOrder.getCustomerOrderProducts();

			for (CustomerOrderProduct product : productList) {
				CustomerOrderProduct customerOrderProductInventoryByProxy =
						getCustomerOrderDao().findCustomerOrderProductInventoryByProxy(product.getId());

				Inventory inventory = customerOrderProductInventoryByProxy.getInventory();
				
				Label inventoryBeforeLabel = new Label();
				inventoryBeforeLabel.setValue(inventory.toString());
				inventoryBeforeLabel.setParent(documentTabPanel);
			}
			
			List<Inventory> updatedInventoryList =
					new ArrayList<Inventory>();
			
			for (CustomerOrderProduct product : productList) {
				CustomerOrderProduct customerOrderProductInventoryByProxy =
						getCustomerOrderDao().findCustomerOrderProductInventoryByProxy(product.getId());
				
				Inventory inventory = customerOrderProductInventoryByProxy.getInventory();
				
				if (product.getInventoryPacking().equals(InventoryPacking.lembaran)) {
					// update shtQty
					updateLembaranShtQtyInventory(inventory, product);
					// update weightQty
					updateLembaranWeightQtyInventory(inventory, product);
				} else {
					inventory.setInventoryStatus(InventoryStatus.ready);
					inventory.setNote("");
				}
				
				updatedInventoryList.add(inventory);
			}
			
			for (int i=0; i<productList.size(); i++) {
				CustomerOrderProduct product = productList.get(i);

				product.setInventory(updatedInventoryList.get(i));
			}
			
			getCustomerOrder().setCustomerOrderProducts(productList);
			
			Label inventoryLabel = new Label();
			inventoryLabel.setValue(customerOrder.getCustomerOrderProducts().toString());
			inventoryLabel.setParent(documentTabPanel);
		}
	}

	private void updateLembaranShtQtyInventory(Inventory inventory, CustomerOrderProduct product) {
		int shtQtyInventory = inventory.getSheetQuantity();
		int shtQtyProduct = product.getSheetQuantity();
		
		inventory.setSheetQuantity(shtQtyProduct+shtQtyInventory);
		
	}

	private void updateLembaranWeightQtyInventory(Inventory inventory, CustomerOrderProduct product) {
		BigDecimal weightQtyInventory = inventory.getWeightQuantity();
		BigDecimal weightQtyProduct = product.getWeightQuantity();
				
		inventory.setWeightQuantity(weightQtyProduct.add(weightQtyInventory));
	}

	private void suratJalanTab() throws Exception {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;
		
		// suratJalan
		if (customerOrder.getSuratJalan()==null) {
			// do nothing -- suratJalan not created
		} else {
			// suratJalanByProxy =
			//		getSuratJalanByProxy(getCustomerOrder().getId());
			// suratJalanByProxy.setSuratJalanStatus(STATUS);
			
			// SuratJalan suratJalan = customerOrderSuratJalanByProxy.getSuratJalan();
			// System.out.println(suratJalan);

			customerOrderSuratJalanByProxy =
					getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
			customerOrderSuratJalanByProxy.getSuratJalan().setSuratJalanStatus(STATUS);
			
			documentTab = new Tab();
			documentTab.setLabel("Surat-Jalan");
			documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			documentTabPanel.setParent(tabpanels);

			Label suratJalanLabel = new Label();
			suratJalanLabel.setValue(customerOrderSuratJalanByProxy.getSuratJalan().toString());
			suratJalanLabel.setParent(documentTabPanel);
			
			// check for DeliveryOrder
			if (customerOrderSuratJalanByProxy.getSuratJalan().getDeliveryOrder()==null) {
				// do nothing -- deliveryOrder not created
			} else {				
				deliveryOrderByProxy =
						getDeliveryOrderByProxy(customerOrderSuratJalanByProxy.getSuratJalan().getId());
				deliveryOrderByProxy.setDeliveryOrderStatus(STATUS);
				
				documentTab = new Tab();
				documentTab.setLabel("Delivery-Order");
				documentTab.setParent(tabs);
				
				documentTabPanel = new Tabpanel();
				documentTabPanel.setParent(tabpanels);
				
				Label deliveryOrderLabel = new Label();
				deliveryOrderLabel.setValue(deliveryOrderByProxy.toString());
				deliveryOrderLabel.setParent(documentTabPanel);
			}
			
			// check for Faktur
			if (customerOrderSuratJalanByProxy.getSuratJalan().getFaktur()==null) {
				// do nothing -- faktur not created
			} else {
				// SuratJalan suratJalanFakturByProxy =
				//	getSuratJalanDao().findFakturByProxy(suratJalan.getId());
				// System.out.println(suratJalanFakturByProxy.getFaktur());

				fakturByProxy =
						getFakturByProxy(customerOrderSuratJalanByProxy.getSuratJalan().getId());
				fakturByProxy.setFakturStatus(STATUS);
				
				documentTab = new Tab();
				documentTab.setLabel("Faktur");
				documentTab.setParent(tabs);
				
				documentTabPanel = new Tabpanel();
				documentTabPanel.setParent(tabpanels);

				Label fakturLabel = new Label();
				fakturLabel.setValue(fakturByProxy.toString());
				fakturLabel.setParent(documentTabPanel);
			}
		}
	}

	/**
	 * voucherSalesTab
	 * 
	 * @throws Exception 
	 */
	private void voucherSalesTab() throws Exception {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;

		if (getCustomerOrder().getVoucherSales() == null) {
			// do nothing -- voucherSales not created
		} else {
			customerOrderVoucherSalesByProxy = 
					getCustomerOrderDao().findVoucherSalesByProxy(getCustomerOrder().getId());
			customerOrderVoucherSalesByProxy.getVoucherSales().setVoucherStatus(STATUS);
			
			List<VoucherSalesDebitCredit> voucherSalesDebitCredits =
					customerOrderVoucherSalesByProxy.getVoucherSales().getVoucherSalesDebitCredits();
			
			VoucherSalesDebitCredit creditAcc 	= null;
			VoucherSalesDebitCredit debitAcc 	= null;
			
			List<VoucherSalesDebitCredit> voucherSalesReverseDebitCredit = 
					new ArrayList<VoucherSalesDebitCredit>();
			
			for (VoucherSalesDebitCredit voucherDbCr : voucherSalesDebitCredits) {
				if (voucherDbCr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
					// create a credit account
					creditAcc = new VoucherSalesDebitCredit();
					creditAcc.setMasterCoa(voucherDbCr.getMasterCoa());
					creditAcc.setDbcrDescription(PEMBATALAN_TEXT+" - CustomerOrder No.:"+
							getCustomerOrder().getDocumentSerialNumber().getSerialComp());
					creditAcc.setDebitAmount(BigDecimal.ZERO);
					creditAcc.setCreditAmount(voucherDbCr.getDebitAmount());
					
					voucherSalesReverseDebitCredit.add(creditAcc);
				} else {
					// create the debit account
					debitAcc = new VoucherSalesDebitCredit();
					debitAcc.setMasterCoa(voucherDbCr.getMasterCoa());
					debitAcc.setDbcrDescription(PEMBATALAN_TEXT+" - CustomerOrder No.:"+
							getCustomerOrder().getDocumentSerialNumber().getSerialComp());
					debitAcc.setDebitAmount(voucherDbCr.getCreditAmount());
					debitAcc.setCreditAmount(BigDecimal.ZERO);
					
					voucherSalesReverseDebitCredit.add(debitAcc);
				}
			}
			
			voucherSalesDebitCredits.addAll(voucherSalesReverseDebitCredit);
			customerOrderVoucherSalesByProxy.getVoucherSales().setVoucherSalesDebitCredits(voucherSalesDebitCredits);

			// get the GL
			VoucherSales voucherSales =
					customerOrderVoucherSalesByProxy.getVoucherSales();
			voucherSalesGeneralLedgerByProxy = getVoucherSalesDao().findGeneralLedgerByProxy(voucherSales.getId());
			
			// create GL from voucherSalesDbCr
			for (VoucherSalesDebitCredit dbcr : voucherSalesDebitCredits) {
				
				if (dbcr.getDbcrDescription().startsWith(PEMBATALAN_TEXT)) {
					GeneralLedger gl = new GeneralLedger();
					
					gl.setMasterCoa(dbcr.getMasterCoa());
					// 09/08/2021 - posting date according to batalDatebox
					// gl.setPostingDate(asDate(getLocalDate()));
					gl.setPostingDate(batalDatebox.getValue());
					gl.setPostingVoucherNumber(voucherSales.getPostingVoucherNumber());
					gl.setCreditAmount(dbcr.getCreditAmount());
					gl.setDebitAmount(dbcr.getDebitAmount());
					gl.setDbcrDescription(dbcr.getDbcrDescription());
					gl.setTransactionDescription(voucherSales.getTransactionDescription()+" - BATAL");
					gl.setDocumentRef(voucherSales.getDocumentRef());
					gl.setTransactionDate(voucherSales.getTransactionDate());
					gl.setVoucherType(voucherSales.getVoucherType());
					gl.setVoucherNumber(voucherSales.getVoucherNumber());
					
					// add
					voucherSalesGeneralLedgerByProxy.getGeneralLedgers().add(gl);					
				}
			}
			
			documentTab = new Tab();
			documentTab.setLabel("Voucher-Sales");
			documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			documentTabPanel.setParent(tabpanels);

			Label inventoryLabel = new Label();
			inventoryLabel.setValue(customerOrderVoucherSalesByProxy.getVoucherSales().toString()+" "+
					voucherSalesGeneralLedgerByProxy.getGeneralLedgers().toString());
			inventoryLabel.setParent(documentTabPanel);			
		}
	}	

	/**
	 * customerReceivable Tab
	 * 
	 * @throws Exception 
	 * 
	 */
	private void accountReceivableActivitiesTab() throws Exception {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;

		if (getCustomerOrder().getVoucherSales() == null) {
			// -- do nothing
		} else if (getCustomerOrder().getPaymentType().compareTo(PaymentType.tunai)==0) {
			// -- do nothing
		} else {
			customerOrderReceivableByProxy =
					getCustomerOrderDao().findCustomerReceivableByProxy(getCustomerOrder().getId());
			
			receivableActivityList =
					customerOrderReceivableByProxy.getCustomerReceivable().getCustomerReceivableActivities();
			
			documentTab = new Tab();
			documentTab.setLabel("Piutang");
			documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			documentTabPanel.setParent(tabpanels);

			Label accountReceivableLabel;

			for (CustomerReceivableActivity receivableActivity : receivableActivityList) {

				if (receivableActivity.getCustomerOrderId()==getCustomerOrder().getId()) {
					receivableActivity.setAmountSales(BigDecimal.ZERO);
					receivableActivity.setAmountSalesPpn(BigDecimal.ZERO);
					receivableActivity.setReceivableStatus(STATUS);
					
					accountReceivableLabel = new Label();
					accountReceivableLabel.setValue(receivableActivity.toString());
					accountReceivableLabel.setParent(documentTabPanel);
					
					customerReceivableActivity_id = receivableActivity.getId();
					
					log.info("Found: "+receivableActivity.toString());

					break;
				}
			}
		}
	}

	public void onClick$saveButton(Event event) throws Exception {
		CustomerOrder customerOrder = getModifiedCustomerOrder();
		
		Events.sendEvent(Events.ON_OK, customerOrderDialogBatalWin, customerOrder);
		
		customerOrderDialogBatalWin.detach();
	}
	
	private CustomerOrder getModifiedCustomerOrder() throws Exception {
		log.info("Updating database...");
		
		// update CustomerOrder
		getCustomerOrderDao().update(getCustomerOrder());
		log.info("CustomerOrder after update : id="+getCustomerOrder().getId()+", status="+
				getCustomerOrder().getOrderStatus().toString());
		
		if (customerOrderEmployeeCommissionsByProxy!=null) {
			getEmployeeCommissionsDao().update(
					customerOrderEmployeeCommissionsByProxy.getEmployeeCommissions());
			
			log.info("EmployeeCommissions after update : id="+customerOrderEmployeeCommissionsByProxy.getEmployeeCommissions().getId()+", status="+
					customerOrderEmployeeCommissionsByProxy.getEmployeeCommissions().getCommissionStatus().toString());
		}
				
		if (customerOrderSuratJalanByProxy!=null) {
			getSuratJalanDao().update(
					customerOrderSuratJalanByProxy.getSuratJalan());
			
			log.info("SuratJalan after update : id="+customerOrderSuratJalanByProxy.getSuratJalan().getId()+", status="+
					customerOrderSuratJalanByProxy.getSuratJalan().getSuratJalanStatus().toString());
		}
		
		if (customerOrderVoucherSalesByProxy!=null) {
			getVoucherSalesDao().update(
					customerOrderVoucherSalesByProxy.getVoucherSales());
			
			log.info("VoucherSales after update : id"+customerOrderVoucherSalesByProxy.getVoucherSales().getId()+", status="+
					customerOrderVoucherSalesByProxy.getVoucherSales().getVoucherStatus().toString());
		}
		
		if (voucherSalesGeneralLedgerByProxy!=null) {
			getVoucherSalesDao().update(
					voucherSalesGeneralLedgerByProxy);
			
			// log.info("GeneralLedger after update : "+voucherSalesGeneralLedgerByProxy.getGeneralLedgers().toString());
		}
		
		if (customerOrderReceivableByProxy!=null) {			
			CustomerReceivable customerReceivable = customerOrderReceivableByProxy.getCustomerReceivable();
			for (CustomerReceivableActivity customerReceivableActivity : customerReceivable.getCustomerReceivableActivities()) {
				if (customerReceivableActivity.getId().compareTo(customerReceivableActivity_id)==0) {
					log.info("Bingo [CustomerReceivableActivity]: id="+customerReceivableActivity.getId()+" : "
							+customerReceivableActivity.getReceivableStatus().toString());
					
					if (customerReceivableActivity.getReceivableStatus().compareTo(DocumentStatus.NORMAL)==0) {
						// it's not BATAL, then user must update it manually
						log.info("ReceivableActivity NOT set to BATAL");
						
						Map<String, CustomerReceivableActivity> arg = 
								Collections.singletonMap("ReceivableActivity", customerReceivableActivity);
						Window custRecvActivityDialogBatalWin = 
								(Window) Executions.createComponents("/customerorder/CustomerOrderDialogBatalReceivable.zul", null, arg);
						
						custRecvActivityDialogBatalWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								CustomerReceivableActivity receivableActivity = 
										(CustomerReceivableActivity) event.getData();
								
								log.info("User Modified: "+receivableActivity);
								
								getCustomerReceivableActivityDao().update(receivableActivity);
								
								CustomerReceivableActivity afterUpdateRecvActivity =
										getCustomerReceivableActivityDao().findCustomerReceivableActivityById(receivableActivity.getId());
								
								log.info(afterUpdateRecvActivity.toString());
							}
							
						});
						
						custRecvActivityDialogBatalWin.doModal();
					} else {
						// it's BATAL
						log.info("ReceivableActivity set to BATAL");
						// update
						getCustomerReceivableActivityDao().update(customerReceivableActivity);
						
						CustomerReceivableActivity afterUpdateRecvActivity =
								getCustomerReceivableActivityDao().findCustomerReceivableActivityById(customerReceivableActivity.getId());
						
						log.info(afterUpdateRecvActivity.toString());						
					}
					
					break;
				}
			}
		}
				
		return getCustomerOrder();
	}

	public void onClick$cancelButton(Event event) throws Exception {
		// 09/08/2021 - must reset back to NORMAL
		// -- otherwise this customerOrder is set to BATAL
		getCustomerOrder().setOrderStatus(DocumentStatus.NORMAL);
		
		customerOrderDialogBatalWin.detach();
	}

	@SuppressWarnings("unused")
	private EmployeeCommissions getEmployeeCommissionsByProxy(long id) throws Exception {
		CustomerOrder customerOrder =
				getCustomerOrderDao().findEmployeeCommissionsByProxy(id);

		return customerOrder.getEmployeeCommissions();
	}	
	
	@SuppressWarnings("unused")
	private SuratJalan getSuratJalanByProxy(long id) throws Exception {
		CustomerOrder customerOrder =
				getCustomerOrderDao().findSuratJalanByProxy(id);

		return customerOrder.getSuratJalan();
	}

	private DeliveryOrder getDeliveryOrderByProxy(long id) throws Exception {
		SuratJalan suratJalan =
				getSuratJalanDao().findDeliveryOrderByProxy(id);

		return suratJalan.getDeliveryOrder();
	}

	private Faktur getFakturByProxy(long id) throws Exception {
		SuratJalan suratJalan =
				getSuratJalanDao().findFakturByProxy(id);

		return suratJalan.getFaktur();
	}	
	
	@SuppressWarnings("unused")
	private VoucherSales getVoucherSalesByProxy(long id) throws Exception {
		CustomerOrder customerOrder = 
				getCustomerOrderDao().findVoucherSalesByProxy(id);

		return customerOrder.getVoucherSales();
	}
	
	@SuppressWarnings("unused")
	private CustomerReceivable getCustomerReceivableByProxy(long id) throws Exception {
		CustomerOrder customerOrder =
				getCustomerOrderDao().findCustomerReceivableByProxy(id);

		return customerOrder.getCustomerReceivable();
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public SuratJalanDao getSuratJalanDao() {
		return suratJalanDao;
	}

	public void setSuratJalanDao(SuratJalanDao suratJalanDao) {
		this.suratJalanDao = suratJalanDao;
	}

	public CustomerReceivableDao getCustomerReceivableDao() {
		return customerReceivableDao;
	}

	public void setCustomerReceivableDao(CustomerReceivableDao customerReceivableDao) {
		this.customerReceivableDao = customerReceivableDao;
	}

	public EmployeeCommissionsDao getEmployeeCommissionsDao() {
		return employeeCommissionsDao;
	}

	public void setEmployeeCommissionsDao(EmployeeCommissionsDao employeeCommissionsDao) {
		this.employeeCommissionsDao = employeeCommissionsDao;
	}

	public VoucherSalesDao getVoucherSalesDao() {
		return voucherSalesDao;
	}

	public void setVoucherSalesDao(VoucherSalesDao voucherSalesDao) {
		this.voucherSalesDao = voucherSalesDao;
	}

	public CustomerReceivableActivityDao getCustomerReceivableActivityDao() {
		return customerReceivableActivityDao;
	}

	public void setCustomerReceivableActivityDao(CustomerReceivableActivityDao customerReceivableActivityDao) {
		this.customerReceivableActivityDao = customerReceivableActivityDao;
	}

	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}
}
