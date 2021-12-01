package com.pyramix.swi.webui.user;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.webui.common.GFCBaseController;
import com.pyramix.swi.webui.common.PasswordDictionary;
import com.pyramix.swi.webui.common.PasswordGenerator;
import com.pyramix.swi.webui.security.PasswordEncoderImpl;

public class PasswordDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6298850238934955796L;

	private Window passwordDialogWin;
	private Combobox passwordLengthCombobox;
	private Checkbox dicAlphaCapsCheckbox, dicAlphaCheckbox, dicNumericCheckbox, 
		dicSpecialCharCheckbox;
	private Textbox passwordTextbox;
	
	private int[] passwordLengthOption = { 10, 15, 20 };	
	
	public void onCreate$passwordDialogWin(Event event) throws Exception {
		setupPasswordLengthCombobox();
		
	}

	private void setupPasswordLengthCombobox() {
		Comboitem comboitem;
		
		for (int length : passwordLengthOption) {
			comboitem = new Comboitem(getFormatedInteger(length));
			comboitem.setValue(length);
			comboitem.setParent(passwordLengthCombobox);
		}
		
		passwordLengthCombobox.setSelectedIndex(0);
	}

	public void onClick$generatePasswordButton(Event event) throws Exception {
		// dictionary
		String includedDic = getUserSelDictionary();
		// length
		int passwordLength = passwordLengthCombobox.getSelectedItem().getValue();

		PasswordGenerator passwordGen = new PasswordGenerator();

		String samplePassword = passwordGen.generatePassword(passwordLength, includedDic); 

		passwordTextbox.setValue(samplePassword);
	}

	private String getUserSelDictionary() {
		String inclDictionary = "";
		
		for (int i = 0; i < 4; i++) {
			if (i==0 && dicAlphaCapsCheckbox.isChecked()) {
				inclDictionary = inclDictionary + PasswordDictionary.ALPHA_CAPS;
			} 
			if (i==1 && dicAlphaCheckbox.isChecked()) {
				inclDictionary = inclDictionary + PasswordDictionary.ALPHA;
			} 
			if (i==2 && dicNumericCheckbox.isChecked()) {
				inclDictionary = inclDictionary + PasswordDictionary.NUMERIC;
			} 
			if (i==3 && dicSpecialCharCheckbox.isChecked()) {
				inclDictionary = inclDictionary + PasswordDictionary.SPECIAL_CHARS;
			}
		}
		
		return inclDictionary;
	}
	
	public void onClick$okButton(Event event) throws Exception {
		// create PasswordEncoderImpl
		PasswordEncoderImpl passwordEncoder = new PasswordEncoderImpl();
		
		// encode the password
		String encodedPassword = passwordEncoder.encode(passwordTextbox.getValue());

		// send event
		Events.sendEvent(Events.ON_OK, passwordDialogWin, encodedPassword);

		// close / detach
		passwordDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		passwordDialogWin.detach();
	}
}
