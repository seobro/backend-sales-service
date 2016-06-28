package com.dev.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.backend.model.Customer;
import com.dev.backend.model.OrderLine;
import com.dev.backend.model.Product;
import com.dev.backend.model.SalesOrder;
import com.dev.backend.repository.CustomerRepository;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.repository.SalesOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {

	@Autowired
	SalesOrderRepository salesOrderRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ProductRepository productRepository;

	@Override
	public String saveSalesOrder(SalesOrder salesOrder) {
		// increase customer current credit
		Customer customer = customerRepository.findByCode(salesOrder.getCustomer().getCode());
		double credit = customer.getCurrentCredit() + salesOrder.getTotalPrice();
		customer.setCurrentCredit(credit);
		customerRepository.save(customer);
		salesOrder.setCustomer(customer);

		List<OrderLine> orderLinesWithUpdatedProducs = new ArrayList<OrderLine>();
		// decrease products quantity
		for (OrderLine orderLine : salesOrder.getOrderLines()) {
			int quantity = orderLine.getProduct().getQuantity() - orderLine.getQuantity();
			Product product = productRepository.findByCode(orderLine.getProduct().getCode());
			product.setQuantity(quantity);
			productRepository.save(product);
			orderLine.setProduct(product);
			orderLine.setUnitPrice(product.getPrice());
			orderLine.setTotalPrice(product.getPrice() * orderLine.getQuantity());
			orderLinesWithUpdatedProducs.add(orderLine);
		}
		salesOrder.setOrderLines(orderLinesWithUpdatedProducs);

		SalesOrder dbOrder = salesOrderRepository.findByOrderNumber(salesOrder.getOrderNumber());
		if (dbOrder != null) {
			salesOrder.setId(dbOrder.getId());
		}
		SalesOrder savedOrder = salesOrderRepository.save(salesOrder);
		ObjectMapper mapper = new ObjectMapper();
		String objectStr = null;
		try {
			objectStr = mapper.writeValueAsString(savedOrder);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return objectStr;
	}

	@Override
	public String validateOrder(SalesOrder salesOrder) {
		for (OrderLine orderLine : salesOrder.getOrderLines()) {
			if (orderLine.getQuantity() > orderLine.getProduct().getQuantity()) {
				return "invalid";
			}
		}
		double totalPrice = salesOrder.getTotalPrice();
		double creditLimit = salesOrder.getCustomer().getCreditLimit();
		double balance = creditLimit - salesOrder.getCustomer().getCurrentCredit();
		if (totalPrice > creditLimit || totalPrice > balance) {
			return "invalid";
		}
		return "valid";
	}

	@Override
	public JsonNode getSalesOrdersList() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(salesOrderRepository.findAll());
	}

	@Override
	public JsonNode getOrderByNumber(String orderNumber) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(salesOrderRepository.findByOrderNumber(orderNumber));
	}

	@Override
	public String deleteOrderByNumber(String orderNumber) {
		updateProductsAndCustomer(salesOrderRepository.findByOrderNumber(orderNumber));
		return String.valueOf(salesOrderRepository.deleteByOrderNumber(orderNumber));
	}

	private void updateProductsAndCustomer(SalesOrder order) {
		// increase products quantity
		for (OrderLine orderLine : order.getOrderLines()) {
			Product product = orderLine.getProduct();
			int quantity = orderLine.getQuantity() + product.getQuantity();
			product.setQuantity(quantity);
			productRepository.save(product);
		}
		// decrease customer current credit
		Customer customer = order.getCustomer();
		double currentCredit = customer.getCurrentCredit() - order.getTotalPrice();
		customer.setCurrentCredit(currentCredit);
		customerRepository.save(customer);
	}

}
