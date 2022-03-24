package com.pyramix.swi.webui.faktur;

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
import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.persistence.faktur.dao.FakturDao;
import com.pyramix.swi.persistence.user.dao.UserDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

/**
 * 
 * 
 * @author rusli
 *
 */
public class FakturListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8310674839007198L;

	private FakturDao fakturDao;
	private UserDao userDao;
	
	private Label formTitleLabel;
	private Tabbox fakturPeriodTabbox;
	private Listbox fakturListbox;
	private Label infoFakturlabel;
	
	private List<Faktur> fakturList;
	private int selectedPeriodTabboxIndex;
	private User loginUser;
	
	private final int WORK_DAY_WEEK 	= 6;
	private final int DEFAULT_TAB_INDEX	= 1; // <-- Hari-Ini
	
	public void onCreate$fakturListInfoWin(Event event) throws Exception {
		// login user
		setLoginUser(getUserDao().findUserByUsername(getLoginUsername()));
		
		// form title
		formTitleLabel.setValue("Faktur");
		
		// view the default selected period
		fakturPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// empty listbox message
		fakturListbox.setEmptyMessage("Tidak Ada");
		
		// list according to the selected tab index
		listBySelection(DEFAULT_TAB_INDEX);		
	}

	public void onSelect$fakturPeriodTabbox(Event event) throws Exception {
		selectedPeriodTabboxIndex = fakturPeriodTabbox.getSelectedIndex();
		
		listBySelection(selectedPeriodTabboxIndex);
	}	
	
	private void listBySelection(int selIndex) {
		Date startDate, endDate;
		
		switch (selIndex) {
		case 0: // semua
			listAllFaktur();

			// load
			loadFakturList();
			break;
		case 1: // hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			listFakturByFakturDate(startDate, endDate);
			
			// load
			loadFakturList();			
			break;
		case 2: // minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			listFakturByFakturDate(startDate, endDate);
			
			// load
			loadFakturList();			
			break;
		case 3: // bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			listFakturByFakturDate(startDate, endDate);
			
			// load
			loadFakturList();			
			break;
		default:
			break;
		}
	}
	
	private void listAllFaktur() {
		setFakturList(
				getFakturDao().findAllFaktur_OrderBy_FakturDate(true));
	}

	private void listFakturByFakturDate(Date startDate, Date endDate) {
		setFakturList(
				getFakturDao().findAllFaktur_OrderBy_FakturDate(startDate, endDate, true));
		
	}

	private void loadFakturList() {
		fakturListbox.setModel(
				new ListModelList<Faktur>(getFakturList()));
		fakturListbox.setItemRenderer(getFakturListitemRenderer());
	}

	private ListitemRenderer<Faktur> getFakturListitemRenderer() {

		return new ListitemRenderer<Faktur>() {
			
			@Override
			public void render(Listitem item, Faktur faktur, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Faktur
				lc = new Listcell(dateToStringDisplay(asLocalDate(faktur.getFakturDate()), getShortDateFormat()));
				lc.setParent(item);
				
				// Faktur-No.
				lc = new Listcell(faktur.getFakturNumber().getSerialComp());
				lc.setParent(item);				
				
				// Customer
				Faktur fakturCustomerByProxy = getFakturDao().findCustomerByProxy(faktur.getId());
				if (fakturCustomerByProxy.getCustomer()==null) {
					lc = new Listcell("tunai");
				} else {
					lc = new Listcell(fakturCustomerByProxy.getCustomer().getCompanyType()+". "+
							fakturCustomerByProxy.getCustomer().getCompanyLegalName());
				}
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Pembayaran
				String pembayaranInfo = (faktur.getPaymentType().compareTo(PaymentType.giro)==0 || 
						faktur.getPaymentType().compareTo(PaymentType.bank)==0) ?
							faktur.getPaymentType().toString()+" - "+faktur.getJumlahHari()+" Hari" : 
								faktur.getPaymentType().toString();
				lc = new Listcell(pembayaranInfo);
				lc.setParent(item);
				
				// Total (Rp.)
				lc = new Listcell(toLocalFormat(faktur.getTotalOrder()));
				lc.setParent(item);
				
				// Ppn (Rp.)
				lc = new Listcell(toLocalFormat(faktur.getTotalPpn()));
				lc.setParent(item);
				
				// Surat Jalan
				lc = initSuratJalanNumber(new Listcell(), faktur);
				lc.setParent(item);
				
				// Surat Jalan button
				lc = initSuratJalanButton(new Listcell(), faktur);
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(faktur.getNote());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// User
				Faktur fakturUserCreateByProxy = getFakturDao().findUserByProxy(faktur.getId());
				if (fakturUserCreateByProxy.getUserCreate()==null) {
					lc = new Listcell("-");
				} else {
					lc = new Listcell(fakturUserCreateByProxy.getUserCreate().getUser_name());
				}
				lc.setParent(item);
				
				// Edit
				lc = initEdit(new Listcell(), faktur);
				lc.setParent(item);
				
				if (faktur.getFakturStatus().compareTo(DocumentStatus.BATAL)==0) {
					item.setClass("red-background");
				}
			}

			private Listcell initSuratJalanNumber(Listcell listcell, Faktur faktur) {
				// consist of label ONLY
				Label suratJalanLabel = new Label();

				if (faktur.getSuratJalan() != null) {
					// by proxy
					Faktur fakturSuratJalanByProxy = getFakturDao().findSuratJalanByProxy(faktur.getId());
					
					suratJalanLabel.setValue(
							fakturSuratJalanByProxy.getSuratJalan().getSuratJalanNumber().getSerialComp());
					suratJalanLabel.setWidth("100px");
					suratJalanLabel.setStyle("font-size: 1em; padding-right: 5px");
					suratJalanLabel.setParent(listcell);
					
				} else {
					// prior to 19/07/2020 no reference were made to SuratJalan from Faktur
					// therefore, we need to provide this so that it will not crash
					suratJalanLabel.setValue("-");
					suratJalanLabel.setWidth("100px");
					suratJalanLabel.setStyle("font-size: 1em; padding-right: 10px");
					suratJalanLabel.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initSuratJalanButton(Listcell listcell, Faktur faktur) {
				if (faktur.getSuratJalan() != null) {
					// by proxy
					Faktur fakturSuratJalanByProxy = getFakturDao().findSuratJalanByProxy(faktur.getId());

					// button
					Button suratJalanButton = new Button();
					
					suratJalanButton.setLabel("Print");
					suratJalanButton.setWidth("50px");
					suratJalanButton.setClass("inventoryEditButton");
					suratJalanButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
						
						@Override
						public void onEvent(Event event) throws Exception {
							Map<String, SuratJalan> arg = 
									Collections.singletonMap("suratJalan", fakturSuratJalanByProxy.getSuratJalan());
																					
							Window printWindow =
									(Window) Executions.createComponents("/suratjalan/SuratJalanPrint.zul", null, arg);
							
							printWindow.doModal();
						}
					});				
					suratJalanButton.setParent(listcell);
				}
				
				return listcell;
			}			
			
			private Listcell initEdit(Listcell listcell, Faktur faktur) {
				Button editButton = new Button();
				
				editButton.setLabel(faktur.getFakturStatus().equals(DocumentStatus.BATAL) ? "View" : "Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// by proxy
						// Faktur fakturSuratJalanByProxy = getFakturDao().findSuratJalanByProxy(faktur.getId());						
						
						FakturData fakturData = new FakturData();
						fakturData.setFaktur(faktur);
						fakturData.setSuratJalan(null);
						fakturData.setPageMode(faktur.getFakturStatus().equals(DocumentStatus.BATAL) ? PageMode.VIEW : PageMode.EDIT);
						
						Map<String, FakturData> arg = Collections.singletonMap("fakturData", fakturData);
						
						Window fakturWin =
								(Window) Executions.createComponents("/faktur/FakturDialog.zul", null, arg);

						fakturWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Faktur faktur = (Faktur) event.getData();
								
								// update
								getFakturDao().update(faktur);
								
								// re-list
								listBySelection(selectedPeriodTabboxIndex);
							}
						});
						
						fakturWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$fakturListbox(Event event) throws Exception {
		infoFakturlabel.setValue("Total: "+fakturListbox.getItemCount()+" Faktur");
	}
	
	public void onClick$addButton(Event event) throws Exception {
		FakturData fakturData = new FakturData();
		fakturData.setFaktur(new Faktur());
		fakturData.setSuratJalan(null);
		fakturData.setPageMode(PageMode.NEW);
		fakturData.setUserCreate(getLoginUser());
		
		Map<String, FakturData> arg = Collections.singletonMap("fakturData", fakturData);
		
		Window fakturWin =
				(Window) Executions.createComponents("/faktur/FakturDialog.zul", null, arg);

		fakturWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Faktur faktur = (Faktur) event.getData();
				
				// update
				getFakturDao().save(faktur);
				
				// re-list
				listBySelection(selectedPeriodTabboxIndex);
			}
		});
		
		fakturWin.doModal();
		
	}
	
	public FakturDao getFakturDao() {
		return fakturDao;
	}

	public void setFakturDao(FakturDao fakturDao) {
		this.fakturDao = fakturDao;
	}

	public List<Faktur> getFakturList() {
		return fakturList;
	}

	public void setFakturList(List<Faktur> fakturList) {
		this.fakturList = fakturList;
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

	/**
	 * @return the userDao
	 */
	public UserDao getUserDao() {
		return userDao;
	}

	/**
	 * @param userDao the userDao to set
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
