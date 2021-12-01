package com.pyramix.swi.webui.kendaraan;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.kendaraan.Golongan;
import com.pyramix.swi.domain.kendaraan.KendaraanGolongan;
import com.pyramix.swi.webui.common.GFCBaseController;

public class KendaraanGolonganDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6025962491273763551L;

	private Window kendaraanGolonganDialogWin;
	private Combobox golonganCombobox;
	private Textbox penjelasanTextbox;
	
	private KendaraanGolongan kendaraanGolongan;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setKendaraanGolongan(
				(KendaraanGolongan) arg.get("kendaraanGolongan"));
	}

	public void onCreate$kendaraanGolonganDialogWin(Event event) throws Exception {
		setGolonganCombobox();
		
		if (getKendaraanGolongan().getId().compareTo(Long.MIN_VALUE)==0) {
			golonganCombobox.setSelectedIndex(0);
		} else for (Comboitem item : golonganCombobox.getItems()) {
			if (getKendaraanGolongan().getGolongan().equals(item.getValue())) {
				golonganCombobox.setSelectedItem(item);
			}
		}
		penjelasanTextbox.setValue(getKendaraanGolongan().getPenjelasan());
	}
	
	private void setGolonganCombobox() {
		Comboitem comboitem;
		
		for (Golongan golongan : Golongan.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(golongan.toString());
			comboitem.setValue(golongan);
			comboitem.setParent(golonganCombobox);
		}		

	}

	public void onClick$saveButton(Event event) throws Exception {
		getKendaraanGolongan().setGolongan(golonganCombobox.getSelectedItem().getValue());
		getKendaraanGolongan().setPenjelasan(penjelasanTextbox.getValue());
		
		Events.sendEvent(Events.ON_OK, kendaraanGolonganDialogWin, getKendaraanGolongan());
		
		kendaraanGolonganDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		kendaraanGolonganDialogWin.detach();
	}

	public KendaraanGolongan getKendaraanGolongan() {
		return kendaraanGolongan;
	}

	public void setKendaraanGolongan(KendaraanGolongan kendaraanGolongan) {
		this.kendaraanGolongan = kendaraanGolongan;
	}
}
