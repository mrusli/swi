package com.pyramix.swi.webui.inventory.deliveryorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderProduct;
import com.pyramix.swi.domain.deliveryorder.DeliveryOrderType;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.persistence.deliveryorder.dao.DeliveryOrderDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

/**
 * DeliveryOrderDialogControl - for DeliveryOrderDialog.zul
 * 
 * This is a controller for the dialog to CREATE (new), EDIT, or VIEW a
 * DeliveryOrder.  Note the followings when modifying this dialog:
 * 
 * 1. This dialog is ONLY called from DeliveryOrderListInfo.zul from
 *    the 'addButton'.
 * 2. DeliveryOrder is usually created in the CustomerOrder when user
 *    creates a customer order.  The delivery order is usually created
 *    as 'per needed' basis and it is created automatically when user
 *    creates a SuratJalan.
 * 3. A DeliveryOrder is also created when an inventory is moved or
 *    transferred from one location to another, thus updating the
 *    inventory automatically.
 * 4. This dialog ONLY provides additional support for user when
 *    a manual process of creating a DeliveryOrder is needed.
 * 5. The following defines the type of DeliveryOrder:
 *      Type 0 (pindah)    - case #3 above - inventory related
 *      Type 1 (penjualan) - case #2 above - customer order related
 *      Type 2 (manual)    - case #4 above - manual / nothing related 
 * 
 * @author rusli
 *
 */
public class DeliveryOrderDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7153552321374892517L;

	private CompanyDao companyDao;
	private DeliveryOrderDao deliveryOrderDao;
	private SerialNumberGenerator serialNumberGenerator;
	
	private Window deliveryOrderDialogWin;
	private Datebox deliveryDatebox;
	private Button saveButton, printButton, cancelButton,
		resetProductButton, addButton;
	private Textbox deliveryOrderNoTextbox, noteTextbox;
	private Combobox locationCombobox;
	private Listbox productListbox;
	private Label infoDeliveryOrderProductlabel;
	
	private DeliveryOrder deliveryOrder;
	private DeliveryOrderData deliveryOrderData;
	private PageMode pageMode;
	
	private Company loc_swi, loc_wsm_str, loc_wsm_krg;
	// private List<DeliveryOrderProduct> deliveryOrderProducts;
	private ListModelList<DeliveryOrderProduct> deliveryOrderProductsListModelList;
	
	private static final DocumentStatus STATUS_NORMAL = DocumentStatus.NORMAL;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setDeliveryOrderData(
				(DeliveryOrderData) arg.get("deliveryOrderData"));
	}

	public void onCreate$deliveryOrderDialogWin(Event event) throws Exception {
		// set data format
		deliveryDatebox.setFormat(getLongDateFormat());
		deliveryDatebox.setLocale(getLocale());
		
		// set page mode
		setPageMode(
				getDeliveryOrderData().getPageMode());
		
		// set window title
		switch (getPageMode()) {
		case EDIT:
			deliveryOrderDialogWin.setTitle("Merubah (Edit) Delivery Order"); 
			break;
		case NEW:
			deliveryOrderDialogWin.setTitle("Membuat (Add) Delivery Order");
			break;
		case VIEW:
			deliveryOrderDialogWin.setTitle("Informasi Delivery Order");
			break;
		default:
			break;
		}

		// get delivery order location - organization / company objects
		List<Company> deliveryOrderLocations = getCompanyDao().findAllCompany();
		setLoc_swi(deliveryOrderLocations.get(0));
		setLoc_wsm_str(deliveryOrderLocations.get(1));
		setLoc_wsm_krg(deliveryOrderLocations.get(2));
		
		// set locationCombobox - swi
		Comboitem loc_swiComboitem = new Comboitem();
		loc_swiComboitem.setLabel(getLoc_swi().getCompanyDisplayName());
		loc_swiComboitem.setValue(getLoc_swi());
		loc_swiComboitem.setParent(locationCombobox);
		// set locationCombobox - str
		Comboitem loc_wsm_strComboitem = new Comboitem();
		loc_wsm_strComboitem.setLabel(getLoc_wsm_str().getCompanyDisplayName());
		loc_wsm_strComboitem.setValue(getLoc_wsm_str());
		loc_wsm_strComboitem.setParent(locationCombobox);
		// set locationCombobox - krg
		Comboitem loc_wsm_krgComboitem = new Comboitem();
		loc_wsm_krgComboitem.setLabel(getLoc_wsm_krg().getCompanyDisplayName());
		loc_wsm_krgComboitem.setValue(getLoc_wsm_krg());
		loc_wsm_krgComboitem.setParent(locationCombobox);
		
		// set delivery order -- if 'new' create DeliveryOrder else use delivery order data
		setDeliveryOrder(getPageMode().compareTo(PageMode.NEW)==0 ?
				new DeliveryOrder() : getDeliveryOrderData().getDeliveryOrder());
		
		// display data
		displaySuratJalanInfo();
		
		// set which data can / can not edit
		setReadOnly();
	}

	private void displaySuratJalanInfo() throws Exception {
		if (getDeliveryOrder().getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			deliveryOrderNoTextbox.setValue("");
			deliveryDatebox.setValue(asDate(getLocalDate()));
			
			// defaulted to 1st item
			locationCombobox.setSelectedIndex(0);
			noteTextbox.setValue("");
			
			// create delivery order product list
			getDeliveryOrder().setDeliveryOrderProducts(
					new ArrayList<DeliveryOrderProduct>());

			// create ListModelList with DeliveryOrderProduct
			// NOTE: in order to add item into the list,
			// MUST add into the ListModelList
			setDeliveryOrderProductsListModelList(
					new ListModelList<DeliveryOrderProduct>());
			productListbox.setModel(getDeliveryOrderProductsListModelList());
			productListbox.setItemRenderer(getDeliveryOrderProductsListitemRenderer());	
			
		} else {
			// edit or view
			deliveryOrderNoTextbox.setValue(getDeliveryOrder().getDeliveryOrderNumber().getSerialComp());
			deliveryDatebox.setValue(getDeliveryOrder().getDeliveryOrderDate());
			
			// use delivery order by proxy
			DeliveryOrder deliveryOrderByProxy = getDeliveryOrderDao().findCompanyByProxy(getDeliveryOrder().getId());
			locationCombobox.setSelectedItem(getLocationComboitem(deliveryOrderByProxy.getCompany()));
			noteTextbox.setValue(getDeliveryOrder().getNote());
			
			productListbox.setModel(
					new ListModelList<DeliveryOrderProduct>(getDeliveryOrder().getDeliveryOrderProducts()));
			productListbox.setItemRenderer(getDeliveryOrderProductsListitemRenderer());
		}
		
	}
	
	private ListitemRenderer<DeliveryOrderProduct> getDeliveryOrderProductsListitemRenderer() {
		
		return new ListitemRenderer<DeliveryOrderProduct>() {
			
			@Override
			public void render(Listitem item, DeliveryOrderProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(String.valueOf(index+1)+".");
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = intQtySht(new Listcell(), product);
				lc.setParent(item);
				
				// Qty (Kg)
				lc = initQtyKg(new Listcell(), product);
				lc.setParent(item);
				
				// Kode
				lc = initKode(new Listcell(), product);
				lc.setParent(item);
				
				// Spesifikasi
				lc = initSpesifikasi(new Listcell(), product);
				lc.setParent(item);
				
				// No.Coil
				lc = initNoCoil(new Listcell(), product);
				lc.setParent(item);
				
				item.setValue(product);
			}

			private Listcell intQtySht(Listcell listcell, DeliveryOrderProduct product) {
				Intbox intbox = new Intbox();
				intbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						product.getSheetQuantity() : product.getP_sheetQuantity());
				intbox.setWidth("70px");
				intbox.setParent(listcell);

				return listcell;
			}

			private Listcell initQtyKg(Listcell listcell, DeliveryOrderProduct product) {
				Decimalbox decimalbox = new Decimalbox();
				decimalbox.setLocale(getLocale());
				decimalbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						product.getWeightQuantity() : product.getP_weightQuantity());
				decimalbox.setWidth("70px");
				decimalbox.setParent(listcell);

				return listcell;
			}

			private Listcell initKode(Listcell listcell, DeliveryOrderProduct product) throws Exception {
				Textbox textbox = new Textbox();
				
				if (getPageMode().compareTo(PageMode.NEW)==0) {
					// NEW
					textbox.setValue(product.getP_inventoryCode());
					
				} else {
					// EDIT
					// by proxy
					DeliveryOrderProduct deliveryOrderProductByProxy = getDeliveryOrderDao().findInventoryCodeByProxy(product.getId());
					if (deliveryOrderProductByProxy.getInventoryCode()==null) {
						// delivery order created manually - no inventory is selected for this delivery order
						textbox.setValue(product.getP_inventoryCode());					
					} else {
						// delivery order create in customer order
						textbox.setValue(deliveryOrderProductByProxy.getInventoryCode().getProductCode());
					}
				}
				
				textbox.setWidth("110px");
				textbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initSpesifikasi(Listcell listcell, DeliveryOrderProduct product) {
				Textbox textbox = new Textbox();
				textbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						product.getDescription() : product.getP_description());
				textbox.setWidth("190px");
				textbox.setParent(listcell);

				return listcell;
			}

			private Listcell initNoCoil(Listcell listcell, DeliveryOrderProduct product) {
				Textbox textbox = new Textbox();
				textbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						product.getMarking() : product.getP_marking());
				textbox.setWidth("90px");
				textbox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$productListbox(Event event) throws Exception {
		
		infoDeliveryOrderProductlabel.setValue("DeliveryOrder: "+productListbox.getItemCount()+" items");
	}
	
	public void onClick$addButton(Event event) throws Exception {
		if (productListbox.getItemCount()>4) {
			
			throw new Exception("Maksimum "+String.valueOf(productListbox.getItemCount())+" item untuk Delivery Order");
		}
		// NEW
		DeliveryOrderProduct product = new DeliveryOrderProduct();
		product.setSheetQuantity(0);
		product.setWeightQuantity(BigDecimal.ZERO);
		// set to null -- manual create -- not related to any inventoryCode
		product.setInventoryCode(null);
		product.setP_inventoryCode(" ");
		product.setDescription(" ");
		product.setMarking(" ");

		getDeliveryOrderProductsListModelList().add(product);
		
	}
	
	public void onClick$resetProductButton(Event event) throws Exception {
		// EDIT

		for (int i = 0; i < productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);

		 	Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
		 	Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
		 	Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
		 	Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
		 	Textbox nocoilTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
		 	
		 	DeliveryOrderProduct product = item.getValue();
		 	qtyShtIntbox.setValue(product.getSheetQuantity());
		 	qtyWeightDecimalbox.setValue(product.getWeightQuantity());
		 	// inventoryCode by proxy
		 	DeliveryOrderProduct deliveryOrderProductByProxy = getDeliveryOrderDao().findInventoryCodeByProxy(product.getId());
			if (deliveryOrderProductByProxy.getInventoryCode()==null) {
				// delivery order created manually - no inventory is selected for this delivery order
				kodeTextbox.setValue(product.getP_inventoryCode());					
			} else {
				// delivery order created from customer order
				kodeTextbox.setValue(deliveryOrderProductByProxy.getInventoryCode().getProductCode());
			}
			spesifikasiTextbox.setValue(product.getDescription());
		 	nocoilTextbox.setValue(product.getMarking());
		}
	}
	
	public void onClick$printButton(Event event) throws Exception {
		// print
		DeliveryOrderData deliveryOrderData = new DeliveryOrderData();
		deliveryOrderData.setDeliveryOrder(getDeliveryOrder());
		deliveryOrderData.setSuratJalan(null);

		Map<String, DeliveryOrderData> arg =
				Collections.singletonMap("deliveryOrderData", deliveryOrderData);
		
		Window deliveryOrderPrintWin = 
				(Window) Executions.createComponents("/deliveryorder/DeliveryOrderPrint.zul", null, arg);
		
		deliveryOrderPrintWin.doModal();
	}
	
	private Comboitem getLocationComboitem(Company company) {
		int index = 0;
		Comboitem item = null;
		
		while (locationCombobox.getItemCount()>0) {
			item = locationCombobox.getItems().get(index);
			
			if (item.getValue().equals(company)) {
				break;
			}
			
			index++;
		}
		
		return item;
	}

	private void setReadOnly() {

		switch (getPageMode()) {
		case EDIT:
			deliveryDatebox.setDisabled(true); 
			locationCombobox.setDisabled(true);
			noteTextbox.setDisabled(false);
			
			resetProductButton.setVisible(true);
			addButton.setVisible(false);
			
			saveButton.setVisible(true);
			printButton.setVisible(true);
			cancelButton.setVisible(true);
			
			break;
		case NEW:
			deliveryDatebox.setDisabled(false);
			locationCombobox.setDisabled(false);
			noteTextbox.setDisabled(false);
			
			resetProductButton.setVisible(false);
			addButton.setVisible(true);

			saveButton.setVisible(true);
			printButton.setVisible(false);
			cancelButton.setVisible(true);
			
			break;
		case VIEW:
			deliveryDatebox.setDisabled(true);
			locationCombobox.setDisabled(true);
			noteTextbox.setDisabled(true);

			resetProductButton.setVisible(false);
			addButton.setVisible(false);

			saveButton.setVisible(false);
			printButton.setVisible(true);
			cancelButton.setVisible(true);

			break;
		default:
			break;
		}
		
	}

	public void onClick$saveButton(Event event) throws Exception {
		if (getDeliveryOrder().getId().compareTo(Long.MIN_VALUE)==0) {
			// NEW - get all the data from the componets including the products
			
			// DeliveryOrderType set 2 (manual)
			DeliveryOrderType deliveryOrderType = DeliveryOrderType.manual;
			
			getDeliveryOrder().setDeliveryOrderDate(deliveryDatebox.getValue());
			getDeliveryOrder().setDeliveryOrderType(deliveryOrderType);
			getDeliveryOrder().setCompany(locationCombobox.getSelectedItem().getValue());
			getDeliveryOrder().setNote(noteTextbox.getValue());
			// status is normal (not batal)
			getDeliveryOrder().setDeliveryOrderStatus(STATUS_NORMAL);
			// delivery order number
			getDeliveryOrder().setDeliveryOrderNumber(
					createDocumentSerialNumber(DocumentType.DELIVERY_ORDER, deliveryDatebox.getValue()));
			// no surat jalan related to this delivery order
			getDeliveryOrder().setSuratJalan(null);
			// create date
			getDeliveryOrder().setCreateDate(asDate(getLocalDate()));
			getDeliveryOrder().setModifiedDate(asDateTime(getLocalDateTime()));
			getDeliveryOrder().setCheckDate(asDateTime(getLocalDateTime()));
			// user create
			getDeliveryOrder().setUserCreate(getDeliveryOrderData().getUserCreate());
			
			// products
			getDeliveryOrder().setDeliveryOrderProducts(getNewDeliveryOrderProducts());
		} else {
			// edit or view - get only the note (in case user adds some notes) and the products
			getDeliveryOrder().setNote(noteTextbox.getValue());
			// modified date
			getDeliveryOrder().setModifiedDate(asDateTime(getLocalDateTime()));
			getDeliveryOrder().setCheckDate(asDateTime(getLocalDateTime()));
			
			// products
			getDeliveryOrder().setDeliveryOrderProducts(getEditDeliveryOrderProducts());
		}
		
		Events.sendEvent(Events.ON_OK, deliveryOrderDialogWin, getDeliveryOrder());
		
		deliveryOrderDialogWin.detach();
		
		// Comboitem item = locationCombobox.getSelectedItem();
		// Company selCompany = item.getValue();
		
		// System.out.println(selCompany);
	}
	
	private List<DeliveryOrderProduct> getNewDeliveryOrderProducts() {
		List<DeliveryOrderProduct> userModDeliveryOrderProducts = 
				getDeliveryOrder().getDeliveryOrderProducts();
		
		for (int i = 0; i < productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);

		 	Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
		 	Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
		 	Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
		 	Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
		 	Textbox nocoilTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
		 	
		 	DeliveryOrderProduct product = item.getValue();
			product.setP_sheetQuantity(qtyShtIntbox.getValue());
			product.setP_weightQuantity(qtyWeightDecimalbox.getValue());
			product.setP_inventoryCode(kodeTextbox.getValue());
			product.setP_description(spesifikasiTextbox.getValue());
			product.setP_marking(nocoilTextbox.getValue());
			
			product.setSheetQuantity(qtyShtIntbox.getValue());			
			product.setWeightQuantity(qtyWeightDecimalbox.getValue());
			product.setInventoryCode(null);
			product.setDescription(spesifikasiTextbox.getValue());
			product.setMarking(nocoilTextbox.getValue());
			
			userModDeliveryOrderProducts.add(product);
		}
		
		
		return userModDeliveryOrderProducts;		
	}

	private List<DeliveryOrderProduct> getEditDeliveryOrderProducts() {
		List<DeliveryOrderProduct> userModDeliveryOrderProducts = 
				getDeliveryOrder().getDeliveryOrderProducts();
		
		for (int i = 0; i < productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);

		 	Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
		 	Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
		 	Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
		 	Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
		 	Textbox nocoilTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
		 	
		 	DeliveryOrderProduct product = userModDeliveryOrderProducts.get(i);
			product.setP_sheetQuantity(qtyShtIntbox.getValue());
			product.setP_weightQuantity(qtyWeightDecimalbox.getValue());
			product.setP_inventoryCode(kodeTextbox.getValue());
			product.setP_description(spesifikasiTextbox.getValue());
			product.setP_marking(nocoilTextbox.getValue());			
		}
				
		return userModDeliveryOrderProducts;
	}	
	
	public void onClick$cancelButton(Event event) throws Exception {
		deliveryOrderDialogWin.detach();
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
	
	public DeliveryOrderData getDeliveryOrderData() {
		return deliveryOrderData;
	}

	public void setDeliveryOrderData(DeliveryOrderData deliveryOrderData) {
		this.deliveryOrderData = deliveryOrderData;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	public DeliveryOrder getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(DeliveryOrder deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public Company getLoc_wsm_str() {
		return loc_wsm_str;
	}

	public void setLoc_wsm_str(Company loc_wsm_str) {
		this.loc_wsm_str = loc_wsm_str;
	}

	public Company getLoc_wsm_krg() {
		return loc_wsm_krg;
	}

	public void setLoc_wsm_krg(Company loc_wsm_krg) {
		this.loc_wsm_krg = loc_wsm_krg;
	}

	public DeliveryOrderDao getDeliveryOrderDao() {
		return deliveryOrderDao;
	}

	public void setDeliveryOrderDao(DeliveryOrderDao deliveryOrderDao) {
		this.deliveryOrderDao = deliveryOrderDao;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public ListModelList<DeliveryOrderProduct> getDeliveryOrderProductsListModelList() {
		return deliveryOrderProductsListModelList;
	}

	public void setDeliveryOrderProductsListModelList(ListModelList<DeliveryOrderProduct> deliveryOrderProductsListModelList) {
		this.deliveryOrderProductsListModelList = deliveryOrderProductsListModelList;
	}

	public Company getLoc_swi() {
		return loc_swi;
	}

	public void setLoc_swi(Company loc_swi) {
		this.loc_swi = loc_swi;
	}
	
}
