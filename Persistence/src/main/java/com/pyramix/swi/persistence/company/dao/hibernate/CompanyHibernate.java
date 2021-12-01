package com.pyramix.swi.persistence.company.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.company.dao.CompanyDao;

public class CompanyHibernate extends DaoHibernate implements CompanyDao {

	@Override
	public Long save(Company company) throws Exception {
		
		return super.save(company);
	}

	@Override
	public void update(Company company) throws Exception {

		super.update(company);
	}

	@Override
	public Company findCompanyById(Long id) throws Exception {

		return (Company) super.findById(Company.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> findAllCompany() throws Exception {
		
		return new ArrayList<Company>(super.findAll(Company.class));
	}
	
	

}
