package com.pyramix.swi.webui.voucher;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.voucher.VoucherSales;
import com.pyramix.swi.webui.common.GFCBaseController;

public class VoucherSalesBatalDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7354889019333452432L;

	private Window voucherSalesBatalDialogWin;
	private Tabbox documentBatalTabbox;

	private Tabs tabs = new Tabs();
	private Tabpanels tabpanels = new Tabpanels();
	private Tab documentTab;
	private Tabpanel documentTabPanel;
	
	private VoucherSales voucherSales;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		setVoucherSales(
				(VoucherSales) arg.get("voucherSales"));
	}
	
	public void onCreate$voucherSalesBatalDialogWin(Event event) throws Exception {
		voucherSalesBatalDialogWin.setTitle("Pembatalan Voucher - Sales");
		
		// set tabs and tabpanels as a child to documentBatalTabbox
		tabs.setParent(documentBatalTabbox);
		tabpanels.setParent(documentBatalTabbox);
		
		// create Voucher-Sales tab and panel
		createVoucherSalesTab();
		
	}

	private void createVoucherSalesTab() {
		documentTab = new Tab();
		documentTab.setLabel("Voucher-Sales");
		documentTab.setParent(tabs);
		
		documentTabPanel = new Tabpanel();
		documentTabPanel.setParent(tabpanels);
		
		Label voucherSalesLabel = new Label();
		voucherSalesLabel.setValue(getVoucherSales().toString());
		voucherSalesLabel.setParent(documentTabPanel);
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		voucherSalesBatalDialogWin.detach();
	}

	public VoucherSales getVoucherSales() {
		return voucherSales;
	}

	public void setVoucherSales(VoucherSales voucherSales) {
		this.voucherSales = voucherSales;
	}
	
}
