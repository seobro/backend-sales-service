package com.dev.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dev.backend.model.SalesOrder;
import com.dev.backend.service.SalesOrderService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class SalesOrderController {

	@Autowired
	SalesOrderService salesOrderService;

	@RequestMapping(value = "/salesorder/save", method = RequestMethod.POST)
	public @ResponseBody String saveOrder(@RequestBody String orderStr) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SalesOrder order = mapper.readValue(orderStr, SalesOrder.class);
		return salesOrderService.saveSalesOrder(order);
	}
	
	@RequestMapping(value = "/salesorder/getall", method = RequestMethod.GET)
	public @ResponseBody JsonNode getOrders() {
		return salesOrderService.getSalesOrdersList();
	}
	
	@RequestMapping(value = "/salesorder/findbynumber/{orderNumber}", method = RequestMethod.GET)
	public @ResponseBody JsonNode getOrderByNumber(@PathVariable String orderNumber) {
		return salesOrderService.getOrderByNumber(orderNumber);
	}
	
	@RequestMapping(value = "/salesorder/validate", method = RequestMethod.POST)
	public @ResponseBody String validateOrder(@RequestBody String orderStr) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SalesOrder order = mapper.readValue(orderStr, SalesOrder.class);
		return salesOrderService.validateOrder(order);
	}
	
	@RequestMapping(value = "/salesorder/delete/{orderNumber}", method = RequestMethod.GET)
	public @ResponseBody String deleteOrderByNumber(@PathVariable String orderNumber) {
		return salesOrderService.deleteOrderByNumber(orderNumber);
	}

}
