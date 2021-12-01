package com.pyramix.swi.webui.voucher;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.coa.Coa_Adjustment;
import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherGiroReceiptDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherStatus;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.persistence.coa.dao.Coa_05_MasterDao;
import com.pyramix.swi.persistence.coa.dao.Coa_AdjustmentDao;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherGiroReceiptDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherSalesDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

/**
 *  NOTE: VoucherGiroReceipt can only be CREATED from SETTLEMENT.
 *  -- One Settlement for One VoucherGiroReceipt
 *  
 * @author mrusli
 *
 */
public class VoucherGiroReceiptDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7523102544615076452L;

	private VoucherSalesDao voucherSalesDao;
	private SettlementDao settlementDao;
	private VoucherGiroReceiptDao voucherGiroReceiptDao;
	private Coa_05_MasterDao coa_05_MasterDao;
	private SerialNumberGenerator serialNumberGenerator;
	private Coa_AdjustmentDao coa_AdjustmentDao;
	
	private Window voucherGiroReceiptDialogWin;
	private Grid createFromGrid;
	private Combobox voucherStatusCombobox;
	private Button voucherSalesSelectButton, saveButton, cancelButton, customerButton,
		totalOrderButton, removeDebitCreditButton, addDebitCreditButton;
	private Textbox voucherSalesInfoTextbox, voucherNoCompTextbox, voucherNoPostTextbox,
		noGiroTextbox, giroBankTextbox, customerTextbox, theSumOfTextbox, descriptionTextbox,
		referenceTextbox;
	private Datebox transactionDatebox, giroDateDatebox;
	private Label voucherTypeLabel, idLabel, infoDebitCreditlabel;
	// private Hlayout dbcrControl;
	private Listbox voucherDbcrListbox;
	private Listfooter totalDebitListfooter, totalCreditListfooter;
	
	private VoucherGiroReceiptData receiptData;
	private PageMode pageMode;
	private String requestPath;
	private VoucherGiroReceipt voucherGiroReceipt;
	private BigDecimal totalOrderValue, adjustmentAmount, totalDebitVal, totalCreditVal;
	private List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDebitCreditList;
	private Giro giro;
	
	// private final String 		VOUCHER_SALES_REQUEST_PATH	= "/voucher/VoucherSalesListInfo.zul";
	private final String 		SETTLEMENT_REQUEST_PATH		= "/settlement/SettlementListInfo.zul";
	private final VoucherType 	RECEIPT_GIRO_VOUCHER 		= VoucherType.RECEIPT_GIRO;
	private final VoucherStatus DEFAULT_FLOW_STATUS 		= VoucherStatus.Posted;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setReceiptData(
				(VoucherGiroReceiptData) arg.get("voucherGiroReceiptData"));
		
		// who is the caller for this dialog?
		setRequestPath(
				Executions.getCurrent().getDesktop().getRequestPath());
		
	}

	public void onCreate$voucherGiroReceiptDialogWin(Event event) throws Exception {
		transactionDatebox.setLocale(getLocale());
		transactionDatebox.setFormat(getLongDateFormat());
		giroDateDatebox.setLocale(getLocale());
		giroDateDatebox.setFormat(getLongDateFormat());
		
		setPageMode(
				getReceiptData().getPageMode());
		
		switch (getPageMode()) {
		case EDIT:
			voucherGiroReceiptDialogWin.setTitle("Merubah (Edit) Voucher Giro Receipt");
			break;
		case NEW:
			voucherGiroReceiptDialogWin.setTitle("Membuat (Tambah) Voucher Giro Receipt");
			break;
		case VIEW:
			voucherGiroReceiptDialogWin.setTitle("Melihat (View) Voucher Giro Receipt");
			break;
		default:
			break;
		}
		
		// voucherStatus - enum type
		createVoucherStatusCombobox();
		
		if (getRequestPath().matches(SETTLEMENT_REQUEST_PATH)) {
			
			if (getPageMode().compareTo(PageMode.NEW)==0) {
				// NEW
				
				// Caller from SettlementListInfo.zul -- to post the giro receipt
				createFromGrid.setVisible(true);
				
				// no need to select -- put Settlement info into the textbox
				voucherSalesSelectButton.setDisabled(true);
				
				// number from getReceiptData().getVoucherSales() data
				voucherSalesInfoTextbox.setValue(
						getReceiptData().getSettlement().getSettlementNumber().getSerialComp());
						// getPageMode().compareTo(PageMode.NEW)==0 ?
						// getReceiptData().getVoucherSales().getVoucherNumber().getSerialComp() : "");
				
				totalOrderButton.setDisabled(false);
						// getPageMode().compareTo(PageMode.NEW)==0 ? false : true);
				
				setVoucherGiroReceipt(
						settlementToVoucherGiroReceipt(new VoucherGiroReceipt(), getReceiptData().getSettlement()));	
					// getPageMode().compareTo(PageMode.NEW)==0 ?
					// voucherSalesToVoucherGiroReceipt(new VoucherGiroReceipt(), getReceiptData().getVoucherSales()) :
					//	getReceiptData().getVoucherGiroReceipt());
				
			} else {
				// VIEW
				createFromGrid.setVisible(false);
				
				// no need to select -- put Settlement info into the textbox
				voucherSalesSelectButton.setDisabled(true);

				// number from getReceiptData().getVoucherSales() data
				voucherSalesInfoTextbox.setValue("");

				totalOrderButton.setDisabled(true);

				setVoucherGiroReceipt(getReceiptData().getVoucherGiroReceipt());

			}
		
		} else {
			// Caller from VoucherGiroReceipt.zul -- to EDIT
			createFromGrid.setVisible(false);
			
			totalOrderButton.setDisabled(true);
			
			// set from data
			setVoucherGiroReceipt(getReceiptData().getVoucherGiroReceipt());			
		}
		
		setReadOnly();
		
		// load info
		setVoucherGiroReceiptInfo();
		
	}

	private VoucherGiroReceipt settlementToVoucherGiroReceipt(VoucherGiroReceipt receipt, Settlement settlement) throws Exception {
		receipt.setCreateDate(asDate(getLocalDate()));		
		receipt.setVoucherType(RECEIPT_GIRO_VOUCHER);
		receipt.setFlowStatus(DEFAULT_FLOW_STATUS);
		receipt.setCustomer(getCustomerFromSettlementByProxy(settlement.getId()));
		receipt.setTheSumOf(settlement.getAmountPaid());
		setTotalOrderValue(settlement.getAmountPaid());
		setAdjustmentAmount(settlement.getPostingAmount());
		
		// create the link
		receipt.setSettlement(settlement);
		
		return receipt;
	}
	
/*	
 * 	NOTE: VoucherGiroReceipt can only be CREATED from SETTLEMENT.
 *  -- One Settlement for One VoucherGiroReceipt
 *  
 * 	public void onClick$voucherSalesSelectButton(Event event) throws Exception {
		// list from voucherSales -- sorted by customer, no-voucher, no-suratjalan
		Window voucherSalesListWin = 
				(Window) Executions.createComponents("/voucher/VoucherSalesListDialog.zul", null, null);
		
		voucherSalesListWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				VoucherSales selVoucherSales = (VoucherSales) event.getData();
				
				// display the voucher number
				voucherSalesInfoTextbox.setValue(
						selVoucherSales.getVoucherNumber().getSerialComp());
				
				// set
				setVoucherGiroReceipt(
						voucherSalesToVoucherGiroReceipt(new VoucherGiroReceipt(), selVoucherSales));
				
				// load info
				setVoucherGiroReceiptInfo();
			}
		});
		
		voucherSalesListWin.doModal();
	}
*
*/
		
/*	
 *  NOTE: VoucherSales TIDAK membuat VoucherGiroReceipt
 *  - Pembayaran HARUS melalui proses Settlement 
 *  - kemudian membuat VoucherGiroReceipt apabila pembayaran dialakukan dengan giro gantung
 * 
 * 	private VoucherGiroReceipt voucherSalesToVoucherGiroReceipt(VoucherGiroReceipt receipt, VoucherSales voucherSales) throws Exception {
		
		receipt.setCreateDate(asDate(getLocalDate()));		
		receipt.setVoucherType(RECEIPT_GIRO_VOUCHER);
		receipt.setFlowStatus(DEFAULT_FLOW_STATUS);
		receipt.setCustomer(getCustomerFromVoucherSalesByProxy(voucherSales.getId()));
		receipt.setTheSumOf(voucherSales.getTheSumOf());
		setTotalOrderValue(voucherSales.getTheSumOf());
		
		// create the link
		receipt.setVoucherSales(voucherSales);
				
		return receipt;
	}
*/


	private void setVoucherGiroReceiptInfo() throws Exception {
		
		if (getVoucherGiroReceipt()==null) {
			idLabel.setValue("");
			voucherNoCompTextbox.setValue(null);
			voucherNoPostTextbox.setValue(null);
			transactionDatebox.setValue(asDate(getLocalDate()));
			voucherTypeLabel.setValue(RECEIPT_GIRO_VOUCHER.toString());
			voucherStatusCombobox.setValue(DEFAULT_FLOW_STATUS.toString());
			customerTextbox.setValue("");
			customerTextbox.setAttribute("customer", null);
			noGiroTextbox.setValue("");
			giroBankTextbox.setValue("");
			giroDateDatebox.setValue(null);
			theSumOfTextbox.setValue(toLocalFormat(BigDecimal.ZERO));
			setTotalOrderValue(BigDecimal.ZERO);
			descriptionTextbox.setValue("");
			referenceTextbox.setValue("");
		} else if (getVoucherGiroReceipt().getId()==Long.MIN_VALUE) {
			// new -- values from settlementToVoucherGiroReceipt function
			idLabel.setValue("");
			voucherNoCompTextbox.setValue(null);
			voucherNoPostTextbox.setValue(null);
			transactionDatebox.setValue(getVoucherGiroReceipt().getCreateDate());
			voucherTypeLabel.setValue(getVoucherGiroReceipt().getVoucherType().toString());
			voucherStatusCombobox.setValue(getVoucherGiroReceipt().getFlowStatus().toString());
			customerTextbox.setValue(getVoucherGiroReceipt().getCustomer()==null ? "ERROR" :
				getVoucherGiroReceipt().getCustomer().getCompanyType()+". "+
					getVoucherGiroReceipt().getCustomer().getCompanyLegalName());
			customerTextbox.setAttribute("customer", getVoucherGiroReceipt().getCustomer());
			// to be filled by the user
			noGiroTextbox.setValue("");
			giroBankTextbox.setValue("");
			giroDateDatebox.setValue(null);
			theSumOfTextbox.setValue(toLocalFormat(getTotalOrderValue()));
			descriptionTextbox.setValue("");
			referenceTextbox.setValue("");
		} else {
			// edit or view -- values from selected VoucherGiroReceipt
			idLabel.setValue(String.valueOf(getVoucherGiroReceipt().getId()));
			voucherNoCompTextbox.setValue(getVoucherGiroReceipt().getVoucherNumber().getSerialComp());
			
			// voucher posting number
			VoucherGiroReceipt receiptByProxy = getPostingVoucherNumberByProxy(getVoucherGiroReceipt().getId());
			// NOTE: older VoucherGiroReceipt do not have posting voucher number
			voucherNoPostTextbox.setValue(receiptByProxy.getPostingVoucherNumber()==null ? "" :
					receiptByProxy.getPostingVoucherNumber().getSerialComp());

			transactionDatebox.setValue(getVoucherGiroReceipt().getCreateDate());
			voucherTypeLabel.setValue(getVoucherGiroReceipt().getVoucherType().toString());
			voucherStatusCombobox.setValue(getVoucherGiroReceipt().getFlowStatus().toString());

			Customer customer = getCustomerFromVoucherGiroReceiptByProxy(getVoucherGiroReceipt().getId());
			
			customerTextbox.setValue(customer==null ? "tunai" : 
				customer.getCompanyType().toString()+". "+
				customer.getCompanyLegalName());
			customerTextbox.setAttribute("customer", customer);

			noGiroTextbox.setValue(getVoucherGiroReceipt().getGiroNumber());
			giroBankTextbox.setValue(getVoucherGiroReceipt().getGiroBank());
			giroDateDatebox.setValue(getVoucherGiroReceipt().getGiroDate());
			theSumOfTextbox.setValue(toLocalFormat(getVoucherGiroReceipt().getTheSumOf()));
			setTotalOrderValue(getVoucherGiroReceipt().getTheSumOf());
			descriptionTextbox.setValue(getVoucherGiroReceipt().getTransactionDescription());
			referenceTextbox.setValue(getVoucherGiroReceipt().getDocumentRef());
			
			// set giro -- giro not displayed
			setGiro(getGiroByProxy(getVoucherGiroReceipt().getId()));
			
			// set and display
			setVoucherGiroReceiptDebitCreditList(getVoucherGiroReceipt().getVoucherGiroReceiptDebitCredits());
			setVoucherGiroReceiptDebitCreditInfo(getVoucherGiroReceiptDebitCreditList());
		}		
	}

	private VoucherGiroReceipt getPostingVoucherNumberByProxy(Long id) throws Exception {
		VoucherGiroReceipt voucherGiroReceiptByProxy = getVoucherGiroReceiptDao().findPostingVoucherNumberByProxy(id);
		
		return voucherGiroReceiptByProxy;
	}

	public void onClick$addDebitCreditButton(Event event) throws Exception {
		if (noGiroTextbox.getValue().isEmpty()) {
			throw new Exception("No Giro Belum diisi.");
		}
		
		if (giroBankTextbox.getValue().isEmpty()) {
			throw new Exception("Informasi Bank Belum diisi.");
		}
		
		if (giroDateDatebox.getValue()==null) {
			throw new Exception("Tanggal Giro Belum diisi.");
		}
		
		String transDescrip = "Penerimaan Giro dari "+customerTextbox.getValue();
		String transRef = "Giro No."+noGiroTextbox.getValue()+
				" "+giroBankTextbox.getValue()+
				" Jatuh Tempo "+
				dateToStringDisplay(asLocalDate(giroDateDatebox.getValue()), getLongDateFormat());
		
		descriptionTextbox.setValue(transDescrip);
		referenceTextbox.setValue(transRef);
		
		// * 		CR	Piutang Langganan (COA 1.241.003 ) #44
		VoucherGiroReceiptDebitCredit creditAccount = new VoucherGiroReceiptDebitCredit();
		creditAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(44)));
		creditAccount.setDbcrDescription("Pembayaran Piutang "+customerTextbox.getValue()+" dgn Giro");
		creditAccount.setDebitAmount(BigDecimal.ZERO);
		creditAccount.setCreditAmount(getTotalAmountToSettle(getReceiptData().getSettlement().getSettlementDetails()));

		// *		DB	Giro Ditangan (1.212.0001)
		VoucherGiroReceiptDebitCredit debitAccount = new VoucherGiroReceiptDebitCredit();
		debitAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(45)));
		debitAccount.setDbcrDescription(transDescrip);
		debitAccount.setDebitAmount(getTotalOrderValue());
		debitAccount.setCreditAmount(BigDecimal.ZERO);
		

		// adjustment account, in case needed
		VoucherGiroReceiptDebitCredit adjustmentAccount = null;
		
		if (getAdjustmentAmount().compareTo(BigDecimal.ZERO)==-1) {
			// Skenario #2 - posisi minus
			// - posisi  Debet - COA:
			// Disc penjualan 	:   41110003 - id=47 - default
			// Retur Penjualan	:   41110004

			adjustmentAccount = new VoucherGiroReceiptDebitCredit();
			adjustmentAccount.setDebitAmount(getAdjustmentAmount().multiply(new BigDecimal(-1)));
			// Adjust the debit account
			// -- should be substract, adjustmentAmount is negative, so just add
			// debitAccount.setDebitAmount(getTotalOrderValue().add(getAdjustmentAmount()));
			adjustmentAccount.setCreditAmount(BigDecimal.ZERO);
			adjustmentAccount.setDbcrDescription("Pembayaran Piutang "+customerTextbox.getValue()+" dgn Giro");
			adjustmentAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(47)));
			
		} else if (getAdjustmentAmount().compareTo(BigDecimal.ZERO)==0) {
			// - posting total pembayaran - TANPA adjustment

			// System.out.println("posisi nol: "+getAdjustmentAmount());
			
		} else {
			// Skenario #3 - posisi plus
			// - posisi Kredit
			// Pembayaran dimuka	:   44110002
			// Pendapatan dll		:   44110001 - id=49 - default
			// Meterai				:   52410004
			
			adjustmentAccount = new VoucherGiroReceiptDebitCredit();
			adjustmentAccount.setCreditAmount(getAdjustmentAmount());
			// Adjust the credit account
			// creditAccount.setCreditAmount(getTotalOrderValue().subtract(getAdjustmentAmount()));
			adjustmentAccount.setDebitAmount(BigDecimal.ZERO);
			adjustmentAccount.setDbcrDescription("Pembayaran Piutang "+customerTextbox.getValue()+" dgn Giro");
			adjustmentAccount.setMasterCoa(getCoa_05_MasterDao().findCoa_05_MasterById(new Long(49)));
		}		
			
		// list
		setVoucherGiroReceiptDebitCreditList(new ArrayList<VoucherGiroReceiptDebitCredit>());
		
		// add
		getVoucherGiroReceiptDebitCreditList().add(creditAccount);
		getVoucherGiroReceiptDebitCreditList().add(debitAccount);
		if (adjustmentAccount!=null) {
			getVoucherGiroReceiptDebitCreditList().add(adjustmentAccount);			
		}
		// display
		setVoucherGiroReceiptDebitCreditInfo(
				getVoucherGiroReceiptDebitCreditList());
	}
	
	private BigDecimal getTotalAmountToSettle(List<SettlementDetail> settlementDetails) {
		BigDecimal totalAmountToSettle = BigDecimal.ZERO;
		
		for (SettlementDetail settlementDetail : settlementDetails) {
			totalAmountToSettle = totalAmountToSettle.add(settlementDetail.getAmountToSettle());
		}
		
		return totalAmountToSettle;
	}

	private void setVoucherGiroReceiptDebitCreditInfo(List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDbCrList) {
		voucherDbcrListbox.setModel(
			new ListModelList<VoucherGiroReceiptDebitCredit>(voucherGiroReceiptDbCrList));
		voucherDbcrListbox.setItemRenderer(
			getVoucherGiroReceiptDebitCreditListitemRenderer());
	}

	private ListitemRenderer<VoucherGiroReceiptDebitCredit> getVoucherGiroReceiptDebitCreditListitemRenderer() {
		
		totalDebitVal = BigDecimal.ZERO; 
		totalCreditVal = BigDecimal.ZERO;
		
		return new ListitemRenderer<VoucherGiroReceiptDebitCredit>() {
			
			@Override
			public void render(Listitem item, VoucherGiroReceiptDebitCredit voucherDbCr, int index) throws Exception {
				Listcell lc;
				
				// No Akun
				lc = initCoaNumber(new Listcell(), voucherDbCr, index);
				lc.setParent(item);
				
				// Nama Akun
				lc = new Listcell(voucherDbCr.getMasterCoa().getMasterCoaName());
				lc.setParent(item);				
				
				// Keterangan
				lc = initDescription(new Listcell(), voucherDbCr.getDbcrDescription(), index);
						// new Listcell(voucherDbCr.getDbcrDescription());
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

			private Listcell initDescription(Listcell listcell, String dbcrDescription, int index) {
				if (index>1) {
					Textbox descripTextbox = new Textbox();
					
					descripTextbox.setWidth("310px");
					descripTextbox.setValue(dbcrDescription);
					descripTextbox.setDisabled((index<2) || (getPageMode().compareTo(PageMode.VIEW) == 0));
					descripTextbox.setParent(listcell);					
				} else {
					listcell.setLabel(dbcrDescription);
				}
				
				return listcell;
			}

			private Listcell initCoaNumber(Listcell listcell, VoucherGiroReceiptDebitCredit voucherDbCr, int index) throws Exception {
				Combobox coaNumberCombobox = new Combobox();
				Comboitem comboitem;
				if (index>1) {
					// adjustment account ONLY
					if (voucherDbCr.getCreditAmount().compareTo(BigDecimal.ZERO)==0) {
						// DEBIT accounts
						List<Coa_Adjustment> coaDebitAdjustmentList = getCoa_AdjustmentDao().findCoaAdjustmentByDebitAccount(true);
						for (Coa_Adjustment coa_Adjustment : coaDebitAdjustmentList) {
							comboitem = new Comboitem();
							comboitem.setLabel(coa_Adjustment.getMasterCoa().getMasterCoaComp());
							comboitem.setValue(coa_Adjustment);
							comboitem.setParent(coaNumberCombobox);
						}
					} else {
						// CREDIT accounts
						List<Coa_Adjustment> coaCreditAdjustmentList = getCoa_AdjustmentDao().findCoaAdjustmentByDebitAccount(false);
						for (Coa_Adjustment coa_Adjustment : coaCreditAdjustmentList) {
							comboitem = new Comboitem();
							comboitem.setLabel(coa_Adjustment.getMasterCoa().getMasterCoaComp());
							comboitem.setValue(coa_Adjustment);
							comboitem.setParent(coaNumberCombobox);
						}
					}
					// event
					coaNumberCombobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Combobox combobox = (Combobox) event.getTarget();
							Comboitem comboitem = combobox.getSelectedItem();
							Coa_Adjustment coa_Adjustment = comboitem.getValue();
							
							Listitem item = voucherDbcrListbox.getItemAtIndex(index);							
							Listcell lc = (Listcell) item.getChildren().get(1);
							lc.setLabel(coa_Adjustment.getMasterCoa().getMasterCoaName());
						}
					});
					
				}
				coaNumberCombobox.setValue(voucherDbCr.getMasterCoa().getMasterCoaComp());
				coaNumberCombobox.setWidth("165px");
				coaNumberCombobox.setDisabled((index<2) || (getPageMode().compareTo(PageMode.VIEW) == 0));
				coaNumberCombobox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$voucherDbcrListbox(Event event) throws Exception {
		if (getVoucherGiroReceiptDebitCreditList()==null) {
			infoDebitCreditlabel.setValue("Debit/Kredit: 0 items");
			
			totalDebitListfooter.setLabel(toLocalFormat(BigDecimal.ZERO)); 
			totalCreditListfooter.setLabel(toLocalFormat(BigDecimal.ZERO));			
		} else {
			int dbcrCount = getVoucherGiroReceiptDebitCreditList().size();
			infoDebitCreditlabel.setValue("Debit/Kredit: "+dbcrCount+" items");
			
			totalDebitListfooter.setLabel(toLocalFormat(totalDebitVal)); 
			totalCreditListfooter.setLabel(toLocalFormat(totalCreditVal));
		}
	}
	
	public void onClick$removeDebitCreditButton(Event event) throws Exception {
		// reset to null
		setVoucherGiroReceiptDebitCreditList(null);
		
		// display
		setVoucherGiroReceiptDebitCreditInfo(new ArrayList<VoucherGiroReceiptDebitCredit>());
		
		// reset
		totalCreditVal = BigDecimal.ZERO;
		totalDebitVal = BigDecimal.ZERO;
	}
	
	public void onClick$totalOrderButton(Event event) throws Exception {
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", getVoucherGiroReceipt()==null ? null : getVoucherGiroReceipt().getTheSumOf());
		
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
	
	private void createVoucherStatusCombobox() {
		Comboitem item;
		
		for (VoucherStatus status : VoucherStatus.values()) {
			item = new Comboitem();
			item.setLabel(status.toString());
			item.setValue(status);
			item.setParent(voucherStatusCombobox);
		}
	}

	public void onClick$saveButton(Event event) throws Exception {
		if (getVoucherGiroReceiptDebitCreditList()==null) {
			throw new Exception("Debit / Kredit Harus Dilengkapi sebelum Disimpan.");
		}
		
		if (getPageMode().compareTo(PageMode.NEW)==0) {
			
			VoucherGiroReceipt receipt = getUserUpdateVoucherGiroReceipt(
				getVoucherGiroReceipt());
			
			// for (VoucherGiroReceiptDebitCredit receiptDbCr : receipt.getVoucherGiroReceiptDebitCredits()) {
			//	System.out.println("COA No: "+receiptDbCr.getMasterCoa().getMasterCoaComp());
			// }
			
			// save
			// getVoucherGiroReceiptDao().save(receipt);
			
			// send a new receipt back
			Events.sendEvent(Events.ON_OK, voucherGiroReceiptDialogWin, receipt);
		} else {
			VoucherGiroReceipt receipt = getUserUpdateVoucherGiroReceipt(
				getVoucherGiroReceipt());

			// update
			// getVoucherGiroReceiptDao().update(receipt);
			
			// send an updated receipt back
			Events.sendEvent(Events.ON_CHANGE, voucherGiroReceiptDialogWin, receipt);
		}
		
		voucherGiroReceiptDialogWin.detach();
	}
	
	private VoucherGiroReceipt getUserUpdateVoucherGiroReceipt(VoucherGiroReceipt receipt) throws Exception {

		Date defaultDate = transactionDatebox.getValue();

		receipt.setTheSumOf(getTotalOrderValue());
		receipt.setTransactionDate(defaultDate);
		receipt.setTransactionDescription(descriptionTextbox.getValue());
		receipt.setDocumentRef(referenceTextbox.getValue());
		receipt.setCreateDate(defaultDate);
		receipt.setModifiedDate(asDate(getLocalDate()));
		receipt.setCheckDate(defaultDate);
		receipt.setFlowStatus(DEFAULT_FLOW_STATUS);
		receipt.setVoucherType(RECEIPT_GIRO_VOUCHER);
		
		receipt.setGiroNumber(noGiroTextbox.getValue());
		receipt.setGiroBank(giroBankTextbox.getValue());
		receipt.setGiroDate(giroDateDatebox.getValue());
		// receipt.setNote(); <-- not avail in the class
		receipt.setCustomer((Customer) customerTextbox.getAttribute("customer"));
		receipt.setVoucherGiroReceiptDebitCredits(getUserUpdateDebitCredit());
		receipt.setVoucherStatus(DocumentStatus.NORMAL);
		
		if (receipt.getId().compareTo(Long.MIN_VALUE)==0) {
			// create the giro
			receipt.setGiro(getGiroInfo(new Giro()));			
			// create the link for giro - voucherGiroReceipt
			receipt.getGiro().setVoucherGiroReceipt(receipt);
			// create the number
			receipt.setVoucherNumber(addVoucherNumber(RECEIPT_GIRO_VOUCHER, defaultDate));
			
			// post directly
			receipt.setPostingDate(asDate(getLocalDate()));
			receipt.setPostingVoucherNumber(addVoucherNumber(VoucherType.POSTING_RECEIPTGIRO, asDate(getLocalDate())));
			receipt.setGeneralLedgers(createGeneralLedgersFromVoucherGiroReceipt(receipt));
		} else {
			receipt.setGiro(getGiroInfo(getGiro()));
		}
		
		return receipt;
	}

	private List<GeneralLedger> createGeneralLedgersFromVoucherGiroReceipt(VoucherGiroReceipt receipt) {
		// use the dbcr to create generalledger		
		List<VoucherGiroReceiptDebitCredit> dbcrList = getVoucherGiroReceiptDebitCreditList();
		// multipe generalledgers are created, so need a new list to pass back to the caller
		List<GeneralLedger> generalLedgerList = new ArrayList<GeneralLedger>();

		for (VoucherGiroReceiptDebitCredit dbcr : dbcrList) {
			GeneralLedger gl = new GeneralLedger();
			
			gl.setMasterCoa(dbcr.getMasterCoa());
			// 30/07/2021 - posting date must be the same as the transaction date
			// gl.setPostingDate(asDate(getLocalDate()));
			gl.setPostingDate(receipt.getTransactionDate());
			gl.setPostingVoucherNumber(receipt.getPostingVoucherNumber());
			gl.setCreditAmount(dbcr.getCreditAmount());
			gl.setDebitAmount(dbcr.getDebitAmount());
			gl.setDbcrDescription(dbcr.getDbcrDescription());
			gl.setTransactionDescription(receipt.getTransactionDescription());
			gl.setDocumentRef(receipt.getDocumentRef());
			gl.setTransactionDate(receipt.getTransactionDate());
			gl.setVoucherType(receipt.getVoucherType());
			gl.setVoucherNumber(receipt.getVoucherNumber());

			// add
			generalLedgerList.add(gl);
		}
		
		return generalLedgerList;
	}

	private List<VoucherGiroReceiptDebitCredit> getUserUpdateDebitCredit() {
		List<VoucherGiroReceiptDebitCredit> dbcrList = getVoucherGiroReceiptDebitCreditList();
		
		for (int i = 0; i < dbcrList.size(); i++) {
			
			if (i>1) {
				Listitem item = voucherDbcrListbox.getItemAtIndex(i);
				Combobox combobox = (Combobox) item.getChildren().get(0).getFirstChild();
				Coa_Adjustment coa_Adjustment = combobox.getSelectedItem().getValue();
				
				Textbox textbox = (Textbox) item.getChildren().get(2).getFirstChild();
				
				VoucherGiroReceiptDebitCredit receiptDbCr = dbcrList.get(i);
				receiptDbCr.setMasterCoa(coa_Adjustment.getMasterCoa());
				receiptDbCr.setDbcrDescription(textbox.getValue());
			}
			
		}
		
		return dbcrList;
	}

	private VoucherSerialNumber addVoucherNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = getSerialNumberGenerator().getSerialNumber(voucherType, currentDate);
		
		VoucherSerialNumber voucherSerialNumber = new VoucherSerialNumber();
		voucherSerialNumber.setVoucherType(voucherType);
		voucherSerialNumber.setSerialDate(currentDate);
		voucherSerialNumber.setSerialNo(serialNum);
		voucherSerialNumber.setSerialComp(formatSerialComp(voucherType.toCode(voucherType.getValue()), 
				currentDate, serialNum));
		
		return voucherSerialNumber;
	}

	private Giro getGiroInfo(Giro giro) {
		
		giro.setCustomer((Customer) customerTextbox.getAttribute("customer"));
		giro.setGiroAmount(getTotalOrderValue());
		giro.setGiroBank(giroBankTextbox.getValue());
		giro.setGiroDate(giroDateDatebox.getValue());
		giro.setGiroNumber(noGiroTextbox.getValue());
		giro.setGiroReceivedDate(transactionDatebox.getValue());
		giro.setPaid(false);
		giro.setGiroStatus(DocumentStatus.NORMAL);
		
		return giro;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		voucherGiroReceiptDialogWin.detach();
	}

	private void setReadOnly() {
		if (getPageMode().compareTo(PageMode.NEW)==0) {
			transactionDatebox.setDisabled(false);
			noGiroTextbox.setDisabled(false);
			giroBankTextbox.setDisabled(false);
			giroDateDatebox.setDisabled(false);
			customerButton.setDisabled(true);
			descriptionTextbox.setDisabled(false);
			referenceTextbox.setDisabled(false);
			// dbcrControl.setVisible(true);
			
			// button
			saveButton.setVisible(true);
			cancelButton.setLabel("Cancel");

			// -- ADD or REMOVE debit-credit
			removeDebitCreditButton.setDisabled(false);
			addDebitCreditButton.setDisabled(false);
		
		} else if (getPageMode().compareTo(PageMode.EDIT)==0) {	
			transactionDatebox.setDisabled(true);
			noGiroTextbox.setDisabled(false);
			giroBankTextbox.setDisabled(false);
			giroDateDatebox.setDisabled(false);
			customerButton.setDisabled(true);
			descriptionTextbox.setDisabled(false);
			referenceTextbox.setDisabled(false);
			// dbcrControl.setVisible(false);

			// button
			saveButton.setVisible(true);
			cancelButton.setLabel("Cancel");
		
			// -- NOT allowed to edit / change the debit-credit part
			removeDebitCreditButton.setDisabled(true);
			addDebitCreditButton.setDisabled(true);
			
		} else {
			// VIEW
			
			transactionDatebox.setDisabled(true);
			noGiroTextbox.setDisabled(true);
			giroBankTextbox.setDisabled(true);
			giroDateDatebox.setDisabled(true);
			customerButton.setDisabled(true);
			descriptionTextbox.setDisabled(true);
			referenceTextbox.setDisabled(true);
			// dbcrControl.setVisible(true);
			
			// button
			saveButton.setVisible(false);
			cancelButton.setLabel("Tutup");

			// -- NOT allowed to edit / change the debit-credit part
			removeDebitCreditButton.setDisabled(true);
			addDebitCreditButton.setDisabled(true);
		}
	}

	private Customer getCustomerFromSettlementByProxy(Long id) throws Exception {
		Settlement settlement = getSettlementDao().findCustomerByProxy(id);
		
		return settlement.getCustomer();
	}	
	
/*	NOTE: VoucherGiroReceipt is NOT from VoucherSales
 *  -- user MUST DO Settlement
 * 	private Customer getCustomerFromVoucherSalesByProxy(Long id) throws Exception {
		VoucherSales voucherSales = getVoucherSalesDao().findCustomerByProxy(id);
		
		return voucherSales.getCustomer();
	}
*/	
	private Customer getCustomerFromVoucherGiroReceiptByProxy(Long id) throws Exception {
		VoucherGiroReceipt receipt = getVoucherGiroReceiptDao().findCustomerByProxy(id);
		
		return receipt.getCustomer();
	}

	private Giro getGiroByProxy(Long id) throws Exception {
		VoucherGiroReceipt receipt = getVoucherGiroReceiptDao().findGiroByProxy(id);
		
		return receipt.getGiro();
	}

	
	public VoucherGiroReceiptData getReceiptData() {
		return receiptData;
	}

	public void setReceiptData(VoucherGiroReceiptData receiptData) {
		this.receiptData = receiptData;
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

/*	public VoucherSales getVoucherSales() {
		return voucherSales;
	}

	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
	}
*/
	public VoucherGiroReceipt getVoucherGiroReceipt() {
		return voucherGiroReceipt;
	}

	public void setVoucherGiroReceipt(VoucherGiroReceipt voucherGiroReceipt) {
		this.voucherGiroReceipt = voucherGiroReceipt;
	}

	public VoucherSalesDao getVoucherSalesDao() {
		return voucherSalesDao;
	}

	public void setVoucherSalesDao(VoucherSalesDao voucherSalesDao) {
		this.voucherSalesDao = voucherSalesDao;
	}

	public VoucherGiroReceiptDao getVoucherGiroReceiptDao() {
		return voucherGiroReceiptDao;
	}

	public void setVoucherGiroReceiptDao(VoucherGiroReceiptDao voucherGiroReceiptDao) {
		this.voucherGiroReceiptDao = voucherGiroReceiptDao;
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

	public List<VoucherGiroReceiptDebitCredit> getVoucherGiroReceiptDebitCreditList() {
		return voucherGiroReceiptDebitCreditList;
	}

	public void setVoucherGiroReceiptDebitCreditList(List<VoucherGiroReceiptDebitCredit> voucherGiroReceiptDebitCreditList) {
		this.voucherGiroReceiptDebitCreditList = voucherGiroReceiptDebitCreditList;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public Giro getGiro() {
		return giro;
	}

	public void setGiro(Giro giro) {
		this.giro = giro;
	}

	public SettlementDao getSettlementDao() {
		return settlementDao;
	}

	public void setSettlementDao(SettlementDao settlementDao) {
		this.settlementDao = settlementDao;
	}

	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public void setAdjustmentAmount(BigDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public Coa_AdjustmentDao getCoa_AdjustmentDao() {
		return coa_AdjustmentDao;
	}

	public void setCoa_AdjustmentDao(Coa_AdjustmentDao coa_AdjustmentDao) {
		this.coa_AdjustmentDao = coa_AdjustmentDao;
	}


}
