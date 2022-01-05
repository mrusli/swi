package com.pyramix.swi.webui.suratjalan;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.swi.persistence.user.dao.UserDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.faktur.FakturData;
import com.pyramix.swi.webui.inventory.deliveryorder.DeliveryOrderData;

public class SuratJalanListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4749064249173577227L;

	private SuratJalanDao suratJalanDao;
	private UserDao userDao;
	
	private Label formTitleLabel, infoSuratJalanlabel;
	private Listbox suratJalanListbox;
	private Tabbox suratJalanPeriodTabbox;
	
	private List<SuratJalan> suratJalanList;
	private int suratjalanCount;
	private BigDecimal totalSuratJalanValue;
	private User loginUser;
	private int selectedPeriodTabboxIndex;
	
	private final int WORK_DAY_WEEK 	= 6;
	private final int DEFAULT_TAB_INDEX	= 1;
	
	public void onCreate$suratJalanListInfoWin(Event event) throws Exception {
		setLoginUser(
				getUserDao().findUserByUsername(getLoginUsername()));
		
		formTitleLabel.setValue("Surat Jalan");
		
		suratJalanListbox.setEmptyMessage("Tidak ada");
		
		suratJalanPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		listBySelection(DEFAULT_TAB_INDEX);
		
		// list
		// listAllSuratJalan();
		// load
		// loadSuratJalanList();
	}
	
	public void onSelect$suratJalanPeriodTabbox(Event event) throws Exception {
		selectedPeriodTabboxIndex = suratJalanPeriodTabbox.getSelectedIndex();
		
		listBySelection(selectedPeriodTabboxIndex);
	}
	
	private void listBySelection(int selIndex) throws Exception {
		Date startDate, endDate;
		
		switch (selIndex) {
		case 0: // semua
			listAllSuratJalan();

			// load
			loadSuratJalanList();
			break;
		case 1: // hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			listSuratJalanBySuratJalanDate(startDate, endDate);
			
			// load
			loadSuratJalanList();			
			break;
		case 2: // minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			listSuratJalanBySuratJalanDate(startDate, endDate);
			
			// load
			loadSuratJalanList();			
			break;
		case 3: // bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			listSuratJalanBySuratJalanDate(startDate, endDate);
			
			// load
			loadSuratJalanList();			
			break;
		default:
			break;
		}
		
	}

	private void listAllSuratJalan() throws Exception {
		setSuratJalanList(
				getSuratJalanDao().findAllSuratJalan_OrderBy_SuratJalanDate(true));
		
		suratjalanCount = getSuratJalanList().size();
	}

	private void listSuratJalanBySuratJalanDate(Date startDate, Date endDate) throws Exception {
		setSuratJalanList(
				getSuratJalanDao().findAllSuratJalan_By_SuratJalanDate(startDate, endDate, true));
		
		suratjalanCount = getSuratJalanList().size();
	}	
	
	private void loadSuratJalanList() {
		suratJalanListbox.setModel(
				new ListModelList<SuratJalan>(getSuratJalanList()));
		suratJalanListbox.setItemRenderer(
				getSuratJalanListitemRenderer());
	}	

	private ListitemRenderer<SuratJalan> getSuratJalanListitemRenderer() {

		totalSuratJalanValue = BigDecimal.ZERO;
		
		return new ListitemRenderer<SuratJalan>() {
			
			@Override
			public void render(Listitem item, SuratJalan suratJalan, int index) throws Exception {
				Listcell lc;
				
				// Tgl.
				lc = new Listcell(dateToStringDisplay(asLocalDate(suratJalan.getSuratJalanDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// Tgl.Pengiriman
				lc = new Listcell(dateToStringDisplay(asLocalDate(suratJalan.getDeliveryDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// SuratJalan-No.
				lc = new Listcell(suratJalan.getSuratJalanNumber().getSerialComp());
				lc.setParent(item);
				
				// Customer
				Customer customerByProxy = getCustomerByProxy(suratJalan.getId());
				
				lc = new Listcell(customerByProxy==null ? "tunai" : customerByProxy.getCompanyType()+". "+
						customerByProxy.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Payment
				lc = new Listcell(suratJalan.getPaymentType().compareTo(PaymentType.tunai)==0 ?
						"tunai" : "giro - "+suratJalan.getJumlahHari()+" Hari");
				lc.setParent(item);
				
				// Total (Rp.)
				lc = new Listcell(toLocalFormat(suratJalan.getTotalOrder()));
				lc.setParent(item);
				
				// Ppn (Rp.)
				lc = new Listcell(toLocalFormat(suratJalan.getTotalPpn()));
				lc.setParent(item);
				
				// Delivery Order
				lc = initDeliveryOrder(new Listcell(), suratJalan);
				lc.setParent(item);
				
				// Faktur
				lc = initFaktur(new Listcell(), suratJalan);
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(suratJalan.getNote());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// edit
				lc = initEditButton(new Listcell(), suratJalan);
				lc.setParent(item);

				// cetak
				// lc = new Listcell();
				// lc.setParent(item);				

				if (suratJalan.getSuratJalanStatus().equals(DocumentStatus.NORMAL)) {
					totalSuratJalanValue = totalSuratJalanValue.add(suratJalan.getTotalOrder());					
				}
				
				// if the status of suratJalan is 'BATAL', change the backgroud color to red
				if (suratJalan.getSuratJalanStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");					
				}
			}

			private Listcell initDeliveryOrder(Listcell listcell, SuratJalan suratJalan) throws Exception {
				// delivery order number
				Label delOrderLabel = new Label();
				delOrderLabel.setStyle("font-size: 1em; padding-right: 5px");
				
				if (suratJalan.getDeliveryOrder()!=null) {
					// proxy
					SuratJalan suratJalanByProxy = getSuratJalanDao().findDeliveryOrderByProxy(suratJalan.getId());
					
					delOrderLabel.setValue(suratJalanByProxy.getDeliveryOrder().getDeliveryOrderNumber().getSerialComp());
					delOrderLabel.setParent(listcell);
					
					// click to print delivery order
					Button delOrderButton = new Button();
					delOrderButton.setClass("inventoryEditButton");
					delOrderButton.setLabel("Print");
					
					delOrderButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {

							DeliveryOrderData deliveryOrderData = new DeliveryOrderData();
							deliveryOrderData.setDeliveryOrder(suratJalanByProxy.getDeliveryOrder());
							deliveryOrderData.setSuratJalan(suratJalan);
							
							Map<String, DeliveryOrderData> arg =
									Collections.singletonMap("deliveryOrderData", deliveryOrderData);
							
							Window deliveryOrderPrintWin = 
									(Window) Executions.createComponents("/deliveryorder/DeliveryOrderPrint.zul", null, arg);
							
							deliveryOrderPrintWin.doModal();							
							
						}
					});

					delOrderButton.setParent(listcell);

				} else {
					
					delOrderLabel.setValue("-");
					delOrderLabel.setParent(listcell);
				}
								
				return listcell;
			}

			private Listcell initFaktur(Listcell listcell, SuratJalan suratJalan) throws Exception {
				// faktur number
				Label fakturLabel = new Label();
				fakturLabel.setStyle("font-size: 1em; padding-right: 5px");
				
				if (suratJalan.getFaktur() != null) {
					// always has a faktur, the 'else' part is just in case
					
					// proxy
					SuratJalan suratJalanByProxy = getSuratJalanDao().findFakturByProxy(suratJalan.getId());
					
					fakturLabel.setValue(suratJalanByProxy.getFaktur().getFakturNumber().getSerialComp());
					fakturLabel.setParent(listcell);
					
					// click to print faktur
					Button fakturButton = new Button();
					fakturButton.setClass("inventoryEditButton");
					fakturButton.setLabel("Print");
					
					fakturButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							FakturData fakturData = new FakturData();
							fakturData.setFaktur(suratJalanByProxy.getFaktur());
							fakturData.setSuratJalan(suratJalan);
							
							Map<String, FakturData> arg =
									Collections.singletonMap("fakturData", fakturData);
							
							Window fakturPrintWin = 
									(Window) Executions.createComponents("/faktur/FakturPrint.zul", null, arg);
							
							fakturPrintWin.doModal();
						}
					});
					
					fakturButton.setParent(listcell);
					
				} else {

					fakturLabel.setValue("-");
					fakturLabel.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initEditButton(Listcell listcell, SuratJalan suratJalan) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						SuratJalanData suratJalanData = new SuratJalanData();
						suratJalanData.setCustomerOrder(null);
						suratJalanData.setSuratJalan(suratJalan);
						suratJalanData.setPageMode(PageMode.EDIT);
						suratJalanData.setRequestPath(
								Executions.getCurrent().getDesktop().getRequestPath());
						
						Map<String, SuratJalanData> arg = 
								Collections.singletonMap("suratJalanData", suratJalanData);
						
						Window suratJalanCreateWin = 
								(Window) Executions.createComponents("/suratjalan/SuratJalanDialog.zul", null, arg);
						
						suratJalanCreateWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								SuratJalan suratJalan = (SuratJalan) event.getData();

								// update
								getSuratJalanDao().update(suratJalan);
								
								// re-list
								listBySelection(selectedPeriodTabboxIndex);
															
							}
						});
						
						suratJalanCreateWin.doModal();						
					}
				
				});	
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$suratJalanListbox(Event event) throws Exception {
		infoSuratJalanlabel.setValue("Total: "+suratjalanCount+" Surat Jalan - Rp."+toLocalFormat(totalSuratJalanValue));
	}

	public void onClick$addButton(Event event) throws Exception {
		SuratJalanData suratJalanData = new SuratJalanData();
		suratJalanData.setCustomerOrder(null);
		suratJalanData.setSuratJalan(new SuratJalan());
		suratJalanData.setDeliveryOrderRequired(false);
		suratJalanData.setDeliveryOrderCompany(null);
		suratJalanData.setPageMode(PageMode.NEW);
		suratJalanData.setUserCreate(getLoginUser());
		suratJalanData.setRequestPath(
				Executions.getCurrent().getDesktop().getRequestPath());
		
		Map<String, SuratJalanData> arg = 
				Collections.singletonMap("suratJalanData", suratJalanData);
		
		Window suratJalanCreateWin = 
				(Window) Executions.createComponents("/suratjalan/SuratJalanDialog.zul", null, arg);

		suratJalanCreateWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				SuratJalan suratJalan = (SuratJalan) event.getData();
				
				// save
				getSuratJalanDao().save(suratJalan);
				
				// re-list
				listBySelection(selectedPeriodTabboxIndex);
			}
		});
		
		suratJalanCreateWin.doModal();
	}
	
	protected Customer getCustomerByProxy(long id) throws Exception {
		SuratJalan suratJalan = getSuratJalanDao().findCustomerByProxy(id);
		
		return suratJalan.getCustomer();
	}

	/**
	 * @return the suratJalanDao
	 */
	public SuratJalanDao getSuratJalanDao() {
		return suratJalanDao;
	}

	/**
	 * @param suratJalanDao the suratJalanDao to set
	 */
	public void setSuratJalanDao(SuratJalanDao suratJalanDao) {
		this.suratJalanDao = suratJalanDao;
	}

	/**
	 * @return the suratJalanList
	 */
	public List<SuratJalan> getSuratJalanList() {
		return suratJalanList;
	}

	/**
	 * @param suratJalanList the suratJalanList to set
	 */
	public void setSuratJalanList(List<SuratJalan> suratJalanList) {
		this.suratJalanList = suratJalanList;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
