package com.dev.backend.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class SalesOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(name = "order_number", nullable = false, unique = true)
	private String orderNumber;
    @ManyToOne
    @JoinColumn(name="customer_id")
	private Customer customer;
	@Column(name = "total_price", nullable = false)
	private double totalPrice;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "salesOrder", orphanRemoval = true, targetEntity = OrderLine.class)
	@JsonManagedReference
	private List<OrderLine> orderLines;

	public SalesOrder() {
	}
	
	public SalesOrder(String orderNumber, Customer customer, double totalPrice) {
		this.orderNumber = orderNumber;
		this.customer = customer;
		this.totalPrice = totalPrice;
	}

	public SalesOrder(String orderNumber, Customer customer, double totalPrice, List<OrderLine> orderLines) {
		this.orderNumber = orderNumber;
		this.customer = customer;
		this.totalPrice = totalPrice;
		this.orderLines = orderLines;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}

}
