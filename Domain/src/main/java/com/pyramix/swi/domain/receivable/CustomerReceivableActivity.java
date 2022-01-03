package com.pyramix.swi.domain.receivable;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pyramix.swi.domain.common.IdBasedObject;
import com.pyramix.swi.domain.common.SchemaUtil;
import com.pyramix.swi.domain.serial.DocumentStatus;

@Entity
@Table(name = "receivable_activity", schema = SchemaUtil.SCHEMA_COMMON)
public class CustomerReceivableActivity extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3479376215003133568L;
	
	// `customer_order_id` bigint(20)
	@Column(name = "customer_order_id")
	private long customerOrderId;
	
	//  `activity_type` int(11) DEFAULT NULL,
	@Column(name = "activity_type")
	@Enumerated(EnumType.ORDINAL)
	private ActivityType activityType;

	//  `sales_date` date DEFAULT NULL,
	@Column(name = "sales_date")
	@Temporal(TemporalType.DATE)
	private Date salesDate;
	
	//  `payment_due_date` date DEFAULT NULL,
	@Column(name = "payment_due_date")
	@Temporal(TemporalType.DATE)
	private Date paymentDueDate;
	
	//  `sales_description` varchar(255) DEFAULT NULL,
	@Column(name = "sales_description")
	private String salesDescription;
	
	//  `amount_sales` decimal(19,2) DEFAULT NULL,
	@Column(name = "amount_sales")
	private BigDecimal amountSales;
	
	//  `amount_sales_ppn` decimal(19,2) DEFAULT NULL,
	@Column(name = "amount_sales_ppn")
	private BigDecimal amountSalesPpn;

	//  `payment_date` date DEFAULT NULL,
	@Column(name = "payment_date")
	@Temporal(TemporalType.DATE)
	private Date paymentDate;
	
	//  `payment_description` varchar(255) DEFAULT NULL,
	@Column(name = "payment_description")
	private String paymentDescription;
	
	//  `overdue_day` int(11) DEFAULT NULL,
	@Column(name = "overdue_day")
	private int overdueDay;
	
	//  `amount_paid` decimal(19,2) DEFAULT NULL,
	@Column(name = "amount_paid")
	private BigDecimal amountPaid;
	
	//  `amount_paid_ppn` decimal(19,2) DEFAULT NULL,  
	@Column(name = "amount_paid_ppn")
	private BigDecimal amountPaidPpn;
	
	//  `payment_complete` char(1) DEFAULT NULL,
	@Column(name = "payment_complete")
	@Type(type = "true_false")
	private boolean paymentComplete;
	
	//  `remaining_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "remaining_amount")
	private BigDecimal remainingAmount;

	// receivable_status int(11)
	@Column(name = "receivable_status")
	private DocumentStatus receivableStatus;

	//	batal_date date
	@Column(name = "batal_date")
	@Temporal(TemporalType.DATE)
	private Date batalDate;
	
	//	batal_note varchar(255)
	@Column(name = "batal_note")
	private String batalNote;
	
	@Override
	public String toString() {
		return "CustomerReceivableActivity[id="+getId()+
			", salesDate="+getSalesDate()+
			", customerOrderId="+getCustomerOrderId()+
			", activityType="+getActivityType().toString()+
			", paymentDueDate="+getPaymentDueDate()+
			", amountSales="+getAmountSales()+
			", salesDescription="+getSalesDescription()+
			", paymentDate="+getPaymentDate()+
			", overDueDay="+getOverdueDay()+
			", amountPaid="+getAmountPaid()+
			", paymentComplete="+isPaymentComplete()+
			", remainingAmount="+getRemainingAmount()+
			", paymentDescription="+getPaymentDescription()+
			", receivableStatus="+getReceivableStatus().toString()+
			"]";
	}
	
	public long getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(long customerOrderId) {
		this.customerOrderId = customerOrderId;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public Date getSalesDate() {
		return salesDate;
	}

	public void setSalesDate(Date salesDate) {
		this.salesDate = salesDate;
	}

	public Date getPaymentDueDate() {
		return paymentDueDate;
	}

	public void setPaymentDueDate(Date paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

	public String getSalesDescription() {
		return salesDescription;
	}

	public void setSalesDescription(String salesDescription) {
		this.salesDescription = salesDescription;
	}

	public BigDecimal getAmountSales() {
		return amountSales;
	}

	public void setAmountSales(BigDecimal amountSales) {
		this.amountSales = amountSales;
	}

	public BigDecimal getAmountSalesPpn() {
		return amountSalesPpn;
	}

	public void setAmountSalesPpn(BigDecimal amountSalesPpn) {
		this.amountSalesPpn = amountSalesPpn;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentDescription() {
		return paymentDescription;
	}

	public void setPaymentDescription(String paymentDescription) {
		this.paymentDescription = paymentDescription;
	}

	public int getOverdueDay() {
		return overdueDay;
	}

	public void setOverdueDay(int overdueDay) {
		this.overdueDay = overdueDay;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public BigDecimal getAmountPaidPpn() {
		return amountPaidPpn;
	}

	public void setAmountPaidPpn(BigDecimal amountPaidPpn) {
		this.amountPaidPpn = amountPaidPpn;
	}

	public boolean isPaymentComplete() {
		return paymentComplete;
	}

	public void setPaymentComplete(boolean paymentComplete) {
		this.paymentComplete = paymentComplete;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public DocumentStatus getReceivableStatus() {
		return receivableStatus;
	}

	public void setReceivableStatus(DocumentStatus receivableStatus) {
		this.receivableStatus = receivableStatus;
	}

	public Date getBatalDate() {
		return batalDate;
	}

	public void setBatalDate(Date batalDate) {
		this.batalDate = batalDate;
	}

	public String getBatalNote() {
		return batalNote;
	}

	public void setBatalNote(String batalNote) {
		this.batalNote = batalNote;
	}
	
	
}
