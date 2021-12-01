package com.pyramix.swi.webui.suratjalan;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.webui.common.PageMode;

public class SuratJalanData {

	private CustomerOrder customerOrder;
	
	private SuratJalan suratJalan;
	
	private boolean deliveryOrderRequired;
	
	private Company deliveryOrderCompany;
	
	private PageMode pageMode;

	private User userCreate;
	
	private String requestPath;
	
	// CustomerOrder Note - to - SuratJalan Note
	private String note;
	
	/**
	 * @return the suratJalan
	 */
	public SuratJalan getSuratJalan() {
		return suratJalan;
	}

	/**
	 * @param suratJalan the suratJalan to set
	 */
	public void setSuratJalan(SuratJalan suratJalan) {
		this.suratJalan = suratJalan;
	}

	/**
	 * @return the pageMode
	 */
	public PageMode getPageMode() {
		return pageMode;
	}

	/**
	 * @param pageMode the pageMode to set
	 */
	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	/**
	 * @return the customerOrder
	 */
	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	/**
	 * @param customerOrder the customerOrder to set
	 */
	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	/**
	 * @return boolean
	 */
	public boolean isDeliveryOrderRequired() {
		return deliveryOrderRequired;
	}

	/**
	 * @param deliveryOrderRequired
	 */
	public void setDeliveryOrderRequired(boolean deliveryOrderRequired) {
		this.deliveryOrderRequired = deliveryOrderRequired;
	}

	/**
	 * @return deliveryOrderCompany
	 */
	public Company getDeliveryOrderCompany() {
		return deliveryOrderCompany;
	}

	/**
	 * @param deliveryOrderCompany
	 */
	public void setDeliveryOrderCompany(Company deliveryOrderCompany) {
		this.deliveryOrderCompany = deliveryOrderCompany;
	}

	/**
	 * @return the userCreate
	 */
	public User getUserCreate() {
		return userCreate;
	}

	/**
	 * @param userCreate the userCreate to set
	 */
	public void setUserCreate(User userCreate) {
		this.userCreate = userCreate;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
}
