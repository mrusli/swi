package com.pyramix.swi.webui.company;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.webui.common.GFCBaseController;

public class CompanyDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6381294370976525893L;

	private Window companyDialogWin;
	private Textbox companyLegalNameTextbox, companyDisplayNameTextbox, phoneTextbox,
		faxTextbox, emailTextbox, address01Textbox, address02Textbox, cityTextbox,
		postalCodeTextbox;
	
	private Company company;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCompany(
				(Company) arg.get("company"));
	}

	public void onCreate$companyDialogWin(Event event) throws Exception {
		companyLegalNameTextbox.setValue(getCompany().getCompanyType().toString()+"."+
				getCompany().getCompanyLegalName());
		companyDisplayNameTextbox.setValue(
				getCompany().getCompanyDisplayName());
		phoneTextbox.setValue(getCompany().getPhone());
		faxTextbox.setValue(getCompany().getFax());
		emailTextbox.setValue(getCompany().getEmail());
		address01Textbox.setValue(getCompany().getAddress01());
		address02Textbox.setValue(getCompany().getAddress02());
		cityTextbox.setValue(getCompany().getCity());
		postalCodeTextbox.setValue(getCompany().getPostalCode());
	}
	
	public void onClick$saveButton(Event event) throws Exception {
		getCompany().setPhone(phoneTextbox.getValue());
		getCompany().setFax(faxTextbox.getValue());
		getCompany().setEmail(emailTextbox.getValue());
		getCompany().setAddress01(address01Textbox.getValue());
		getCompany().setAddress02(address02Textbox.getValue());
		getCompany().setCity(cityTextbox.getValue());
		getCompany().setPostalCode(postalCodeTextbox.getValue());
		
		Events.sendEvent(Events.ON_OK, companyDialogWin, getCompany());
		
		companyDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		companyDialogWin.detach();
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}
