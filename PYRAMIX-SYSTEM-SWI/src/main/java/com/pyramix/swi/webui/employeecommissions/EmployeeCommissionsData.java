package com.pyramix.swi.webui.employeecommissions;

import com.pyramix.swi.domain.organization.EmployeeCommissions;
import com.pyramix.swi.webui.common.PageMode;

public class EmployeeCommissionsData {

	private EmployeeCommissions employeeCommissions;
	
	private PageMode pageMode;

	public EmployeeCommissions getEmployeeCommissions() {
		return employeeCommissions;
	}

	public void setEmployeeCommissions(EmployeeCommissions employeeCommissions) {
		this.employeeCommissions = employeeCommissions;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
