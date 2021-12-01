package com.pyramix.swi.webui.report;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.settlement.Settlement;
import com.pyramix.swi.domain.voucher.VoucherGiroReceipt;
import com.pyramix.swi.webui.common.GFCBaseController;

public class ReportSalesVoucherGiroReceiptDialogControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139135675723231517L;
	
	private Window reportSalesVoucherGiroReceiptDialogWin;
	
	private SettlementData settlementData;
	private Settlement settlement;
	private VoucherGiroReceipt voucherGiroReceipt;
	
	private final Logger log = Logger.getLogger(ReportSalesVoucherGiroReceiptDialogControl.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// settlementData
		setSettlementData((SettlementData) arg.get("settlementData"));
	}

	public void onCreate$reportSalesVoucherGiroReceiptDialogWin(Event event) throws Exception {
		log.info("creating ReportSalesVoucherGiroReceiptDialog");
		
		// settlement
		setSettlement(getSettlementData().getSettlement());
		// voucherGiroReceipt
		setVoucherGiroReceipt(getSettlementData().getVoucherGiroReceipt());
		
		
	}
	
	public void onClick$closeButton(Event event) throws Exception {
		reportSalesVoucherGiroReceiptDialogWin.detach();
	}

	public SettlementData getSettlementData() {
		return settlementData;
	}

	public void setSettlementData(SettlementData settlementData) {
		this.settlementData = settlementData;
	}

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public VoucherGiroReceipt getVoucherGiroReceipt() {
		return voucherGiroReceipt;
	}

	public void setVoucherGiroReceipt(VoucherGiroReceipt voucherGiroReceipt) {
		this.voucherGiroReceipt = voucherGiroReceipt;
	}
}
