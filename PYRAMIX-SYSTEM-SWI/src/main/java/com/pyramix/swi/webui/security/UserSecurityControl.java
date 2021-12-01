package com.pyramix.swi.webui.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.domain.user.UserRole;
import com.pyramix.swi.persistence.user.dao.UserDao;

import com.pyramix.swi.webui.common.GFCBaseController;

public class UserSecurityControl extends GFCBaseController implements UserDetailsService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5194661139101138975L;

	private UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserSecurityDetails userDetails = getNewUserDetails(username);
		
		return userDetails;
	}

	private UserSecurityDetails getNewUserDetails(String userName) {
		// equiv to '123'
		// User dbUser = new User("james", "$2a$10$ueHJqjoiHpmyWoBhex.GouF1/HqqMzMqAHJsGcEMv1DNSj6bJNSMG");
		
		User dbUser = //getUserDao().findUserByUsername(userName);
				getUserDao().findUserByUsername(userName);
		
		UserSecurityDetails userSecurityDetails =
				// new UserSecurityDetails(dbUser.getUser_name(), dbUser.getUser_password(), getGrantedAuthorities(dbUser), dbUser);
				new UserSecurityDetails(dbUser.getUser_name(), dbUser.getUser_password(), getGrantedAuthorities(dbUser), dbUser);
		
		// check whether this user is enabled / disabled -- need a better way to pass a message
		// if (dbUser.isEnabled()) {
		//	return userSecurityDetails;			
		// } else {
		//	return null;
		// }
	
		return userSecurityDetails;
		
	}

	private Collection<GrantedAuthority> getGrantedAuthorities(User dbUser) {
		
		List<GrantedAuthority> rolesGrantedAuthorityList = new ArrayList<GrantedAuthority>();
		
		for (UserRole userRole : dbUser.getUser_roles()) {
			rolesGrantedAuthorityList.add(new SimpleGrantedAuthority(userRole.getRole_name()));			
		}
		
		return rolesGrantedAuthorityList;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	
}
