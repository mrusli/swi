package com.pyramix.swi.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name="auth_user_role", schema=SchemaUtil.SCHEMA_COMMON)
public class UserRole extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2614385762417224726L;

	@Column(name="user_role")
	private String role_name;
	
	@Transient
	private boolean enabled;

	@Override
	public String toString() {
		return "UserRole[id="+getId()+
				", role_name="+getRole_name()+
				"]";
	}
	
	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((role_name == null) ? 0 : role_name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserRole other = (UserRole) obj;
		if (role_name == null) {
			if (other.role_name != null)
				return false;
		} else if (!role_name.equals(other.role_name))
			return false;
		return true;
	}
	
	
	
}
