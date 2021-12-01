package com.pyramix.swi.persistence.settings.dao;

import java.util.List;

import com.pyramix.swi.domain.settings.Settings;

public interface SettingsDao {

	/**
	 * Get the settings by ID
	 * 
	 * @param id
	 * @return Settings
	 * @throws Exception
	 */
	public Settings findSettingsById(long id) throws Exception;
	
	/**
	 * Get all the settings -- will be only one settings
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Settings> findAllSettings() throws Exception;
	
	/**
	 * Save a new settings
	 * 
	 * @param settings
	 * @return
	 * @throws Exception
	 */
	public Long save(Settings settings) throws Exception;
	
	/**
	 * Update an existing settings
	 * 
	 * @param settings
	 * @throws Exception
	 */
	public void update(Settings settings) throws Exception;
	
}
