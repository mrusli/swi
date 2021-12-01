package com.pyramix.swi.domain.user;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.domain.organization.Employee;

@Entity
@Table(name = "auth_user", schema = SchemaUtil.SCHEMA_COMMON)
public class User extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 142528588323313942L;

	//  `user_name` varchar(255) DEFAULT NULL,
	@Column(name="user_name")
	private String user_name;
	
	//  `user_password` varchar(255) DEFAULT NULL,
	@Column(name="user_password")	
	private String user_password;
	
	//  `real_name` varchar(255) DEFAULT NULL,
	@Column(name="real_name")
	private String real_name;
	
	//  `email` varchar(255) DEFAULT NULL,
	@Column(name="email")
	private String email;
	
	//  `enabled` char(1) DEFAULT NULL,
	@Column(name="enabled")
	@Type(type="true_false")	
	private boolean enabled;
	
	//  `employee_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_employee_id_02` (`employee_id_fk`),
	//  CONSTRAINT `fk_employee_02` FOREIGN KEY (`employee_id_fk`) REFERENCES `employee` (`id`)
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "employee_id_fk")
	private Employee employee;
	
	//  `organization_id_fk` bigint(20) DEFAULT NULL,
	//  KEY `key_organization_id_03` (`organization_id_fk`),
	//  CONSTRAINT `fk_organization_03` FOREIGN KEY (`organization_id_fk`) REFERENCES `organization` (`id`)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id_fk")
	private Company company;
	
	//  `create_date` datetime DEFAULT NULL,
	@Column(name="create_date")
	@Temporal(TemporalType.DATE)	
	private Date createDate;
	
	//  `last_edit_date` datetime DEFAULT NULL,
	@Column(name="last_edit_date")
	@Temporal(TemporalType.DATE)			
	private Date lastEditDate;
	
	// IMPORTANT: set orphanRemoval to false, otherwise when this user removes the role,
	// the role in the UserRole table will be removed as well !!!
	@OneToMany(cascade={ CascadeType.ALL }, orphanRemoval=false, fetch=FetchType.EAGER)
	@JoinTable(
			name="auth_user_join_role",
			joinColumns = @JoinColumn(name="id_user"),
			inverseJoinColumns = @JoinColumn(name="id_role"))	
	private Set<UserRole> user_roles;

	@Override
	public String toString() {
		return "id: "+getId()+", user_name: "+getUser_name();
	}
	
	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_password() {
		return user_password;
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastEditDate() {
		return lastEditDate;
	}

	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	public Set<UserRole> getUser_roles() {
		return user_roles;
	}

	public void setUser_roles(Set<UserRole> user_roles) {
		this.user_roles = user_roles;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
