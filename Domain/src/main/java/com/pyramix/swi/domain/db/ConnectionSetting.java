package com.pyramix.swi.domain.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "connection_setting", schema = SchemaUtil.SCHEMA_COMMON)
public class ConnectionSetting extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3325868349679230190L;

	@Column(name = "url")
	private String url;
	
	@Column(name = "driverclass")
	private String driverClass;
	
	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;
	
	@Column(name = "dialect")
	private String dialect;
	
	@Column(name = "poolsize")
	private String poolSize;
	
	@Column(name = "showsql")
	private String showSql;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public String getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(String poolSize) {
		this.poolSize = poolSize;
	}

	public String getShowSql() {
		return showSql;
	}

	public void setShowSql(String showSql) {
		this.showSql = showSql;
	}
	
	
}
