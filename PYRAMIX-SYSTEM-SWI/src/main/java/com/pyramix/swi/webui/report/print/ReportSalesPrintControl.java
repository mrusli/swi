package com.pyramix.swi.webui.report.print;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageHandler;
import com.pyramix.swi.webui.report.ReportSalesData;

public class ReportSalesPrintControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7033936581633441834L;

	private CustomerOrderDao customerOrderDao;
	private CustomerOrderProductDao customerOrderProductDao;
	
	private Iframe iframe;
	
	private ReportSalesData reportSalesData;
	private List<CustomerOrder> customerOrderList;
	private PageHandler handler= new PageHandler();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setReportSalesData(
				(ReportSalesData) arg.get("reportSalesData"));
	}

	public void onCreate$reportSalesPrintWin(Event event) throws Exception {
		setCustomerOrderList(getReportSalesData().getCustomerOrders());
		
		BigDecimal totalPenjTunai 	= BigDecimal.ZERO;
		BigDecimal totalPenjKredit	= BigDecimal.ZERO;
		
		List<ReportSalesHeader> headerPrintList = new ArrayList<ReportSalesHeader>();
		for (CustomerOrder customerOrder : getCustomerOrderList()) {
			ReportSalesHeader header = new ReportSalesHeader();
			
			header.setId(customerOrder.getId());
			header.setCustomerOrderNo(customerOrder.getDocumentSerialNumber().getSerialComp());
			SuratJalan suratJalanByProxy = getSuratJalanByProxy(customerOrder.getId());
			header.setSuratJalanNo(
				suratJalanByProxy==null ? "-" : 
				suratJalanByProxy.getSuratJalanNumber().getSerialComp());
			Customer customerByProxy = getCustomerByProxy(customerOrder.getId());
			if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
				header.setNamaCustomer(customerByProxy==null? "BATAL - Tunai" :
					"BATAL - " + customerByProxy.getCompanyType().toString()+". "+
					customerByProxy.getCompanyLegalName());								
			} else {
				header.setNamaCustomer(customerByProxy==null? "Tunai" :
					customerByProxy.getCompanyType().toString()+". "+
					customerByProxy.getCompanyLegalName());				
			}
			EmployeeCommissions employeeCommissionsByProxy = 
			 		getEmployeeCommissionsByProxy(customerOrder.getId());
			header.setNamaSales(employeeCommissionsByProxy==null ? " - " : 
					employeeCommissionsByProxy.getEmployee().getName());
			header.setPembayaran(customerOrder.getPaymentType().compareTo(PaymentType.tunai)==0 ?
					"Tunai" : customerOrder.getPaymentType().toString()+"-"+customerOrder.getCreditDay()+" Hari");
			
			BigDecimal subTotal = customerOrder.getTotalOrder().subtract(customerOrder.getTotalPpn());
			header.setOrderSubTotal(toLocalFormat(subTotal));
			header.setOrderPpnTotal(toLocalFormat(customerOrder.getTotalPpn()));
			header.setOrderTotal(toLocalFormat(customerOrder.getTotalOrder()));
			
			List<ReportSalesDetail> detailPrintList = new ArrayList<ReportSalesDetail>();
			
			BigDecimal subTotalPenj = BigDecimal.ZERO;
			
			// detail for each header
			for (CustomerOrderProduct product : customerOrder.getCustomerOrderProducts()) {
				ReportSalesDetail detailData = new ReportSalesDetail();

				detailData.setIdDetail(product.getId());
				detailData.setQuantity(product.isByKg() ? 
						getFormatedFloatLocal(product.getWeightQuantity())+" Kg." :
							getFormatedInteger(product.getSheetQuantity())+" Sht.");
				InventoryCode inventoryCodeByProxy = getInventoryCodeByProxy(product.getId());
				detailData.setCode(inventoryCodeByProxy.getProductCode());
				detailData.setDescription(product.getDescription());
				if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
					detailData.setPrice(toLocalFormat(BigDecimal.ZERO));
					detailData.setSubTotal(toLocalFormat(BigDecimal.ZERO));
					detailData.setBdSubTotal(BigDecimal.ZERO);					
				} else {
					detailData.setPrice(toLocalFormat(product.getSellingPrice()));
					detailData.setSubTotal(toLocalFormat(product.getSellingSubTotal()));
					detailData.setBdSubTotal(product.getSellingSubTotal());
				}
								
				if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
					subTotalPenj = subTotalPenj.add(BigDecimal.ZERO);
				} else {
					// acc
					subTotalPenj = subTotalPenj.add(product.getSellingSubTotal());
				}
				detailPrintList.add(detailData);
			}
			if (customerOrder.isUsePpn()) {
				// add a ppn row
				ReportSalesDetail detailData = new ReportSalesDetail();

				detailData.setQuantity("");
				detailData.setCode("");
				detailData.setDescription("PPN 10%");
				detailData.setPrice("");
				if (customerOrder.getOrderStatus().compareTo(DocumentStatus.BATAL)==0) {
					detailData.setSubTotal(toLocalFormat(BigDecimal.ZERO));
					detailData.setBdSubTotal(BigDecimal.ZERO);
					// acc
					subTotalPenj = subTotalPenj.add(BigDecimal.ZERO);
				} else {
					detailData.setSubTotal(toLocalFormat(customerOrder.getTotalPpn()));
					detailData.setBdSubTotal(customerOrder.getTotalPpn());
					// acc
					subTotalPenj = subTotalPenj.add(customerOrder.getTotalPpn());
				}

				detailPrintList.add(detailData);
			}
			header.setSalesDetails(detailPrintList);
			
			if (customerOrder.getPaymentType().compareTo(PaymentType.tunai)==0) {
				totalPenjTunai = totalPenjTunai.add(subTotalPenj);
			} else {
				totalPenjKredit = totalPenjKredit.add(subTotalPenj);
			}
			
			headerPrintList.add(header);
		}

		BigDecimal totalPenj = totalPenjTunai.add(totalPenjKredit);
		
		HashMap<String, Object> dataField = new HashMap<String, Object>();
		HashMap<String, Object> dataList = new HashMap<String, Object>();
		dataList.put("headerData", headerPrintList);
		dataField.put("totalPenjTunai", toLocalFormat(totalPenjTunai));
		dataField.put("totalPenjKredit", toLocalFormat(totalPenjKredit));
		dataField.put("totalPenj", toLocalFormat(totalPenj));
		dataField.put("startDate", dateToStringDisplay(asLocalDate(getReportSalesData().getStartDate()),
				getLongDateFormat()));
		dataField.put("endDate", dateToStringDisplay(asLocalDate(getReportSalesData().getEndDate()),
				getLongDateFormat()));
		dataField.put("printedDate", dateToStringDisplay(getLocalDate(), getShortDateFormat()));
		
		iframe.setContent(handler.generateReportAMedia(dataField, dataList, 
				"/report/print/ReportSalesPrint.jasper", 
				"LaporanPenjualan-"+
				dateToStringDisplay(getLocalDate(), getEmphYearMonthShort())));
	}
	
	private InventoryCode getInventoryCodeByProxy(long id) throws Exception {
		CustomerOrderProduct customerOrderProduct = getCustomerOrderProductDao().findInventoryCodeByProxy(id);
		
		return customerOrderProduct.getInventoryCode();
	}

	private EmployeeCommissions getEmployeeCommissionsByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findEmployeeCommissionsByProxy(id);
		
		return customerOrder.getEmployeeCommissions();
	}

	private SuratJalan getSuratJalanByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findSuratJalanByProxy(id);
		
		return customerOrder.getSuratJalan();
	}

	private Customer getCustomerByProxy(long id) throws Exception {
		CustomerOrder customerOrder = getCustomerOrderDao().findCustomerByProxy(id);
		
		return customerOrder.getCustomer();
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public ReportSalesData getReportSalesData() {
		return reportSalesData;
	}

	public void setReportSalesData(ReportSalesData reportSalesData) {
		this.reportSalesData = reportSalesData;
	}

	public List<CustomerOrder> getCustomerOrderList() {
		return customerOrderList;
	}

	public void setCustomerOrderList(List<CustomerOrder> customerOrderList) {
		this.customerOrderList = customerOrderList;
	}

	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}

}
