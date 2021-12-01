package com.pyramix.swi.persistence.serial.dao;

import java.util.List;

import com.pyramix.swi.domain.serial.DocumentSerialNumber;
import com.pyramix.swi.domain.serial.DocumentType;

public interface DocumentSerialNumberDao {

	public DocumentSerialNumber findDocumentSerialNumberById(long id) throws Exception;
	
	public List<DocumentSerialNumber> findAllDocumentSerialNumber() throws Exception;
	
	public long save(DocumentSerialNumber documentSerialNumber) throws Exception;
	
	public void update(DocumentSerialNumber documentSerialNumber) throws Exception;
	
	public DocumentSerialNumber findLastDocumentSerialNumberByDocumentType(DocumentType documentType) throws Exception;
}
