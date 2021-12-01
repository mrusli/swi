package com.pyramix.swi.webui.faktur;

import com.pyramix.swi.domain.faktur.Faktur;
import com.pyramix.swi.domain.suratjalan.SuratJalan;
import com.pyramix.swi.domain.user.User;
import com.pyramix.swi.webui.common.PageMode;

public class FakturData {

	private Faktur faktur;
	
	private SuratJalan suratJalan;

	private PageMode pageMode;
	
	private User userCreate;
	
	public Faktur getFaktur() {
		return faktur;
	}

	public void setFaktur(Faktur faktur) {
		this.faktur = faktur;
	}

	public SuratJalan getSuratJalan() {
		return suratJalan;
	}

	public void setSuratJalan(SuratJalan suratJalan) {
		this.suratJalan = suratJalan;
	}

	public PageMode getPageMode() {
		return pageMode;
	}

	public void setPageMode(PageMode pageMode) {
		this.pageMode = pageMode;
	}

	/**
	 * @return the userCreate
	 */
	public User getUserCreate() {
		return userCreate;
	}

	/**
	 * @param userCreate the userCreate to set
	 */
	public void setUserCreate(User userCreate) {
		this.userCreate = userCreate;
	}
	
}
