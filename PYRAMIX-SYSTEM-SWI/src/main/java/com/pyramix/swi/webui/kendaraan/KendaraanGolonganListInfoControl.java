package com.pyramix.swi.webui.kendaraan;

import java.util.Collections;
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
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.kendaraan.KendaraanGolongan;
import com.pyramix.swi.persistence.kendaraan.dao.KendaraanGolonganDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class KendaraanGolonganListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5163178220716018527L;

	private KendaraanGolonganDao kendaraanGolonganDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox kendaraanGolonganListbox;
	
	private List<KendaraanGolongan> kendaraanGolonganList;
	
	public void onCreate$kendaraanGolonganListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Golongan Kendaraan");
		
		displayKendaraanGolonganInfo();
	}

	private void displayKendaraanGolonganInfo() throws Exception {
		setKendaraanGolonganList(
				getKendaraanGolonganDao().findAllKendaraanGolongan());
		
		infoResultlabel.setValue(
				"Total: "+getKendaraanGolonganList().size()+" Golongan Kendaraan");
		
		kendaraanGolonganListbox.setModel(
				new ListModelList<KendaraanGolongan>(getKendaraanGolonganList()));
		kendaraanGolonganListbox.setItemRenderer(
				getKendaraanGolonganListitemRenderer());
	}

	private ListitemRenderer<KendaraanGolongan> getKendaraanGolonganListitemRenderer() {
		
		return new ListitemRenderer<KendaraanGolongan>() {
			
			@Override
			public void render(Listitem item, KendaraanGolongan golongan, int index) throws Exception {
				Listcell lc;
				
				// Golongan
				lc = new Listcell(golongan.getGolongan().toString());
				lc.setParent(item);
				
				// Penjelasan
				lc = new Listcell(golongan.getPenjelasan());
				lc.setParent(item);
				
				// edit
				lc = initEdit(new Listcell(), golongan);
				lc.setParent(item);
				
				// delete
				lc = initDelete(new Listcell(), golongan);
				lc.setParent(item);
			}

			private Listcell initEdit(Listcell listcell, KendaraanGolongan golongan) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setSclass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, KendaraanGolongan> args = 
								Collections.singletonMap("kendaraanGolongan", golongan);
						
						Window kendaraanGolonganEditWin = 
								(Window) Executions.createComponents("/kendaraan/KendaraanGolonganDialog.zul", null, args);
						
						kendaraanGolonganEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								KendaraanGolongan kendaraanGolongan = 
										(KendaraanGolongan) event.getData();
								
								// update
								getKendaraanGolonganDao().update(kendaraanGolongan);
								
								// display
								displayKendaraanGolonganInfo();
							}
						});
						
						kendaraanGolonganEditWin.doModal();
						
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
			
			private Listcell initDelete(Listcell listcell, KendaraanGolongan golongan) {
				Button editButton = new Button();
				editButton.setLabel("Delete");
				editButton.setSclass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// delete
						getKendaraanGolonganDao().delete(golongan);
						
						// display
						displayKendaraanGolonganInfo();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}

		};
	}

	public void onClick$addButton(Event event) throws Exception {
		Map<String, KendaraanGolongan> args = 
				Collections.singletonMap("kendaraanGolongan", new KendaraanGolongan());
		
		Window kendaraanGolonganWin = 
				(Window) Executions.createComponents("/kendaraan/KendaraanGolonganDialog.zul", null, args);
		
		kendaraanGolonganWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				KendaraanGolongan kendaraanGolongan = 
						(KendaraanGolongan) event.getData();
				
				// save
				getKendaraanGolonganDao().save(kendaraanGolongan);
				
				// display
				displayKendaraanGolonganInfo();
			}
		});
		
		kendaraanGolonganWin.doModal();
	}
	
	public KendaraanGolonganDao getKendaraanGolonganDao() {
		return kendaraanGolonganDao;
	}

	public void setKendaraanGolonganDao(KendaraanGolonganDao kendaraanGolonganDao) {
		this.kendaraanGolonganDao = kendaraanGolonganDao;
	}

	public List<KendaraanGolongan> getKendaraanGolonganList() {
		return kendaraanGolonganList;
	}

	public void setKendaraanGolonganList(List<KendaraanGolongan> kendaraanGolonganList) {
		this.kendaraanGolonganList = kendaraanGolonganList;
	}
}
