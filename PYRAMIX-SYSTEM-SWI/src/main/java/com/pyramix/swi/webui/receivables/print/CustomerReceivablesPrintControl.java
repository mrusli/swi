package com.pyramix.swi.webui.receivables.print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderProductDao;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageHandler;

public class CustomerReceivablesPrintControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8612436271599991691L;

	private CustomerReceivableDao customerReceivableDao;
	private CustomerOrderDao customerOrderDao;
	private CustomerOrderProductDao customerOrderProductDao;
	
	private Iframe iframe;
	
	private CustomerReceivable customerReceivable;
	private PageHandler handler = new PageHandler();
		
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setCustomerReceivable(
				(CustomerReceivable) arg.get("selectedReceivable"));
	}
	
	public void onCreate$customerReceivablesPrintWin(Event event) throws Exception {

		Customer customerByProxy = getCustomerByProxy(getCustomerReceivable().getId());

		String nomorPerusahaan = "NP-"+formatTo4DigitWithLeadingZeo(getCustomerReceivable().getId().intValue())+" : ";
		String namaPerusahaan = nomorPerusahaan+customerByProxy.getCompanyType()+". "+
				customerByProxy.getCompanyLegalName();
		
		String totalReceivable = toLocalFormat(getCustomerReceivable().getTotalReceivable());
		
		List<CustomerReceivableHeader> receivableHeaderList = new ArrayList<CustomerReceivableHeader>();
				
		int activitySize = getCustomerReceivable().getCustomerReceivableActivities().size();
		
		for (int i = 0; i < activitySize; i++) {
			CustomerReceivableActivity activity = 
					getCustomerReceivable().getCustomerReceivableActivities().get(i);

			if (activity.getReceivableStatus().compareTo(DocumentStatus.BATAL)==0) {
				continue;
			}
			
			CustomerReceivableHeader header = new CustomerReceivableHeader();
			// obtain customerOrder ref
			CustomerOrder customerOrder = 
					getCustomerOrderDao().findCustomerOrderById(activity.getCustomerOrderId());
			
			// set Order-No.
			header.setOrderNo(
					customerOrder.getDocumentSerialNumber().getSerialComp());
			// set orderDate
			header.setOrderDate(dateToStringDisplay(
					asLocalDate(activity.getSalesDate()), getShortDateFormat()));
			
			// set dueDate
			header.setDueDate(dateToStringDisplay(
					asLocalDate(activity.getPaymentDueDate()), getShortDateFormat()));
			
			// set totalOrder
			header.setTotalOrder(toLocalFormat(activity.getAmountSales()));
			
			// set paymentDate
			header.setPaymentDate(activity.getPaymentDate()==null ? " - " :
					dateToStringDisplay(
							asLocalDate(activity.getPaymentDate()), getLongDateFormat()));
			
			// set paymentAmount
			header.setPaymentAmount(toLocalFormat(activity.getAmountPaid()));
			
			// set remainingAmount / status
			header.setRemainingAmount(activity.isPaymentComplete() ? "Lunas" :
					toLocalFormat(activity.getRemainingAmount()));
			
			if ((i+1)!=activitySize) {
				// can lookup
				CustomerReceivableActivity lookupActivity = 
						getCustomerReceivable().getCustomerReceivableActivities().get(i+1);
				// obtain customerOrder ref
				CustomerOrder lookupCustomerOrder = 
						getCustomerOrderDao().findCustomerOrderById(lookupActivity.getCustomerOrderId());
				
				if (customerOrder.getDocumentSerialNumber().getSerialComp().compareTo(lookupCustomerOrder.getDocumentSerialNumber().getSerialComp())==0) {
					// no need the details
					
				} else {
					// set detail from CustomerOrderProduct
					header.setCustomerReceivableDetails(getCustomerReceivableDetailsFromCustomerOrderProduct(
							new ArrayList<CustomerReceivableDetail>(), customerOrder.getCustomerOrderProducts()));					
				}
				
			} else {
				// set detail from CustomerOrderProduct
				header.setCustomerReceivableDetails(getCustomerReceivableDetailsFromCustomerOrderProduct(
						new ArrayList<CustomerReceivableDetail>(), customerOrder.getCustomerOrderProducts()));				
			}
			
			if (customerOrder.isUsePpn()) {
				// add a ppn row
				CustomerReceivableDetail detail = new CustomerReceivableDetail();
				
				detail.setSheetQuantity("");
				detail.setWeightQuantity("");
				detail.setInventoryCode("");
				detail.setDescription("PPN 10%");
				detail.setSellingPrice("");
				detail.setSellingSubTotal(toLocalFormat(customerOrder.getTotalPpn()));
				detail.setQuantityUnit("");
				detail.setBdSellingSubTotal(customerOrder.getTotalPpn());
				
				if (header.getCustomerReceivableDetails()!=null) {
					header.getCustomerReceivableDetails().add(detail);
				}
			}
			
			// add
			receivableHeaderList.add(header);
		}
		
		
		// dataField and dataList to pass data into the JasperReport
		HashMap<String, Object> dataField = new HashMap<String, Object>();
		HashMap<String, Object> dataList = new HashMap<String, Object>();
		
		dataList.put("headerData", receivableHeaderList);
		dataField.put("printedDate", dateToStringDisplay(getLocalDate(), getLongDateFormat()));
		dataField.put("namaPerusahaan", namaPerusahaan);
		dataField.put("totalReceivable", totalReceivable);
		
		iframe.setContent(handler.generateReportAMedia(dataField, dataList, 
				"/receivables/print/CustomerReceivablesPrint.jasper", 
				"LaporanPiutang-"+
				dateToStringDisplay(getLocalDate(), getEmphYearMonthShort())));		

	}

	private List<CustomerReceivableDetail> getCustomerReceivableDetailsFromCustomerOrderProduct(
			ArrayList<CustomerReceivableDetail> receivableDetail, List<CustomerOrderProduct> customerOrderProducts) 
					throws Exception {
		
		for (CustomerOrderProduct product : customerOrderProducts) {
			CustomerReceivableDetail detail = new CustomerReceivableDetail();
			
			detail.setSheetQuantity(getFormatedInteger(product.getSheetQuantity()));
			detail.setWeightQuantity(getFormatedFloatLocal(product.getWeightQuantity()));
			
			InventoryCode inventoryCodeByProxy = getInventoryCodeByProxy(product.getId());
			detail.setInventoryCode(inventoryCodeByProxy.getProductCode());
			detail.setDescription(product.getDescription());
			detail.setSellingPrice(toLocalFormat(product.getSellingPrice()));
			detail.setQuantityUnit("/"+(product.isByKg() ? "KG" : "SHT"));
			detail.setSellingSubTotal(toLocalFormat(product.getSellingSubTotal()));
			detail.setBdSellingSubTotal(product.getSellingSubTotal());
			
			receivableDetail.add(detail);
			
		}
		
		return receivableDetail;
	}

	private InventoryCode getInventoryCodeByProxy(long id) throws Exception {
		CustomerOrderProduct product = getCustomerOrderProductDao().findInventoryCodeByProxy(id);
		
		return product.getInventoryCode();
	}

	private Customer getCustomerByProxy(long id) throws Exception {
		CustomerReceivable receivable = getCustomerReceivableDao().findCustomerByProxy(id);
		
		return receivable.getCustomer();
	}

	public CustomerReceivable getCustomerReceivable() {
		return customerReceivable;
	}

	public void setCustomerReceivable(CustomerReceivable customerReceivable) {
		this.customerReceivable = customerReceivable;
	}

	public CustomerReceivableDao getCustomerReceivableDao() {
		return customerReceivableDao;
	}

	public void setCustomerReceivableDao(CustomerReceivableDao customerReceivableDao) {
		this.customerReceivableDao = customerReceivableDao;
	}

	public CustomerOrderDao getCustomerOrderDao() {
		return customerOrderDao;
	}

	public void setCustomerOrderDao(CustomerOrderDao customerOrderDao) {
		this.customerOrderDao = customerOrderDao;
	}

	public CustomerOrderProductDao getCustomerOrderProductDao() {
		return customerOrderProductDao;
	}

	public void setCustomerOrderProductDao(CustomerOrderProductDao customerOrderProductDao) {
		this.customerOrderProductDao = customerOrderProductDao;
	}

}
