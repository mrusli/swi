package com.pyramix.swi.persistence.user.dao;

import java.util.List;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.user.User;

public interface UserDao {

	public User findUserById(Long id);
	
	public User findUserByUsername(String username);

	/**
	 * All users authorized to use the system
	 * 
	 * @return list of User (table: auth_user)
	 */
	public List<User> findAllUsers();

	/**
	 * Save the new user, including the password, to the database
	 * 
	 * @param user 
	 * @return the new user id
	 */
	public Long save(User user) throws Exception;

	/**
	 * Update the existing user, including the password and roles
	 * 
	 * @param user
	 */
	public void update(User user) throws Exception;

	/**
	 * Find the users by company
	 * 
	 * @param Company defaultCompany
	 * @return List<User>
	 */
	public List<User> findAllUsersByCompany(Company defaultCompany) throws Exception;

	/**
	 * Find user's company by proxy
	 * 
	 * @param id
	 * @return User
	 * @throws Exception
	 */
	public User findUserCompanyByProxy(long id) throws Exception;

	
}
