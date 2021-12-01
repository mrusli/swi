package com.pyramix.swi.webui.kendaraan;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.kendaraan.Jenis;
import com.pyramix.swi.domain.kendaraan.Kendaraan;
import com.pyramix.swi.domain.kendaraan.KendaraanGolongan;
import com.pyramix.swi.domain.kendaraan.Merk;
import com.pyramix.swi.persistence.kendaraan.dao.KendaraanGolonganDao;
import com.pyramix.swi.webui.common.DateComboValue;
import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PageMode;

public class KendaraanDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2909165235553787534L;

	private KendaraanGolonganDao kendaraanGolonganDao;
	
	private Window kendaraanDialogWin;
	private Combobox kendaraanJenisCombobox, kendaraanMerkCombobox, kendaraanGolonganCombobox,
		tahunPembuatanCombobox;
	private Checkbox activeCheckbox;
	private Datebox stnkExpiryDatebox;
	private Textbox noplatTextbox, tipeTextbox, warnaTextbox, noteTextbox;
	
	private Kendaraan kendaraan;
	private List<KendaraanGolongan> golonganList;
	private PageMode pageMode;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setKendaraan(
				(Kendaraan) arg.get("kendaraan"));
	}

	public void onCreate$kendaraanDialogWin(Event event) throws Exception {
		setPageMode(getKendaraan().getId()==Long.MIN_VALUE ?
				PageMode.NEW : PageMode.EDIT);
		
		kendaraanDialogWin.setTitle(getPageMode().compareTo(PageMode.NEW)==0 ?
				"Tambah Kendaraan" : "Edit Kendaraan");
		
		stnkExpiryDatebox.setLocale(getLocale());
		stnkExpiryDatebox.setFormat(getLongDateFormat());
		
		setGolonganList(
				getKendaraanGolonganDao().findAllKendaraanGolongan());
		
		setupComboboxes();
		
		setKendaraanInfo();
	}

	private void setupComboboxes() {
		Comboitem comboitem;
		
		// jenis kendaraan
		for (Jenis jenis : Jenis.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(jenis.toString());
			comboitem.setValue(jenis);
			comboitem.setParent(kendaraanJenisCombobox);
		}
		
		// merk kendaraan
		for (Merk merk : Merk.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(merk.toString());
			comboitem.setValue(merk);
			comboitem.setParent(kendaraanMerkCombobox);			
		}

		// golongan kendaraan
		for (KendaraanGolongan golongan : getGolonganList()) {
			comboitem = new Comboitem();
			comboitem.setLabel(golongan.getGolongan()+"-"+golongan.getPenjelasan());
			comboitem.setValue(golongan);
			comboitem.setParent(kendaraanGolonganCombobox);						
		}
		
		// tahun pembuatan
		List<DateComboValue> yearList = createYearComboValues(20);
		for (DateComboValue year : yearList) {
			comboitem = new Comboitem();
			comboitem.setLabel(year.getDisplayValue());
			comboitem.setValue(year.getDateValue());
			comboitem.setParent(tahunPembuatanCombobox);
		}
		
	}

	private void setKendaraanInfo() {
		
		if (getPageMode().compareTo(PageMode.NEW)==0) {
			// new			
			kendaraanJenisCombobox.setSelectedIndex(0);
			kendaraanMerkCombobox.setSelectedIndex(21);
			kendaraanGolonganCombobox.setSelectedIndex(0);
			// aktif
			activeCheckbox.setChecked(true);
			stnkExpiryDatebox.setValue(asDate(getLocalDate()));
			noplatTextbox.setValue("");
			// tahunPembuatanCombobox
			tahunPembuatanCombobox.setSelectedIndex(0);
			tipeTextbox.setValue("");
			warnaTextbox.setValue("");
			noteTextbox.setValue("");
		} else {
			// edit
			
			// kendaraanJenisCombobox
			for (Comboitem item : kendaraanJenisCombobox.getItems()) {
				if (item.getValue().equals(getKendaraan().getJenisKendaraan())) {
					kendaraanJenisCombobox.setSelectedItem(item);
				}
			}

			// kendaraanMerkCombobox
			for (Comboitem item : kendaraanMerkCombobox.getItems()) {
				if (item.getValue().equals(getKendaraan().getMerkKendaraan())) {
					kendaraanMerkCombobox.setSelectedItem(item);
				}
			}
			
			// kendaraanGolonganCombobox
			Long golonganId = getKendaraan().getGolonganKendaraan().getId();
			for (Comboitem item : kendaraanGolonganCombobox.getItems()) {
				KendaraanGolongan golonganItem = item.getValue();
				Long golonganItemId = golonganItem.getId(); 
				
				if (golonganItemId==golonganId) {
					kendaraanGolonganCombobox.setSelectedItem(item);
				}
			}
			
			activeCheckbox.setChecked(getKendaraan().isActive());
			stnkExpiryDatebox.setValue(getKendaraan().getStnkExpiryDate());
			noplatTextbox.setValue(getKendaraan().getNomorPolisi());
			
			// tahunPembuatanCombobox
			for (Comboitem item : tahunPembuatanCombobox.getItems()) {
				if (item.getValue().equals(asLocalDate(getKendaraan().getTahunPembuatan()))) {
					tahunPembuatanCombobox.setSelectedItem(item);
				}
			}
			
			tipeTextbox.setValue(getKendaraan().getTipeKendaraan());
			warnaTextbox.setValue(getKendaraan().getWarna());
			noteTextbox.setValue(getKendaraan().getNote());
		}
		
	}

	public void onClick$saveButton(Event event) throws Exception {
				
		Events.sendEvent(Events.ON_OK, 
				kendaraanDialogWin, 
				getUserModifiedKendaraanInfo());
		
		kendaraanDialogWin.detach();
	}
	
	private Kendaraan getUserModifiedKendaraanInfo() {
		Kendaraan userModKendaraan = getKendaraan();
		
		userModKendaraan.setStnkExpiryDate(stnkExpiryDatebox.getValue());
		userModKendaraan.setJenisKendaraan(kendaraanJenisCombobox.getSelectedItem().getValue());
		userModKendaraan.setMerkKendaraan(kendaraanMerkCombobox.getSelectedItem().getValue());
		userModKendaraan.setGolonganKendaraan(kendaraanGolonganCombobox.getSelectedItem().getValue());
		userModKendaraan.setNomorPolisi(noplatTextbox.getValue());
		userModKendaraan.setActive(activeCheckbox.isChecked());
		userModKendaraan.setTahunPembuatan(asDate(tahunPembuatanCombobox.getSelectedItem().getValue()));
		userModKendaraan.setTipeKendaraan(tipeTextbox.getValue());
		userModKendaraan.setWarna(warnaTextbox.getValue());
		userModKendaraan.setNote(noteTextbox.getValue());
		
		return userModKendaraan;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		kendaraanDialogWin.detach();
	}
	
	public Kendaraan getKendaraan() {
		return kendaraan;
	}

	public void setKendaraan(Kendaraan kendaraan) {
		this.kendaraan = kendaraan;
	}

	public KendaraanGolonganDao getKendaraanGolonganDao() {
		return kendaraanGolonganDao;
	}

	public void setKendaraanGolonganDao(KendaraanGolonganDao kendaraanGolonganDao) {
		this.kendaraanGolonganDao = kendaraanGolonganDao;
	}

	public List<KendaraanGolongan> getGolonganList() {
		return golonganList;
	}

	public void setGolonganList(List<KendaraanGolongan> golonganList) {
		this.golonganList = golonganList;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
