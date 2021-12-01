package com.pyramix.swi.webui.inventory.sqlserver;

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
import org.zkoss.zul.Tabbox;

import com.pyramix.swi.domain.db.ConnectionSetting;
import com.pyramix.swi.domain.inventory.Inventory;
import com.pyramix.swi.domain.inventory.InventoryCode;
import com.pyramix.swi.domain.inventory.InventoryStatus;
import com.pyramix.swi.domain.sqlserver.SqlInventory;
import com.pyramix.swi.persistence.inventory.dao.InventoryDao;
import com.pyramix.swi.persistence.sql.dao.SqlUtilityDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class SqlServerInventoryControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2452910835098252469L;

	private SqlUtilityDao sqlUtilityDao;
	private InventoryDao inventoryDao;
	
	private Tabbox shapeTabbox;
	private Listbox inventoryListbox;
	
	private ConnectionSetting sqlSetting;
	private SessionFactory sessionFactory;
	private List<SqlInventory> sqlInventoryList;
	
	private final String hibernateConfigXMLSqlServer = "hibernate.cfg.sqlserver.xml";
	private final int DEFAULT_OTHER_CODE = 101;
	
	public void onCreate$sqlServerInventoryListInfo(Event event) throws Exception {
		// load setting from database
		setSqlSetting(
				getSqlUtilityDao().findSqlSettingById(new Long(1)));
		
		// set hibernate configuration
		Configuration configuration = getSqlUtilityDao().setHibernateConfiguration(
				getSqlSetting());

		// build service registry
		ServiceRegistry serviceRegistry = getSqlUtilityDao().buildServiceRegistry(
				configuration, hibernateConfigXMLSqlServer);

		// get sesstionfactory
		setSessionFactory(getSqlUtilityDao().getSessionFactory(
				serviceRegistry));
		
		setSqlInventoryList(
				getSqlUtilityDao().findAllSqlInventory(getSessionFactory()));

		listSqlInventory();
	}

	private void listSqlInventory() {
		inventoryListbox.setModel(
				new ListModelList<SqlInventory>(getSqlInventoryList()));
		inventoryListbox.setItemRenderer(getSqlInventoryListitemRenderer());
	}

	private ListitemRenderer<SqlInventory> getSqlInventoryListitemRenderer() {

		return new ListitemRenderer<SqlInventory>() {
			
			@Override
			public void render(Listitem item, SqlInventory data, int index) throws Exception {
				Listcell lc;
				
				// Status
				lc = new Listcell(data.getStatus());
				lc.setParent(item);
				
				
				// Deskripsi
				String length;
				if (data.getLength()==0) {
					length="Coil";
				} else {
					length=getFormatedFloatLocal(data.getLength());
				}
				lc = new Listcell(getFormatedFloatLocal(data.getThickness())+" x "+
						getFormatedFloatLocal(data.getWidth())+" x "+
						length);
				lc.setParent(item);
				
				// Shape
				lc = new Listcell(data.getShape());
				lc.setParent(item);
				
				// Qty (Sht)
				lc = new Listcell(String.valueOf(data.getSheetCount()));
				lc.setParent(item);
				
				// Qty (Kg)
				lc = new Listcell(String.valueOf(data.getQuantity()));
				lc.setParent(item);
				
				// Location
				lc = new Listcell(data.getLocation());
				lc.setParent(item);
				
				// Note
				lc = new Listcell(data.getNote());
				lc.setStyle("white-space:nowrap;");
				lc.setParent(item);
				
				// Coil Num
				lc = new Listcell(data.getCoilNum());
				lc.setParent(item);
				
				// Cnrt Num
				lc = new Listcell(data.getCnrtNum());
				lc.setStyle("white-space:nowrap;");
				lc.setParent(item);
				
				// LC Num
				lc = new Listcell(data.getLcNum());
				lc.setStyle("white-space:nowrap;");
				lc.setParent(item);
				
				// Date In
				lc = new Listcell(dateToStringDisplay(asLocalDate(data.getDateIn()), getShortDateFormat()));
				lc.setParent(item);
				
				// Order Num
				lc = new Listcell(data.getOrderNum());
				lc.setParent(item);
				
				// Code
				lc = new Listcell(data.getCode());
				lc.setParent(item);
				
				// Type
				lc = new Listcell(data.getType());
				lc.setStyle("white-space:nowrap;");
				lc.setParent(item);
				
				// Transfer Button
				lc = initTransferButton(new Listcell(), data);
				lc.setParent(item);
				
			}

			private Listcell initTransferButton(Listcell listcell, SqlInventory sqlData) {
				Button transButton = new Button();
				transButton.setLabel(">>>");
				transButton.setWidth("35px");
				transButton.setSclass("inventoryEditButton");
				transButton.setParent(listcell);
				transButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						// use list -- for compatibility reason
						List<Inventory> inventoryList = new ArrayList<Inventory>();
						
						// add into the list
						inventoryList.add(getSqlInventoryData(sqlData, new Inventory()));
								// getInventoryToTransfer(data));
						
						// save
						getInventoryDao().save(inventoryList);
						
						// notif
						Clients.showNotification("Transfer Sukses...");
					}

				});
				transButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onSelect$shapeTabbox(Event event) throws Exception {
		switch (shapeTabbox.getSelectedIndex()) {
		case 0:
			// semua
			setSqlInventoryList(
					getSqlUtilityDao().findAllSqlInventory(getSessionFactory()));
			// list
			listSqlInventory();
			break;
		case 1:
			// coil
			setSqlInventoryList(
					getSqlUtilityDao().findSqlInventory_ByShape(getSessionFactory(), "STK"));
			// list
			listSqlInventory();			
			break;
		case 2:
			// petian
			setSqlInventoryList(
					getSqlUtilityDao().findSqlInventory_ByShape(getSessionFactory(), "CRT"));
			// list
			listSqlInventory();			
			break;
		case 3:
			// lembaran
			setSqlInventoryList(
					getSqlUtilityDao().findSqlInventory_ByShape(getSessionFactory(), "SHT"));
			// list
			listSqlInventory();						
			break;
		default:
			break;
		}
	}
	
	public void onClick$transferAllButton(Event event) throws Exception {
		// use list -- for compatibility reason
		List<Inventory> inventoryList = new ArrayList<Inventory>();		
		
		// NOTE !!! Set to limit the transfer -- by SHT, CRT or STK
		// petian
		setSqlInventoryList(
				getSqlUtilityDao().findSqlInventory_ByShape(getSessionFactory(), "SHT"));

		for (SqlInventory sqlData : getSqlInventoryList()) {

			inventoryList.add(getSqlInventoryData(sqlData, new Inventory()));
		}
		
		getInventoryDao().save(inventoryList);
	}

	private Inventory getSqlInventoryData(SqlInventory data, Inventory inventory) throws Exception {
		// thickness
		inventory.setThickness(new BigDecimal(data.getThickness()));
		
		// width
		inventory.setWidth(new BigDecimal(data.getWidth()));
		
		// length
		inventory.setLength(new BigDecimal(data.getLength()));
		
		// sheet_quantity
		inventory.setSheetQuantity((int)data.getSheetCount());
		
		// weight_quantity
		inventory.setWeightQuantity(new BigDecimal(data.getQuantity()));
		
		// marking
		inventory.setMarking(data.getCoilNum());
		
		// description -- not used
		
		// contract no
		inventory.setContractNumber(data.getCnrtNum());
		
		// lc no
		inventory.setLcNumber(data.getLcNum());
		
		// receive date
		inventory.setReceiveDate(asDate(getLocalDate()));
		
		// sku -- not used
		
		// inventory_code_id_fk
		inventory.setInventoryCode(getInventoryCode(data));
		
		// inventory_status
		inventory.setInventoryStatus(InventoryStatus.ready);
		
		// inventory_location
		int ordinalLocation = SqlServerInventoryLocation.valueOf(data.getLocation()).ordinal();
		inventory.setInventoryLocation(
				SqlServerInventoryLocation.toInventoryLocation(ordinalLocation));
		
		// inventory_packing
		int ordinalShape = SqlServerInventoryShape.valueOf(data.getShape()).ordinal();
		inventory.setInventoryPacking(
				SqlServerInventoryShape.toInventoryPacking(ordinalShape));
		
		return inventory;
	}
	
	private InventoryCode getInventoryCode(SqlInventory data) throws Exception {
		InventoryCode inventoryCode = null;
		
		for (SqlServerInventoryType inventoryType : SqlServerInventoryType.values()) {
			if (data.getType().compareTo(inventoryType.toString(inventoryType.ordinal()))==0) {
				Long id = inventoryType.toId(inventoryType.ordinal());
				
				inventoryCode = getInventoryDao().findInventoryCodeById(id);
			}
		}
		
		// if NOT in pyramix db -- sign to DEFAULT_OTHER_CODE (OT) id 151
		if (inventoryCode == null) {
			Long id = new Long(DEFAULT_OTHER_CODE);
			
			inventoryCode = getInventoryDao().findInventoryCodeById(id);
		}
		
		return inventoryCode;
	}

	public SqlUtilityDao getSqlUtilityDao() {
		return sqlUtilityDao;
	}

	public void setSqlUtilityDao(SqlUtilityDao sqlUtilityDao) {
		this.sqlUtilityDao = sqlUtilityDao;
	}

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	public ConnectionSetting getSqlSetting() {
		return sqlSetting;
	}

	public void setSqlSetting(ConnectionSetting sqlSetting) {
		this.sqlSetting = sqlSetting;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public List<SqlInventory> getSqlInventoryList() {
		return sqlInventoryList;
	}

	public void setSqlInventoryList(List<SqlInventory> sqlInventoryList) {
		this.sqlInventoryList = sqlInventoryList;
	}
	
}
