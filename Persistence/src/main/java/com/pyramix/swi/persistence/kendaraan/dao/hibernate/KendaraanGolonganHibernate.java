package com.pyramix.swi.persistence.kendaraan.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import com.pyramix.swi.domain.kendaraan.KendaraanGolongan;
import com.pyramix.swi.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.swi.persistence.kendaraan.dao.KendaraanGolonganDao;

public class KendaraanGolonganHibernate extends DaoHibernate implements KendaraanGolonganDao {

	@Override
	public KendaraanGolongan findKendaraanGolonganById(long id) throws Exception {
		
		return (KendaraanGolongan) super.findById(KendaraanGolongan.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KendaraanGolongan> findAllKendaraanGolongan() throws Exception {
		
		return new ArrayList<KendaraanGolongan>(super.findAll(KendaraanGolongan.class));
	}

	@Override
	public Long save(KendaraanGolongan kendaraanGolongan) throws Exception {
		
		return super.save(kendaraanGolongan);
	}

	@Override
	public void update(KendaraanGolongan kendaraanGolongan) throws Exception {
		
		super.update(kendaraanGolongan);
	}

	@Override
	public void delete(KendaraanGolongan golongan) throws Exception {
		
		super.delete(golongan);
	}

}
