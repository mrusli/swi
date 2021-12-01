package com.pyramix.swi.domain.settlement;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.customerorder.CustomerOrder;

@Entity
@Table(name = "settlement_detail", schema = SchemaUtil.SCHEMA_COMMON)
public class SettlementDetail extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2436598642796430432L;

	// amount_to_settle decimal(19,2)
	@Column(name = "amount_to_settle")
	private BigDecimal amountToSettle;
	
	// amount_settled decimal(19,2)
	@Column(name = "amount_settled")
	private BigDecimal amountSettled;
	
	// remaining_amount_to_settle decimal(19,2)
	@Column(name = "remaining_amount_to_settle")
	private BigDecimal remainingAmountToSettle;
	
	// `customer_order_id_fk` bigint(20) DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.EAGER)
	@JoinColumn(name = "customer_order_id_fk")
	private CustomerOrder customerOrder;

	// settlement_join_detail
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "settlement_join_detail",
			joinColumns = @JoinColumn(name = "id_settlement_detail"),
			inverseJoinColumns = @JoinColumn(name = "id_settlement"))
	private Settlement settlement;
	
	@Override
	public String toString() {
		
		return "SettlementDetail: [id="+getId()+
				", amountToSettle="+getAmountToSettle()+
				", amountSettled="+getAmountSettled()+
				", remainingAmountToSettle="+getRemainingAmountToSettle()+
				", customerOrder="+getCustomerOrder()+"]";
		
	}
	
	public BigDecimal getAmountToSettle() {
		return amountToSettle;
	}

	public void setAmountToSettle(BigDecimal amountToSettle) {
		this.amountToSettle = amountToSettle;
	}

	public BigDecimal getAmountSettled() {
		return amountSettled;
	}

	public void setAmountSettled(BigDecimal amountSettled) {
		this.amountSettled = amountSettled;
	}

	public BigDecimal getRemainingAmountToSettle() {
		return remainingAmountToSettle;
	}

	public void setRemainingAmountToSettle(BigDecimal remainingAmountToSettle) {
		this.remainingAmountToSettle = remainingAmountToSettle;
	}

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}
}
