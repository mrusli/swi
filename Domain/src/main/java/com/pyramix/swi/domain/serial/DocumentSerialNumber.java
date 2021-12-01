package com.pyramix.swi.domain.serial;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "document_serial_number", schema = SchemaUtil.SCHEMA_COMMON)
public class DocumentSerialNumber extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6322404986669209154L;
	
	// document_type int(11)
	@Column(name = "document_type")
	private DocumentType documentType;
	
	// serial_date date
	@Column(name = "serial_date")
	@Temporal(TemporalType.DATE)
	private Date serialDate;
	
	// serial_no int(11)
	@Column(name = "serial_no")
	private int serialNo;

	// serial_comp varchar(255)
	@Column(name = "serial_comp")
	private String serialComp;
	
	@Override
	public String toString() {
		return "DocumentSerialNumber[id="+getId()+
				", DocumentType="+getDocumentType().toString()+
				", SerialDate="+getSerialDate()+
				", SerialNo="+getSerialNo()+"]";
	}
	
	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Date getSerialDate() {
		return serialDate;
	}

	public void setSerialDate(Date serialDate) {
		this.serialDate = serialDate;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	public String getSerialComp() {
		return serialComp;
	}

	public void setSerialComp(String serialComp) {
		this.serialComp = serialComp;
	}

}
