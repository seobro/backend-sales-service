package com.dev.backend.service;

import com.dev.backend.model.SalesOrder;
import com.fasterxml.jackson.databind.JsonNode;

public interface SalesOrderService {
	
	public String saveSalesOrder(SalesOrder salesOrder);
	
	public JsonNode getSalesOrdersList();
	
	public JsonNode getOrderByNumber(String orderNumber);
	
	public String deleteOrderByNumber(String orderNumber);
	
	public String validateOrder(SalesOrder orderStr);
}
