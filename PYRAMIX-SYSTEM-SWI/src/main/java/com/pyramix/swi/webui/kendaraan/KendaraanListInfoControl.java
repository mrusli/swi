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

import com.pyramix.swi.domain.kendaraan.Kendaraan;
import com.pyramix.swi.persistence.kendaraan.dao.KendaraanDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class KendaraanListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8542977776729318403L;

	private KendaraanDao kendaraanDao;
	
	private Label formTitleLabel, infoResultlabel;
	private Listbox kendaraanListbox;
	
	private List<Kendaraan> kendaraanList;
	
	public void onCreate$kendaraanListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Kendaraan");
		
		displayKendaraanListInfo();
	}

	private void displayKendaraanListInfo() throws Exception {
		setKendaraanList(
				getKendaraanDao().findAllKendaraan());
		
		// Total: 15 Kendaraan
		infoResultlabel.setValue(
				"Total: "+getKendaraanList().size()+" Kendaraan");
		
		kendaraanListbox.setModel(
				new ListModelList<Kendaraan>(getKendaraanList()));
		kendaraanListbox.setItemRenderer(getKendaraanListitemRenderer());
	}

	private ListitemRenderer<Kendaraan> getKendaraanListitemRenderer() {

		return new ListitemRenderer<Kendaraan>() {
			
			@Override
			public void render(Listitem item, Kendaraan kendaraan, int index) throws Exception {
				Listcell lc;
				
				// Jenis Kendaraan
				lc = new Listcell(kendaraan.getJenisKendaraan().toString());
				lc.setParent(item);
				
				// Merk
				lc = new Listcell(kendaraan.getMerkKendaraan().toString());
				lc.setParent(item);
				
				// Golongan
				lc = new Listcell(kendaraan.getGolonganKendaraan().getGolongan()+"-"+
						kendaraan.getGolonganKendaraan().getPenjelasan());
				lc.setParent(item);
				
				// Plat No.
				lc = new Listcell(kendaraan.getNomorPolisi());
				lc.setParent(item);
				
				// Aktif
				lc = new Listcell(kendaraan.isActive() ?
						"Aktif":"Non-Aktif");
				lc.setParent(item);
				
				// Tahun Pembuatan
				lc = new Listcell(getLocalDateYear(asLocalDate(kendaraan.getTahunPembuatan())));
				lc.setParent(item);
				
				// Catatan
				lc = new Listcell(kendaraan.getNote());
				lc.setParent(item);
				
				// edit
				lc = initEdit(new Listcell(), kendaraan);
				lc.setParent(item);
			}

			private Listcell initEdit(Listcell listcell, Kendaraan kendaraan) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setSclass("inventoryEditButton");
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, Kendaraan> args = 
								Collections.singletonMap("kendaraan", kendaraan);
						
						Window kendaraanEditWin = 
								(Window) Executions.createComponents("/kendaraan/KendaraanDialog.zul", null, args);
						
						kendaraanEditWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Kendaraan kendaraan = (Kendaraan) event.getData();
								
								// update
								getKendaraanDao().update(kendaraan);
								
								// display
								displayKendaraanListInfo();
							}
						});
						
						kendaraanEditWin.doModal();
					}
				});
				editButton.setParent(listcell);
				
				return listcell;
			}
		};
	}

	public void onClick$addButton(Event event) throws Exception {
		Map<String, Kendaraan> args = 
				Collections.singletonMap("kendaraan", new Kendaraan());
		
		Window addKendaraanWin = 
				(Window) Executions.createComponents("/kendaraan/KendaraanDialog.zul", null, args);
		
		addKendaraanWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Kendaraan kendaraan = (Kendaraan) event.getData();
				
				// save
				getKendaraanDao().save(kendaraan);
				
				// display
				displayKendaraanListInfo();
			}
		});
		
		addKendaraanWin.doModal();
		
	}
	
	public KendaraanDao getKendaraanDao() {
		return kendaraanDao;
	}

	public void setKendaraanDao(KendaraanDao kendaraanDao) {
		this.kendaraanDao = kendaraanDao;
	}

	public List<Kendaraan> getKendaraanList() {
		return kendaraanList;
	}

	public void setKendaraanList(List<Kendaraan> kendaraanList) {
		this.kendaraanList = kendaraanList;
	}
	
	
}
