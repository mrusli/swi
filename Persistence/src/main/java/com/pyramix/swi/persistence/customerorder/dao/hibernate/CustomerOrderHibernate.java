package com.pyramix.swi.persistence.customerorder.dao.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.pyramix.swi.domain.customerorder.CustomerOrder;
import com.pyramix.swi.domain.customerorder.CustomerOrderProduct;
import com.pyramix.swi.domain.customerorder.PaymentType;
import com.pyramix.swi.domain.organization.Customer;
import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.domain.serial.DocumentStatus;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.customerorder.dao.CustomerOrderDao;

public class CustomerOrderHibernate extends DaoHibernate implements CustomerOrderDao {

	@Override
	public CustomerOrder findCustomerOrderById(Long id) throws Exception {
		
		return (CustomerOrder) super.findById(CustomerOrder.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder() throws Exception {
		
		return new ArrayList<CustomerOrder>(super.findAll(CustomerOrder.class));
	}

	@Override
	public Long save(CustomerOrder customerOrder) throws Exception {
		
		return super.save(customerOrder);
	}

	@Override
	public void update(CustomerOrder customerOrder) throws Exception {
		
		super.update(customerOrder);
	}

	@Override
	public CustomerOrder findCustomerByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		CustomerOrder customerOrder = (CustomerOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(customerOrder.getCustomer());
			
			return customerOrder;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
	
	
 	@Override
	public CustomerOrder findVoucherSalesByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		CustomerOrder customerOrder = (CustomerOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(customerOrder.getVoucherSales());
			
			return customerOrder;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findNonPostingCustomerOrder() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.isNull("voucherSales"));
		
		try {
			
			return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder_OrderBy_OrderDate(boolean desc, boolean checkTransaction, boolean usePpn) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		if (checkTransaction) {
			criteria.add(Restrictions.eq("usePpn", usePpn));
		}
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findCustomerOrder_By_OrderDate(Date startDate, Date endDate, boolean desc, boolean checkTransaction, boolean usePpn)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		if (checkTransaction) {
			criteria.add(Restrictions.eq("usePpn", usePpn));
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return criteria.list();
			// return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder_OrderBy_Customer(boolean desc) throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.createAlias("customer", "customerName");
		criteria.addOrder(desc ? Order.desc("customerName.companyLegalName") : Order.asc("customerName.companyLegalName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {
			
			return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder_By_PaymentComplete(boolean paymentComplete) {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.eq("paymentComplete", paymentComplete));
		criteria.createAlias("customer", "customerName");
		criteria.addOrder(Order.asc("customerName.companyLegalName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder_By_CustomerPaymentComplete(
			Customer customer,
			boolean paymentComplete) throws Exception {

		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.eq("paymentComplete", paymentComplete));
		criteria.add(Restrictions.isNotNull("voucherSales"));
		criteria.createAlias("customer", "customerId");
		criteria.add(Restrictions.eq("customerId.id", customer.getId()));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
	
	@Override
	public CustomerOrder findEmployeeCommissionsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		CustomerOrder customerOrder = (CustomerOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(customerOrder.getEmployeeCommissions());
			
			return customerOrder;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
		}
		
	}

	@Override
	public CustomerOrder findSuratJalanByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		CustomerOrder customerOrder = (CustomerOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(customerOrder.getSuratJalan());
			
			return customerOrder;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder_By_SuratJalanNotNull() throws Exception {
		Session session = super.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.isNotNull("suratJalan"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		try {

			return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder_By_DateRange(Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.add(Restrictions.isNotNull("suratJalan"));
		// 05/10/2021 - Mnt Setting nomor muda di paling atas
		// order by orderDate and serialComp
		criteria.addOrder(Order.asc("orderDate"));
		criteria.createAlias("documentSerialNumber", "documentSerialNumberComp");
		criteria.addOrder(Order.asc("documentSerialNumberComp.serialComp"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder_By_DateRange_By_EmployeeSales(Date startDate, Date endDate, Employee employeeSales) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.add(Restrictions.isNotNull("suratJalan"));
		criteria.add(Restrictions.eq("employeeSales", employeeSales));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			return new ArrayList<CustomerOrder>(criteria.list());
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
	
	@Override
	public CustomerOrder findCustomerReceivableByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		CustomerOrder customerOrder = (CustomerOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();

		try {
			Hibernate.initialize(customerOrder.getCustomerReceivable());
			
			return customerOrder;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@Override
	public CustomerOrderProduct findCustomerOrderProductInventoryByProxy(long id) {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrderProduct.class);
		CustomerOrderProduct customerOrderProduct = (CustomerOrderProduct) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(customerOrderProduct.getInventory());
			
			return customerOrderProduct;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
	
	@Override
	public BigDecimal sumTotalOrderPaymentTypeTunai(Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.add(Restrictions.eq("paymentType", PaymentType.tunai));
		criteria.add(Restrictions.eq("orderStatus", DocumentStatus.NORMAL));
		criteria.add(Restrictions.isNotNull("suratJalan"));
		criteria.setProjection(Projections.sum("totalOrder"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		if (criteria.list().get(0)==null) {
			return BigDecimal.ZERO;
		}
		
		return (BigDecimal)criteria.list().get(0);
	}
	
	@Override
	public BigDecimal sumTotalOrderPaymentTypeAll(Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.add(Restrictions.eq("orderStatus", DocumentStatus.NORMAL));
		criteria.add(Restrictions.isNotNull("suratJalan"));
		criteria.setProjection(Projections.sum("totalOrder"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			if (criteria.list().get(0)==null) {
				return BigDecimal.ZERO;
			}

			return (BigDecimal)criteria.list().get(0);		

		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}
	
	public BigDecimal sumTotalOrderPaymentTypeTunai_By_EmployeeSales(Date startDate, Date endDate, Employee employeeSales) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.add(Restrictions.eq("paymentType", PaymentType.tunai));
		criteria.add(Restrictions.eq("orderStatus", DocumentStatus.NORMAL));
		criteria.add(Restrictions.eq("employeeSales", employeeSales));
		criteria.add(Restrictions.isNotNull("suratJalan"));
		criteria.setProjection(Projections.sum("totalOrder"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			if (criteria.list().get(0)==null) {
				return BigDecimal.ZERO;
			}
			
			return (BigDecimal)criteria.list().get(0);

		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}
	
	
	
	public BigDecimal sumTotalOrderPaymentTypeAll_By_EmployeeSales(Date startDate, Date endDate, Employee employeeSales) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.add(Restrictions.eq("orderStatus", DocumentStatus.NORMAL));
		criteria.add(Restrictions.eq("employeeSales", employeeSales));
		criteria.add(Restrictions.isNotNull("suratJalan"));
		criteria.setProjection(Projections.sum("totalOrder"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {

			if (criteria.list().get(0)==null) {
				return BigDecimal.ZERO;
			}
			
			return (BigDecimal)criteria.list().get(0);		

		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}		
	}
	
	
	
	public CustomerOrder findEmployeeSalesByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		CustomerOrder customerOrder = (CustomerOrder) criteria.add(Restrictions.idEq(id)).uniqueResult();
		
		try {
			Hibernate.initialize(customerOrder.getEmployeeSales());
			
			return customerOrder;
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findSelectedCustomerOrder_By_Filter(boolean desc, boolean checkTransaction, boolean usePpn,
			Customer customer, Date startDate, Date endDate) throws Exception {

		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		if (checkTransaction) {
			criteria.add(Restrictions.eq("usePpn", usePpn));
		}
		criteria.add(Restrictions.eq("customer", customer));
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return criteria.list();
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerOrder> findAllCustomerOrder_By_Filter(boolean desc, boolean checkTransaction,
			boolean usePpn, Date startDate, Date endDate) {

		Session session = super.getSessionFactory().openSession();
		
		Criteria criteria = session.createCriteria(CustomerOrder.class);
		if (checkTransaction) {
			criteria.add(Restrictions.eq("usePpn", usePpn));
		}
		criteria.add(Restrictions.between("orderDate", startDate, endDate));
		criteria.addOrder(desc ? Order.desc("orderDate") : Order.asc("orderDate"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		try {
			
			return criteria.list();
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();
			
		}
	}
}
