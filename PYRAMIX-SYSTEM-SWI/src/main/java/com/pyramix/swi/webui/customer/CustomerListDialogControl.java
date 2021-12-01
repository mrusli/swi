package com.pyramix.swi.webui.customer;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.persistence.customer.dao.CustomerDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class CustomerListDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1069186858669764422L;

	private CustomerDao customerDao;

	private Window customerListDialog;
	private Listbox customerListbox;
	private Textbox searchCustomerTextbox;
	
	private List<Customer> customerList;
	
	public void onCreate$customerListDialog(Event event) throws Exception {
		customerListDialog.setTitle("Pilih Customer");
		
		// making textbox responds to "Enter" key
		setSearchTextboxEventListener();
		
		listCustomer();
	}
	
	private void setSearchTextboxEventListener() {
		searchCustomerTextbox.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				searchCustomer();				
			}

		});
	}

	public void onClick$searchCodeButton(Event event) throws Exception {
		searchCustomer();
	}

	private void searchCustomer() throws Exception {
		// index
		// getCustomerDao().createIndexer();
		
		try {
			// search
			setCustomerList(
					getCustomerDao().searchActiveCustomer(searchCustomerTextbox.getValue()));
			
			customerListbox.setModel(
					new ListModelList<Customer>(getCustomerList()));
			
			customerListbox.setItemRenderer(
					getCustomerListitemRenderer());
			
		} catch (Exception e) {
			listCustomer();
			
		}
	}
	
	private void listCustomer() {
		try {
			setCustomerList(customerDao.findActiveCustomer_OrderBy_CompanyName(false));
					// customerDao.findAllCustomer());
			
			customerListbox.setModel(
					new ListModelList<Customer>(getCustomerList()));
			
			customerListbox.setItemRenderer(getCustomerListitemRenderer());
			
		} catch (Exception e) {
			Messagebox.show("Daftar Customer Order gagal. "+e.getMessage(), 
					"Error", Messagebox.OK,  Messagebox.ERROR);			
			
		}
	}

	private ListitemRenderer<Customer> getCustomerListitemRenderer() {

		return new ListitemRenderer<Customer>() {
			
			@Override
			public void render(Listitem item, Customer customer, int index) throws Exception {
				Listcell lc;
				
				String customerName = customer.getCompanyType().toString()+". "+
						customer.getCompanyLegalName();
				
				lc = new Listcell(customerName);
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
		
				item.setValue(customer);
			}
		};
	}

	public void onClick$selectButton(Event event) throws Exception {
		if (customerListbox.getSelectedItem()==null) {
			throw new Exception("Pilih Nama Perusahaan Customer sebelum klik tombol");
		}
		
		Customer selCustomer = customerListbox.getSelectedItem().getValue();
		
		Events.sendEvent(Events.ON_SELECT, customerListDialog, selCustomer);
		
		customerListDialog.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		customerListDialog.detach();
	}
	
	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public List<Customer> getCustomerList() {
		return customerList;
	}

	public void setCustomerList(List<Customer> customerList) {
		this.customerList = customerList;
	}
}
