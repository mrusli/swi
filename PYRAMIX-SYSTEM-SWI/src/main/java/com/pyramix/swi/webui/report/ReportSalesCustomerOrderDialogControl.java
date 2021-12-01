package com.pyramix.swi.webui.report;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Include;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class ReportSalesCustomerOrderDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231516L;

	private CustomerOrderDao customerOrderDao;
	private SuratJalanDao suratJalanDao;
	
	private Window reportSalesCustomerOrderDialogWin;
	private Include suratJalanInclude, deliveryOrderInclude, fakturInclude, voucherSalesInclude,
		customerOrderInclude;
	private Tab deliveryOrderTab;
	
	private CustomerOrder customerOrder;

	private final Logger log = Logger.getLogger(ReportSalesCustomerOrderDialogControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerOrder((CustomerOrder) arg.get("customerOrder"));

	}

	public void onCreate$reportSalesCustomerOrderDialogWin(Event event) throws Exception {
		log.info("Creating ReportSalesCustomerOrderDialog");
		// suratJalan from customerOrder
		CustomerOrder customerOrderSJByProxy =
				getCustomerOrderDao().findSuratJalanByProxy(getCustomerOrder().getId());
		
		log.info(customerOrderSJByProxy.getSuratJalan().toString());

		suratJalanInclude.setDynamicProperty("suratJalan", customerOrderSJByProxy.getSuratJalan());
		suratJalanInclude.setSrc("/report/panel/PanelSuratJalan.zul");

		// deliveryOrder from suratJalan
		SuratJalan suratJalanDOByProxy =
				getSuratJalanDao().findDeliveryOrderByProxy(customerOrderSJByProxy.getSuratJalan().getId());
		
		log.info(suratJalanDOByProxy.getDeliveryOrder()==null? "DeliveryOrder is NULL" : suratJalanDOByProxy.getDeliveryOrder().toString());
		
		// makes tab visible if deliveryOrder is NOT null
		deliveryOrderTab.setVisible(suratJalanDOByProxy.getDeliveryOrder()!=null);
		
		deliveryOrderInclude.setDynamicProperty("deliveryOrder", suratJalanDOByProxy.getDeliveryOrder());
		deliveryOrderInclude.setSrc("/report/panel/PanelDeliveryOrder.zul");
		
		// faktur from suratJalan
		SuratJalan suratJalanFktByProxy =
				getSuratJalanDao().findFakturByProxy(customerOrderSJByProxy.getSuratJalan().getId());
		
		log.info(suratJalanFktByProxy.getFaktur().toString());
		
		fakturInclude.setDynamicProperty("faktur", suratJalanFktByProxy.getFaktur());
		fakturInclude.setSrc("/report/panel/PanelFaktur.zul");
		
		// voucherSales from CustomerOrder
		CustomerOrder customerOrderVSByProxy =
				getCustomerOrderDao().findVoucherSalesByProxy(getCustomerOrder().getId());
		
		log.info(customerOrderVSByProxy.getVoucherSales().toString());
		
		voucherSalesInclude.setDynamicProperty("voucherSales", customerOrderVSByProxy.getVoucherSales());
		voucherSalesInclude.setSrc("/report/panel/PanelVoucherSales.zul");
		
		// customerOrder
		log.info(getCustomerOrder().toString());
		
		customerOrderInclude.setDynamicProperty("customerOrder", getCustomerOrder());
		customerOrderInclude.setSrc("/report/panel/PanelCustomerOrder.zul");
	}

	public void onClick$closeButton(Event event) throws Exception {
		reportSalesCustomerOrderDialogWin.detach();
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public SuratJalanDao getSuratJalanDao() {
		return suratJalanDao;
	}

	public void setSuratJalanDao(SuratJalanDao suratJalanDao) {
		this.suratJalanDao = suratJalanDao;
	}


}
