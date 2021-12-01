package com.pyramix.swi.webui.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserSecurityDetails extends User {

	private static final long serialVersionUID = 5015525262711088345L;

	private com.pyramix.swi.domain.user.User loginUser;
	
	public UserSecurityDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}	
	
	public UserSecurityDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, 
			com.pyramix.swi.domain.user.User dbUser) {
		
		super(username, password, authorities);
		
		setLoginUser(dbUser);
	}

	public com.pyramix.swi.domain.user.User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(com.pyramix.swi.domain.user.User dbUser) {
		this.loginUser = dbUser;
	}		


}
