package com.pyramix.swi.webui.inventory.deliveryorder;

import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.webui.common.PageMode;

public class DeliveryOrderData {

	private DeliveryOrder deliveryOrder;
	
	private SuratJalan suratJalan;

	private PageMode pageMode;
	
	private User userCreate;
	
	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public SuratJalan getSuratJalan() {
		return suratJalan;
	}

	public void setSuratJalan(SuratJalan suratJalan) {
		this.suratJalan = suratJalan;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public User getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(User userCreate) {
		this.userCreate = userCreate;
	}

	
}
