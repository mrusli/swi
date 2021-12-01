package com.pyramix.swi.webui.settlement;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class SettlementDialogViewControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6555502711714645879L;
	
	private SettlementDao settlementDao;
	private CustomerOrderDao customerOrderDao;
	
	private Window settlementDialogViewWin;
	private Listbox settlementDetailListbox;
	private Datebox settlementDatebox;
	private Textbox settlementNoCompTextbox, customerTextbox, referenceTextbox,
		paymentAmountTextbox, settlementDescriptionTextbox, remainingTextbox; 
	private Checkbox installmentCheckbox;
	
	private Settlement settlement;
	private BigDecimal totalAmount, remainingAmount;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setSettlement(
				(Settlement) arg.get("settlement"));
	}
	
	public void onCreate$settlementDialogViewWin(Event event) throws Exception {
		settlementDialogViewWin.setTitle("Detail (View) Settlement");
		
		// set locale to datebox
		settlementDatebox.setLocale(getLocale());
		settlementDatebox.setFormat(getLongDateFormat());
		
		// init
		setTotalAmount(BigDecimal.ZERO);

		// set info
		setSettlementInfo();

	}

	private void setSettlementInfo() throws Exception {
		settlementNoCompTextbox.setValue(getSettlement().getSettlementNumber().getSerialComp());
		settlementDatebox.setValue(getSettlement().getCreateDate());
		
		Settlement settlementCustomerByProxy = getSettlementDao().findCustomerByProxy(getSettlement().getId());
		customerTextbox.setValue(settlementCustomerByProxy.getCustomer().getCompanyType()+"."+
				settlementCustomerByProxy.getCustomer().getCompanyLegalName());
		
		referenceTextbox.setValue(getSettlement().getDocumentRef());
		settlementDescriptionTextbox.setValue(getSettlement().getSettlementDescription());
		
		paymentAmountTextbox.setValue(toLocalFormat(getSettlement().getAmountPaid()));
		setTotalAmount(getSettlement().getAmountPaid());
		
		settlementDetailListbox.setModel(
				new ListModelList<SettlementDetail>(getSettlement().getSettlementDetails()));
		settlementDetailListbox.setItemRenderer(getSettlementDetailListitemRenderer());
	}

	private ListitemRenderer<SettlementDetail> getSettlementDetailListitemRenderer() {
		
		return new ListitemRenderer<SettlementDetail>() {
			
			@Override
			public void render(Listitem item, SettlementDetail detail, int index) throws Exception {
				Listcell lc;
				
				// No.Order - CustomerOrder
				lc = new Listcell(
						detail.getCustomerOrder().getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// Tgl.Order - CustomerOrder
				lc = new Listcell(
						dateToStringDisplay(asLocalDate(
							detail.getCustomerOrder().getOrderDate()), getShortDateFormat()));
				lc.setParent(item);
				
				SuratJalan suratJalanByProxy = getCustomerOrderSuratJalanByProxy(detail.getCustomerOrder().getId());
				
				// No.SuratJalan
				lc = new Listcell(suratJalanByProxy.getSuratJalanNumber().getSerialComp());
				lc.setParent(item);
				
				// Tgl.SuratJalan
				lc = new Listcell(
						dateToStringDisplay(asLocalDate(
								suratJalanByProxy.getSuratJalanDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// Tgl.Jth-Tempo - CustomerOrder
				LocalDate dueDate = addDate(
						detail.getCustomerOrder().getCreditDay(), 
						asLocalDate(detail.getCustomerOrder().getOrderDate()));
				lc = new Listcell(
						dateToStringDisplay(dueDate, getShortDateFormat()));
				lc.setParent(item);
				
				// Total Order
				lc = new Listcell(toLocalFormat(detail.getAmountToSettle()));
				lc.setParent(item);
				
				// Jumlah Pembayaran
				lc = new Listcell(toLocalFormat(detail.getAmountSettled()));
				lc.setParent(item);
				
				// Sisa
				lc = new Listcell(toLocalFormat(detail.getRemainingAmountToSettle()));
				lc.setParent(item);
				
				// Jumlah Dibayarkan
				lc = initPayment(new Listcell(), detail, index);
				lc.setParent(item);				
				
				
			}

			private Listcell initPayment(Listcell listcell, SettlementDetail detail, int index) {
				Decimalbox paymentDecimalbox = new Decimalbox();
				paymentDecimalbox.setValue(BigDecimal.ZERO);
				paymentDecimalbox.setLocale(getLocale());
				paymentDecimalbox.setSclass("textboxInList");
				paymentDecimalbox.setWidth("110px");
				paymentDecimalbox.setReadonly(true);
				paymentDecimalbox.setParent(listcell);
				
				if (index==0) {
					
					if (getTotalAmount().compareTo(detail.getRemainingAmountToSettle())==-1) {
						// money is less than 
						
						paymentDecimalbox.setValue(getTotalAmount());
						
						setRemainingAmount(getTotalAmount().subtract(detail.getRemainingAmountToSettle()));
						
						// remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
					} else {
						// money is more than
						
						setRemainingAmount(getTotalAmount().subtract(detail.getAmountSettled()));
								// detail.getRemainingAmountToSettle()
						paymentDecimalbox.setValue(getTotalAmount().subtract(getRemainingAmount()));
							
						// remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
					}

				} else {
					
					if (getRemainingAmount().compareTo(detail.getAmountToSettle())==-1) {
						// remaining money is less than
						
						paymentDecimalbox.setValue(getRemainingAmount());

						setRemainingAmount(BigDecimal.ZERO);
								// getRemainingAmount().subtract(detail.getAmountSettled()));
						// remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
					} else {
						// remaining money is more than
						
						setRemainingAmount(getRemainingAmount().subtract(detail.getRemainingAmountToSettle()));
						
						paymentDecimalbox.setValue(detail.getRemainingAmountToSettle());
						
						// remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
					}
					
				}
				
				return listcell;
			}
		};
	}
	
	public void onAfterRender$settlementDetailListbox(Event event) throws Exception {		
		installmentCheckbox.setChecked(getSettlement().getPostingAmount().compareTo(BigDecimal.ZERO)==0);
		remainingTextbox.setValue(installmentCheckbox.isChecked() ? 
				toLocalFormat(BigDecimal.ZERO) : toLocalFormat(getSettlement().getPostingAmount()));
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		settlementDialogViewWin.detach();
	}

	private SuratJalan getCustomerOrderSuratJalanByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findSuratJalanByProxy(id);
		
		return customerOrder.getSuratJalan();
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}
	
}
