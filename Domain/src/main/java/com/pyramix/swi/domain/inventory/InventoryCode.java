package com.pyramix.swi.domain.inventory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Indexed
@Table(name = "inventory_code", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryCode extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 215568877003494202L;

	@Fields({
		@Field(termVector = TermVector.YES),
		@Field(name = "sortProductCode", analyze = Analyze.YES, store = Store.NO, index = Index.YES)
	})
	// @SortableField(forField = "sortProductCode")
	@Column(name = "product_code")
	private String productCode;

	@ManyToOne
	@JoinTable(
			name = "inventory_type_join_inventory_code",
			joinColumns = @JoinColumn(name = "id_code"),
			inverseJoinColumns = @JoinColumn(name = "id_type"))
	private InventoryType inventoryType;
	
	@Override
	public String toString() {
		return "InventoryCode[id="+getId()+", productCode="+getProductCode()+"]";
	}
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public InventoryType getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}
}
