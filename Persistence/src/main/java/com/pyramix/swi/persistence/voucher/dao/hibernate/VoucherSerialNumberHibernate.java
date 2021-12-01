package com.pyramix.swi.persistence.voucher.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.voucher.VoucherSerialNumber;
import com.pyramix.swi.domain.voucher.VoucherType;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.voucher.dao.VoucherSerialNumberDao;

public class VoucherSerialNumberHibernate extends DaoHibernate implements VoucherSerialNumberDao {

	@Override
	public VoucherSerialNumber findVoucherSerialNumberById(long id) throws Exception {
		
		return (VoucherSerialNumber) super.findById(VoucherSerialNumber.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherSerialNumber> findAllVoucherSerialNumber() throws Exception {
		
		return new ArrayList<VoucherSerialNumber>(super.findAll(VoucherSerialNumber.class));
	}

	@Override
	public long save(VoucherSerialNumber voucherSerialNumber) throws Exception {
		
		return super.save(voucherSerialNumber);
	}

	@Override
	public void update(VoucherSerialNumber voucherSerialNumber) throws Exception {
		
		super.update(voucherSerialNumber);
	}

	@Override
	public VoucherSerialNumber findLastVoucherSerialNumberByVoucherType(VoucherType voucherType) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(VoucherSerialNumber.class);
		criteria.add(Restrictions.eq("voucherType", voucherType));
		criteria.addOrder(Order.desc("id"));
		
		try {
			
			if (criteria.list().isEmpty()) {
				return null;
			} else {
				
				return (VoucherSerialNumber) criteria.list().get(0);
				
			}
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

}
