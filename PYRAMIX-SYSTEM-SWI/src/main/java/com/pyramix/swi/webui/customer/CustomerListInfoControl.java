package com.pyramix.swi.webui.customer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.persistence.customer.dao.CustomerDao;
import com.pyramix.swi.webui.common.Alphabet;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

/**
 * @author rusli
 *
 * CustomerListInfoControl provides the CRUD for the user.
 * 
 * Everytime the user creates a new Customer, a new account Receivable
 * IS CREATED AUTOMATICALLY for the customer.
 * 
 * We're assuming the customer is a credit customer, and an account
 * Receivable must be provided for the customer.
 *
 */
public class CustomerListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2316622067954924498L;

	private CustomerDao customerDao;
	private SerialNumberGenerator serialNumberGenerator;
	
	private Label formTitleLabel, infoCustomerlabel;
	private Listbox customerListbox;
	private Tabbox customerTabbox;
	
	private List<Customer> customerList;
	private int customerCount;
	
	private final int ALPHA_GROUP = 1;
	
	public void onCreate$customerListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Langganan (Customer)");

		setupCustomerTabbox();

		// load
		setCustomerList(
				getCustomerDao().findAllCustomer_OrderBy_CompanyName(false));

		customerCount = getCustomerList().size();
		
		// display
		displayCustomerListInfo();
	}

	private void setupCustomerTabbox() {
		int i = 0;
		int groupNum = 0;

		int groupOf = ALPHA_GROUP;
		int numberOfGroup = 26 / groupOf;
		
		String alphaGroupDisp = "";
		String alphaGroupValue = "";
		
		Tabs tabs = new Tabs();
		Tab tab;
		
		tab = new Tab();
		tab.setLabel("Semua");
		tab.setParent(tabs);
		
		tabs.setParent(customerTabbox);
			
		while (i < Alphabet.values().length) {
			for (int k = 0; k < groupOf; k++) {
				if ((k==0) || (k==(groupOf-1))) {
					alphaGroupDisp = alphaGroupDisp+Alphabet.toString(i);				
				} else {
					alphaGroupDisp = alphaGroupDisp+".";
				}
				alphaGroupValue = alphaGroupValue+Alphabet.toString(i);
				i++;
			}
			tab = new Tab();
			tab.setLabel(alphaGroupDisp);
			tab.setValue(alphaGroupValue);
			tab.setParent(tabs);
			
			// System.out.println(alphaGroupDisp);
			alphaGroupDisp = "";
			alphaGroupValue = "";
			groupNum++;
			if (!(groupNum<numberOfGroup)) {
				break;
			}
		}
		// remainder
		int idxAlpha = numberOfGroup*groupOf;
		while (idxAlpha < Alphabet.values().length) {
			// display
			alphaGroupDisp = alphaGroupDisp+Alphabet.toString(idxAlpha);
			
			// value
			alphaGroupValue = alphaGroupValue+Alphabet.toString(idxAlpha);
			idxAlpha++;
		}
		tab = new Tab();
		tab.setLabel(alphaGroupDisp);
		tab.setValue(alphaGroupValue);
		tab.setParent(tabs);
		
		tabs.setParent(customerTabbox);
		// System.out.println(alphaGroupDisp);
	}

	private void displayCustomerListInfo() throws Exception {
		customerListbox.setModel(
				new ListModelList<Customer>(getCustomerList()));
		customerListbox.setItemRenderer(getCustomerListitemRenderer());
	}

	private ListitemRenderer<Customer> getCustomerListitemRenderer() {
		
		return new ListitemRenderer<Customer>() {
			
			@Override
			public void render(Listitem item, Customer customer, int index) throws Exception {
				Listcell lc;
				
				// Nama Perusahaan
				lc = new Listcell(customer.getCompanyType()+". "+
						customer.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Nama Personil
				lc = new Listcell(customer.getContactPerson());
				lc.setParent(item);
				
				// No.Telp
				lc = new Listcell(customer.getPhone());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// No.Telp-Ext.
				lc = new Listcell(customer.getExtension());
				lc.setParent(item);
				
				// No.Fax
				lc = new Listcell(customer.getFax());
				lc.setParent(item);

				// Email
				lc = new Listcell(customer.getEmail());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(customer.getNote());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Aktif
				lc = new Listcell(customer.isActive() ? "Aktif" : "Tidak");
				lc.setParent(item);
				
				// Edit
				lc = initEditButton(new Listcell(), customer);
				lc.setParent(item);

			}
		};
	}

	protected Listcell initEditButton(Listcell listcell, Customer customer) {
		Button editButton = new Button();
		
		editButton.setLabel("...");
		editButton.setClass("inventoryEditButton");
		editButton.addEventListener("onClick", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				CustomerData customerData = new CustomerData();
				customerData.setCustomer(customer);
				customerData.setPageMode(PageMode.EDIT);
				
				Map<String, CustomerData> arg = 
						Collections.singletonMap("customerData", customerData);
				
				Window customerEditWin = 
						(Window) Executions.createComponents("/customer/CustomerDialog.zul", null, arg);
				
				customerEditWin.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						getCustomerDao().update((Customer) event.getData());
						
						// load and display
						displayCustomerListInfo();
					}
				});
				
				customerEditWin.doModal();
			}
		});
		editButton.setParent(listcell);
		
		return listcell;
	}

	public void onAfterRender$customerListbox(Event event) throws Exception {
		infoCustomerlabel.setValue("Total: "+customerCount+" Customer");
	}
	
	public void onClick$addButton(Event event) throws Exception {
		CustomerData customerData = new CustomerData();
		customerData.setCustomer(new Customer());
		customerData.setPageMode(PageMode.NEW);
		
		Map<String, CustomerData> arg = 
				Collections.singletonMap("customerData", customerData);
		
		Window customerAddWin = 
				(Window) Executions.createComponents("/customer/CustomerDialog.zul", null, arg);
		
		customerAddWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				
				try {
					// save
					getCustomerDao().save((Customer) event.getData());
					
					// create index -- because indexer is set to normal
					getCustomerDao().createIndexer();
					
					// load
					loadCustomerInfo();
					
					displayCustomerListInfo();
				} catch (ConstraintViolationException cve) {
					// System.out.println("here...");
					Messagebox.show(cve.getCause().getMessage()+" : "+
							"Penambahan dibatalkan.", 
							"Error:"+cve.getErrorCode(), Messagebox.OK,  Messagebox.ERROR);
					// cve.printStackTrace();					
				}
				
			}
		});
		
		customerAddWin.doModal();		
	}

	public void onSelect$customerTabbox(Event event) throws Exception {
		// System.out.println(customerTabbox.getSelectedTab());
		
		loadCustomerInfo();
		
		displayCustomerListInfo();
	}

	private void loadCustomerInfo() throws Exception {
		Tab selTab = customerTabbox.getSelectedTab();
		String alphaGroupDisp = selTab.getValue();

		if (selTab.getIndex()==0) {
			setCustomerList(
					getCustomerDao().findAllCustomer_OrderBy_CompanyName(false));
			
			customerCount = getCustomerList().size();
			
		} else {
			setCustomerList(
					getCustomerDao().findCustomer_OfAlphabet(alphaGroupDisp));
			
			customerCount = getCustomerList().size();
		}
	}
	
	public void onClick$printButton(Event event) throws Exception {
		Map<String, List<Customer>> arg =
				Collections.singletonMap("customerList", getCustomerList());
		
		Window customerReportWin = 
				(Window) Executions.createComponents("/report/print/CustomerReport.zul", null, arg);
		
		customerReportWin.doModal();
		 
	}
	
/*	NOTE: 	Enable this if you need to re-create the customer receivables account
 * 			FOR ALL CUSTOMER.
 * 			Make the button in the zul file 'visible'
 * 
 * 	public void onClick$createReceivableButton(Event event) throws Exception {
		for (Customer customer : getCustomerList()) {
			if (customer.getCustomerReceivable()==null) {
				// create the CustomerReceivable
				CustomerReceivable receivable = createCustomerReceivable();
				receivable.setCustomer(customer);
				
				// set
				customer.setCustomerReceivable(receivable);
				System.out.println("Customer : Receivable : "+receivable.getDocumentSerialNumber().getSerialComp());
				
				// update
				getCustomerDao().update(customer);
				System.out.println("Customer : id : "+customer.getId()+" - updated success.");
			} else {
				// do nothing
			}
		}
	}

	
	private CustomerReceivable createCustomerReceivable() throws Exception {
		CustomerReceivable receivable = new CustomerReceivable();
		receivable.setLastUpdate(asDate(getLocalDate()));
		//  `earliest_due` date DEFAULT NULL,
		receivable.setEarliestDue(null);
		//  `latest_due` date DEFAULT NULL,
		receivable.setLatestDue(null);
		//  `total_receivable` decimal(19,2) DEFAULT NULL,
		receivable.setTotalReceivable(BigDecimal.ZERO);
		//  `total_ppn_receivable` decimal(19,2) DEFAULT NULL,
		receivable.setTotalPpnReceivable(BigDecimal.ZERO);
		//  `payment_complete` char(1) DEFAULT NULL,
		receivable.setPaymentComplete(false);
		//  `amount_paid` decimal(19,2) DEFAULT NULL,
		receivable.setAmountPaid(BigDecimal.ZERO);
		//  `amount_remaining` decimal(19,2) DEFAULT NULL,
		receivable.setAmountRemaining(BigDecimal.ZERO);
		//  `note` varchar(255) DEFAULT NULL,  
		receivable.setNote("");
		//  `customer_id_fk` bigint(20) DEFAULT NULL,
		// --> set it later... during save
		//  `receivable_number_id_fk` bigint(20) DEFAULT NULL,  
		receivable.setDocumentSerialNumber(
				createDocumentSerialNumber(DocumentType.RECEIVABLE, asDate(getLocalDate())));
		return receivable;
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
*/

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

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}
}
