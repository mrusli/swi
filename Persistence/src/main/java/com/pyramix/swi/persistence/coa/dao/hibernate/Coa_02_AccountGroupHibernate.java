package com.pyramix.swi.persistence.coa.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.coa.Coa_01_AccountType;
import com.pyramix.swi.domain.coa.Coa_02_AccountGroup;
import com.pyramix.swi.persistence.coa.dao.Coa_02_AccountGroupDao;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;

public class Coa_02_AccountGroupHibernate extends DaoHibernate implements Coa_02_AccountGroupDao {

	@Override
	public Coa_02_AccountGroup findCoa_02_AccountGroupById(long id) throws Exception {

		return (Coa_02_AccountGroup) super.findById(Coa_02_AccountGroup.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_02_AccountGroup> findAllCoa_02_AccountGroup() throws Exception {
		
		return new ArrayList<Coa_02_AccountGroup>(super.findAll(Coa_02_AccountGroup.class));
	}

	@Override
	public long save(Coa_02_AccountGroup accountGroup) throws Exception {
		
		return super.save(accountGroup);
	}

	@Override
	public void update(Coa_02_AccountGroup accountGroup) throws Exception {

		super.update(accountGroup);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_02_AccountGroup> findCoa_02_AccountGroupByAccountType(Coa_01_AccountType selAccountType)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_02_AccountGroup.class);
		criteria.add(Restrictions.eq("accountType", selAccountType));
		criteria.addOrder(Order.asc("accountGroupNumber"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
		
			return new ArrayList<Coa_02_AccountGroup>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_02_AccountGroup> findCoa_02_AccountGroup_By_AccountType(int selAccountType) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_02_AccountGroup.class);
		if (selAccountType>0) {
			criteria.add(Restrictions.eq("typeCoaNumber", selAccountType));
		}
		criteria.addOrder(Order.asc("typeCoaNumber"));
		criteria.addOrder(Order.asc("accountGroupNumber"));
		
		try {
			
			return new ArrayList<Coa_02_AccountGroup>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}

	}

	@Override
	public Coa_02_AccountGroup findCoa_01_AccountType_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(Coa_02_AccountGroup.class);
		Coa_02_AccountGroup accGroup = (Coa_02_AccountGroup) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(accGroup.getAccountType());
			
			return accGroup;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
