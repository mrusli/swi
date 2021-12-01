package com.pyramix.swi.webui.voucher;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.gl.GeneralLedger;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.domain.user.UserRole;
import com.pyramix.swi.domain.voucher.VoucherJournal;
import com.pyramix.swi.domain.voucher.VoucherJournalDebitCredit;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherStatus;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.swi.persistence.user.dao.UserDao;
import com.pyramix.swi.persistence.userrole.dao.UserRoleDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherJournalDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.SerialNumberGenerator;

public class VoucherJournalControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3816799587866516853L;

	private VoucherJournalDao voucherJournalDao;
	private SerialNumberGenerator serialNumberGenerator;
	private GeneralLedgerDao generalLedgerDao;
	private UserRoleDao userRoleDao;
	private UserDao userDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox voucherJournalListbox;
	private Tabbox voucherJournalPeriodTabbox;
	
	private List<VoucherJournal> voucherJournalList;
	private int voucherCount;
	private BigDecimal totalPaymentVal = BigDecimal.ZERO;
	private User loginUser;
	private boolean hasRoleManager = false;
	
	private final int WORK_DAY_WEEK		= 6;
	private final int DEFAULT_TAB_INDEX = 1;
	private final Long MANAGER_ROLE_ID	= new Long(1);
	
	/**
	 * list the VoucherJournal Descending (the latest voucher on top of the list)
	 */
	private final boolean DESCENDING	= true;
	
	/**
	 * list the VoucherJournal Ascending (the latest voucher last in the list)
	 */
	@SuppressWarnings("unused")
	private final boolean ASCENDING		= false;
	
	public void onCreate$voucherJournalListInfoWin(Event event) throws Exception {
		// loginUser is User object
		setLoginUser(getUserDao().findUserByUsername(getLoginUsername()));
		// hasRoleManager determines whether the loginUser has role manager
		setHasRoleManager(checkCurrentLoginUser());

		// System.out.println(getLoginUsername());
		
		formTitleLabel.setValue("Voucher Umum dan PettyCash");
				
		// set if list is empty
		voucherJournalListbox.setEmptyMessage("Tidak Ada");
		
		// set the tab
		voucherJournalPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// list today's voucher transaction
		listBySelection(DEFAULT_TAB_INDEX);
	}
	
	private boolean checkCurrentLoginUser() throws Exception {
		UserRole userRole = getUserRoleDao().findUserRoleById(MANAGER_ROLE_ID);
		
		// System.out.println(userRole);
		
		return isLoginUserHasRoleManager(userRole);
	}

	public void onSelect$voucherJournalPeriodTabbox(Event event) throws Exception {
		int selIndex = voucherJournalPeriodTabbox.getSelectedIndex();
		totalPaymentVal = BigDecimal.ZERO;
		
		listBySelection(selIndex);
	}

	private void listBySelection(int selIndex) throws Exception {
		Date startDate, endDate;

		switch (selIndex) {
			case 0: // semua
				loadAllVoucherJournal();
				
				listVoucherJournalListInfo();
				break;
			case 1: // hari-ini
				startDate = asDate(getLocalDate());
				endDate = asDate(getLocalDate());

				// load data by transactionDate
				loadVoucherJournalByTransactionDate(startDate, endDate);

				listVoucherJournalListInfo();
				break;
			case 2: // minggu-ini	
				startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
				endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));

				// load data by transactionDate
				loadVoucherJournalByTransactionDate(startDate, endDate);

				listVoucherJournalListInfo();
				break;
			case 3: // bulan-ini
				startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
				endDate = asDate(getLastDateOfTheMonth(getLocalDate()));

				// load data by transactionDate
				loadVoucherJournalByTransactionDate(startDate, endDate);

				listVoucherJournalListInfo();
				break;
			default:
				break;				
		}
	}

	private void loadAllVoucherJournal() throws Exception {
		setVoucherJournalList(
				getVoucherJournalDao().findAllVoucherJournal_OrderBy_TransactionDate(DESCENDING));
		
		voucherCount = getVoucherJournalList().size();
	}

	private void loadVoucherJournalByTransactionDate(Date startDate, Date endDate) throws Exception {
		setVoucherJournalList(
				getVoucherJournalDao().findAllVoucherJournal_OrderBy_TransactionDate(startDate, endDate, DESCENDING));
		
		voucherCount = getVoucherJournalList().size();
	}
	
	private void listVoucherJournalListInfo() {
		voucherJournalListbox.setModel(
				new ListModelList<VoucherJournal>(getVoucherJournalList()));
		voucherJournalListbox.setItemRenderer(getVoucherJournalListitemRenderer());
	}

	private ListitemRenderer<VoucherJournal> getVoucherJournalListitemRenderer() {
		
		return new ListitemRenderer<VoucherJournal>() {
			
			@Override
			public void render(Listitem item, VoucherJournal voucherJournal, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Jurnal
				lc = new Listcell(dateToStringDisplay(
					asLocalDate(voucherJournal.getTransactionDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// No.Voucher
				lc = new Listcell(voucherJournal.getVoucherNumber().getSerialComp());
				lc.setParent(item);
				
				// Nominal (Rp.)
				lc = new Listcell(toLocalFormatWithDecimal(voucherJournal.getTheSumOf()));
				lc.setParent(item);
				
				// Tipe Jurnal
				lc = new Listcell(voucherJournal.getVoucherType().toString());
				lc.setParent(item);
				
				// Status
				lc = new Listcell(voucherJournal.getFlowStatus().toString());
				lc.setParent(item);
				
				// posting-button
				lc = initPosting(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// Transaksi Info
				lc = new Listcell(voucherJournal.getTransactionDescription());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Referensi
				lc = new Listcell(voucherJournal.getDocumentRef());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// User
				lc = initUserCreate(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// userCreate by proxy
				VoucherJournal voucherJournalByProxy = getVoucherJournalDao().getVoucherJournalUserCreateByProxy(voucherJournal.getId());

				// View / Edit detail
				// is it posting yet?
				if (voucherJournal.getPostingVoucherNumber()==null) {
					// not posting yet -- allow to post
					lc = initEdit(new Listcell(), voucherJournal);
				} else {
					// posting already -- check whether login username is the same as the usercreate
					// -- ONLY userCreate allowed to make changes after permission is given by the manager
					if (voucherJournalByProxy.getUserCreate().getUser_name().equals(getLoginUsername())) {
						
						// manager allows this user to edit?
						lc = voucherJournal.isAllowEdit() ?
								initEdit(new Listcell(), voucherJournal) :
									initView(new Listcell(), voucherJournal);
								
					} else {
						lc = initView(new Listcell(), voucherJournal);
					}
				}
				lc.setParent(item);
				
				totalPaymentVal = totalPaymentVal.add(voucherJournal.getTheSumOf());
			}

			private Listcell initUserCreate(Listcell listcell, VoucherJournal voucherJournal) throws Exception {
				// checkbox for manager ONLY !!!
				Checkbox checkbox = new Checkbox();
				checkbox.setChecked(voucherJournal.isAllowEdit());
				checkbox.setVisible(isRoleManager());
				checkbox.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Checkbox checkbox = (Checkbox) event.getTarget();
						
						// set this record to allow edit by the userCreate
						voucherJournal.setAllowEdit(checkbox.isChecked());
						
						// update
						getVoucherJournalDao().update(voucherJournal);
						
						// update
						// getVoucherJournalDao().update(voucherJournal);
						
						// identify selected tab and list
						int selIndex = voucherJournalPeriodTabbox.getSelectedIndex();
						listBySelection(selIndex);

						// userCreate by proxy
						VoucherJournal voucherJournalByProxy = getVoucherJournalDao().getVoucherJournalUserCreateByProxy(voucherJournal.getId());
						
						String editAllowed = "Voucher Journal diijinkan dirubah oleh: "+
								voucherJournalByProxy.getUserCreate().getUser_name();
						String editRevoked = "Perubahan Voucher Journal oleh: "+
								voucherJournalByProxy.getUserCreate().getUser_name()+" dibatalkan";

						Clients.showNotification(checkbox.isChecked() ? editAllowed : editRevoked, 
										"info", null, "bottom_right", 0);
						
						
					}
				});
				checkbox.setParent(listcell);
				
				// userCreate by proxy
				VoucherJournal voucherJournalByProxy = getVoucherJournalDao().getVoucherJournalUserCreateByProxy(voucherJournal.getId());

				// userCreate - username
				Label label = new Label();
				label.setValue(voucherJournalByProxy.getUserCreate().getUser_name());
				label.setParent(listcell);
				
				return listcell;
			}

			private Listcell initPosting(Listcell listcell, VoucherJournal voucherJournal) {				
				Button postingButton = new Button();
				postingButton.setLabel("Post");
				postingButton.setClass("inventoryEditButton");
				postingButton.setDisabled(voucherJournal.getPostingVoucherNumber()!=null);
				postingButton.setParent(listcell);
				postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// change flowstatus to post
						voucherJournal.setFlowStatus(VoucherStatus.Posted);
						
						// obtain posting number
						voucherJournal.setPostingVoucherNumber(
								addVoucherNumber(voucherJournal.getVoucherType().compareTo(VoucherType.GENERAL)==0 ?
										VoucherType.POSTING_GENERAL :
										VoucherType.POSTING_PETTYCASH,
										asDate(getLocalDate())));
						
						// update
						getVoucherJournalDao().update(voucherJournal);
						
						// identify selected tab and list
						int selIndex = voucherJournalPeriodTabbox.getSelectedIndex();
						listBySelection(selIndex);
						
						// create gl
						postToGeneralLedger(voucherJournal);
						
						Clients.showNotification("Post berhasil", "info", null, "bottom_right", 0);
					}

					private void postToGeneralLedger(VoucherJournal voucherJournal) throws Exception {
						
						for (VoucherJournalDebitCredit dbcr : voucherJournal.getVoucherJournalDebitCredits()) {
							GeneralLedger gl = new GeneralLedger();
							
							gl.setMasterCoa(dbcr.getMasterCoa());
							gl.setPostingDate(asDate(getLocalDate()));
							gl.setPostingVoucherNumber(voucherJournal.getPostingVoucherNumber());
							gl.setCreditAmount(dbcr.getCreditAmount());
							gl.setDebitAmount(dbcr.getDebitAmount());
							gl.setDbcrDescription(dbcr.getDbcrDescription());
							gl.setTransactionDescription(voucherJournal.getTransactionDescription());
							gl.setDocumentRef(voucherJournal.getDocumentRef());
							gl.setTransactionDate(voucherJournal.getTransactionDate());
							gl.setVoucherType(voucherJournal.getVoucherType());
							gl.setVoucherNumber(voucherJournal.getVoucherNumber());
							
							getGeneralLedgerDao().save(gl);
						}
						
					}
				});
				
				return listcell;
			}

			private Listcell initEdit(Listcell listcell, VoucherJournal voucherJournal) {
				Button editButton = new Button();
				editButton.setLabel("Edit");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, VoucherJournal> arg = 
								Collections.singletonMap("voucherJournal", voucherJournal);

						// display the VoucherJournal -- new
						Window voucherJournalDialogWin =
								(Window) Executions.createComponents(
									"/voucher/VoucherJournalDialog.zul", null, arg);
						
						voucherJournalDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								VoucherJournal voucherJournal = (VoucherJournal) event.getData();
								
								// disable the edit for this user
								voucherJournal.setAllowEdit(false);
								
								// update
								getVoucherJournalDao().update(voucherJournal);
								
								// identify selected tab and list
								int selIndex = voucherJournalPeriodTabbox.getSelectedIndex();
								listBySelection(selIndex);
							}
						});
						
						voucherJournalDialogWin.doModal();
						
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initView(Listcell listcell, VoucherJournal voucherJournal) {
				Button editButton = new Button();
				editButton.setLabel("View");
				editButton.setClass("inventoryEditButton");
				editButton.setParent(listcell);
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, VoucherJournal> arg = 
								Collections.singletonMap("voucherJournal", voucherJournal);
						
						Window voucherJournalViewWin = 
								(Window) Executions.createComponents("/voucher/VoucherJournalViewDialog.zul", null, arg);
						
						voucherJournalViewWin.doModal();
					}
				});
				
				return listcell;
			}
		};
	}

	public void onAfterRender$voucherJournalListbox(Event event) throws Exception {
		infoResultlabel.setValue("Total: "+getFormatedInteger(voucherCount)+" Jurnal Voucher - Rp."+toLocalFormat(totalPaymentVal));
	}
	
	public void onClick$addButton(Event event) throws Exception {
		Map<String, VoucherJournal> arg = 
				Collections.singletonMap("voucherJournal", new VoucherJournal());

		// display the VoucherJournal -- new
		Window voucherJournalDialogWin =
				(Window) Executions.createComponents(
					"/voucher/VoucherJournalDialog.zul", null, arg);
		
		voucherJournalDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				VoucherJournal voucherJournal = (VoucherJournal) event.getData();
				
				// set the current login user
				voucherJournal.setUserCreate(getLoginUser());
				
				// save
				getVoucherJournalDao().save(voucherJournal);
				
				// identify selected tab
				int selIndex = voucherJournalPeriodTabbox.getSelectedIndex();
				listBySelection(selIndex);
			}
		});
		
		voucherJournalDialogWin.doModal();

	}
	
	private VoucherSerialNumber addVoucherNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = getSerialNumberGenerator().getSerialNumber(voucherType, currentDate);
		
		VoucherSerialNumber voucherSerNum = new VoucherSerialNumber();
		voucherSerNum.setVoucherType(voucherType);
		voucherSerNum.setSerialDate(currentDate);
		voucherSerNum.setSerialNo(serialNum);
		voucherSerNum.setSerialComp(formatSerialComp(voucherType.toCode(voucherType.getValue()), currentDate, serialNum));
		
		return voucherSerNum;
	}	
	
	public List<VoucherJournal> getVoucherJournalList() {
		return voucherJournalList;
	}

	public void setVoucherJournalList(List<VoucherJournal> voucherJournalList) {
		this.voucherJournalList = voucherJournalList;
	}

	public VoucherJournalDao getVoucherJournalDao() {
		return voucherJournalDao;
	}

	public void setVoucherJournalDao(VoucherJournalDao voucherJournalDao) {
		this.voucherJournalDao = voucherJournalDao;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}

	public UserRoleDao getUserRoleDao() {
		return userRoleDao;
	}

	public void setUserRoleDao(UserRoleDao userRoleDao) {
		this.userRoleDao = userRoleDao;
	}

	public boolean isRoleManager() {
		return hasRoleManager;
	}

	public void setHasRoleManager(boolean hasRoleManager) {
		this.hasRoleManager = hasRoleManager;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
	
}
