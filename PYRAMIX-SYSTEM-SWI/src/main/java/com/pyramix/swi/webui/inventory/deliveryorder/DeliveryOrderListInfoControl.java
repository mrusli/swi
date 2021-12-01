package com.pyramix.swi.webui.inventory.deliveryorder;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.deliveryorder.DeliveryOrder;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.persistence.deliveryorder.dao.DeliveryOrderDao;
import com.pyramix.swi.persistence.user.dao.UserDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class DeliveryOrderListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6029115858098694884L;

	private DeliveryOrderDao deliveryOrderDao;
	private CompanyDao companyDao;
	private UserDao userDao;
	
	private Label formTitleLabel, infoDeliveryOrderlabel;
	private Listbox deliveryOrderListbox;
	private Tabbox deliveryOrderPeriodTabbox;
	private Combobox locationCombobox;
	
	private int deliveryOrderCount;
	private List<DeliveryOrder> deliveryOrderList;
	private int selectedPeriodTabboxIndex;
	private long locationId;
	private User loginUser;
	
	private final int WORK_DAY_WEEK 	= 6;
	private final int DEFAULT_TAB_INDEX	= 1;
	
	// db index organization table
	private final long SWI_IDX			= 1;
	private final long WSM_STR_IDX		= 2;
	private final long WSM_KRG_IDX		= 3;
	
	
	public void onCreate$deliveryOrderListInfoWin(Event event) throws Exception {
		// login user
		setLoginUser(getUserDao().findUserByUsername(getLoginUsername()));
		
		// defaul to all location -- id set to 0L
		locationId = 0L;
		
		formTitleLabel.setValue("Delivery Order");
		
		deliveryOrderListbox.setEmptyMessage("Tidak ada");
		
		// default tab index
		selectedPeriodTabboxIndex = DEFAULT_TAB_INDEX;
		deliveryOrderPeriodTabbox.setSelectedIndex(selectedPeriodTabboxIndex);
		
		setupLocationComboboxSelection();
		
		listBySelection(DEFAULT_TAB_INDEX, locationId);
	}

	private void setupLocationComboboxSelection() throws Exception {
		Company company_swi		= getCompanyDao().findCompanyById(SWI_IDX);
		Company company_wsm_str = getCompanyDao().findCompanyById(WSM_STR_IDX);
		Company company_wsm_krg = getCompanyDao().findCompanyById(WSM_KRG_IDX);
		
		String[] compLocation = {
				"Semua",
				company_swi.getCompanyDisplayName(),
				company_wsm_str.getCompanyDisplayName(), 
				company_wsm_krg.getCompanyDisplayName()};
		
		Comboitem comboitem;
		for (String compLoc : compLocation) {
			comboitem = new Comboitem();
			comboitem.setLabel(compLoc.toString());
			comboitem.setParent(locationCombobox);
		}

		locationCombobox.setSelectedIndex(0);
	}

	public void onSelect$locationCombobox(Event event) throws Exception {		
		int selLocationIndex = locationCombobox.getSelectedIndex();
		int selPeriodIndex = deliveryOrderPeriodTabbox.getSelectedIndex();
		
		switch (selLocationIndex) {
		case 0:
			// semua - set locationId to 0 (zero)
			locationId = 0L;
			listBySelection(selPeriodIndex, locationId);
			
			break;
		case 1:
			// swi
			locationId = SWI_IDX;
			listBySelection(selPeriodIndex, locationId);
			
			break;
		case 2:
			// wsm-str
			locationId = WSM_STR_IDX;
			listBySelection(selPeriodIndex, locationId);
			
			break;
		case 3:
			// wsm-krg
			locationId = WSM_KRG_IDX;
			listBySelection(selPeriodIndex, locationId);
			
			break;
		default:
			break;
		}
	}
	
	public void onSelect$deliveryOrderPeriodTabbox(Event event) throws Exception {
		selectedPeriodTabboxIndex = deliveryOrderPeriodTabbox.getSelectedIndex();
		
		listBySelection(selectedPeriodTabboxIndex, locationId);
	}
	
	private void listBySelection(int selIndex, long locationId) throws Exception {
		Date startDate, endDate;
		
		switch (selIndex) {
		case 0: // semua
			listAllDeliveryOrder(locationId);

			// load
			loadDeliveryOrderList();
			break;
		case 1: // hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			listDeliveryOrderByDeliveryOrderDate(startDate, endDate, locationId);
			
			// load
			loadDeliveryOrderList();			
			break;
		case 2: // minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			listDeliveryOrderByDeliveryOrderDate(startDate, endDate, locationId);
			
			// load
			loadDeliveryOrderList();			
			break;
		case 3: // bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			listDeliveryOrderByDeliveryOrderDate(startDate, endDate, locationId);
			
			// load
			loadDeliveryOrderList();			
			break;
		default:
			break;
		}
	}

	private void listAllDeliveryOrder(long locationId) throws Exception {
		setDeliveryOrderList(
				getDeliveryOrderDao().findAllDeliveryOrder_OrderBy_DeliveryOrderDate(true, locationId));
		
		deliveryOrderCount = getDeliveryOrderList().size();
	}

	private void listDeliveryOrderByDeliveryOrderDate(Date startDate, Date endDate, long locationId) throws Exception {
		setDeliveryOrderList(
				getDeliveryOrderDao().findAllDeliveryOrder_By_DeliveryOrderDate(startDate, endDate, true, locationId));
		
		deliveryOrderCount = getDeliveryOrderList().size();
	}
	
	private void loadDeliveryOrderList() {
		deliveryOrderListbox.setModel(
				new ListModelList<DeliveryOrder>(getDeliveryOrderList()));
		deliveryOrderListbox.setItemRenderer(getDeliveryOrderListitemRenderer());
	}

	private ListitemRenderer<DeliveryOrder> getDeliveryOrderListitemRenderer() {
		
		return new ListitemRenderer<DeliveryOrder>() {

			@Override
			public void render(Listitem item, DeliveryOrder deliveryOrder, int index) throws Exception {
				Listcell lc;
				
				// Tgl.
				lc = new Listcell(dateToStringDisplay(asLocalDate(deliveryOrder.getDeliveryOrderDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// DeliveryOrder-No.
				lc = new Listcell(deliveryOrder.getDeliveryOrderNumber().getSerialComp());
				lc.setParent(item);
				
				// Lokasi
				lc = initDeliveryOrderLocation(new Listcell(), deliveryOrder);
				lc.setParent(item);
				
				// Surat Jalan
				lc = initSuratJalan(new Listcell(), deliveryOrder);
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(deliveryOrder.getNote());
				lc.setParent(item);
				
				// User
				DeliveryOrder deliveryOrderUserCreateByProxy = 
						getDeliveryOrderDao().findUserCreateByProxy(deliveryOrder.getId());
				if (deliveryOrderUserCreateByProxy.getUserCreate()==null) {
					lc = new Listcell("-");
				} else {
					lc = new Listcell(
							deliveryOrderUserCreateByProxy.getUserCreate().getUser_name());
				}
				lc.setParent(item);
				
				// Edit
				lc = initEdit(new Listcell(), deliveryOrder);
				lc.setParent(item);
				
				if (deliveryOrder.getDeliveryOrderStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");
				}
				
			}

			private Listcell initDeliveryOrderLocation(Listcell listcell, DeliveryOrder deliveryOrder) throws Exception {
				Label deliveryOrderLocationLabel = new Label();
				
				DeliveryOrder deliveryOrderByProxy = getDeliveryOrderDao().findCompanyByProxy(deliveryOrder.getId());
				
				deliveryOrderLocationLabel.setValue(deliveryOrderByProxy.getCompany().getCompanyDisplayName());
				deliveryOrderLocationLabel.setWidth("160px");
				deliveryOrderLocationLabel.setStyle("font-size: 1em; padding-right: 5px");
				deliveryOrderLocationLabel.setParent(listcell);
				
				return listcell;
			}

			private Listcell initSuratJalan(Listcell listcell, DeliveryOrder deliveryOrder) throws Exception {
				// consist of label
				Label suratJalanLabel = new Label();

				if (deliveryOrder.getSuratJalan()!=null) {
					Button suratJalanButton = new Button();
					
					// DeliveryOrder is fetched Lazy -- need to use proxy / initialize
					DeliveryOrder deliveryOrderByProxy = getDeliveryOrderDao().findSuratJalanByProxy(deliveryOrder.getId());
					DocumentSerialNumber suratJalanNumber = deliveryOrderByProxy.getSuratJalan().getSuratJalanNumber();
					
					suratJalanLabel.setValue(suratJalanNumber.getSerialComp());
					suratJalanLabel.setWidth("100px");
					suratJalanLabel.setStyle("font-size: 1em; padding-right: 5px");
					suratJalanLabel.setParent(listcell);
					
					suratJalanButton.setLabel("Print");
					suratJalanButton.setWidth("50px");
					suratJalanButton.setClass("inventoryEditButton");
					suratJalanButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Map<String, SuratJalan> arg = 
									Collections.singletonMap("suratJalan", deliveryOrderByProxy.getSuratJalan());
																					
							Window printWindow =
									(Window) Executions.createComponents("/suratjalan/SuratJalanPrint.zul", null, arg);
							
							printWindow.doModal();
						}
					});
					
					suratJalanButton.setParent(listcell);
					
				} else {
					// prior to 19/07/2020 no reference were made to SuratJalan from DeliveryOrder
					// therefore, we need to provide this so that it will not crash
					suratJalanLabel.setValue("-");
					suratJalanLabel.setWidth("100px");
					suratJalanLabel.setStyle("font-size: 1em; padding-right: 10px");
					suratJalanLabel.setParent(listcell);
				}
				
				return listcell;
			}
			
			private Listcell initEdit(Listcell listcell, DeliveryOrder deliveryOrder) {
				Button editButton = new Button();

				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						DeliveryOrderData deliveryOrderData = new DeliveryOrderData();
						deliveryOrderData.setDeliveryOrder(deliveryOrder);
						deliveryOrderData.setPageMode(PageMode.EDIT);
						
						Map<String, DeliveryOrderData> arg = 
								Collections.singletonMap("deliveryOrderData", deliveryOrderData);
						
						Window deliveryOrderWin =
								(Window) Executions.createComponents("/deliveryorder/DeliveryOrderDialog.zul", null, arg);
						
						deliveryOrderWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								DeliveryOrder deliveryOrder = (DeliveryOrder) event.getData();
								
								// update
								getDeliveryOrderDao().update(deliveryOrder);
								
								// re-load and list
								loadDeliveryOrderList();
							}
						});
						
						deliveryOrderWin.doModal();
					}
				});
				
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$deliveryOrderListbox(Event event) throws Exception {
		infoDeliveryOrderlabel.setValue("Total: "+deliveryOrderCount);
	}
	
	public void onClick$addButton(Event event) throws Exception {
		DeliveryOrderData deliveryOrderData = new DeliveryOrderData();
		deliveryOrderData.setDeliveryOrder(new DeliveryOrder());
		deliveryOrderData.setPageMode(PageMode.NEW);
		deliveryOrderData.setUserCreate(getLoginUser());
		
		Map<String, DeliveryOrderData> arg = 
				Collections.singletonMap("deliveryOrderData", deliveryOrderData);
		
		Window deliveryOrderWin =
				(Window) Executions.createComponents("/deliveryorder/DeliveryOrderDialog.zul", null, arg);
		
		deliveryOrderWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				DeliveryOrder deliveryOrder = (DeliveryOrder) event.getData();
				
				// save
				getDeliveryOrderDao().save(deliveryOrder);
				
				// re-load and list
				listBySelection(selectedPeriodTabboxIndex, locationId);
			}
		});
		
		deliveryOrderWin.doModal();		
	}
	
	public DeliveryOrderDao getDeliveryOrderDao() {
		return deliveryOrderDao;
	}

	public void setDeliveryOrderDao(DeliveryOrderDao deliveryOrderDao) {
		this.deliveryOrderDao = deliveryOrderDao;
	}

	public List<DeliveryOrder> getDeliveryOrderList() {
		return deliveryOrderList;
	}

	public void setDeliveryOrderList(List<DeliveryOrder> deliveryOrderList) {
		this.deliveryOrderList = deliveryOrderList;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * @return the loginUser
	 */
	public User getLoginUser() {
		return loginUser;
	}

	/**
	 * @param loginUser the loginUser to set
	 */
	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
	
	
}
