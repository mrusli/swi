package com.pyramix.swi.persistence.userrole.dao;

import java.util.List;

import com.pyramix.swi.domain.user.UserRole;

public interface UserRoleDao {

	/**
	 * Find a user role by passing an id
	 * 
	 * @param id - type long
	 * @return UserRole
	 * @throws Exception
	 */
	public UserRole findUserRoleById(long id) throws Exception;
	
	/**
	 * All user roles authorized / given to the user to use the system
	 * 
	 * @return List<UserRole>
	 * @throws Exception
	 */
	public List<UserRole> findAllUserRole() throws Exception;
	
	/**
	 * Save the new user role to the database
	 * 
	 * @param userRole
	 * @return long
	 * @throws Exception
	 */
	public Long save(UserRole userRole) throws Exception;
	
	/**
	 * Update the existing user role to the database
	 * 
	 * @param UserRole userRole
	 * @throws Exception
	 */
	public void update(UserRole userRole) throws Exception;
	
	/**
	 * Delete the existing user role from the database
	 * 
	 * @param UserRole userRole
	 * @throws Exception
	 */
	public void delete(UserRole userRole) throws Exception;
	
}
