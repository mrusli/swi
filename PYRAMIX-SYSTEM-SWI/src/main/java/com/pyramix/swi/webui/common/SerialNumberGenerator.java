package com.pyramix.swi.webui.common;

import java.util.Date;

import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.persistence.serial.dao.DocumentSerialNumberDao;
import com.pyramix.swi.persistence.voucher.dao.VoucherSerialNumberDao;

public class SerialNumberGenerator extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5203520684708567842L;
	
	private DocumentSerialNumberDao documentSerialNumberDao;
	private VoucherSerialNumberDao voucherSerialNumberDao;
	
	public int getSerialNumber(DocumentType documentType, Date currentDate) throws Exception {
		int serialNum = 1;
		
		DocumentSerialNumber documentSerNum =
				getDocumentSerialNumberDao().findLastDocumentSerialNumberByDocumentType(documentType);

		if (documentSerNum!=null) {
			Date lastDate = documentSerNum.getSerialDate();

			// compare year
			int lastYearValue = getLocalDateYearValue(asLocalDate(lastDate));
			int currYearValue = getLocalDateYearValue(asLocalDate(currentDate));
			// compare month
			int lastMonthValue = getLocalDateMonthValue(asLocalDate(lastDate));
			int currMonthValue = getLocalDateMonthValue(asLocalDate(currentDate));

			if (lastYearValue==currYearValue) {

				if (lastMonthValue==currMonthValue) {

					serialNum = documentSerNum.getSerialNo()+1;
					
				}
			}
		}
		
		
		return serialNum;
	}
	
	public int getSerialNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = 1;
		
		VoucherSerialNumber voucherSerNum = 
			getVoucherSerialNumberDao().findLastVoucherSerialNumberByVoucherType(voucherType);
		
		if (voucherSerNum!=null) {
			Date lastDate = voucherSerNum.getSerialDate();
			
			// compare year
			int lastYearValue = getLocalDateYearValue(asLocalDate(lastDate));
			int currYearValue = getLocalDateYearValue(asLocalDate(currentDate));
			// compare month
			int lastMonthValue = getLocalDateMonthValue(asLocalDate(lastDate));
			int currMonthValue = getLocalDateMonthValue(asLocalDate(currentDate));
			
			if (lastYearValue==currYearValue) {
				
				if (lastMonthValue==currMonthValue) {
					
					serialNum = voucherSerNum.getSerialNo()+1;
					
				}
			}
		}
		
		return serialNum;
	}
	
	public DocumentSerialNumberDao getDocumentSerialNumberDao() {
		return documentSerialNumberDao;
	}

	public void setDocumentSerialNumberDao(DocumentSerialNumberDao documentSerialNumberDao) {
		this.documentSerialNumberDao = documentSerialNumberDao;
	}

	public VoucherSerialNumberDao getVoucherSerialNumberDao() {
		return voucherSerialNumberDao;
	}

	public void setVoucherSerialNumberDao(VoucherSerialNumberDao voucherSerialNumberDao) {
		this.voucherSerialNumberDao = voucherSerialNumberDao;
	}
	
}
