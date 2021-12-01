package com.pyramix.swi.domain.sqlserver;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "taSWIInventory")
public class SqlInventory {

	//   * NoUrut	int 		4
	@Id
	private int noUrut;
	
	// 	 * Code		varchar 	2
	private String code;
	
	// 	 * CnrtNum	varchar 	20
	private String cnrtNum;
	
	//	 * CoilNum	varchar 	30
	private String coilNum;
	
	//	 * LCNum	varchar 	20
	private String lcNum;
	
	//	 * DateIn	datetime	8
	private Date dateIn;
	
	//	 * OrderNum	varchar		15
	private String orderNum;
	
	//	 * Quantity	float		8
	private float quantity;
	
	//	 * Shape	varchar		3
	private String shape;
	
	//	 * Type		varchar		25
	private String type;
	
	//	 * Thickness	float	8
	private float thickness;
	
	//	 * Width	float		8
	private float width;
	
	//	 * Length	float		8
	private float length;
	
	//	 * SheetCount	float	8		
	private float sheetCount;
	
	//	 * DoNum	float		8
	private String doNum;
	
	//	 * DoDate	datetime	8
	private Date doDate;
	
	//	 * MemorialNum	varchar	30
	private String memorialNum;
	
	//	 * MemorialDate	datetime	8
	private Date memorialDate;
	
	//	 * Location	varchar		5
	private String location;
	
	//	 * Status	varchar		15
	private String status;
	
	//	 * Note		varchar		200	
	private String note;
	
	public int getNoUrut() {
		return noUrut;
	}

	public void setNoUrut(int noUrut) {
		this.noUrut = noUrut;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCnrtNum() {
		return cnrtNum;
	}

	public void setCnrtNum(String cnrtNum) {
		this.cnrtNum = cnrtNum;
	}

	public String getCoilNum() {
		return coilNum;
	}

	public void setCoilNum(String coilNum) {
		this.coilNum = coilNum;
	}

	public String getLcNum() {
		return lcNum;
	}

	public void setLcNum(String lcNum) {
		this.lcNum = lcNum;
	}

	public Date getDateIn() {
		return dateIn;
	}

	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getThickness() {
		return thickness;
	}

	public void setThickness(float thickness) {
		this.thickness = thickness;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getSheetCount() {
		return sheetCount;
	}

	public void setSheetCount(float sheetCount) {
		this.sheetCount = sheetCount;
	}

	public String getDoNum() {
		return doNum;
	}

	public void setDoNum(String doNum) {
		this.doNum = doNum;
	}

	public Date getDoDate() {
		return doDate;
	}

	public void setDoDate(Date doDate) {
		this.doDate = doDate;
	}

	public String getMemorialNum() {
		return memorialNum;
	}

	public void setMemorialNum(String memorialNum) {
		this.memorialNum = memorialNum;
	}

	public Date getMemorialDate() {
		return memorialDate;
	}

	public void setMemorialDate(Date memorialDate) {
		this.memorialDate = memorialDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}
