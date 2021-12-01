package com.pyramix.swi.webui.faktur;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
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

import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.customerorder.ProductType;
import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.faktur.FakturProduct;
import com.pyramix.swi.domain.faktur.FakturType;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.persistence.faktur.dao.FakturDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

/**
 * FakturDialogControl - for FakturDialog.zul
 * 
 * This is a controller for the dialog to CREATE (new), EDIT, or VIEW a
 * DeliveryOrder.  Note the followings when modifying this dialog:
 * 
 * 1. This dialog is ONLY called from FakturListInfo.zul when user clicks
 *    the 'addButton'.
 * 2. Faktur is usually created in the CustomerOrder when user
 *    creates a new SuratJalan.  The faktur is usually created
 *    created automatically when user creates a SuratJalan.
 * 3. This dialog ONLY provides additional support for the user when
 *    a manual process of creating a Faktur is needed.
 * 4. The following defines the type of Faktur:
 *      Type 1 (penjualan) - case #2 above - customer order related
 *      Type 2 (manual)    - case #3 above - manual / nothing related
 *       
 * @author rusli
 *
 */
public class FakturDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2248054823350431811L;

	private FakturDao fakturDao;
	private SerialNumberGenerator serialNumberGenerator;
	
	private Window fakturDialogWin;
	private Textbox fakturNoTextbox, customerTextbox, noteTextbox, 
		subTotalTextbox, ppnTextbox, totalTextbox;
	private Datebox fakturDatebox;
	private Combobox pembayaranCombobox;
	private Intbox jumlahHariIntbox;	
	private Listbox productListbox;
	private Button customerButton, saveButton, printButton, cancelButton,
		addButton, resetProductButton;
	private Label infoFakturProductlabel;
	private Checkbox ppnCheckbox;
	
	private FakturData fakturData;
	private PageMode pageMode;
	private Faktur faktur;
	private BigDecimal accSubTotal 	= BigDecimal.ZERO;
	private BigDecimal ppnTotal		= BigDecimal.ZERO;
	private BigDecimal fakturTotal	= BigDecimal.ZERO;
	
	// to add faktur products
	private ListModelList<FakturProduct> fakturProductListModelList;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setFakturData(
				(FakturData) arg.get("fakturData"));
	}

	public void onCreate$fakturDialogWin(Event event) throws Exception {
		// set date format and locale
		fakturDatebox.setFormat(getLongDateFormat());
		fakturDatebox.setLocale(getLocale());
		
		// set pembayaran - payment type for the combobox
		setPaymentTypeCombobox();
		
		// set the page mode
		setPageMode(
				getFakturData().getPageMode());
		
		// set window title
		switch (getPageMode()) {
		case EDIT:
			fakturDialogWin.setTitle("Merubah (Edit) Faktur"); 
			break;
		case NEW:
			fakturDialogWin.setTitle("Membuat (Add) Faktur");
			break;
		case VIEW:
			fakturDialogWin.setTitle("Informasi Faktur");
			break;
		default:
			break;
		}
		
		// set current faktur data
		setFaktur(getFakturData().getFaktur());
		
		// display info
		setFakturInfo();
		
		// set ReadOnly - which data can edit
		setReadOnly();
	}

	private void setPaymentTypeCombobox() {
		for (PaymentType paymentType : PaymentType.values()) {
			Comboitem comboitem = new Comboitem();
			
			comboitem.setLabel(paymentType.toString());
			comboitem.setValue(paymentType);
			comboitem.setParent(pembayaranCombobox);		
		}
	}

	public void onSelect$pembayaranCombobox(Event event) throws Exception {
		if (pembayaranCombobox.getSelectedIndex()>0) {
			// set enable
			jumlahHariIntbox.setValue(0);
			jumlahHariIntbox.setDisabled(false);
			
			// enable selection of customer
			customerTextbox.setValue("");
			customerTextbox.setDisabled(false);
			customerButton.setDisabled(false);			
		} else {
			// make disable
			jumlahHariIntbox.setValue(0);
			jumlahHariIntbox.setDisabled(true);

			// set customer to null -- since pembayaran is 'tunai'
			customerTextbox.setValue("tunai");
			customerTextbox.setAttribute("customer", null);
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
		}
	}
	
	public void onCheck$ppnCheckbox(Event event) throws Exception {
		if (ppnCheckbox.isChecked()) {
			// calc ppn
			ppnTotal = accSubTotal.multiply(new BigDecimal(0.1));
			ppnTextbox.setValue(toLocalFormat(ppnTotal));
			ppnTextbox.setAttribute("ppnTotal", ppnTotal.setScale(0, RoundingMode.HALF_EVEN));
			// calc fakturTotal
			fakturTotal = accSubTotal.add(ppnTotal);
			// display
			totalTextbox.setValue(toLocalFormat(fakturTotal));
			totalTextbox.setAttribute("fakturTotal", fakturTotal.setScale(0, RoundingMode.HALF_EVEN));								
		} else {
			// ppn set to 0 (zero)
			ppnTotal = BigDecimal.ZERO;
			ppnTextbox.setValue(toLocalFormat(ppnTotal));
			ppnTextbox.setAttribute("ppnTotal", ppnTotal.setScale(0, RoundingMode.HALF_EVEN));
			// calc fakturTotal - ppn is 0 (zero)								
			fakturTotal = accSubTotal.add(ppnTotal);
			// display
			totalTextbox.setValue(toLocalFormat(fakturTotal));
			totalTextbox.setAttribute("fakturTotal", accSubTotal.setScale(0, RoundingMode.HALF_EVEN));
		}
	}
	
	public void onClick$customerButton(Event event) throws Exception {
		Window customerDialogWin = 
				(Window) Executions.createComponents("/customer/CustomerListDialog.zul", null, null);
		
		customerDialogWin.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Customer selCustomer = (Customer) event.getData();
				
				customerTextbox.setValue(selCustomer.getCompanyType()+". "+
						selCustomer.getCompanyLegalName());
				customerTextbox.setAttribute("customer", selCustomer);				
			}
		});
		
		customerDialogWin.doModal();
	}
	
	private void setFakturInfo() throws Exception {
		if (getFaktur().getId().compareTo(Long.MIN_VALUE)==0) {
			// create NEW
			fakturNoTextbox.setValue("");
			fakturDatebox.setValue(asDate(getLocalDate()));
			
			// set customer to null -- since pembayaran is 'tunai'
			customerTextbox.setValue("tunai");
			customerTextbox.setAttribute("customer", null);
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
			
			// payment set to tunai
			pembayaranCombobox.setSelectedIndex(0);			
			jumlahHariIntbox.setValue(0);
			jumlahHariIntbox.setDisabled(true);
			
			noteTextbox.setValue("");
			
			// create a new list for the product
			getFaktur().setFakturProducts(new ArrayList<FakturProduct>());
			// create a new list model list for the product -- for the listbox
			setFakturProductListModelList(
					new ListModelList<FakturProduct>());
			
			productListbox.setModel(getFakturProductListModelList());
			productListbox.setItemRenderer(getFakturProductListitemRenderer());
		} else {
			// edit or view
			fakturNoTextbox.setValue(getFaktur().getFakturNumber().getSerialComp());
			fakturDatebox.setValue(getFaktur().getFakturDate());
			// faktur customer by proxy
			Faktur fakturCustomerByProxy = getFakturDao().findCustomerByProxy(getFaktur().getId());
			if (fakturCustomerByProxy.getCustomer()==null) {
				// no customer object
				customerTextbox.setValue("tunai");
				customerTextbox.setAttribute("customer", null);
			} else {
				// use customer object
				customerTextbox.setValue(fakturCustomerByProxy.getCustomer().getCompanyType()+". "+
						fakturCustomerByProxy.getCustomer().getCompanyLegalName());
				customerTextbox.setAttribute("customer", fakturCustomerByProxy.getCustomer());				
			}
			// set selected combobox item according to payment type
			for (Comboitem item : pembayaranCombobox.getItems()) {
				if (faktur.getPaymentType().equals(item.getValue())) {
					pembayaranCombobox.setSelectedItem(item);
				}
			}
			// pembayaran & jumlah hari
			// boolean tunaiPayment = faktur.getPaymentType().compareTo(PaymentType.tunai)==0;
			jumlahHariIntbox.setValue(getFaktur().getJumlahHari());
			// use PPN?
			ppnCheckbox.setChecked(getFaktur().isUsePpn());
			// catatan
			noteTextbox.setValue(getFaktur().getNote());
			// products
			productListbox.setModel(
					new ListModelList<FakturProduct>(getFaktur().getFakturProducts()));
			productListbox.setItemRenderer(getFakturProductListitemRenderer());
		}
	}

	private ListitemRenderer<FakturProduct> getFakturProductListitemRenderer() {
		
		return new ListitemRenderer<FakturProduct>() {
			
			@Override
			public void render(Listitem item, FakturProduct product, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell((index+1)+".");
				lc.setParent(item);
				
				// Qty (Sht/Line)
				lc = initQtyShtLine(new Listcell(), product);
				lc.setParent(item);
				
				// Qty (Kg)
				lc = initQtyKg(new Listcell(), product);
				lc.setParent(item);
				
				// Kode
				lc = initInventoryCode(new Listcell(), product);
				lc.setParent(item);
				
				// No.Coil
				lc = initNoCoil(new Listcell(), product);
				lc.setParent(item);
				
				// Spesifikasi
				lc = initSpesifikasi(new Listcell(), product);
				lc.setParent(item);
				
				// Unit
				lc = initUnit(new Listcell(), product);
				lc.setParent(item);
				
				// Harga (Rp.)
				lc = initHarga(new Listcell(), product);
				lc.setParent(item);
				
				// SubTotal (Rp.)
				lc = initSubTotal(new Listcell(), product);
				lc.setParent(item);
				
				item.setValue(product);
				
				if (product.getId().compareTo(Long.MIN_VALUE)!=0) {
					// NOT new
					accSubTotal = accSubTotal.add(product.getP_subTotal());
				}
			}

			private Listcell initUnit(Listcell listcell, FakturProduct product) {
				Checkbox unitCheckbox = new Checkbox();
				unitCheckbox.setChecked(false);
				unitCheckbox.setLabel("Sht");
				unitCheckbox.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						if (unitCheckbox.isChecked()) {
							unitCheckbox.setLabel("Kg");
						} else {
							unitCheckbox.setLabel("Sht");
						}
						
					}
				});
				unitCheckbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initQtyShtLine(Listcell listcell, FakturProduct product) {
				Intbox qtyShtLine = new Intbox();
				qtyShtLine.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						0 : product.getP_sheetQuantity());
				qtyShtLine.setStyle("text-align:right;");
				qtyShtLine.setWidth("80px");
				qtyShtLine.setParent(listcell);
				
				return listcell;
			}

			private Listcell initQtyKg(Listcell listcell, FakturProduct product) {
				Decimalbox qtyKgDecimalbox = new Decimalbox();
				qtyKgDecimalbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						BigDecimal.ZERO : product.getP_weightQuantity());
				qtyKgDecimalbox.setStyle("text-align:right;");
				qtyKgDecimalbox.setWidth("60px");
				qtyKgDecimalbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initInventoryCode(Listcell listcell, FakturProduct product) {
				Textbox inventoryCodeTextbox = new Textbox();
				inventoryCodeTextbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						"" : product.getP_inventoryCode());
				inventoryCodeTextbox.setWidth("100px");
				inventoryCodeTextbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initNoCoil(Listcell listcell, FakturProduct product) {
				Textbox coilNoTextbox = new Textbox();
				coilNoTextbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						"" : product.getP_inventoryMarking());
				coilNoTextbox.setWidth("80px");
				coilNoTextbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initSpesifikasi(Listcell listcell, FakturProduct product) {
				Textbox spesifikasiTextbox = new Textbox();
				spesifikasiTextbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						"" : product.getP_description());
				spesifikasiTextbox.setWidth("180px");
				spesifikasiTextbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initHarga(Listcell listcell, FakturProduct product) {
				Decimalbox hargaDecimalbox = new Decimalbox();
				hargaDecimalbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ?
						BigDecimal.ZERO : product.getP_unitPrice());
				hargaDecimalbox.setStyle("text-align:right;");
				hargaDecimalbox.setLocale(getLocale());
				hargaDecimalbox.setWidth("100px");
				hargaDecimalbox.setDisabled(getPageMode().compareTo(PageMode.NEW)==0 ?
						false : true);
				hargaDecimalbox.setParent(listcell);
				
				// only when NEW
				if (getPageMode().compareTo(PageMode.NEW)==0) {					
					Button calcButton = new Button();
					calcButton.setLabel("...");
					calcButton.setClass("selectButton");
					calcButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
						
						@Override
						public void onEvent(Event event) throws Exception {
							BigDecimal unitPrice = hargaDecimalbox.getValue();
							// check the unit (index#6) -- if sht, use children index#1, else index#2
							// access the listitem from the event parameter var
							Listitem listitem = (Listitem) event.getTarget().getParent().getParent();
							// index#6
							Checkbox unitCheckbox = (Checkbox) listitem.getChildren().get(6).getFirstChild();
							// index#8
							Decimalbox subTotalDecimalbox = (Decimalbox) listitem.getChildren().get(8).getFirstChild();
							// sht or kg?
							if (unitCheckbox.isChecked()) {
								// kg -- use index#2
								Decimalbox kgDecimalbox = (Decimalbox) listitem.getChildren().get(2).getFirstChild();
								BigDecimal kgQty = kgDecimalbox.getValue();
								// calc the subTotal and put in children index#8
								subTotalDecimalbox.setValue(kgQty.multiply(unitPrice));
							} else {
								// sht -- use index#1
								Intbox shtIntbox = (Intbox) listitem.getChildren().get(1).getFirstChild();
								int shtQty = shtIntbox.getValue();
								// calc the subTotal and put in children index#8
								subTotalDecimalbox.setValue(unitPrice.multiply(new BigDecimal(shtQty)));
							}
							// reset accSubTotal
							accSubTotal = BigDecimal.ZERO;
							// accumulate subTotal
							for (int i = 0; i < productListbox.getItemCount(); i++) {
								Listitem listitemProduct = productListbox.getItemAtIndex(i);
								// subTotal decimalbox -- index#8
								Decimalbox subTotalForAcc = (Decimalbox) listitemProduct.getChildren().get(8).getFirstChild(); 
								accSubTotal = accSubTotal.add(subTotalForAcc.getValue());
							}
							// display
							subTotalTextbox.setValue(toLocalFormat(accSubTotal));
							// ppn?
							if (ppnCheckbox.isChecked()) {
								// calc ppn
								ppnTotal = accSubTotal.multiply(new BigDecimal(0.1));
								ppnTextbox.setValue(toLocalFormat(ppnTotal));
								ppnTextbox.setAttribute("ppnTotal", ppnTotal.setScale(0, RoundingMode.HALF_EVEN));
								// calc fakturTotal
								fakturTotal = accSubTotal.add(ppnTotal);
								// display
								totalTextbox.setValue(toLocalFormat(fakturTotal));
								totalTextbox.setAttribute("fakturTotal", fakturTotal.setScale(0, RoundingMode.HALF_EVEN));								
							} else {
								// calc fakturTotal - no ppn								
								fakturTotal = accSubTotal.add(BigDecimal.ZERO);
								ppnTextbox.setValue(toLocalFormat(ppnTotal));
								ppnTextbox.setAttribute("ppnTotal", ppnTotal.setScale(0, RoundingMode.HALF_EVEN));
								// display
								totalTextbox.setValue(toLocalFormat(fakturTotal));
								totalTextbox.setAttribute("fakturTotal", accSubTotal.setScale(0, RoundingMode.HALF_EVEN));
							}
						}
					});
					calcButton.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initSubTotal(Listcell listcell, FakturProduct product) {
				Decimalbox subTotalDecimalbox = new Decimalbox();
				subTotalDecimalbox.setValue(getPageMode().compareTo(PageMode.NEW)==0 ? 
						BigDecimal.ZERO : product.getP_subTotal());
				subTotalDecimalbox.setStyle("text-align:right;");
				subTotalDecimalbox.setLocale(getLocale());
				subTotalDecimalbox.setWidth("100px");
				subTotalDecimalbox.setDisabled(getPageMode().compareTo(PageMode.NEW)==0 ?
						false : true);
				subTotalDecimalbox.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$productListbox(Event event) throws Exception {
		infoFakturProductlabel.setValue("Faktur: "+productListbox.getItemCount()+" items");
		
		if (getPageMode().compareTo(PageMode.NEW)==0) {
			// new access children index#8 and accumulate the subTotal
			// System.out.println("onAfterRender...after calc subTotal...???...NOT...");
		} else {
			// NOT new
			subTotalTextbox.setValue(toLocalFormat(accSubTotal));
			ppnTextbox.setValue(toLocalFormat(getFaktur().getTotalPpn()));
			totalTextbox.setValue(toLocalFormat(getFaktur().getTotalOrder()));
		}
	}
	
	public void onClick$resetProductButton(Event event) throws Exception {
		// EDIT only
		for (int i = 0; i < productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);

			// get the components
			Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
			Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
			Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
			Textbox coilNoTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
			Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(5).getFirstChild();

			FakturProduct product = item.getValue();
			qtyShtIntbox.setValue(product.getSheetQuantity());
			qtyWeightDecimalbox.setValue(product.getWeightQuantity());
			coilNoTextbox.setValue(product.getInventoryMarking());
			spesifikasiTextbox.setValue(product.getDescription());
			if (product.getInventoryCode()==null) {
				// faktur created manually -- no inventory code selected
				kodeTextbox.setValue(product.getP_inventoryCode());
			} else {
				// faktur created from customer order
				kodeTextbox.setValue(product.getInventoryCode().getProductCode());				
			}
		}
	}
	
	public void onClick$addButton(Event event) throws Exception {
		
		// NEW
		FakturProduct product = new FakturProduct();
		
		product.setSheetQuantity(0);
		product.setWeightQuantity(BigDecimal.ZERO);
		product.setInventoryCode(null);
		product.setInventoryMarking("");
		product.setDescription("");
		// by kg or sheet
		product.setByKg(false);
		product.setUnitPrice(BigDecimal.ZERO);
		product.setSubTotal(BigDecimal.ZERO);
		
		getFakturProductListModelList().add(product);
	}
	
	private void setReadOnly() {
		switch (getPageMode()) {
		case EDIT:
			fakturDatebox.setDisabled(true);
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
			pembayaranCombobox.setDisabled(true);
			jumlahHariIntbox.setDisabled(true);
			ppnCheckbox.setDisabled(true);
			noteTextbox.setDisabled(false);
			
			addButton.setVisible(false);
			resetProductButton.setVisible(true);
			
			saveButton.setVisible(true);
			printButton.setVisible(true);
			cancelButton.setVisible(true);
			
			break;
		case NEW:
			fakturDatebox.setDisabled(false);
			// customerTextbox.setDisabled(false);
			// customerButton.setDisabled(false);
			pembayaranCombobox.setDisabled(false);
			// jumlahHariIntbox.setDisabled(false);
			ppnCheckbox.setDisabled(false);
			noteTextbox.setDisabled(false);
			
			addButton.setVisible(true);
			resetProductButton.setVisible(false);

			saveButton.setVisible(true);
			printButton.setVisible(false);
			cancelButton.setVisible(true);
			
			break;
		case VIEW:
			fakturDatebox.setDisabled(true);
			customerTextbox.setDisabled(true);
			customerButton.setDisabled(true);
			pembayaranCombobox.setDisabled(true);
			jumlahHariIntbox.setDisabled(true);
			ppnCheckbox.setDisabled(true);
			noteTextbox.setDisabled(true);
			
			addButton.setVisible(false);
			resetProductButton.setVisible(false);

			saveButton.setVisible(false);
			printButton.setVisible(true);
			cancelButton.setVisible(true);

			break;
		default:
			break;
		}
	}	
	
	public void onClick$saveButton(Event event) throws Exception {
		if (getFaktur().getId().compareTo(Long.MIN_VALUE)==0) {
			// new
			getFaktur().setFakturDate(fakturDatebox.getValue());
			getFaktur().setFakturType(FakturType.manual);
			getFaktur().setPaymentType(
					pembayaranCombobox.getSelectedItem().getValue());
			getFaktur().setJumlahHari(jumlahHariIntbox.getValue());
			getFaktur().setUsePpn(ppnCheckbox.isChecked());
			getFaktur().setTotalOrder(
					(BigDecimal) totalTextbox.getAttribute("fakturTotal"));
			getFaktur().setTotalPpn(
					(BigDecimal) ppnTextbox.getAttribute("ppnTotal"));
			getFaktur().setNote(noteTextbox.getValue());
			getFaktur().setFakturNumber(
					createDocumentSerialNumber(DocumentType.FAKTUR, fakturDatebox.getValue()));
			getFaktur().setCustomer(
					(Customer) customerTextbox.getAttribute("customer"));
			getFaktur().setFakturStatus(DocumentStatus.NORMAL);
			// no surat jalan associated with this faktur
			getFaktur().setSuratJalan(null);
			// create date
			getFaktur().setCreateDate(asDate(getLocalDate()));
			getFaktur().setModifiedDate(asDateTime(getLocalDateTime()));
			getFaktur().setCheckDate(asDateTime(getLocalDateTime()));
			// user create
			getFaktur().setUserCreate(getFakturData().getUserCreate());
			
			// products
			getFaktur().setFakturProducts(getNewFakturProducts());
		} else {
			// edit
			getFaktur().setNote(noteTextbox.getValue());

			// modified datetime
			getFaktur().setModifiedDate(asDateTime(getLocalDateTime()));
			getFaktur().setCheckDate(asDateTime(getLocalDateTime()));
			
			// get updated faktur product
			getFaktur().setFakturProducts(getEditedFakturProducts());
		}
		
		Events.sendEvent(Events.ON_OK, fakturDialogWin, getFaktur());
		
		fakturDialogWin.detach();
	}
	
	private List<FakturProduct> getNewFakturProducts() {
		List<FakturProduct> userModFakturProducts = getFaktur().getFakturProducts();
		
		for (int i=0; i<productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);
			
			// get the components
			Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
			Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
			Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
			Textbox coilNoTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
			Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
			Checkbox byKgCheckbox = (Checkbox) item.getChildren().get(6).getFirstChild();
			Decimalbox hargaDecimalbox = (Decimalbox) item.getChildren().get(7).getFirstChild();
			Decimalbox subTotalDecimalbox = (Decimalbox) item.getChildren().get(8).getFirstChild();

			// access the each product from the faktur product list
			FakturProduct product = item.getValue();

			product.setProductType(ProductType.barang);
			product.setInventoryMarking(coilNoTextbox.getValue());
			product.setDescription(spesifikasiTextbox.getValue());
			product.setByKg(byKgCheckbox.isChecked());
			product.setSheetQuantity(qtyShtIntbox.getValue());
			product.setWeightQuantity(qtyWeightDecimalbox.getValue());
			product.setUnitPrice(hargaDecimalbox.getValue());
			product.setSubTotal(subTotalDecimalbox.getValue());
			product.setDiscount(false);
			product.setDiscountPercent(BigDecimal.ZERO);
			product.setUsePpn(false);
			product.setInventoryPacking(InventoryPacking.lembaran);
			product.setInventoryLocation(InventoryLocation.swi);
			product.setInventoryCode(null);
			
			// set product's for printing attribute
			product.setP_sheetQuantity(qtyShtIntbox.getValue());
			product.setP_weightQuantity(qtyWeightDecimalbox.getValue());
			product.setP_inventoryCode(kodeTextbox.getValue());
			product.setP_inventoryMarking(coilNoTextbox.getValue());
			product.setP_description(spesifikasiTextbox.getValue());
			product.setP_unitPrice(hargaDecimalbox.getValue());
			product.setP_subTotal(subTotalDecimalbox.getValue());
		
			userModFakturProducts.add(product);
		}
		
		return userModFakturProducts;
	}

	private List<FakturProduct> getEditedFakturProducts() {
		List<FakturProduct> userModFakturProducts = getFaktur().getFakturProducts();
		
		for (int i=0; i<productListbox.getItemCount(); i++) {
			Listitem item = productListbox.getItemAtIndex(i);
			
			// get the components
			Intbox qtyShtIntbox = (Intbox) item.getChildren().get(1).getFirstChild();
			Decimalbox qtyWeightDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
			Textbox kodeTextbox = (Textbox) item.getChildren().get(3).getFirstChild();
			Textbox coilNoTextbox = (Textbox) item.getChildren().get(4).getFirstChild();
			Textbox spesifikasiTextbox = (Textbox) item.getChildren().get(5).getFirstChild();
			Decimalbox hargaDecimalbox = (Decimalbox) item.getChildren().get(7).getFirstChild();
			Decimalbox subTotalDecimalbox = (Decimalbox) item.getChildren().get(8).getFirstChild();
			
			// access the each product from the faktur product list
			FakturProduct product = userModFakturProducts.get(i);
			
			// set product's attribute
			product.setP_sheetQuantity(qtyShtIntbox.getValue());
			product.setP_weightQuantity(qtyWeightDecimalbox.getValue());
			product.setP_inventoryCode(kodeTextbox.getValue());
			product.setP_inventoryMarking(coilNoTextbox.getValue());
			product.setP_description(spesifikasiTextbox.getValue());
			product.setP_unitPrice(hargaDecimalbox.getValue());
			product.setP_subTotal(subTotalDecimalbox.getValue());
		}
		
		return userModFakturProducts;
	}

	public void onClick$printButton(Event event) throws Exception {
		FakturData fakturData = new FakturData();
		fakturData.setFaktur(getFaktur());
		fakturData.setSuratJalan(null);
		
		Map<String, FakturData> arg =
				Collections.singletonMap("fakturData", fakturData);
		
		Window fakturPrintWin = 
				(Window) Executions.createComponents("/faktur/FakturPrint.zul", null, arg);
		
		fakturPrintWin.doModal();
		
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		fakturDialogWin.detach();
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
	
	/**
	 * @return the fakturData
	 */
	public FakturData getFakturData() {
		return fakturData;
	}

	/**
	 * @param fakturData the fakturData to set
	 */
	public void setFakturData(FakturData fakturData) {
		this.fakturData = fakturData;
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
	 * @return the faktur
	 */
	public Faktur getFaktur() {
		return faktur;
	}

	/**
	 * @param faktur the faktur to set
	 */
	public void setFaktur(Faktur faktur) {
		this.faktur = faktur;
	}

	/**
	 * @return the fakturDao
	 */
	public FakturDao getFakturDao() {
		return fakturDao;
	}

	/**
	 * @param fakturDao the fakturDao to set
	 */
	public void setFakturDao(FakturDao fakturDao) {
		this.fakturDao = fakturDao;
	}

	/**
	 * @return the fakturProductListModelList
	 */
	public ListModelList<FakturProduct> getFakturProductListModelList() {
		return fakturProductListModelList;
	}

	/**
	 * @param fakturProductListModelList the fakturProductListModelList to set
	 */
	public void setFakturProductListModelList(ListModelList<FakturProduct> fakturProductListModelList) {
		this.fakturProductListModelList = fakturProductListModelList;
	}

	/**
	 * @return the serialNumberGenerator
	 */
	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	/**
	 * @param serialNumberGenerator the serialNumberGenerator to set
	 */
	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}


}
