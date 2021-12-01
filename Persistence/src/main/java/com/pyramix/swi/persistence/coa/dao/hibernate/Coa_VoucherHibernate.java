package com.pyramix.swi.persistence.coa.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_Voucher;
import com.pyramix.swi.persistence.coa.dao.Coa_VoucherDao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class Coa_VoucherHibernate extends DaoHibernate implements Coa_VoucherDao {

	@Override
	public Coa_Voucher findCoaVoucherById(long id) throws Exception {
		
		return (Coa_Voucher) super.findById(Coa_Voucher.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_Voucher> findAllCoaVoucher() throws Exception {

		return new ArrayList<Coa_Voucher>(super.findAll(Coa_Voucher.class));
	}

	@Override
	public long save(Coa_Voucher coaVoucher) throws Exception {
		
		return super.save(coaVoucher);
	}

	@Override
	public void update(Coa_Voucher coaVoucher) throws Exception {

		super.update(coaVoucher);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_Voucher> findCoaVoucherByDebitAccount(boolean debitAccount)
			throws Exception {

		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_Voucher.class);
		// criteria.add(Restrictions.eq("voucherType", voucherType));
		criteria.add(Restrictions.eq("debitAccount", debitAccount));
		criteria.add(Restrictions.eq("active", true));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<Coa_Voucher>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

}
