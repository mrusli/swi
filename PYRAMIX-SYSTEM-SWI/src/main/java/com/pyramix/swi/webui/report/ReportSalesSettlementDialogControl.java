package com.pyramix.swi.webui.report;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class ReportSalesSettlementDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231509L;

	private SettlementDao settlementDao;
	
	private Textbox settlementNoCompTextbox, customerTextbox, paymentAmountTextbox,
		referenceTextbox, settlementDescriptionTextbox, remainingTextbox;
	private Label settlementDetailLabel;
	private Window reportSalesSettlementDialogWin;
	private Datebox settlementDatebox;
	private Checkbox installmentCheckbox;
	private Listbox settlementDetailListbox;
	
	private Settlement settlement;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setSettlement((Settlement)arg.get("settlement"));
	}

	public void onCreate$reportSalesSettlementDialogWin(Event event) throws Exception {
		settlementNoCompTextbox.setValue(getSettlement().getSettlementNumber().getSerialComp());
		settlementDatebox.setValue(getSettlement().getSettlementDate());
		// customer req. proxy
		Settlement settlementCustomerByProxy = 
				getSettlementDao().findCustomerByProxy(getSettlement().getId());
		customerTextbox.setValue(
				settlementCustomerByProxy.getCustomer().getCompanyType()+"."+
				settlementCustomerByProxy.getCustomer().getCompanyLegalName());
		paymentAmountTextbox.setValue(toLocalFormat(getSettlement().getAmountPaid()));
		referenceTextbox.setValue(getSettlement().getDocumentRef());
		settlementDescriptionTextbox.setValue(getSettlement().getSettlementDescription());
		
		// detail
		settlementDetailListbox.setModel(
				new ListModelList<SettlementDetail>(getSettlement().getSettlementDetails()));
		settlementDetailListbox.setItemRenderer(getSettlementDetailListitemRenderer());
		
		// to be completed...
		settlementDetailLabel.setValue("");
		remainingTextbox.setValue(toLocalFormat(BigDecimal.ZERO));
		installmentCheckbox.setChecked(false);
	}
	
	private ListitemRenderer<SettlementDetail> getSettlementDetailListitemRenderer() {
		
		return new ListitemRenderer<SettlementDetail>() {
			
			@Override
			public void render(Listitem item, SettlementDetail settlementDetail, int index) throws Exception {
				Listcell lc;
				
				// No.Order
				lc = new Listcell(
						settlementDetail.getCustomerOrder().getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// Tgl.Order
				lc = new Listcell(dateToStringDisplay(asLocalDate(
						settlementDetail.getCustomerOrder().getOrderDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// No.SuratJalan
				lc = new Listcell();
				lc.setParent(item);
				
				// Tgl.SuratJalan
				lc = new Listcell();
				lc.setParent(item);
				
				// Tgl.Jth-Tempo
				LocalDate dueDate = addDate(
						settlementDetail.getCustomerOrder().getCreditDay(), 
						asLocalDate(settlementDetail.getCustomerOrder().getOrderDate()));
				lc = new Listcell(
						dateToStringDisplay(dueDate, getShortDateFormat()));
				lc.setParent(item);
				
				// Total Order
				lc = new Listcell(toLocalFormat(settlementDetail.getAmountToSettle()));
				lc.setParent(item);
				
				// Jumlah Pembayaran
				lc = new Listcell(toLocalFormat(settlementDetail.getAmountSettled()));
				lc.setParent(item);
				
				// Sisa
				lc = new Listcell(toLocalFormat(settlementDetail.getRemainingAmountToSettle()));
				lc.setParent(item);
				
				// Jumlah Dibayarkan
				lc = new Listcell();
				lc.setParent(item);
				
			}
		};
		
	}
	
	public void onClick$closeButton(Event event) throws Exception {
		reportSalesSettlementDialogWin.detach();		
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

	
	
}
