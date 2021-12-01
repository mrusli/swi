package com.pyramix.swi.webui.customer;

import java.math.BigDecimal;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.CompanyType;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.persistence.customer.dao.CustomerDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

public class CustomerDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1069186858669764422L;

	private SerialNumberGenerator serialNumberGenerator;
	private CustomerDao customerDao;
	
	private Window customerDialogWin;
	private Combobox organizationTypeCombobox;
	private Textbox organizationLegalNameTextbox, contactPersonTextbox, phoneTextbox, 
		extensionTextbox, emailTextbox, faxTextbox, address01Textbox, address02Textbox,
		cityTextbox, postalCodeTextbox, noteTextbox;
	private Checkbox activeCheckbox;
	private Label receivableInfoLabel;
	
	private CustomerData customerData;
	private PageMode pageMode;
	private CustomerReceivable customerReceivable;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerData((CustomerData) arg.get("customerData"));
	}

	public void onCreate$customerDialogWin(Event event) throws Exception {
		setPageMode(
				getCustomerData().getPageMode());
		
		// init
		setCustomerReceivable(null);
		
		setupOrganizationTypeCombobox();
		
		switch (getPageMode()) {
		case EDIT:
			customerDialogWin.setTitle("Merubah (Edit) Customer");			
			break;
		case NEW:
			customerDialogWin.setTitle("Menambah (Add) Customer");
			break;
		default:
			break;
		}
		
		setCustomerInfo();
	}
	
	private void setupOrganizationTypeCombobox() {
		Comboitem comboitem;

		for (CompanyType companyType : CompanyType.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(companyType.toString());
			comboitem.setValue(companyType);
			comboitem.setParent(organizationTypeCombobox);
		}
		
	}

	private void setCustomerInfo() throws Exception {
		Customer customer = getCustomerData().getCustomer();
		
		if (customer.getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			organizationTypeCombobox.setSelectedIndex(0);
			organizationLegalNameTextbox.setValue("");
			contactPersonTextbox.setValue("");
			phoneTextbox.setValue(""); 
			extensionTextbox.setValue("");
			emailTextbox.setValue("");
			faxTextbox.setValue("");
			address01Textbox.setValue("");
			address02Textbox.setValue("");
			cityTextbox.setValue("");
			postalCodeTextbox.setValue("");
			noteTextbox.setValue("");
			activeCheckbox.setChecked(true);
			
			// set disabled to false so that user can create Receivable
			// createReceivableButton.setDisabled(false);
			receivableInfoLabel.setValue("No Account Receivable: - dibuat setelah 'Save'");
		} else {
			// edit
			for (Comboitem item : organizationTypeCombobox.getItems()) {
				if (item.getValue().equals(customer.getCompanyType())) {
					organizationTypeCombobox.setSelectedItem(item);
				}
			}
			organizationLegalNameTextbox.setValue(customer.getCompanyLegalName());
			contactPersonTextbox.setValue(customer.getContactPerson());
			phoneTextbox.setValue(customer.getPhone()); 
			extensionTextbox.setValue(customer.getExtension());
			emailTextbox.setValue(customer.getEmail());
			faxTextbox.setValue(customer.getFax());
			address01Textbox.setValue(customer.getAddress01());
			address02Textbox.setValue(customer.getAddress02());
			cityTextbox.setValue(customer.getCity());
			postalCodeTextbox.setValue(customer.getPostalCode());
			noteTextbox.setValue(customer.getNote());
			activeCheckbox.setChecked(customer.isActive());
			
			// depending whether the receivable has been created or not, allow the user
			// to create the receivable
			// createReceivableButton.setDisabled(customer.getCustomerReceivable()!=null);
			CustomerReceivable receivableProxy = getCustomerReceivableByProxy(customer.getId());
			receivableInfoLabel.setValue(receivableProxy==null ? 
					"No Account Receivable:  - dibuat setelah 'Save'" : 
					"No Account Receivable: "+
						receivableProxy.getDocumentSerialNumber().getSerialComp());
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
	
	public void onClick$saveButton(Event event) throws Exception {
		Customer userModCustomer = getCustomerData().getCustomer();

		if (userModCustomer.getId()==Long.MIN_VALUE) {
			// new			
			setCustomerReceivable(createCustomerReceivable());			
		} else {
			// edit
			
			if (userModCustomer.getCustomerReceivable()==null) {
				// no receivable yet
				setCustomerReceivable(createCustomerReceivable());
			} else {
				CustomerReceivable receivableByProxy = getCustomerReceivableByProxy(userModCustomer.getId());
				
				setCustomerReceivable(receivableByProxy);
			}
		}		
		
		userModCustomer.setCompanyType(organizationTypeCombobox.getSelectedItem().getValue());
		userModCustomer.setCompanyLegalName(organizationLegalNameTextbox.getValue());
		userModCustomer.setContactPerson(contactPersonTextbox.getValue());
		userModCustomer.setAddress01(address01Textbox.getValue());
		userModCustomer.setAddress02(address02Textbox.getValue());
		userModCustomer.setCity(cityTextbox.getValue());
		userModCustomer.setPostalCode(postalCodeTextbox.getValue());
		userModCustomer.setPhone(phoneTextbox.getValue());
		userModCustomer.setExtension(extensionTextbox.getValue());
		userModCustomer.setEmail(emailTextbox.getValue());
		userModCustomer.setFax(faxTextbox.getValue());
		userModCustomer.setNote(noteTextbox.getValue());
		userModCustomer.setActive(activeCheckbox.isChecked());
		
		// can we do this? -- can, no problem
		getCustomerReceivable().setCustomer(userModCustomer);
		// then set to customer
		userModCustomer.setCustomerReceivable(getCustomerReceivable());
		
		// send event
		Events.sendEvent(getPageMode().compareTo(PageMode.EDIT)==0 ?
				Events.ON_CHANGE : Events.ON_OK, customerDialogWin, userModCustomer);
		
		// detach
		customerDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		customerDialogWin.detach();
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

	private CustomerReceivable getCustomerReceivableByProxy(long id) throws Exception {
		Customer customer = getCustomerDao().findCustomerReceivableByProxy(id);
	
		return customer.getCustomerReceivable();
	}
	
	
	public CustomerData getCustomerData() {
		return customerData;
	}

	public void setCustomerData(CustomerData customerData) {
		this.customerData = customerData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public CustomerReceivable getCustomerReceivable() {
		return customerReceivable;
	}

	public void setCustomerReceivable(CustomerReceivable customerReceivable) {
		this.customerReceivable = customerReceivable;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
}
