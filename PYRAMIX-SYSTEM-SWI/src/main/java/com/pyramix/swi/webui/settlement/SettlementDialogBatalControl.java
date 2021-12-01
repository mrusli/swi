package com.pyramix.swi.webui.settlement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherGiroReceiptDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.domain.voucher.VoucherPaymentDebitCredit;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.persistence.voucher.dao.GiroDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherGiroReceiptDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class SettlementDialogBatalControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -555508437082374957L;

	private SettlementDao settlementDao;
	private VoucherGiroReceiptDao voucherGiroReceiptDao;
	private GiroDao giroDao;
	
	private Window settlementDialogBatalWin;
	private Tabbox documentBatalTabbox;
	private Tabs tabs 			= new Tabs();
	private Tabpanels tabpanels = new Tabpanels();

	private Settlement settlement;
	private Giro giro;
	
	private VoucherGiroReceipt	voucherGiroReceiptByProxy 		= null;
	private VoucherPayment 		giroVoucherPaymentByProxy		= null;
	private VoucherPayment		settlementVoucherPaymentByProxy	= null;

	private static final DocumentStatus STATUS = DocumentStatus.BATAL; 
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		setSettlement(
				(Settlement) arg.get("settlement"));
	}
	
	public void onCreate$settlementDialogBatalWin(Event event) throws Exception {
		settlementDialogBatalWin.setTitle("Pembatalan Settlement");

		// tabs and tabpanels assigned to Tabbox
		tabs.setParent(documentBatalTabbox);
		tabpanels.setParent(documentBatalTabbox);
		
		// settlement
		settlementTab();
		
		// voucherGiroReceipt
		voucherGiroReceiptTab();

		// voucherPayment
		voucherPaymentTab();
	}

	private void settlementTab() {
		Tab 		documentTab;
		Tabpanel 	documentTabPanel;
		
		documentTab = new Tab();
		documentTab.setLabel("Settlement");
		documentTab.setParent(tabs);
		
		documentTabPanel = new Tabpanel();
		documentTabPanel.setParent(tabpanels);
		
		getSettlement().setSettlementStatus(STATUS);
				
		Label customerOrderLabel = new Label();
		customerOrderLabel.setValue(getSettlement().toString());
		customerOrderLabel.setParent(documentTabPanel);
		
	}

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
			documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			documentTabPanel.setParent(tabpanels);
			
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
				documentTab.setParent(tabs);
				
				documentTabPanel = new Tabpanel();
				documentTabPanel.setParent(tabpanels);
				
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
					documentTab.setParent(tabs);
					
					documentTabPanel = new Tabpanel();
					documentTabPanel.setParent(tabpanels);
					
					Label voucherPaymentLabel = new Label();
					voucherPaymentLabel.setValue(giroVoucherPaymentByProxy.toString());
					voucherPaymentLabel.setParent(documentTabPanel);					
					
				}
			}
		}
	}

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
			documentTab.setParent(tabs);
			
			documentTabPanel = new Tabpanel();
			documentTabPanel.setParent(tabpanels);
			
			Label voucherPaymentLabel = new Label();
			voucherPaymentLabel.setValue(settlementVoucherPaymentByProxy.toString());
			voucherPaymentLabel.setParent(documentTabPanel);
		}
	}

	public void onClick$saveButton(Event event) throws Exception {
		Settlement settlement = getModifiedSettlement();
		
		Events.sendEvent(Events.ON_OK, settlementDialogBatalWin, settlement);
		
		settlementDialogBatalWin.detach();
	}
	
	private Settlement getModifiedSettlement() {
		Settlement modSettlement = getSettlement();
		
		modSettlement.setSettlementStatus(STATUS);
		if (giroVoucherPaymentByProxy==null) {
			// giroVoucherPayment not posting yet
			// do nothing
		} else {
			giro.setVoucherPayment(giroVoucherPaymentByProxy);
			voucherGiroReceiptByProxy.setGiro(giro);
			modSettlement.setVoucherGiroReceipt(voucherGiroReceiptByProxy);			
		}
		
		return modSettlement;
	}

	public void onClick$cancelButton(Event event) throws Exception {
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
}
