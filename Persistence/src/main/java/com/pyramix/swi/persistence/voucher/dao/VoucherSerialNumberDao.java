package com.pyramix.swi.persistence.voucher.dao;

import java.util.List;

import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherType;

public interface VoucherSerialNumberDao {

	public VoucherSerialNumber findVoucherSerialNumberById(long id) throws Exception;
	
	public List<VoucherSerialNumber> findAllVoucherSerialNumber() throws Exception;
	
	public long save(VoucherSerialNumber voucherSerialNumber) throws Exception;
	
	public void update(VoucherSerialNumber voucherSerialNumber) throws Exception;
	
	public VoucherSerialNumber findLastVoucherSerialNumberByVoucherType(VoucherType voucherType) throws Exception;
	
}
