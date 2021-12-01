package com.pyramix.swi.webui.inventory.deliveryorder;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Vbox;

import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderProduct;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.settings.Settings;
import com.pyramix.swi.persistence.deliveryorder.dao.DeliveryOrderDao;
import com.pyramix.swi.persistence.settings.dao.SettingsDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PrintUtil;

public class DeliveryOrderPrintControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5515022750447334767L;

	private DeliveryOrderDao deliveryOrderDao;
	private SettingsDao settingsDao;
	
	private Label deliveryOrderDateLabel, companyNameLabel, deliveryOrderNumberLabel,
		companyAddress01Label, companyAddress02Label, companyCityLabel, companyTelephoneLabel,
		companyFaxLabel, noteLabel, referensiSuratJalanLabel, companyLabel;
	private Grid deliveryOrderProductGrid;
	private Vbox printVbox;
	
	private DeliveryOrder deliveryOrder;
	private DeliveryOrderData deliveryOrderData;
	
	private final long DEFAULT_COMPANY_ID	= 1L;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setDeliveryOrderData(
				(DeliveryOrderData) arg.get("deliveryOrderData"));
	}

	public void onCreate$deliveryOrderPrintWin(Event event) throws Exception {
		setDeliveryOrder(
				getDeliveryOrderData().getDeliveryOrder());
		Settings settings = getSettingsDao().findSettingsById(DEFAULT_COMPANY_ID);
		Company company = settings.getSelectedCompany();
		
		deliveryOrderDateLabel.setValue(dateToStringDisplay(
				asLocalDate(getDeliveryOrder().getDeliveryOrderDate()), getLongDateFormat()));
		deliveryOrderNumberLabel.setValue(
				getDeliveryOrder().getDeliveryOrderNumber().getSerialComp());
		
		Company companyByProxy = getCompanyByProxy(getDeliveryOrder().getId());
		companyNameLabel.setValue(companyByProxy.getCompanyType()+". "+
				companyByProxy.getCompanyLegalName());
		companyAddress01Label.setValue(companyByProxy.getAddress01());
		companyAddress02Label.setValue(companyByProxy.getAddress02());
		companyCityLabel.setValue(companyByProxy.getCity());
		companyTelephoneLabel.setValue("Tel:"+companyByProxy.getPhone());
		companyFaxLabel.setValue("Fax:"+companyByProxy.getFax());
		
		deliveryOrderProductGrid.setModel(
				new ListModelList<DeliveryOrderProduct>(
						getDeliveryOrder().getDeliveryOrderProducts()));
		deliveryOrderProductGrid.setRowRenderer(new RowRenderer<DeliveryOrderProduct>() {

			@Override
			public void render(Row row, DeliveryOrderProduct product, int index) throws Exception {
				// No.
				row.appendChild(new Label(String.valueOf(index+1)+"."));
				
				// Qty(Sht)
				row.appendChild(new Label(getFormatedInteger(product.getP_sheetQuantity())));
				
				// Qty(Kg)
				row.appendChild(new Label(getFormatedFloatLocal(product.getP_weightQuantity())));
				
				// Tipe
				row.appendChild(new Label(product.getP_inventoryCode()));
				
				// Deskripsi
				row.appendChild(new Label(product.getP_description()));
				
				// Coil No.
				row.appendChild(new Label(product.getP_marking()));
			}
		});
		
		noteLabel.setValue(getDeliveryOrder().getNote());
		if (getDeliveryOrderData().getSuratJalan()==null) {
			referensiSuratJalanLabel.setValue(" ");
		} else {
			referensiSuratJalanLabel.setValue(
					"D/O sesuai dengan SuratJalan No.:"+getDeliveryOrderData().getSuratJalan().getSuratJalanNumber().getSerialComp());
		}
		
		companyLabel.setValue(company.getCompanyType()+". "+
				company.getCompanyLegalName());
	}
	
	public void onClick$printButton(Event event) throws Exception {
		
		PrintUtil.print(printVbox);
		
	}

	private Company getCompanyByProxy(long id) throws Exception {
		DeliveryOrder deliveryOrder = getDeliveryOrderDao().findCompanyByProxy(id);
		
		return deliveryOrder.getCompany();
	}

	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public DeliveryOrderDao getDeliveryOrderDao() {
		return deliveryOrderDao;
	}

	public void setDeliveryOrderDao(DeliveryOrderDao deliveryOrderDao) {
		this.deliveryOrderDao = deliveryOrderDao;
	}

	public DeliveryOrderData getDeliveryOrderData() {
		return deliveryOrderData;
	}

	public void setDeliveryOrderData(DeliveryOrderData deliveryOrderData) {
		this.deliveryOrderData = deliveryOrderData;
	}

	public SettingsDao getSettingsDao() {
		return settingsDao;
	}

	public void setSettingsDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}
}
