package com.pyramix.swi.persistence.userrole.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import com.pyramix.swi.domain.user.UserRole;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.userrole.dao.UserRoleDao;

public class UserRoleHibernate extends DaoHibernate implements UserRoleDao {

	@Override
	public UserRole findUserRoleById(long id) throws Exception {

		return (UserRole) super.findById(UserRole.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findAllUserRole() throws Exception {
		
		return new ArrayList<UserRole>(super.findAll(UserRole.class));
	}

	@Override
	public Long save(UserRole userRole) throws Exception {
		
		return super.save(userRole);
	}

	@Override
	public void update(UserRole userRole) throws Exception {
		
		super.update(userRole);
	}

	@Override
	public void delete(UserRole userRole) throws Exception {
		
		super.delete(userRole);
	}

}
