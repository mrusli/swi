package com.pyramix.swi.webui.settlement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherGiroReceiptDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.domain.voucher.VoucherPaymentDebitCredit;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.persistence.voucher.dao.GiroDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherGiroReceiptDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherPaymentDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SuppressedException;

public class SettlementDialogBatalControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -555508437082374957L;

	private SettlementDao settlementDao;
	private VoucherGiroReceiptDao voucherGiroReceiptDao;
	private GiroDao giroDao;
	private VoucherPaymentDao voucherPaymentDao;
	
	private Window settlementDialogBatalWin;
	private Tabbox settlementBatalTabbox;
	private Tab settlementTab, voucherTab, giroTab, giroPaymentTab, glTab, glGiroPaymentTab, 
		piutangTab;
	private Grid giroReceiptGrid, voucherPaymentGrid;
	private Combobox statusSettlementCombobox, statusVoucherCombobox, statusGiroCombobox, 
		statusGiroPaymentCombobox;
	private Button saveBatalButton, saveRevertButton;
	// private Tabs tabs 			= new Tabs();
	// private Tabpanels tabpanels = new Tabpanels();
	
	private Settlement settlement;
	private Giro giro;
	
	private VoucherGiroReceipt	voucherGiroReceiptByProxy 		= null;
	private VoucherPayment 		giroVoucherPaymentByProxy		= null;
	private VoucherPayment		settlementVoucherPaymentByProxy	= null;

	private static final DocumentStatus STATUS = DocumentStatus.BATAL; 
	private static final Logger log = Logger.getLogger(SettlementDialogBatalControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setSettlement(
				(Settlement) arg.get("settlement"));
	}
	
	public void onCreate$settlementDialogBatalWin(Event event) throws Exception {
		settlementDialogBatalWin.setTitle("Pembatalan Settlement");
				
		// settlement, settlementDetails, customerOrder, customerOrderProducts
		settlementTab();
		
		// <tab id="voucherTab" label="Voucher" visible="true"></tab>
		voucherTab();

		// <tab id="giroTab" label="Giro" visible="true"></tab>
		giroTab();
		
		// <tab id="glTab" label="GL" visible="true"></tab>
		glTab();
		
		// <tab id="piutangTab" label="Piutang" visible="true"></tab>
		piutangTab();
		
		saveBatalButton.setVisible(getSettlement().getSettlementStatus().equals(DocumentStatus.NORMAL));
		saveRevertButton.setVisible(getSettlement().getSettlementStatus().equals(DocumentStatus.BATAL));
	}

	private void settlementTab() {
		settlementTab.setVisible(true);
		
		// statusSettlementCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusSettlementCombobox);
		}
		
		for (Comboitem comboItem : statusSettlementCombobox.getItems()) {
			if (getSettlement().getSettlementStatus().equals(comboItem.getValue())) {
				statusSettlementCombobox.setSelectedItem(comboItem);				
			}
		}
		
	}

	private void voucherTab() throws Exception {
		// voucherGiroReceipt or voucherPayment
		boolean visible = (getSettlement().getVoucherGiroReceipt()!=null) || (getSettlement().getVoucherPayment()!=null);
		voucherTab.setVisible(visible);
		log.info("voucherTab: visible: "+visible);
		if (!visible) {
			return;
		}
		
		// which grid to display?
		giroReceiptGrid.setVisible(getSettlement().getVoucherGiroReceipt()!=null);
		voucherPaymentGrid.setVisible(getSettlement().getVoucherPayment()!=null);
		
		// statusVoucherCombobox
		Comboitem comboitem;
		for (DocumentStatus documentStatus : DocumentStatus.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(documentStatus.toString());
			comboitem.setValue(documentStatus);
			comboitem.setParent(statusVoucherCombobox);
		}

		DocumentStatus voucherStatus = null;
		if (getSettlement().getVoucherGiroReceipt()!=null) {
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceipt = 
					settlement.getVoucherGiroReceipt();
			
			voucherStatus = voucherGiroReceipt.getVoucherStatus();
		} else {
			Settlement settlement =
					getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
			VoucherPayment voucherPayment =
					settlement.getVoucherPayment();
			
			voucherStatus = voucherPayment.getVoucherStatus();
		}
		
		for (Comboitem comboItem : statusVoucherCombobox.getItems()) {
			if (voucherStatus.equals(comboItem.getValue())) {
				statusVoucherCombobox.setSelectedItem(comboItem);				
			}
		}
		
	}
	
	private void giroTab() throws Exception {
		// voucherGiroReceipt not null means there's a giro
		boolean visible = (getSettlement().getVoucherGiroReceipt()!=null);
		giroTab.setVisible(visible);
		
		if (visible) {
			// use voucherGiroReceipt to obtain the Giro
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceiptByProxy = 
					settlement.getVoucherGiroReceipt();
			
			// get the giro and check whether the giro has been cashed
			Giro giro = voucherGiroReceiptByProxy.getGiro();
			log.info(giro.toString());
			
			// statusGiroCombobox
			Comboitem comboitem;
			for (DocumentStatus documentStatus : DocumentStatus.values()) {
				comboitem = new Comboitem();
				comboitem.setLabel(documentStatus.toString());
				comboitem.setValue(documentStatus);
				comboitem.setParent(statusGiroCombobox);
			}
			for (Comboitem comboItem : statusGiroCombobox.getItems()) {
				if (giro.getGiroStatus().equals(comboItem.getValue())) {
					statusGiroCombobox.setSelectedItem(comboItem);				
				}
			}

			// voucherPayment not null means giro has been cashed
			boolean giroVoucherPayment = (giro.getVoucherPayment()!=null);
			giroPaymentTab(giroVoucherPayment, giro.getId());
			
		}
	}

	private void giroPaymentTab(boolean giroVoucherPayment, Long giroId) throws Exception {
		giroPaymentTab.setVisible(giroVoucherPayment);
		
		if (giroVoucherPayment) {
			Giro giroVoucherPaymentByProxy = 
					getGiroDao().findVoucherPaymentByProxy(giroId);
			VoucherPayment voucherPaymentByGiro = giroVoucherPaymentByProxy.getVoucherPayment();
			
			// statusGiroPaymentCombobox
			Comboitem comboitem;
			for (DocumentStatus documentStatus : DocumentStatus.values()) {
				comboitem = new Comboitem();
				comboitem.setLabel(documentStatus.toString());
				comboitem.setValue(documentStatus);
				comboitem.setParent(statusGiroPaymentCombobox);
			}
			for (Comboitem comboItem : statusGiroPaymentCombobox.getItems()) {
				if (voucherPaymentByGiro.getVoucherStatus().equals(comboItem.getValue())) {
					statusGiroPaymentCombobox.setSelectedItem(comboItem);				
				}
			}
			
			log.info(voucherPaymentByGiro.toString());
		}
	}

	private void glTab() throws Exception {
		// voucherGiroReceipt or voucherPayment not null means GL has been created and needs to be reversed
		boolean visible = (getSettlement().getVoucherGiroReceipt()!=null) || (getSettlement().getVoucherPayment()!=null);
		glTab.setVisible(visible);
		log.info("glTab: visible: "+visible);
		if (!visible) {
			return;
		}

		if (getSettlement().getVoucherGiroReceipt()!=null) {
			// gl from voucherGiroReceipt
			Settlement settlement = 
					getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
			VoucherGiroReceipt voucherGiroReceiptByProxy =
					settlement.getVoucherGiroReceipt();
			VoucherGiroReceipt voucherGiroReceiptGeneralLedgerByProxy =
					getVoucherGiroReceiptDao().findGeneralLedgerByProxy(voucherGiroReceiptByProxy.getId());
			List<GeneralLedger> glList = 
					voucherGiroReceiptGeneralLedgerByProxy.getGeneralLedgers();
			// log
			glList.forEach(gl -> log.info(gl.toString()));
			
			// get the giro and check whether the giro has been cashed (whether there's a voucherPayment)
			Giro giro = voucherGiroReceiptByProxy.getGiro();
			
			log.info(giro.toString());
			
			// voucherPayment not null means giro has been cashed and gl need to be reversed
			boolean giroVoucherPayment = (giro.getVoucherPayment()!=null);
			glGiroPaymentTab(giroVoucherPayment, giro.getId());
			
		} else {
			// gl from voucherPayment
			Settlement settlement =
					getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
			VoucherPayment voucherPaymentByProxy =
					settlement.getVoucherPayment();
			VoucherPayment voucherPaymentGeneralLedgerByProxy =
					getVoucherPaymentDao().findGeneralLedgerByProxy(voucherPaymentByProxy.getId());
			List<GeneralLedger> glList =
					voucherPaymentGeneralLedgerByProxy.getGeneralLedgers();
			// log
			glList.forEach(gl -> log.info(gl.toString()));
		}
	}
	
	private void glGiroPaymentTab(boolean giroVoucherPayment, Long giroId) throws Exception {
		glGiroPaymentTab.setVisible(giroVoucherPayment);
		
		if (giroVoucherPayment) {
			Giro giroVoucherPaymentByProxy = 
					getGiroDao().findVoucherPaymentByProxy(giroId);
			VoucherPayment voucherPaymentByGiro = 
					giroVoucherPaymentByProxy.getVoucherPayment();
			VoucherPayment voucherPaymentGeneralLedgerByProxy =
					getVoucherPaymentDao().findGeneralLedgerByProxy(voucherPaymentByGiro.getId());
			List<GeneralLedger> glList =
					voucherPaymentGeneralLedgerByProxy.getGeneralLedgers();
			// log
			glList.forEach(gl -> log.info(gl.toString()));
		
		}
		
	}

	private void piutangTab() throws Exception {
		Settlement settlementCustomerReceivableByProxy =
				getSettlementDao().findCustomerReceivableByProxy(getSettlement().getId());
		CustomerReceivable customerReceivable =
				settlementCustomerReceivableByProxy.getCustomerReceivable();
		boolean visible = (customerReceivable != null);
		piutangTab.setVisible(visible);
		
		// receivableActivity
		List<CustomerReceivableActivity> receivableActivityList = 
				customerReceivable.getCustomerReceivableActivities();
		// receivableActivity Holder - receivableActivity matching settlementDetail
		List<CustomerReceivableActivity> activityHolderList =
				new ArrayList<CustomerReceivableActivity>();
		
		for (SettlementDetail settlementDetail : getSettlement().getSettlementDetails()) {

			for (CustomerReceivableActivity receivableActivity : receivableActivityList) {
				// find settlementDetail matching the receivableActivity
				if (settlementDetail.getCustomerOrder().getId().compareTo(receivableActivity.getCustomerOrderId())==0) {
					activityHolderList.add(receivableActivity);
					break;
				}
			}
		}
		
		activityHolderList.forEach(act -> log.info(act.toString()));
		
		// voucherGiroReceipt or voucherPayment not null means payment already posted into the receivable
		// boolean visible = (getSettlement().getVoucherGiroReceipt()!=null) || (getSettlement().getVoucherPayment()!=null);
		
	}
	
	@SuppressWarnings("unused")
	private void voucherGiroReceiptTab() throws Exception {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;

		if (getSettlement().getVoucherGiroReceipt()==null) {
			// do nothing -- either have not received giro or not using giro for payment (use bank transfer)
		} else {
			voucherGiroReceiptByProxy =
					getSettlementVoucherGiroReceiptByProxy(getSettlement().getId());
			voucherGiroReceiptByProxy.setVoucherStatus(STATUS);
			List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDbCr =
					voucherGiroReceiptByProxy.getVoucherGiroReceiptDebitCredits();

			VoucherGiroReceiptDebitCredit debitAcc = null;
			VoucherGiroReceiptDebitCredit creditAcc = null;
			
			List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptReverseDbCr =
					new ArrayList<VoucherGiroReceiptDebitCredit>();
			
			for (VoucherGiroReceiptDebitCredit receiptDbCr : voucherGiroReceiptDbCr) {
				if (receiptDbCr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
					debitAcc = new VoucherGiroReceiptDebitCredit();
					debitAcc.setMasterCoa(receiptDbCr.getMasterCoa());					
					debitAcc.setDbcrDescription("Pembatalan Settlement No.:"+
							getSettlement().getSettlementNumber().getSerialComp());
					debitAcc.setDebitAmount(BigDecimal.ZERO);
					debitAcc.setCreditAmount(receiptDbCr.getDebitAmount());
					
					voucherGiroReceiptReverseDbCr.add(debitAcc);
				} else {
					creditAcc = new VoucherGiroReceiptDebitCredit();
					creditAcc.setMasterCoa(receiptDbCr.getMasterCoa());
					creditAcc.setDbcrDescription("Pembatalan Settlement No.:"+
							getSettlement().getSettlementNumber().getSerialComp());
					creditAcc.setDebitAmount(receiptDbCr.getCreditAmount());
					creditAcc.setCreditAmount(BigDecimal.ZERO);
					
					voucherGiroReceiptReverseDbCr.add(creditAcc);
				}
			}
			
			voucherGiroReceiptDbCr.addAll(voucherGiroReceiptReverseDbCr);
			voucherGiroReceiptByProxy.setVoucherGiroReceiptDebitCredits(voucherGiroReceiptDbCr);
			
			documentTab = new Tab();
			documentTab.setLabel("Voucher-GiroReceipt");
			// documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			// documentTabPanel.setParent(tabpanels);
			
			Label voucherGiroReceiptLabel = new Label();
			voucherGiroReceiptLabel.setValue(voucherGiroReceiptByProxy.toString());
			voucherGiroReceiptLabel.setParent(documentTabPanel);
			
			if (voucherGiroReceiptByProxy.getGiro()==null) {
				// not receiving giro yet
			} else {
				// VoucherGiroReceipt voucherGiroReceiptGiroByProxy = 
				//		getVoucherGiroReceiptDao().findGiroByProxy(
				//				settlementVoucherGiroReceiptByProxy.getVoucherGiroReceipt().getId());
				// VoucherGiroReceipt voucherGiroReceipt =
				//		settlementVoucherGiroReceiptByProxy.getVoucherGiroReceipt();

				giro = voucherGiroReceiptByProxy.getGiro();
				giro.setGiroStatus(STATUS);
				
				documentTab = new Tab();
				documentTab.setLabel("Giro");
				// documentTab.setParent(tabs);
				
				documentTabPanel = new Tabpanel();
				// documentTabPanel.setParent(tabpanels);
				
				Label giroLabel = new Label();
				giroLabel.setValue(giro.toString());
				giroLabel.setParent(documentTabPanel);
				
				if (giro.getVoucherPayment()==null) {
					// giro not cair yet --
				} else {
					// Giro giroVoucherPaymentByProxy = 
					//		getGiroDao().findVoucherPaymentByProxy(voucherGiroReceipt.getGiro().getId());
					
					giroVoucherPaymentByProxy =
							getGiroVoucherPaymentByProxy(giro.getId());
					giroVoucherPaymentByProxy.setVoucherStatus(STATUS);

					List<VoucherPaymentDebitCredit> voucherPaymentDbCrList = 
							giroVoucherPaymentByProxy.getVoucherPaymentDebitCredits();
					
					VoucherPaymentDebitCredit voucherPaymentdebitAcc = null;
					VoucherPaymentDebitCredit voucherPaymentcreditAcc = null;
					
					List<VoucherPaymentDebitCredit> voucherPaymentReverseDbCrList =
							new ArrayList<VoucherPaymentDebitCredit>();
					
					for (VoucherPaymentDebitCredit voucherDbCr : voucherPaymentDbCrList) {
						if (voucherDbCr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
							voucherPaymentdebitAcc = new VoucherPaymentDebitCredit();
							voucherPaymentdebitAcc.setMasterCoa(voucherDbCr.getMasterCoa());
							voucherPaymentdebitAcc.setDbcrDescription("Pembatalan Settlement No.:"+
									getSettlement().getSettlementNumber().getSerialComp());
							voucherPaymentdebitAcc.setDebitAmount(BigDecimal.ZERO);
							voucherPaymentdebitAcc.setCreditAmount(voucherDbCr.getDebitAmount());
							
							voucherPaymentReverseDbCrList.add(voucherPaymentdebitAcc);
						} else {
							voucherPaymentcreditAcc = new VoucherPaymentDebitCredit();
							voucherPaymentcreditAcc.setMasterCoa(voucherDbCr.getMasterCoa());
							voucherPaymentcreditAcc.setDbcrDescription("Pembatalan Settlement No.:"+
									getSettlement().getSettlementNumber().getSerialComp());					
							voucherPaymentcreditAcc.setDebitAmount(voucherDbCr.getCreditAmount());
							voucherPaymentcreditAcc.setCreditAmount(BigDecimal.ZERO);
							
							voucherPaymentReverseDbCrList.add(voucherPaymentcreditAcc);
						}
					}
					
					voucherPaymentDbCrList.addAll(voucherPaymentReverseDbCrList);
					giroVoucherPaymentByProxy.setVoucherPaymentDebitCredits(voucherPaymentDbCrList);

					documentTab = new Tab();
					documentTab.setLabel("Voucher-Payment");
					// documentTab.setParent(tabs);
					
					documentTabPanel = new Tabpanel();
					// documentTabPanel.setParent(tabpanels);
					
					Label voucherPaymentLabel = new Label();
					voucherPaymentLabel.setValue(giroVoucherPaymentByProxy.toString());
					voucherPaymentLabel.setParent(documentTabPanel);					
					
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void voucherPaymentTab() throws Exception {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;

		if (getSettlement().getVoucherPayment()==null) {
			// do nothing -- not paid by bank transfer
		} else {
			// Settlement settlementVoucherPaymentByProxy =
			//		getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
			
			settlementVoucherPaymentByProxy =
					getSettlementVoucherPaymentByProxy(getSettlement().getId());
			settlementVoucherPaymentByProxy.setVoucherStatus(STATUS);
			List<VoucherPaymentDebitCredit> voucherPaymentDbCrList = 
					settlementVoucherPaymentByProxy.getVoucherPaymentDebitCredits();
			
			VoucherPaymentDebitCredit debitAcc = null;
			VoucherPaymentDebitCredit creditAcc = null;
			
			List<VoucherPaymentDebitCredit> voucherPaymentReverseDbCrList =
					new ArrayList<VoucherPaymentDebitCredit>();
			
			for (VoucherPaymentDebitCredit voucherDbCr : voucherPaymentDbCrList) {
				if (voucherDbCr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
					debitAcc = new VoucherPaymentDebitCredit();
					debitAcc.setMasterCoa(voucherDbCr.getMasterCoa());
					debitAcc.setDbcrDescription("Pembatalan Settlement No.:"+
							getSettlement().getSettlementNumber().getSerialComp());
					debitAcc.setDebitAmount(BigDecimal.ZERO);
					debitAcc.setCreditAmount(voucherDbCr.getCreditAmount());
					
					voucherPaymentReverseDbCrList.add(debitAcc);
				} else {
					creditAcc = new VoucherPaymentDebitCredit();
					creditAcc.setMasterCoa(voucherDbCr.getMasterCoa());
					creditAcc.setDbcrDescription("Pembatalan Settlement No.:"+
							getSettlement().getSettlementNumber().getSerialComp());					
					creditAcc.setDebitAmount(voucherDbCr.getCreditAmount());
					creditAcc.setCreditAmount(BigDecimal.ZERO);
					
					voucherPaymentReverseDbCrList.add(creditAcc);
				}
			}
			
			voucherPaymentDbCrList.addAll(voucherPaymentReverseDbCrList);
			settlementVoucherPaymentByProxy.setVoucherPaymentDebitCredits(voucherPaymentDbCrList);
			
			documentTab = new Tab();
			documentTab.setLabel("Voucher-Payment(non-giro)");
			// documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			// documentTabPanel.setParent(tabpanels);
			
			Label voucherPaymentLabel = new Label();
			voucherPaymentLabel.setValue(settlementVoucherPaymentByProxy.toString());
			voucherPaymentLabel.setParent(documentTabPanel);
		}
	}
	
	public void onClick$saveBatalButton(Event event) throws Exception {
		Tabs tabs = settlementBatalTabbox.getTabs();
		List<Tab> tabList = tabs.getChildren();
		List<Tab> activeTabs = new ArrayList<Tab>();
		for (Tab tab : tabList) {
			if (tab.isVisible()) {
				activeTabs.add(tab);
			}
		}
		activeTabs.forEach(tab->log.info(tab.getId()));
		
		boolean confirm = false;
		for (Tab tab : activeTabs) {
			if (tab.getId().compareTo("settlementTab")==0) {
				// check statusSettlementCombobox
				confirm = statusSettlementCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("voucherTab")==0) {
				// statusVoucherCombobox
				confirm = statusVoucherCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("giroTab")==0) {
				// statusGiroCombobox
				confirm = statusGiroCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			} else if (tab.getId().compareTo("giroPaymentTab")==0) {
				// statusGiroPaymentCombobox
				confirm = statusGiroPaymentCombobox.getSelectedItem().getValue().equals(DocumentStatus.BATAL);
			}
			tab.setAttribute("confirm", confirm);								
		}
		activeTabs.forEach(tab->log.info(tab.getAttribute("confirm")));

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
		
		settlementDialogBatalWin.detach();
	}
	
	private void updatePembatalan(List<Tab> activeTabs) throws Exception {	
		for (Tab tab : activeTabs) {
			if (tab.getId().compareTo("settlementTab")==0) {
				Settlement modSettlement = getModifiedSettlement();
				getSettlementDao().update(modSettlement);
				// verify
				long modSettlementId = modSettlement.getId();
				modSettlement = getSettlementDao().findSettlementById(modSettlementId);
				log.info("Settlement Status: "+modSettlement.getSettlementStatus().toString()+
					" Date: "+modSettlement.getBatalDate()+
					" Note: "+modSettlement.getBatalNote());
			} else if (tab.getId().compareTo("voucherTab")==0) {
				if (getSettlement().getVoucherGiroReceipt()!=null) {
					VoucherGiroReceipt modVoucherGiroReceipt = getModifiedVoucherGiroReceipt();
					getVoucherGiroReceiptDao().update(modVoucherGiroReceipt);
					// verify
					long modVoucherGiroReceiptId = modVoucherGiroReceipt.getId();
					modVoucherGiroReceipt = getVoucherGiroReceiptDao().findVoucherGiroReceiptById(modVoucherGiroReceiptId);
					log.info("VoucherGiroReceipt Status: "+modVoucherGiroReceipt.getVoucherStatus().toString()+
							" Date: "+modVoucherGiroReceipt.getBatalDate()+
							" Note: "+modVoucherGiroReceipt.getBatalNote());
					// additional logs - reversed db/cr in the details
				} else {
					VoucherPayment modVoucherPayment = getModifiedVoucherPayment();
					getVoucherPaymentDao().update(modVoucherPayment);
					// verify
					long modVoucherPaymentId = modVoucherPayment.getId();
					modVoucherPayment = getVoucherPaymentDao().findVoucherPaymentById(modVoucherPaymentId);
					log.info("VoucherGiroReceipt Status: "+modVoucherPayment.getVoucherStatus().toString()+
							" Date: "+modVoucherPayment.getBatalDate()+
							" Note: "+modVoucherPayment.getBatalNote());
					// additional logs - reversed db/cr in the details					
				}
			} else if (tab.getId().compareTo("giroTab")==0) {

			} else if (tab.getId().compareTo("giroPaymentTab")==0) {

			}
		}
	}

	public void onClick$saveRevertButton(Event event) throws Exception {
		log.info("revert back to normal");
		
		Tabs tabs = settlementBatalTabbox.getTabs();
		List<Tab> tabList = tabs.getChildren();
		List<Tab> activeTabs = new ArrayList<Tab>();
		for (Tab tab : tabList) {
			if (tab.isVisible()) {
				activeTabs.add(tab);
			}
		}
		activeTabs.forEach(tab->log.info(tab.getId()));
		
		boolean confirm = false;
		for (Tab tab : activeTabs) {
			if (tab.getId().compareTo("settlementTab")==0) {
				// check statusSettlementCombobox
				confirm = statusSettlementCombobox.getSelectedItem().getValue().equals(DocumentStatus.NORMAL);
			} else if (tab.getId().compareTo("voucherTab")==0) {
				// statusVoucherCombobox
				confirm = statusVoucherCombobox.getSelectedItem().getValue().equals(DocumentStatus.NORMAL);
			} else if (tab.getId().compareTo("giroTab")==0) {
				// statusGiroCombobox
				confirm = statusGiroCombobox.getSelectedItem().getValue().equals(DocumentStatus.NORMAL);
			} else if (tab.getId().compareTo("giroPaymentTab")==0) {
				// statusGiroPaymentCombobox
				confirm = statusGiroPaymentCombobox.getSelectedItem().getValue().equals(DocumentStatus.NORMAL);
			}
			tab.setAttribute("confirm", confirm);								
		}
		activeTabs.forEach(tab->log.info(tab.getAttribute("confirm")));

		// check whether all tabs are confirmed -- if not confirmed throw exception
		for (Tab tab : activeTabs) {
			if (tab.getAttribute("confirm")==null) {
				continue;
			}			
			if (!(boolean) tab.getAttribute("confirm")) {
				throw new SuppressedException(tab.getLabel()+" NOT set to 'NORMAL'", true);
			}
		}
		
		// update
		updatePembatalan(activeTabs);

	}
	
	private Settlement getModifiedSettlement() {
		Settlement modSettlement = getSettlement();
		
		modSettlement.setSettlementStatus(statusSettlementCombobox.getSelectedItem().getValue());
		modSettlement.setBatalDate(null);
		modSettlement.setBatalNote(null);
		
		return modSettlement;
	}
	
	private VoucherGiroReceipt getModifiedVoucherGiroReceipt() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherGiroReceiptByProxy(getSettlement().getId());
		VoucherGiroReceipt modVoucherGiroReceipt = settlement.getVoucherGiroReceipt();
		
		modVoucherGiroReceipt.setVoucherStatus(statusVoucherCombobox.getSelectedItem().getValue());
		modVoucherGiroReceipt.setBatalDate(null);
		modVoucherGiroReceipt.setBatalNote(null);
		
		// clear the details list
		// read from listbox and add into the details list
		
		return modVoucherGiroReceipt;
	}

	private VoucherPayment getModifiedVoucherPayment() throws Exception {
		Settlement settlement = 
				getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
		VoucherPayment modVoucherPayment = settlement.getVoucherPayment();
		
		modVoucherPayment.setVoucherStatus(statusVoucherCombobox.getSelectedItem().getValue());
		modVoucherPayment.setBatalDate(null);
		modVoucherPayment.setBatalNote(null);

		// clear the details list
		// read from listbox and add into the details list		
		
		return modVoucherPayment;
	}
	
	
	public void onClick$closeButton(Event event) throws Exception {
		
		settlementDialogBatalWin.detach();
	}
	
	private VoucherGiroReceipt getSettlementVoucherGiroReceiptByProxy(long id) throws Exception {
		Settlement settlement =
				getSettlementDao().findVoucherGiroReceiptByProxy(id);

		return settlement.getVoucherGiroReceipt();
	}
	
	private VoucherPayment getGiroVoucherPaymentByProxy(long id) throws Exception {
		Giro giroVoucherPaymentByProxy = 
				getGiroDao().findVoucherPaymentByProxy(id);
		
		return giroVoucherPaymentByProxy.getVoucherPayment();
	}

	private VoucherPayment getSettlementVoucherPaymentByProxy(Long id) throws Exception {
		Settlement settlement =
				getSettlementDao().findVoucherPaymentByProxy(getSettlement().getId());
		
		return settlement.getVoucherPayment();
	}
	
	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public SettlementDao getSettlementDao() {
		return settlementDao;
	}

	public void setSettlementDao(SettlementDao settlementDao) {
		this.settlementDao = settlementDao;
	}

	public VoucherGiroReceiptDao getVoucherGiroReceiptDao() {
		return voucherGiroReceiptDao;
	}

	public void setVoucherGiroReceiptDao(VoucherGiroReceiptDao voucherGiroReceiptDao) {
		this.voucherGiroReceiptDao = voucherGiroReceiptDao;
	}

	public GiroDao getGiroDao() {
		return giroDao;
	}

	public void setGiroDao(GiroDao giroDao) {
		this.giroDao = giroDao;
	}

	public VoucherPaymentDao getVoucherPaymentDao() {
		return voucherPaymentDao;
	}

	public void setVoucherPaymentDao(VoucherPaymentDao voucherPaymentDao) {
		this.voucherPaymentDao = voucherPaymentDao;
	}
}
