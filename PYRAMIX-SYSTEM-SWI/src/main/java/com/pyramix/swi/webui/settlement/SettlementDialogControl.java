package com.pyramix.swi.webui.settlement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
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
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SerialNumberGenerator;
import com.pyramix.swi.webui.customerorder.CustomerOrderListData;

public class SettlementDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -897840345235710978L;

	private SerialNumberGenerator serialNumberGenerator;
	private CustomerOrderDao customerOrderDao;
	
	private Window settlementDialogWin;
	private Listbox settlementDetailListbox;
	private Datebox settlementDatebox;
	private Textbox settlementNoCompTextbox, customerTextbox, referenceTextbox,
		paymentAmountTextbox, settlementDescriptionTextbox, remainingTextbox; 
	private Checkbox installmentCheckbox;
	
	private Settlement settlement;
	private List<SettlementDetail> settlementDetailList;
	private BigDecimal totalAmount, remainingAmount;
	private Customer selectedCustomer;
	private List<CustomerOrder> selectedCustomerOrderList;
	
	private final DocumentType DEFAULT_DOCUMENT_TYPE = DocumentType.SETTLEMENT;
	
	public void onCreate$settlementDialogWin(Event event) throws Exception {
		// set locale to datebox
		settlementDatebox.setLocale(getLocale());
		settlementDatebox.setFormat(getLongDateFormat());
		
		settlementDialogWin.setTitle("Menambah Settlement - CustomerOrder");
		
		// init
		setTotalAmount(BigDecimal.ZERO);
		setSelectedCustomer(null);
		initSettlement(new Settlement());
		setSelectedCustomerOrderList(new ArrayList<CustomerOrder>());
		
		// set info
		setSettlementInfo();
		
		// init list
		setSettlementDetailList(new ArrayList<SettlementDetail>());
		
		// list
		listSettlementDetail(getSettlementDetailList());
	}
	
	private void initSettlement(Settlement settlement) {
		
		Date defaultDate = asDate(getLocalDate());
		
		settlement.setAmountPaid(BigDecimal.ZERO);
		settlement.setCheckDate(defaultDate);
		settlement.setCreateDate(defaultDate);
		settlement.setCustomer(null);
		settlement.setDocumentRef("");
		settlement.setModifiedDate(defaultDate);
		settlement.setNote("");
		settlement.setSettlementDate(defaultDate);
		settlement.setSettlementDescription("");
		settlement.setSettlementDetails(null);
		settlement.setSettlementNumber(null);
		settlement.setSettlementPosition(null);
		
		setSettlement(settlement);
	}

	private void setSettlementInfo() {
		settlementNoCompTextbox.setValue("");
		settlementDatebox.setValue(getSettlement().getCreateDate());
		customerTextbox.setValue("");
		referenceTextbox.setValue("");
		settlementDescriptionTextbox.setValue("");
		
	}
	
	private void listSettlementDetail(List<SettlementDetail> settlementDetails) {
		
		settlementDetailListbox.setModel(
				new ListModelList<SettlementDetail>(settlementDetails));
		settlementDetailListbox.setItemRenderer(
				getSettlementDetailListitemRenderer());
		
	}

	private ListitemRenderer<SettlementDetail> getSettlementDetailListitemRenderer() {

		return new ListitemRenderer<SettlementDetail>() {
			
			@Override
			public void render(Listitem item, SettlementDetail detail, int index) throws Exception {
				Listcell lc;
				
				// No.Customer-Order
				lc = new Listcell(
						detail.getCustomerOrder().getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// Tgl.Order
				lc = new Listcell(
						dateToStringDisplay(asLocalDate(
							detail.getCustomerOrder().getOrderDate()), getShortDateFormat()));
				lc.setParent(item);

				// No.SuratJalan
				SuratJalan suratJalanByProxy = getCustomerOrderSuratJalanByProxy(detail.getCustomerOrder().getId());

				lc = new Listcell(suratJalanByProxy.getSuratJalanNumber().getSerialComp());
				lc.setParent(item);
				
				lc = new Listcell(
						dateToStringDisplay(asLocalDate(
								suratJalanByProxy.getSuratJalanDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// Tgl.Jth-Tempo
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
				paymentDecimalbox.setParent(listcell);
				
				if (index==0) {
					
					if (getTotalAmount().compareTo(detail.getRemainingAmountToSettle())==-1) {
						// money is less than 
						
						paymentDecimalbox.setValue(getTotalAmount());
						
						setRemainingAmount(getTotalAmount().subtract(detail.getRemainingAmountToSettle()));
						
						remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
					} else {
						// money is more than
						
						setRemainingAmount(getTotalAmount().subtract(detail.getRemainingAmountToSettle()));
						
						paymentDecimalbox.setValue(getTotalAmount().subtract(getRemainingAmount()));
						
						remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
					}

				} else {
					
					if (getRemainingAmount().compareTo(detail.getRemainingAmountToSettle())==-1) {
						// remaining money is less than
						paymentDecimalbox.setValue(getRemainingAmount());

						setRemainingAmount(getRemainingAmount().subtract(detail.getRemainingAmountToSettle()));
						
						remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
					} else {
						// remaining money is more than
						
						setRemainingAmount(getRemainingAmount().subtract(detail.getRemainingAmountToSettle()));
						
						paymentDecimalbox.setValue(detail.getRemainingAmountToSettle());
						
						remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
					}
					
				}
				
				
				return listcell;
			}
		};
	}

	public void onClick$paymentAmountButton(Event event) throws Exception {
		Map<String, BigDecimal> arg = 
				Collections.singletonMap("totalOrder", null);		
				
		Window totalOrderDialogWin = 
				(Window) Executions.createComponents("/dialogs/TotalOrderDialog.zul", null, arg);
		
		totalOrderDialogWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				paymentAmountTextbox.setValue(toLocalFormat((BigDecimal) event.getData()));
				setTotalAmount((BigDecimal) event.getData());				
			}
		});
		
		totalOrderDialogWin.doModal();
		
	}
	
	public void onClick$customerButton(Event event) throws Exception {		
		Window selectCustomerWin = 
				(Window) Executions.createComponents("/customer/CustomerListDialog.zul", null, null);
		
		selectCustomerWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				setSelectedCustomer((Customer) event.getData());
				
				customerTextbox.setValue(getSelectedCustomer().getCompanyType()+". "+
						getSelectedCustomer().getCompanyLegalName());
			}
		});
		
		selectCustomerWin.doModal();
	}
	
	public void onClick$addCustomerOrderButton(Event event) throws Exception {
		if (getSelectedCustomer()==null) {
			throw new Exception("Belum pilih Customer.  Pilih Customer sebelum memilih Customer Order.");
		}
		
		if (getTotalAmount().compareTo(BigDecimal.ZERO)==0) {
			throw new Exception("Belum mengisi Jumlah Pembayaran.  Isi Jumlah Pembayaran sebelum memilih Customer Order.");
		}
		
		CustomerOrderListData customerOrderListData = new CustomerOrderListData();
		customerOrderListData.setSelectedCustomer(getSelectedCustomer());
		customerOrderListData.setSelectedCustomerOrder(getSelectedCustomerOrderList());
		
		Map<String, CustomerOrderListData> arg = 
				Collections.singletonMap("customerOrderListData", customerOrderListData);
		
		Window customerOrderSelectWin = 
				(Window) Executions.createComponents("/customerorder/CustomerOrderListDialog.zul", null, arg);

		customerOrderSelectWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				CustomerOrder selCustomerOrder = (CustomerOrder) event.getData();
				getSelectedCustomerOrderList().add(selCustomerOrder);
				
				getSettlementDetailList().add(
						customerOrderToSettlementDetail(new SettlementDetail(), selCustomerOrder));
				
				listSettlementDetail(getSettlementDetailList());
			}

			private SettlementDetail customerOrderToSettlementDetail(SettlementDetail detail,
					CustomerOrder order) {
				
				BigDecimal amountToSettle 	= order.getTotalOrder();
				BigDecimal amountSettled	= order.getAmountPaid();
				BigDecimal remainingAmount	= amountToSettle.subtract(amountSettled);
				
				// Total Order
				detail.setAmountToSettle(amountToSettle);
				// Jumlah Pembayaran
				detail.setAmountSettled(amountSettled);
				// Sisa
				detail.setRemainingAmountToSettle(remainingAmount);
				
				// link
				detail.setCustomerOrder(order);
				
				return detail;
			}
		});
		
		customerOrderSelectWin.doModal();
	}
	
	public void onClick$removeCustomerOrderButton(Event event) throws Exception {
		// init list
		setSettlementDetailList(new ArrayList<SettlementDetail>());
		
		// list
		listSettlementDetail(getSettlementDetailList());
		
		// init remaining amount
		remainingTextbox.setValue("");
		setRemainingAmount(BigDecimal.ZERO);
		
		// clear the selected customer order - otherwise the customer order for 
		// the selected customer will not be complete in the CustomerOrderListDialog
		getSelectedCustomerOrderList().clear();
	}
	
	public void onCheck$installmentCheckbox(Event event) throws Exception {
		if (installmentCheckbox.isChecked()) {
			remainingTextbox.setValue(toLocalFormat(BigDecimal.ZERO));
			
			
		} else {
			remainingTextbox.setValue(toLocalFormat(getRemainingAmount()));
			
		}
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		if (getSettlementDetailList().isEmpty()) {
			throw new Exception("Belum pilih Customer Order untuk dibayarkan.  Pilih Customer Order sebelum Save.");
		}
		
		BigDecimal amountToSettle			= BigDecimal.ZERO;
		BigDecimal amountSettled			= BigDecimal.ZERO;
		BigDecimal remainingAmountToSettle	= BigDecimal.ZERO;
		
		int index = 0;
		int lastIndex = settlementDetailListbox.getItemCount()-1;
		
		for (SettlementDetail detail : getSettlementDetailList()) {
			amountToSettle = detail.getRemainingAmountToSettle();
					// 23/02/2022 - not using amountToSettle
					// detail.getAmountToSettle();

			// Jumlah Pembayaran
			Listitem item = settlementDetailListbox.getItemAtIndex(index);
			Decimalbox amountSettledDecimalbox = (Decimalbox) item.getChildren().get(8).getFirstChild();
			amountSettled = amountSettledDecimalbox.getValue(); 
			detail.setAmountSettled(amountSettled);
			
			// Sisa
			remainingAmountToSettle = amountSettled.subtract(amountToSettle);
			detail.setRemainingAmountToSettle(remainingAmountToSettle.compareTo(BigDecimal.ZERO)==-1 ?
					remainingAmountToSettle.multiply(new BigDecimal(-1)) : remainingAmountToSettle);			
						
			// NOTE: installmentCheckbox is ONLY applicable towards the last item in the listbox
			// -- controled by lastIndex
			if ((index==lastIndex) && (installmentCheckbox.isChecked())) {
				BigDecimal amountPaidPrev = detail.getCustomerOrder().getAmountPaid();
				
				detail.getCustomerOrder().setAmountPaid(amountPaidPrev.compareTo(BigDecimal.ZERO)==0 ?
						// has not been paid AT ALL
						amountSettled :
						// paid previously
						amountPaidPrev.add(amountSettled));
				detail.getCustomerOrder().setPaymentComplete(false);
				
				// System.out.println("customerOrder-Cicilan-AmountPaid: "+detail.getCustomerOrder().getAmountPaid());
			} else {
				detail.getCustomerOrder().setAmountPaid(amountSettled);
				detail.getCustomerOrder().setPaymentComplete(true);
				
				// System.out.println("customerOrder-NotCicilan-AmountPaid: "+detail.getCustomerOrder().getAmountPaid());
			}

			index++;
		}
		
		Settlement settlement = new Settlement();
		settlement.setSettlementDetails(getSettlementDetailList());
		settlement.setAmountPaid(getTotalAmount());
		settlement.setSettlementDate(settlementDatebox.getValue());
		settlement.setSettlementDescription(settlementDescriptionTextbox.getValue());
		// settlement.setSettlementPosition();
		settlement.setPostingAmount(installmentCheckbox.isChecked() ? 
				BigDecimal.ZERO : getRemainingAmount());
		// System.out.println("settlementPostingAmount: "+settlement.getPostingAmount());
		settlement.setDocumentRef(referenceTextbox.getValue());
		settlement.setCreateDate(settlementDatebox.getValue());
		settlement.setModifiedDate(asDateTime(getLocalDateTime()));
		settlement.setCheckDate(asDateTime(getLocalDateTime()));
		// settlement.setNote();
		settlement.setSettlementNumber(
				createDocumentSerialNumber(DEFAULT_DOCUMENT_TYPE, settlementDatebox.getValue()));
		settlement.setCustomer(getSelectedCustomer());
		settlement.setSettlementStatus(DocumentStatus.NORMAL);
		
		Events.sendEvent(Events.ON_OK, settlementDialogWin, settlement);
		
		settlementDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		settlementDialogWin.detach();
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
	
	private SuratJalan getCustomerOrderSuratJalanByProxy(long id) throws Exception {
		
		CustomerOrder customerOrder = getCustomerOrderDao().findSuratJalanByProxy(id);
		
		return customerOrder.getSuratJalan();
	}
	
	public List<SettlementDetail> getSettlementDetailList() {
		return settlementDetailList;
	}

	public void setSettlementDetailList(List<SettlementDetail> settlementDetailList) {
		this.settlementDetailList = settlementDetailList;
	}

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
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

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public List<CustomerOrder> getSelectedCustomerOrderList() {
		return selectedCustomerOrderList;
	}

	public void setSelectedCustomerOrderList(List<CustomerOrder> selectedCustomerOrderList) {
		this.selectedCustomerOrderList = selectedCustomerOrderList;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}
}
