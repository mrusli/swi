package com.pyramix.swi.webui.settlement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.receivable.ActivityType;
import com.pyramix.swi.domain.receivable.CustomerReceivable;
import com.pyramix.swi.domain.receivable.CustomerReceivableActivity;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.settlement.SettlementDetail;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.domain.voucher.VoucherPayment;
import com.pyramix.swi.persistence.customer.dao.CustomerDao;
import com.pyramix.swi.persistence.receivable.dao.CustomerReceivableDao;
import com.pyramix.swi.persistence.settlement.dao.SettlementDao;
import com.pyramix.swi.persistence.user.dao.UserDao;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;
import com.pyramix.swi.webui.voucher.VoucherGiroReceiptData;
import com.pyramix.swi.webui.voucher.VoucherPaymentData;

public class SettlementListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 234913570069351070L;

	private SettlementDao settlementDao;
	private CustomerDao customerDao;
	private UserDao userDao;
	private CustomerReceivableDao customerReceivableDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox settlementListbox;
	private Tabbox settlementPeriodTabbox;
	private Button batalButton;
	
	private List<Settlement> settlementList;
	private BigDecimal totalSettlementVal;
	private int settlementCount;
	private User loginUser;
	
	private final int WORK_DAY_WEEK 	= 6;
	private final int DEFAULT_TAB_INDEX = 1;
	
	private static final Logger log = Logger.getLogger(SettlementListInfoControl.class);
	
	public void onCreate$settlementListInfoWin(Event event) throws Exception {
		// loginUser is User object
		setLoginUser(getUserDao().findUserByUsername(getLoginUsername()));

		// System.out.println(getLoginUsername());		
		
		formTitleLabel.setValue("Settlement");
		
		settlementListbox.setEmptyMessage("Tidak ada");
		
		// set tabbox
		settlementPeriodTabbox.setSelectedIndex(DEFAULT_TAB_INDEX);
		
		// list
		listBySelection(DEFAULT_TAB_INDEX);
		
		// load
		// listAllSettlement();
		
		// display
		// displaySettlementListInfo();
	}

	public void onSelect$settlementPeriodTabbox(Event event) throws Exception {
		int selIndex = settlementPeriodTabbox.getSelectedIndex();
		
		listBySelection(selIndex);
	}

	private void listBySelection(int selIndex) throws Exception {
		Date startDate;
		Date endDate;
		
		switch (selIndex) {
		case 0: // Semua
			listAllSettlement();
			
			displaySettlementListInfo();
			break;
		case 1: // Hari-ini
			startDate = asDate(getLocalDate());
			endDate = asDate(getLocalDate());
			
			listSettlementByDate(startDate, endDate);
			
			displaySettlementListInfo();
			break;
		case 2: // Minggu-ini
			startDate = asDate(getFirstDateOfTheWeek(getLocalDate()));
			endDate = asDate(getLastDateOfTheWeek(getLocalDate(), WORK_DAY_WEEK));
			
			listSettlementByDate(startDate, endDate);
			
			displaySettlementListInfo();
			break;
		case 3: // Bulan-ini
			startDate = asDate(getFirstdateOfTheMonth(getLocalDate()));
			endDate = asDate(getLastDateOfTheMonth(getLocalDate()));
			
			listSettlementByDate(startDate, endDate);
			
			displaySettlementListInfo();
			break;
		default:
			break;
		}
	}
	
	private void listAllSettlement() throws Exception {
		setSettlementList(
				getSettlementDao().findAllSettlement_OrderBy_SettlementDate(true));
		
		settlementCount = getSettlementList().size();
		
		batalButton.setDisabled(settlementCount==0);
	}
	
	private void listSettlementByDate(Date startDate, Date endDate) throws Exception {
		setSettlementList(
				getSettlementDao().findSettlement_By_SettlementDate(true, startDate, endDate));
		
		settlementCount = getSettlementList().size();
		
		batalButton.setDisabled(settlementCount==0);
	}
	
	private void displaySettlementListInfo() throws Exception {
		settlementListbox.setModel(
				new ListModelList<Settlement>(getSettlementList()));
		settlementListbox.setItemRenderer(
				getSettlementListitemRenderer());
	}

	private ListitemRenderer<Settlement> getSettlementListitemRenderer() {

		totalSettlementVal = BigDecimal.ZERO;
		
		return new ListitemRenderer<Settlement>() {
			
			@Override
			public void render(Listitem item, Settlement settlement, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Settlement
				lc = new Listcell(dateToStringDisplay(asLocalDate(settlement.getSettlementDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// No.Settlement
				lc = new Listcell(settlement.getSettlementNumber().getSerialComp());
				lc.setParent(item);
				
				// Customer
				Customer customerByProxy = getCustomerByProxy(settlement.getId());
				
				lc = new Listcell(customerByProxy.getCompanyType()+". "+
						customerByProxy.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Nominal (Rp.)
				lc = new Listcell(toLocalFormat(settlement.getAmountPaid()));
				lc.setParent(item);
				
				// Posisi Settlement
				lc = new Listcell(toLocalFormat(settlement.getPostingAmount()));
				lc.setParent(item);
				
				// Giro-Receipt
				lc = initPostingVoucherGiroReceipt(new Listcell(), settlement);
				lc.setParent(item);
				
				// Pembayaran
				lc = initPostingVoucherPayment(new Listcell(), settlement);
				lc.setParent(item);
				
				// User
				lc = initUserCreate(new Listcell(), settlement);
				lc.setParent(item);
				
				// edit
				lc = initEdit(new Listcell(), settlement);
				lc.setParent(item);
				
				totalSettlementVal = totalSettlementVal.add(settlement.getAmountPaid());
				
				item.setValue(settlement);
				
				// if the status of settlement is 'BATAL', change the backgroud color to red
				if (settlement.getSettlementStatus().equals(DocumentStatus.BATAL)) {
					item.setClass("red-background");
				}
			}

			private Listcell initPostingVoucherPayment(Listcell listcell, Settlement settlement) throws Exception {
				// consist of label and button
				Label 	postingLabel 	= new Label();
				Button 	postingButton 	= new Button();

				if (settlement.getVoucherGiroReceipt()==null) {
					
					// paid by transfer bank / tunai (cash)
					if (settlement.getVoucherPayment()==null) {
						// not paid using bank / cash
						postingLabel.setValue("Belum Posting");
						postingLabel.setStyle("color: red; font-size: 1em; padding-right: 5px");
						postingLabel.setParent(listcell);
						
						postingButton.setLabel("Posting");
						postingButton.setWidth("60px");
						postingButton.setClass("inventoryEditButton");
						postingButton.setDisabled(settlement.getSettlementStatus().compareTo(DocumentStatus.BATAL)==0);
						postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								VoucherPaymentData data = new VoucherPaymentData();
								data.setPageMode(PageMode.NEW);
								data.setVoucherPayment(null);
								data.setSettlement(settlement);
								
								Map<String, VoucherPaymentData> arg = 
										Collections.singletonMap("voucherPaymentData", data);
								
								Window voucherPaymentDialogWin = 
										(Window) Executions.createComponents("/voucher/VoucherPaymentDialog.zul", null, arg);
								
								voucherPaymentDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

									@Override
									public void onEvent(Event event) throws Exception {
										VoucherPayment payment = (VoucherPayment) event.getData();
										
										// set userCreate
										payment.setUserCreate(getLoginUser());
										
										// update the current VoucherPayment
										settlement.setVoucherPayment(payment);
										
										log.info("Updating: "+settlement.toString());
										
										// -- use voucherPaymentDao
										getSettlementDao().update(settlement);
										
										// check this settlement posting amount
										// if 0 do nothing
										// if - create an entry for CustomerReceivableActivity with + value
										// if + do nothing
										
										// returns -1, 0, or 1 as this BigDecimal is numericallyless than, equal to, or greater than val (BigDecimal.ZERO)
										boolean recevableFullyPaid = settlement.getPostingAmount().compareTo(BigDecimal.ZERO)==-1;
										if (recevableFullyPaid) {
											log.info("create a new entry for CustomerReceivableActivity with + value");
											
											int index = settlement.getSettlementDetails().size()-1;
											SettlementDetail detail = settlement.getSettlementDetails().get(index);												
											// sales
											CustomerReceivableActivity selectedActivity = new CustomerReceivableActivity();
											selectedActivity.setCustomerOrderId(detail.getCustomerOrder().getId());
											selectedActivity.setActivityType(ActivityType.PEMBAYARAN);
											selectedActivity.setSalesDate(detail.getCustomerOrder().getOrderDate());
											selectedActivity.setPaymentDueDate(asDate(addDate(
													detail.getCustomerOrder().getCreditDay(), 
													asLocalDate(detail.getCustomerOrder().getOrderDate()))));
											selectedActivity.setSalesDescription("Kekurangan Pembayaran - Dianggap Lunas");
											selectedActivity.setAmountSales(BigDecimal.ZERO);
											selectedActivity.setAmountSalesPpn(BigDecimal.ZERO);					
											// payment
											selectedActivity.setPaymentDate(payment.getPostingDate());
											selectedActivity.setPaymentDescription("Kekurangan Pembayaran - Dianggap Lunas");
											long overDueDay = dayDiff(asLocalDate(detail.getCustomerOrder().getOrderDate()), asLocalDate(settlement.getSettlementDate()));
											selectedActivity.setOverdueDay(java.lang.Math.toIntExact(overDueDay));
											// 
											selectedActivity.setAmountPaid(settlement.getPostingAmount().negate());
											selectedActivity.setAmountPaidPpn(BigDecimal.ZERO);
											selectedActivity.setPaymentComplete(true);
											selectedActivity.setRemainingAmount(BigDecimal.ZERO);
											selectedActivity.setReceivableStatus(DocumentStatus.NORMAL);
											
											log.info("Add to CustomerReceivableActivityList: "+selectedActivity.toString());
											
											// update - get the receivable from settlement
											Settlement settlementReceivableByProxy =
													getSettlementDao().findCustomerReceivableByProxy(settlement.getId());
											CustomerReceivable customerReceivableFromSettlement =
													settlementReceivableByProxy.getCustomerReceivable();
											List<CustomerReceivableActivity> activityList =
													customerReceivableFromSettlement.getCustomerReceivableActivities();
											
											activityList.forEach(activity -> log.info("ExistingList: "+activity.toString()));
											
											// update - add to list
											activityList.add(selectedActivity);
											
											activityList.forEach(activity -> log.info("New List: "+activity.toString()));
											
											// update
											getCustomerReceivableDao().update(customerReceivableFromSettlement);
										}
										
										// re-load / re-list based on selected tab
										int selIndex = settlementPeriodTabbox.getSelectedIndex();	
										listBySelection(selIndex);
									}
								});
								
								voucherPaymentDialogWin.doModal();
							}
						});						
						postingButton.setParent(listcell);
						
					} else {
						// paid using bank / cash
						
						VoucherPayment paymentByProxy = getVoucherPaymentByProxy(settlement.getId());
						
						postingLabel.setValue(paymentByProxy.getVoucherNumber().getSerialComp());
						postingLabel.setStyle("font-size: 1em; padding-right: 5px");
						postingLabel.setParent(listcell);
						
						postingButton.setLabel("View");
						postingButton.setWidth("60px");
						postingButton.setClass("inventoryEditButton");
						postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								VoucherPaymentData paymentData = new VoucherPaymentData();
								paymentData.setPageMode(PageMode.VIEW);
								paymentData.setVoucherPayment(paymentByProxy);
								paymentData.setSettlement(null);
								
								Map<String, VoucherPaymentData> arg =
										Collections.singletonMap("voucherPaymentData", paymentData);

								Window paymentDialogWin =
										(Window) Executions.createComponents(
											"/voucher/VoucherPaymentDialog.zul", null, arg);

								paymentDialogWin.doModal();
							}
						});
						postingButton.setParent(listcell);
						
					}
					
				} else {
					// paid by giro
					postingLabel.setValue("Pembayaran dgn Giro");
					postingLabel.setStyle("font-size: 1em; padding-right: 5px");
					postingLabel.setParent(listcell);
				}
				
				return listcell;
			}

			private Listcell initPostingVoucherGiroReceipt(Listcell listcell, Settlement settlement) throws Exception {
				// consist of label and button
				Label 	postingLabel 	= new Label();
				Button 	postingButton 	= new Button();
				
				if (settlement.getVoucherPayment()==null) {
					// not paid by bank / cash
					if (settlement.getVoucherGiroReceipt()==null) {
						// not paid using giro
						postingLabel.setValue("Belum Posting");
						postingLabel.setStyle("color: red; font-size: 1em; padding-right: 9px");
						postingLabel.setParent(listcell);
						
						postingButton.setLabel("Posting");
						postingButton.setWidth("60px");
						postingButton.setClass("inventoryEditButton");
						postingButton.setDisabled(settlement.getSettlementStatus().compareTo(DocumentStatus.BATAL)==0);
						postingButton.addEventListener("onClick", new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								VoucherGiroReceiptData receiptData = new VoucherGiroReceiptData();
								receiptData.setPageMode(PageMode.NEW);
								receiptData.setVoucherGiroReceipt(null);
								receiptData.setSettlement(settlement);
								
								Map<String, VoucherGiroReceiptData> arg = 
										Collections.singletonMap("voucherGiroReceiptData", receiptData);
											
								Window receiptDialogWin =
										(Window) Executions.createComponents(
											"/voucher/VoucherGiroReceiptDialog.zul", null, arg);

									receiptDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

										@Override
										public void onEvent(Event event) throws Exception {
											VoucherGiroReceipt receipt = (VoucherGiroReceipt) event.getData();
											
											// set userCreate
											receipt.setUserCreate(getLoginUser());
											
											// set link
											settlement.setVoucherGiroReceipt(receipt);
											
											// update
											getSettlementDao().update(settlement);
											
											// re-load / re-list based on selected tab
											int selIndex = settlementPeriodTabbox.getSelectedIndex();
											
											listBySelection(selIndex);
										}
									});
									
									receiptDialogWin.doModal();
							}
							
						});
						postingButton.setParent(listcell);
					} else {
						// paid using giro
						
						VoucherGiroReceipt receiptByProxy = getVoucherGiroReceiptByProxy(settlement.getId());
						
						postingLabel.setValue(receiptByProxy.getVoucherNumber().getSerialComp());
						postingLabel.setStyle("font-size: 1em; padding-right: 5px");
						postingLabel.setParent(listcell);

						postingButton.setLabel("View");
						postingButton.setWidth("60px");
						postingButton.setClass("inventoryEditButton");
						postingButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

							@Override
							public void onEvent(Event arg0) throws Exception {
								VoucherGiroReceiptData receiptData = new VoucherGiroReceiptData();
								receiptData.setPageMode(PageMode.VIEW);
								receiptData.setVoucherGiroReceipt(receiptByProxy);
								receiptData.setSettlement(null);

								Map<String, VoucherGiroReceiptData> arg = 
										Collections.singletonMap("voucherGiroReceiptData", receiptData);
											
								Window receiptDialogWin =
										(Window) Executions.createComponents(
											"/voucher/VoucherGiroReceiptDialog.zul", null, arg);

								receiptDialogWin.doModal();
							}
						});
						postingButton.setParent(listcell);

					}
					
				} else {
					// paid by bank / cash
					postingLabel.setValue("Pembayaran dgn Bank/Tunai");
					postingLabel.setStyle("font-size: 1em; padding-right: 5px");
					postingLabel.setParent(listcell);					
				}
				
				
				return listcell;
			}

			private Listcell initUserCreate(Listcell listcell, Settlement settlement) throws Exception {
				// userCreate by proxy
				Settlement settlementByProxy = 
						getSettlementDao().getSettlementUserCreateByProxy(settlement.getId());
				
				// System.out.println(settlementByProxy.getUserCreate()==null);
				
				// userCreate - username
				Label label = new Label();
				label.setValue(settlementByProxy.getUserCreate()==null ? "" : 
					settlementByProxy.getUserCreate().getUser_name());
				label.setParent(listcell);				
				
				return listcell;
			}
						
			private Listcell initEdit(Listcell listcell, Settlement settlement) {
				Button editButton = new Button();
				
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.addEventListener("onClick", new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						Map<String, Settlement> args = 
								Collections.singletonMap("settlement", settlement);
						
						Window settlementDialogViewWin = 
								(Window) Executions.createComponents("/settlement/SettlementDialogView.zul", null, args);
						
						settlementDialogViewWin.doModal();
						
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onAfterRender$settlementListbox(Event event) throws Exception {
		infoResultlabel.setValue("Total: "+getFormatedInteger(settlementCount)+
				" settlement - Rp."+toLocalFormat(totalSettlementVal));
	}
	
	public void onClick$addButton(Event event) throws Exception {
		Window settlementDialogWin = (Window) Executions.createComponents("/settlement/SettlementDialog.zul", null, null);
		
		settlementDialogWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Settlement settlement = (Settlement) event.getData();
				
				// settlement date --> can't access this from the settlementDetail
				Date settlementDate = settlement.getSettlementDate();
				
				// selected customer to settle
				Customer customer = settlement.getCustomer();
				
				CustomerReceivable receivable = 
						getCustomerReceivableFromCustomerByProxy(customer.getId()); 
				List<CustomerReceivableActivity> activityList = 
						receivable.getCustomerReceivableActivities();
												
				for (SettlementDetail detail : settlement.getSettlementDetails()) {
					
					// System.out.println("customerOrderId to Settle="+detail.getCustomerOrder().getId());

					boolean installment = false;
					CustomerReceivableActivity selectedActivity = null;
					
					for (CustomerReceivableActivity activity : activityList) {
						
						if (activity.getCustomerOrderId()==detail.getCustomerOrder().getId()) {
						
							installment = activity.getPaymentDate()!=null;
							
							selectedActivity = activity;
							
							break;
						}
					}
					
					if (installment) {
						// add new												
						activityList.add(getCustomerReceivableActivity(
								detail, new CustomerReceivableActivity(), settlementDate));
					} else {
						// update
						getCustomerReceivableActivity(detail, selectedActivity, settlementDate);						
					}
				}
				
				// assign receivable
				settlement.setCustomerReceivable(receivable);
				
				// assign the current login user
				settlement.setUserCreate(getLoginUser());
				
				// save
				getSettlementDao().save(settlement);
				
				// re-load / re-list based on selected tab
				int selIndex = settlementPeriodTabbox.getSelectedIndex();
				
				listBySelection(selIndex);

			}

			private CustomerReceivableActivity getCustomerReceivableActivity(
					SettlementDetail detail, CustomerReceivableActivity selectedActivity, Date settlementDate) {
				
				if (selectedActivity.getId()==Long.MIN_VALUE) {
					// sales
					selectedActivity.setCustomerOrderId(detail.getCustomerOrder().getId());
					selectedActivity.setActivityType(ActivityType.PEMBAYARAN);
					selectedActivity.setSalesDate(detail.getCustomerOrder().getOrderDate());
					selectedActivity.setPaymentDueDate(asDate(addDate(
							detail.getCustomerOrder().getCreditDay(), 
							asLocalDate(detail.getCustomerOrder().getOrderDate()))));
					selectedActivity.setSalesDescription("Cicilan");
					selectedActivity.setAmountSales(BigDecimal.ZERO);
					selectedActivity.setAmountSalesPpn(BigDecimal.ZERO);					
				}
				// payment
				selectedActivity.setPaymentDate(settlementDate);
				selectedActivity.setPaymentDescription("");
				long overDueDay = dayDiff(asLocalDate(detail.getCustomerOrder().getOrderDate()), asLocalDate(settlementDate));
				selectedActivity.setOverdueDay(java.lang.Math.toIntExact(overDueDay));
				selectedActivity.setAmountPaid(detail.getAmountSettled());
				selectedActivity.setAmountPaidPpn(BigDecimal.ZERO);
				selectedActivity.setPaymentComplete(detail.getCustomerOrder().isPaymentComplete());
				selectedActivity.setRemainingAmount(detail.getCustomerOrder().isPaymentComplete() ?
						BigDecimal.ZERO :
						detail.getRemainingAmountToSettle());
				selectedActivity.setReceivableStatus(DocumentStatus.NORMAL);
				
				return selectedActivity;
			}
		});
		
		settlementDialogWin.doModal();
	}

	public void onClick$batalButton(Event event) throws Exception {
		if (settlementListbox.getSelectedItem()==null) {
			throw new Exception("Belum memilih Settlement untuk dibatalkan.");
		} else {
			Settlement selSettlement = settlementListbox.getSelectedItem().getValue();
			
			Map<String, Settlement> args =
					Collections.singletonMap("settlement", selSettlement);
			
			Window settlementBatalWin = 
					(Window) Executions.createComponents("/settlement/SettlementDialogBatal.zul", null, args);
			
			settlementBatalWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					Settlement settlement = (Settlement) event.getData();
					
					// NOTE: need to:
					// 1. 	reset the 'amountPaid' and 'paymentComplete' in CustomerOrder (if multiple 
					// 		CustomerOrder is selected, each one of them must be updated)
					// 2. 	reset the 'amountPaid'; 'amountPaidPPN'; 'paymentComplete'; 'remainingAmount';
					//		and 'receivableStatus' in CustomerReceivableActivity
					
					// 1. reset CustomerOrder
					// create a list to hold customerOrder id
					List<Long> customerOrderIdList = new ArrayList<Long>();
					
					// access the SettlementDetail and store the CustomerOrder id into a list
					for (SettlementDetail settlementDetail : settlement.getSettlementDetails()) {
						log.info("Resetting: "+settlementDetail.toString());
						
						// save the customerOrder id into a list (because we need it later to reset 
						// the customer receivable based on the customerOrder id)
						customerOrderIdList.add(settlementDetail.getCustomerOrder().getId());
						
						// reset the 'amountPaid' and 'paymentComplete' in CustomerOrder
						settlementDetail.getCustomerOrder().setAmountPaid(BigDecimal.ZERO);
						settlementDetail.getCustomerOrder().setPaymentComplete(false);
					}
					
					// 2. reset CustomerReceivableActivity
					// get all the receivable activities from the Settlement
					// Settlement settlementByProxy = getSettlementDao().findCustomerReceivableByProxy(settlement.getId());
					// List<CustomerReceivableActivity> receivableActivities = settlementByProxy.getCustomerReceivable().getCustomerReceivableActivities();
					
					Customer customerByProxy = getCustomerByProxy(settlement.getId());
					
					CustomerReceivable receivable =
							getCustomerReceivableFromCustomerByProxy(customerByProxy.getId());
					List<CustomerReceivableActivity> receivableActivities =
							receivable.getCustomerReceivableActivities();
					
					// find the customerOrderId in the list and find the ActivityType of PENJUALAN (don't want ActivityType of PEMBAYARAN)
					for (CustomerReceivableActivity customerReceivableActivity : receivableActivities) {
						Long customerOrderId = customerReceivableActivity.getCustomerOrderId();
						
						if (customerOrderIdList.contains(customerOrderId)) {
							if (customerReceivableActivity.getActivityType().compareTo(ActivityType.PENJUALAN)==0) {
								log.info("Resetting: "+customerReceivableActivity.toString());
								
								// reset the 'amountPaid'; 'amountPaidPPN'; 'paymentComplete'; 'remainingAmount'; 'receivableStatus'
								customerReceivableActivity.setPaymentDate(null);
								customerReceivableActivity.setAmountPaid(BigDecimal.ZERO);
								customerReceivableActivity.setAmountPaidPpn(BigDecimal.ZERO);
								customerReceivableActivity.setPaymentComplete(false);
								customerReceivableActivity.setRemainingAmount(BigDecimal.ZERO);
							}
						} 
						
						//else {
						//	log.error("CustomerOrderId from CustomerReceivableActivity no match for the SettlementDetail(s)");
						//}
						
					}
					
					// assign receivable to settlement
					settlement.setCustomerReceivable(receivable);
					
					// update
					getSettlementDao().update(settlement);
					
					// re-load / re-list
					int selIndex = settlementPeriodTabbox.getSelectedIndex();
					
					listBySelection(selIndex);					
					
				}
			});
			
			settlementBatalWin.doModal();
		}
		
	}
	
	protected CustomerReceivable getCustomerReceivableFromCustomerByProxy(long id) throws Exception {
		Customer customer = getCustomerDao().findCustomerReceivableByProxy(id);
		
		return customer.getCustomerReceivable();
	}

	protected VoucherGiroReceipt getVoucherGiroReceiptByProxy(Long id) throws Exception {
		Settlement settlement = getSettlementDao().findVoucherGiroReceiptByProxy(id);
		
		return settlement.getVoucherGiroReceipt();
	}	
	
	protected VoucherPayment getVoucherPaymentByProxy(Long id) throws Exception {
		Settlement settlement = getSettlementDao().findVoucherPaymentByProxy(id);
		
		return settlement.getVoucherPayment();
	}
	
	protected Customer getCustomerByProxy(Long id) throws Exception {
		Settlement settlement = getSettlementDao().findCustomerByProxy(id);
		
		return settlement.getCustomer();
	}	
	
	public SettlementDao getSettlementDao() {
		return settlementDao;
	}

	public void setSettlementDao(SettlementDao settlementDao) {
		this.settlementDao = settlementDao;
	}

	public List<Settlement> getSettlementList() {
		return settlementList;
	}

	public void setSettlementList(List<Settlement> settlementList) {
		this.settlementList = settlementList;
	}

	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
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

	public CustomerReceivableDao getCustomerReceivableDao() {
		return customerReceivableDao;
	}

	public void setCustomerReceivableDao(CustomerReceivableDao customerReceivableDao) {
		this.customerReceivableDao = customerReceivableDao;
	}
	
}
