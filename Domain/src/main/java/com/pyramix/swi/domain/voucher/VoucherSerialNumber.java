package com.pyramix.swi.domain.voucher;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "voucher_serial_number", schema = SchemaUtil.SCHEMA_COMMON)
public class VoucherSerialNumber extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3019527216200526134L;

	//	`voucher_type` int(11)
	@Column(name = "voucher_type")
	@Enumerated(EnumType.ORDINAL)
	private VoucherType voucherType;
	
	//  `serial_date` date DEFAULT NULL,
	@Column(name = "serial_date")
	@Temporal(TemporalType.DATE)
	private Date serialDate;
	
	//  `serial_no` int(11) NOT NULL,
	@Column(name = "serial_no")
	private int serialNo;
	
	// serial_comp varchar(255)
	@Column(name = "serial_comp")
	private String serialComp;

	@Override
	public String toString() {
		return "VoucherSerialNumber[id="+getId()+
				", VoucherType="+getVoucherType().toString()+
				", Date="+getSerialDate()+
				", SerialNo="+getSerialNo()+"]";
	}
	
	public Date getSerialDate() {
		return serialDate;
	}

	public void setSerialDate(Date serialDate) {
		this.serialDate = serialDate;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public String getSerialComp() {
		return serialComp;
	}

	public void setSerialComp(String serialComp) {
		this.serialComp = serialComp;
	}
}
