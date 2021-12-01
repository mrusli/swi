package com.pyramix.swi.webui.company;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.swi.domain.organization.Company;
import com.pyramix.swi.persistence.company.dao.CompanyDao;
import com.pyramix.swi.webui.common.GFCBaseController;

public class CompanyListInfoControl extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3873537929010502719L;

	private CompanyDao companyDao;
	
	private Label formTitleLabel, infoCompanylabel;
	private Listbox companyListbox;
	
	private List<Company> companyList;
	
	public void onCreate$companyListInfoWin(Event event) throws Exception {
		formTitleLabel.setValue("Perusahaan");
		
		// load
		loadCompanyList();
		
		// list
		listCompanyListInfo();
	}

	private void loadCompanyList() throws Exception {
		setCompanyList(
				getCompanyDao().findAllCompany());
	}

	private void listCompanyListInfo() {
		companyListbox.setModel(
				new ListModelList<Company>(getCompanyList()));
		companyListbox.setItemRenderer(
				getCompanyListitemRenderer());
	}

	private ListitemRenderer<Company> getCompanyListitemRenderer() {

		return new ListitemRenderer<Company>() {
			
			@Override
			public void render(Listitem item, Company comp, int index) throws Exception {
				Listcell lc;
				
				item.setHeight("85px");
				
				// Perusahaan
				lc = new Listcell(comp.getCompanyType().toString()+"."+
						comp.getCompanyLegalName()+" ("+
						comp.getCompanyDisplayName()+")");
				lc.setParent(item);
								
				// Alamat
				lc = new Listcell(
						comp.getAddress01()+" "+
						(comp.getAddress02()==null ?
							" " : comp.getAddress02()));
				lc.setParent(item);
								
				// Kota
				lc = new Listcell(comp.getCity());
				lc.setParent(item);
				
				// Kode-Pos
				lc = new Listcell(comp.getPostalCode());
				lc.setParent(item);
				
				// Telpon
				lc = new Listcell(comp.getPhone());
				lc.setParent(item);
				
				// Email
				lc = new Listcell(comp.getEmail());
				lc.setParent(item);
				
				// Edit
				lc = initEdit(new Listcell(), comp);
				lc.setParent(item);
				
			}

			private Listcell initEdit(Listcell listcell, Company comp) {
				Button editButton = new Button();
				editButton.setLabel("...");
				editButton.setClass("inventoryEditButton");
				editButton.setParent(listcell);
				editButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						Map<String, Company> args = Collections.singletonMap("company", comp);
						
						Window editCompanyWin = (Window) Executions.createComponents(
								"/company/CompanyDialog.zul", null, args);
						
						editCompanyWin.addEventListener(Events.ON_OK, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Company company = (Company) event.getData();
								
								// update
								getCompanyDao().update(company);
								
								// load
								loadCompanyList();
								
								// list
								listCompanyListInfo();
							}
						});
						
						editCompanyWin.doModal();
					}
				});
				
				return listcell;
			}
		};
	}

	public void onAfterRender$companyListbox(Event event) throws Exception {
		infoCompanylabel.setValue("Total: "+companyListbox.getItemCount()+" Company");
	}
	
	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public List<Company> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;
	}
}
