package com.pyramix.swi.webui.giro;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.voucher.Giro;
import com.pyramix.swi.persistence.voucher.dao.GiroDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class GiroDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4230030561640352103L;

	private GiroDao giroDao;
	
	private Window giroDialogWin;
	private Textbox giroNoTextbox, giroBankTextbox, customerTextbox;
	private Datebox giroDateDatebox, giroReceivedDatebox;
	private Decimalbox giroAmountDecimalbox;
	
	private Giro giro;
	
	private final Logger log = Logger.getLogger(GiroDialogControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	
		// giro 
		setGiro((Giro) arg.get("giro"));
	}

	public void onCreate$giroDialogWin(Event event) throws Exception {
		log.info("create giroDialogWin");
		log.info(getGiro().toString());
		
		giroDialogWin.setTitle("Edit Informasi Giro");

		giroDateDatebox.setLocale(getLocale());
		giroDateDatebox.setFormat(getLongDateFormat());
		giroReceivedDatebox.setLocale(getLocale());
		giroReceivedDatebox.setFormat(getLongDateFormat());
		
		giroAmountDecimalbox.setLocale(getLocale());
		
		loadGiroInfo();
		
	}
	
	private void loadGiroInfo() throws Exception {
		giroNoTextbox.setValue(getGiro().getGiroNumber());
		giroBankTextbox.setValue(getGiro().getGiroBank());
		giroDateDatebox.setValue(getGiro().getGiroDate());
		giroReceivedDatebox.setValue(getGiro().getGiroReceivedDate());
		giroAmountDecimalbox.setValue(getGiro().getGiroAmount());
		
		Giro giroCustomerByProxy = getGiroDao().findCustomerByProxy(getGiro().getId());
		
		customerTextbox.setValue(giroCustomerByProxy.getCustomer().getCompanyType()+"."+
				giroCustomerByProxy.getCustomer().getCompanyLegalName());
	}

	public void onClick$saveButton(Event event) throws Exception {
		Events.sendEvent(Events.ON_OK, giroDialogWin, getModifiedGiroInfo());
		
		giroDialogWin.detach();
	}
	
	private Giro getModifiedGiroInfo() {
		Giro modGiro = getGiro();
		
		modGiro.setGiroNumber(giroNoTextbox.getValue());
		modGiro.setGiroBank(giroBankTextbox.getValue());
		modGiro.setGiroDate(giroDateDatebox.getValue());
		modGiro.setGiroReceivedDate(giroReceivedDatebox.getValue());
		modGiro.setGiroAmount(giroAmountDecimalbox.getValue());
		
		return modGiro;
	}

	public void onClick$cancelButton(Event event) throws Exception {
		giroDialogWin.detach();
	}

	public Giro getGiro() {
		return giro;
	}

	public void setGiro(Giro giro) {
		this.giro = giro;
	}

	public GiroDao getGiroDao() {
		return giroDao;
	}

	public void setGiroDao(GiroDao giroDao) {
		this.giroDao = giroDao;
	}
}
