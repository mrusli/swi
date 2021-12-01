package com.pyramix.swi.webui.customerorder;

import java.math.BigDecimal;

public class CustomerOrderProductData {

	private String coilNo;
	
	private String kode;
	
	private String spesifikasi;
	
	private BigDecimal qtyKg;
	
	private int qtySht;
	
	private boolean byKg;
	
	private BigDecimal harga;
	
	private BigDecimal subTotal;
	
	public String getCoilNo() {
		return coilNo;
	}

	public void setCoilNo(String coilNo) {
		this.coilNo = coilNo;
	}

	public String getKode() {
		return kode;
	}

	public void setKode(String kode) {
		this.kode = kode;
	}

	public String getSpesifikasi() {
		return spesifikasi;
	}

	public void setSpesifikasi(String spesifikasi) {
		this.spesifikasi = spesifikasi;
	}

	public BigDecimal getQtyKg() {
		return qtyKg;
	}

	public void setQtyKg(BigDecimal qtyKg) {
		this.qtyKg = qtyKg;
	}

	public int getQtySht() {
		return qtySht;
	}

	public void setQtySht(int qtySht) {
		this.qtySht = qtySht;
	}

	public BigDecimal getHarga() {
		return harga;
	}

	public void setHarga(BigDecimal harga) {
		this.harga = harga;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	/**
	 * @return the byKg
	 */
	public boolean isByKg() {
		return byKg;
	}

	/**
	 * @param byKg the byKg to set
	 */
	public void setByKg(boolean byKg) {
		this.byKg = byKg;
	}
	
}
