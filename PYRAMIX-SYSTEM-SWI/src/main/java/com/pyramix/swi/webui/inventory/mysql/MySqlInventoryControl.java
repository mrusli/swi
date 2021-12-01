package com.pyramix.swi.webui.inventory.mysql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;

import com.pyramix.swi.domain.db.ConnectionSetting;
import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryLocation;
import com.pyramix.swi.domain.inventory.InventoryPacking;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.mysql.MySqlInventory;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.persistence.sql.dao.SqlUtilityDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class MySqlInventoryControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long 	serialVersionUID 		= -841344570021719621L;
	private static final String hibernateConfigXMLMySql = "hibernate.cfg.mysql.xml";
	
	private SqlUtilityDao sqlUtilityDao;
	private InventoryDao inventoryDao;
	private ConnectionSetting sqlSetting;
	private SessionFactory sessionFactory;
	private Listbox inventoryListbox;
	
	public void onCreate$mySqlInventoryListInfo(Event event) throws Exception {
		// load setting from database
		setSqlSetting(
				getSqlUtilityDao().findSqlSettingById(new Long(2)));
		
		// set hibernate configuration
		Configuration configuration = getSqlUtilityDao().setHibernateConfiguration(
				getSqlSetting());
		
		// build service registry
		ServiceRegistry serviceRegistry = getSqlUtilityDao().buildServiceRegistry(
				configuration, hibernateConfigXMLMySql);
		
		// get sesstionfactory
		// SessionFactory sessionFactory = 
		//		serviceRegistry);
		setSessionFactory(getSqlUtilityDao().getSessionFactory(
				serviceRegistry));

		// get inventory where status is 'current' (3)
		List<MySqlInventory> inventoryList = 
				getSqlUtilityDao().findAllMySqlInventoryByStatus(getSessionFactory(), 3);
		
		inventoryListbox.setModel(new ListModelList<MySqlInventory>(inventoryList));
		inventoryListbox.setItemRenderer(getMySqlInventoryListitemRenderer());
	}
	
	private ListitemRenderer<MySqlInventory> getMySqlInventoryListitemRenderer() {
		
		return new ListitemRenderer<MySqlInventory>() {
			
			@Override
			public void render(Listitem item, MySqlInventory data, int index) throws Exception {
				Listcell lc;
				
				// Specification
				lc = initSpecification(new Listcell(), data); 
				lc.setParent(item);
				
				// Packing
				lc = initPacking(new Listcell(), data);
				lc.setParent(item);
				
				// Qty (S/L)
				lc = new Listcell(getFormatedInteger(data.getSheetQuantity()));
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell(getFormatedFloatLocal(data.getQtyInventory()));
				lc.setParent(item);
				
				// Location
				lc = initLocation(new Listcell(), data);
				lc.setParent(item);
				
				// Note
				lc = new Listcell(data.getNote());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Coil No
				lc = new Listcell(data.getInventoryMarking());
				lc.setParent(item);
				
				// Contract No
				lc = new Listcell(data.getContractNum());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Received Date
				lc = new Listcell(dateToStringDisplay(
						asLocalDate(data.getReceivedDate()), getLongDateFormat()));
				lc.setParent(item);
				
				// Code
				lc = initCode(new Listcell(), data);
				lc.setParent(item);

				// Transfer Button
				lc = initTransferButton(new Listcell(), data);
				lc.setParent(item);
			}

			private Listcell initSpecification(Listcell listcell, MySqlInventory data) {
				String thickness 	= getFormatedFloatLocal(data.getThickness());
				String width 		= getFormatedFloatLocal(data.getWidth());
				String length 		= data.getLength().compareTo(BigDecimal.ZERO) == 0 ? 
						"Coil" : getFormatedFloatLocal(data.getLength());
				
				listcell.setLabel(thickness + " x " + width + " x " + length);
				
				return listcell;
			}
			
			private Listcell initPacking(Listcell listcell, MySqlInventory data) {
				
				int enumVal = data.getInventoryPacking().intValue() - 1;
				
				listcell.setLabel(InventoryPacking.toString(enumVal));
				
				return listcell;
			}

			private Listcell initLocation(Listcell listcell, MySqlInventory data) {
				
				int enumVal = data.getInventoryLocation().intValue() - 1;
				
				listcell.setLabel(InventoryLocation.toString(enumVal));
				
				return listcell;
			}

			private Listcell initCode(Listcell listcell, MySqlInventory data) throws Exception {
				
				InventoryCode inventoryCode = getInventoryDao().findInventoryCodeById(data.getInventoryCode());
				
				listcell.setLabel(inventoryCode.getProductCode());
				
				return listcell;
			}
			
			private Listcell initTransferButton(Listcell listcell, MySqlInventory data) {
				Button transButton = new Button();
				transButton.setLabel(">>>");
				transButton.setWidth("35px");
				transButton.setSclass("inventoryEditButton");
				transButton.setParent(listcell);
				transButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// use list -- for compatibility reason
						List<Inventory> inventoryList = new ArrayList<Inventory>();
						
						// add into the list
						inventoryList.add(getInventoryToTransfer(data));
						
						try {
							// save
							getInventoryDao().save(inventoryList);
							
							// notif
							Clients.showNotification("Transfer Sukses...");
							
						} catch (Exception e) {
							Messagebox.show("Transfer Inventory gagal. "+e.getMessage(), 
									"Error", Messagebox.OK,  Messagebox.ERROR);			
						}
					}

					private Inventory getInventoryToTransfer(MySqlInventory data) throws Exception {
						int enumVal;
						
						// prepare InventoryCode
						InventoryCode inventoryCode = getInventoryDao().findInventoryCodeById(data.getInventoryCode());

						// prepare InventoryPacking
						enumVal = data.getInventoryPacking().intValue() - 1;
						InventoryPacking inventoryPacking = InventoryPacking.toInventoryPacking(enumVal);
						
						// prepare InventoryLocation
						enumVal = data.getInventoryLocation().intValue() - 1;
						InventoryLocation inventoryLocation = InventoryLocation.toInventoryLocation(enumVal);
						
						// create a new Inventory object
						Inventory inventory = new Inventory();
						inventory.setInventoryCode(inventoryCode);
						inventory.setThickness(data.getThickness());
						inventory.setWidth(data.getWidth());
						inventory.setLength(data.getLength());
						inventory.setSheetQuantity(data.getSheetQuantity());
						inventory.setWeightQuantity(data.getQtyInventory());
						inventory.setInventoryPacking(inventoryPacking);
						inventory.setInventoryLocation(inventoryLocation);
						inventory.setMarking(data.getInventoryMarking());
						inventory.setContractNumber(data.getContractNum());
						inventory.setReceiveDate(data.getReceivedDate());
						inventory.setNote(data.getNote());
						
						// not visible
						inventory.setLcNumber(data.getLcNum());
						
						// set to current / ready
						inventory.setInventoryStatus(InventoryStatus.ready);
						
						return inventory;
					}
				});
				
				return listcell;
			}
		};
	}

	public void onClick$allTab(Event event) throws Exception {
		// get inventory where status is 'current' (3)
		List<MySqlInventory> inventoryList = 
				getSqlUtilityDao().findAllMySqlInventoryByStatus(getSessionFactory(), 3);
		
		inventoryListbox.setModel(new ListModelList<MySqlInventory>(inventoryList));
		inventoryListbox.setItemRenderer(getMySqlInventoryListitemRenderer());		
	}
	
	public void onClick$coilTab(Event event) throws Exception {
		// get inventory where packing is 'coil' (1)
		List<MySqlInventory> inventoryList = 
				getSqlUtilityDao().findAllMySqlInventoryByPacking(getSessionFactory(), new Long(1));

		inventoryListbox.setModel(new ListModelList<MySqlInventory>(inventoryList));
		inventoryListbox.setItemRenderer(getMySqlInventoryListitemRenderer());
	}
	
	public void onClick$petianTab(Event event) throws Exception {
		// get inventory where packing is 'petian' (2)
		List<MySqlInventory> inventoryList = 
				getSqlUtilityDao().findAllMySqlInventoryByPacking(getSessionFactory(), new Long(2));

		inventoryListbox.setModel(new ListModelList<MySqlInventory>(inventoryList));
		inventoryListbox.setItemRenderer(getMySqlInventoryListitemRenderer());		
	}
	
	public void onClick$lembaranTab(Event event) throws Exception {
		// get inventory where packing is 'petian' (2)
		List<MySqlInventory> inventoryList = 
				getSqlUtilityDao().findAllMySqlInventoryByPacking(getSessionFactory(), new Long(3));

		inventoryListbox.setModel(new ListModelList<MySqlInventory>(inventoryList));
		inventoryListbox.setItemRenderer(getMySqlInventoryListitemRenderer());				
	}
	
	public SqlUtilityDao getSqlUtilityDao() {
		return sqlUtilityDao;
	}

	public void setSqlUtilityDao(SqlUtilityDao sqlUtilityDao) {
		this.sqlUtilityDao = sqlUtilityDao;
	}

	public ConnectionSetting getSqlSetting() {
		return sqlSetting;
	}

	public void setSqlSetting(ConnectionSetting sqlSetting) {
		this.sqlSetting = sqlSetting;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
