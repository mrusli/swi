package com.pyramix.swi.persistence.company.dao;

import java.util.List;

import com.pyramix.swi.domain.organization.Company;

public interface CompanyDao {

	public Company findCompanyById(Long id) throws Exception;
	
	public List<Company> findAllCompany() throws Exception;
	
	public Long save(Company company) throws Exception;
	
	public void update(Company company) throws Exception;
	
}
