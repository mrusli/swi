package com.pyramix.swi.persistence.serial.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentType;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.serial.dao.DocumentSerialNumberDao;

public class DocumentSerialNumberHibernate extends DaoHibernate implements DocumentSerialNumberDao {

	@Override
	public DocumentSerialNumber findDocumentSerialNumberById(long id) throws Exception {
		
		return (DocumentSerialNumber) super.findById(DocumentSerialNumber.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentSerialNumber> findAllDocumentSerialNumber() throws Exception {
		
		return new ArrayList<DocumentSerialNumber>(super.findAll(DocumentSerialNumber.class));
	}

	@Override
	public long save(DocumentSerialNumber documentSerialNumber) throws Exception {
		
		return super.save(documentSerialNumber);
	}

	@Override
	public void update(DocumentSerialNumber documentSerialNumber) throws Exception {
		
		super.update(documentSerialNumber);
	}

	@Override
	public DocumentSerialNumber findLastDocumentSerialNumberByDocumentType(DocumentType documentType) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(DocumentSerialNumber.class);
		criteria.add(Restrictions.eq("documentType", documentType));
		criteria.addOrder(Order.desc("id"));
		
		try {
			
			if (criteria.list().isEmpty()) {
				return null;
			} else {
				
				return (DocumentSerialNumber) criteria.list().get(0);
				
			}
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
}
