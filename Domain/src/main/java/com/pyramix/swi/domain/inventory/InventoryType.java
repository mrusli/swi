package com.pyramix.swi.domain.inventory;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.IndexedEmbedded;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;

@Entity
@Table(name = "inventory_type", schema = SchemaUtil.SCHEMA_COMMON)
public class InventoryType extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4981938043016550394L;

	@Column(name = "product_density")
	private BigDecimal density;
	
	@Column(name = "product_type")
	private String productType;
	
	@Column(name = "product_type_description")
	private String productDescription;

    @OneToMany(cascade={ CascadeType.ALL }, orphanRemoval=true, fetch=FetchType.EAGER)
    @JoinTable(
            name="inventory_type_join_inventory_code",
            joinColumns = @JoinColumn(name="id_type"),
            inverseJoinColumns = @JoinColumn(name ="id_code"))    	
    @IndexedEmbedded
	private List<InventoryCode> inventoryCodes;
	
    @Override
	public String toString() {
    	return "InventoryType[id="+getId()+
    			", productType="+getProductType()+
    			", density="+getDensity()+
    			"]";
    }
    
	public BigDecimal getDensity() {
		return density;
	}

	public void setDensity(BigDecimal density) {
		this.density = density;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public List<InventoryCode> getInventoryCodes() {
		return inventoryCodes;
	}

	public void setInventoryCodes(List<InventoryCode> inventoryCodes) {
		this.inventoryCodes = inventoryCodes;
	}

}
