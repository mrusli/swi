package com.pyramix.swi.persistence.settings.dao.hibernate;

import java.util.List;

import com.pyramix.swi.domain.settings.Settings;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.settings.dao.SettingsDao;

public class SettingsHibernate extends DaoHibernate implements SettingsDao {

	@Override
	public Settings findSettingsById(long id) throws Exception {

		return (Settings) super.findById(Settings.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Settings> findAllSettings() throws Exception {

		return super.findAll(Settings.class);
	}

	@Override
	public Long save(Settings settings) throws Exception {

		return super.save(settings);
	}

	@Override
	public void update(Settings settings) throws Exception {

		super.update(settings);
	}

}
