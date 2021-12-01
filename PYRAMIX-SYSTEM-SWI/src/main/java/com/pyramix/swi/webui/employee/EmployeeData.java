package com.pyramix.swi.webui.employee;

import com.pyramix.swi.domain.organization.Employee;
import com.pyramix.swi.webui.common.PageMode;

public class EmployeeData {

	private Employee employee;
	
	private PageMode pageMode;

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}
	
}
