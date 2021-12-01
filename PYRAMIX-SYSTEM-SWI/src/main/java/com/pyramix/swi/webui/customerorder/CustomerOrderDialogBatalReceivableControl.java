package com.pyramix.swi.webui.customerorder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class CustomerOrderDialogBatalReceivableControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8109656303936197158L;

	private CustomerOrderDao customerOrderDao;
	
	private Window customerOrderDialogBatalReceivableWin;
	private Listbox receivableActivityListbox;
	
	private CustomerReceivableActivity receivableActivity;
	
	private static final Logger log = Logger.getLogger(CustomerOrderDialogBatalReceivableControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// ReceivableActivity
		setReceivableActivity(
				(CustomerReceivableActivity) arg.get("ReceivableActivity"));
	}

	public void onCreate$customerOrderDialogBatalReceivableWin(Event event) throws Exception {
		List<CustomerReceivableActivity> receivableActivityList =
				new ArrayList<CustomerReceivableActivity>();
		receivableActivityList.add(getReceivableActivity());
		
		receivableActivityListbox.setModel(
				new ListModelList<CustomerReceivableActivity>(receivableActivityList));
		receivableActivityListbox.setItemRenderer(getReceivableActivityListitemRenderer());
	}

	private ListitemRenderer<CustomerReceivableActivity>getReceivableActivityListitemRenderer() {
		
		return new ListitemRenderer<CustomerReceivableActivity>() {
			
			@Override
			public void render(Listitem item, CustomerReceivableActivity activity, int index) throws Exception {
				Listcell lc;
				
				CustomerOrder customerOrder = 
						getCustomerOrderDao().findCustomerOrderById(activity.getCustomerOrderId());				

				// Order-No.
				lc = new Listcell(customerOrder.getDocumentSerialNumber().getSerialComp());
				lc.setParent(item);
				
				// Tgl.Order
				lc = new Listcell(dateToStringDisplay(
						asLocalDate(customerOrder.getOrderDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// Tgl.Jatuh-Tempo
				LocalDate paymentDueDate = addDate(customerOrder.getCreditDay(), 
						asLocalDate(customerOrder.getOrderDate()));
				lc = new Listcell(
						dateToStringDisplay(paymentDueDate, getShortDateFormat()));
				lc.setParent(item);
				
				// Total-Order
				lc = new Listcell(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getAmountSales()));
				lc.setParent(item);
				
				// Tgl.Pembayaran
				lc = new Listcell(activity.getPaymentDate()==null? " - " :
					dateToStringDisplay(asLocalDate(activity.getPaymentDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// Pembayaran
				lc = new Listcell(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getAmountPaid()));
				lc.setParent(item);
				
				// Sisa
				lc = new Listcell(activity.getReceivableStatus().equals(DocumentStatus.BATAL) ?
						toLocalFormat(BigDecimal.ZERO) : toLocalFormat(activity.getRemainingAmount()));
				lc.setParent(item);
				
				// Status
				lc = new Listcell(activity.isPaymentComplete() ? "Lunas" : " - ");
				lc.setParent(item);
				
				// NORMAL / BATAL
				lc = initReceivableStatus(new Listcell(), activity);
				lc.setParent(item);

				item.setValue(activity);
			}
			
			private Listcell initReceivableStatus(Listcell listcell, CustomerReceivableActivity activity) throws Exception {
				Combobox receivableStatusCombobox = new Combobox();
				receivableStatusCombobox.setWidth("120px");
				receivableStatusCombobox.setParent(listcell);
				
				// create the comboitems
				Comboitem comboitem;
				for (DocumentStatus documentStatus : DocumentStatus.values()) {
					comboitem = new Comboitem();
					comboitem.setLabel(documentStatus.toString());
					comboitem.setValue(documentStatus);
					comboitem.setParent(receivableStatusCombobox);
				}
				// select the comboitem
				for (Comboitem recvStatComboitem : receivableStatusCombobox.getItems()) {
					if (recvStatComboitem.getValue().equals(activity.getReceivableStatus())) {
						receivableStatusCombobox.setSelectedItem(recvStatComboitem);
						
						break;
					}
				}
		
				receivableStatusCombobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						// Comboitem selItem = (Comboitem) event.getData();
						// log.info("Selected: "+event.getData()+" "+event.getTarget());
						Combobox statusCombobox = (Combobox) event.getTarget();
						// log.info("Selected: "+statusCombobox.getSelectedItem().getValue());
						DocumentStatus selStatus = statusCombobox.getSelectedItem().getValue();
						
						activity.setReceivableStatus(selStatus);
					}
					
				});
				
				return listcell;
			}
		};
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		CustomerReceivableActivity updatedReceivableActivity =
				receivableActivityListbox.getItemAtIndex(0).getValue();
		
		log.info(updatedReceivableActivity.toString());
		
		Events.sendEvent(Events.ON_CHANGE, customerOrderDialogBatalReceivableWin, updatedReceivableActivity);
		
		customerOrderDialogBatalReceivableWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		
		customerOrderDialogBatalReceivableWin.detach();
	}
	
	public CustomerReceivableActivity getReceivableActivity() {
		return receivableActivity;
	}

	public void setReceivableActivity(CustomerReceivableActivity receivableActivity) {
		this.receivableActivity = receivableActivity;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}
}
