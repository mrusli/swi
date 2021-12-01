package com.pyramix.swi.webui.voucher;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.webui.common.PageMode;

public class VoucherSalesData {

	private VoucherSales voucherSales;
	
	private CustomerOrder customerOrder;
	
	private PageMode pageMode;

	public VoucherSales getVoucherSales() {
		return voucherSales;
	}

	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	} 
	
}
