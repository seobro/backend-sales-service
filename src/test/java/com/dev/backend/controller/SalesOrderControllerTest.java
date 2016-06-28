package com.dev.backend.controller;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.dev.backend.Application;
import com.dev.backend.model.Customer;
import com.dev.backend.model.OrderLine;
import com.dev.backend.model.Product;
import com.dev.backend.model.SalesOrder;
import com.dev.backend.repository.CustomerRepository;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.repository.SalesOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.greaterThan;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration

public class SalesOrderControllerTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("UTF-8"));

	private MockMvc mockMvc;

	private Customer customer;
	private List<Product> productList = new ArrayList<>();
	private List<OrderLine> orderLineList1 = new ArrayList<>();
	private List<OrderLine> orderLineList2 = new ArrayList<>();
	private List<SalesOrder> salesOrderList = new ArrayList<>();

	private SalesOrder validOrder;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	SalesOrderRepository salesOrderRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ProductRepository productRepository;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

		// setup test data
		this.customer = customerRepository
				.save(new Customer("123", "Customer 1", "Address, 1", "123456", "654321", 10000));
		this.productList.add(productRepository.save(new Product("111", "Product 1 description", 100, 200)));
		this.productList.add(productRepository.save(new Product("222", "Product 2 description", 20, 300)));

		int orderLine1Quantity = 5;
		double orderLine1UnitPrice = this.productList.get(0).getPrice();
		int orderLine2Quantity = 10;
		double orderLine2UnitPrice = this.productList.get(1).getPrice();
		int orderLine3Quantity = 15;
		double orderLine3UnitPrice = this.productList.get(1).getPrice();
		double totalPrice1 = (orderLine1Quantity * orderLine1UnitPrice) + (orderLine2Quantity * orderLine2UnitPrice);
		this.salesOrderList.add(salesOrderRepository.save(new SalesOrder("11", this.customer, totalPrice1)));
		this.salesOrderList.add(salesOrderRepository
				.save(new SalesOrder("22", this.customer, orderLine3Quantity * orderLine3UnitPrice)));

		this.orderLineList1.add(new OrderLine(this.productList.get(0), orderLine1Quantity, orderLine1UnitPrice,
				orderLine1Quantity * orderLine1UnitPrice, this.salesOrderList.get(0)));
		this.orderLineList1.add(new OrderLine(this.productList.get(0), orderLine2Quantity, orderLine2UnitPrice,
				orderLine2Quantity * orderLine2UnitPrice, this.salesOrderList.get(0)));
		this.orderLineList2.add(new OrderLine(this.productList.get(0), orderLine3Quantity, orderLine3UnitPrice,
				orderLine3Quantity * orderLine3UnitPrice, this.salesOrderList.get(1)));

		this.salesOrderList.get(0).setOrderLines(orderLineList1);
		salesOrderRepository.save(this.salesOrderList.get(0));
		this.salesOrderList.get(1).setOrderLines(orderLineList2);
		salesOrderRepository.save(this.salesOrderList.get(1));
		this.customer.setCurrentCredit(salesOrderList.get(0).getTotalPrice() + salesOrderList.get(1).getTotalPrice());
		customerRepository.save(this.customer);

		for (OrderLine orderLine : this.salesOrderList.get(0).getOrderLines()) {
			int quantity = orderLine.getProduct().getQuantity() - orderLine.getQuantity();
			Product product = orderLine.getProduct();
			product.setQuantity(quantity);
			productRepository.save(product);
		}

		for (OrderLine orderLine : this.salesOrderList.get(1).getOrderLines()) {
			int quantity = orderLine.getProduct().getQuantity() - orderLine.getQuantity();
			Product product = orderLine.getProduct();
			product.setQuantity(quantity);
			productRepository.save(product);
		}

		// create objects to test validation and creation
		int orderLine4Quantity = 2;
		double orderLine4UnitPrice = this.productList.get(0).getPrice();

		this.validOrder = new SalesOrder("33", this.customer, orderLine4Quantity * orderLine4UnitPrice);
		OrderLine validOrderLine = new OrderLine(this.productList.get(0), orderLine4Quantity, orderLine4UnitPrice,
				orderLine4Quantity * orderLine4UnitPrice, validOrder);
		List<OrderLine> validList = new ArrayList<OrderLine>();
		validList.add(validOrderLine);
		this.validOrder.setOrderLines(validList);
	}

	@After
	public void removeFakeData() {
		for (SalesOrder order : this.salesOrderList) {
			this.salesOrderRepository.deleteByOrderNumber(order.getOrderNumber());
		}
		this.customerRepository.deleteByCode(this.customer.getCode());

		for (Product product : this.productList) {
			this.productRepository.deleteByCode(product.getCode());
		}
	}

	@Test
	public void readSingleOrder() throws Exception {
		mockMvc.perform(get("/salesorder/findbynumber/" + this.salesOrderList.get(0).getOrderNumber()))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.id", is((int) this.salesOrderList.get(0).getId())))
				.andExpect(jsonPath("$.orderNumber", is("11"))).andExpect(jsonPath("$.totalPrice", is((double) 700.0)))
				.andExpect(jsonPath("$.customer.code", is("123"))).andExpect(jsonPath("$.orderLines", hasSize(2)))
				.andExpect(jsonPath("$.orderLines[0].quantity", is(5)))
				.andExpect(jsonPath("$.orderLines[0].unitPrice", is((double) 100.0)))
				.andExpect(jsonPath("$.orderLines[0].totalPrice", is((double) 500.0)))
				.andExpect(jsonPath("$.orderLines[1].quantity", is(10)))
				.andExpect(jsonPath("$.orderLines[1].unitPrice", is((double) 20.0)))
				.andExpect(jsonPath("$.orderLines[1].totalPrice", is((double) 200.0)));
	}

	@Test
	public void readSalesOrders() throws Exception {
		mockMvc.perform(get("/salesorder/getall")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$", hasSize(greaterThan(1))))
				.andExpect(jsonPath("$[0].id", is(not(""))))
				.andExpect(jsonPath("$[0].orderNumber", is(not(""))))
				.andExpect(jsonPath("$[0].totalPrice", is(not(""))))
				.andExpect(jsonPath("$[1].id", is(not(""))))
				.andExpect(jsonPath("$[1].orderNumber", is(not(""))))
				.andExpect(jsonPath("$[1].totalPrice", is(not(""))));
	}

	@Test
	public void orderValidation() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String orderJson = mapper.writeValueAsString(this.validOrder);
		this.mockMvc.perform(post("/salesorder/validate").contentType(contentType).content(orderJson))
				.andExpect(content().string("valid"));
	}

	@Test
	public void orderSave() throws Exception {
		this.salesOrderList.get(0).setOrderNumber("44");
		ObjectMapper mapper = new ObjectMapper();
		String orderJson = mapper.writeValueAsString(this.salesOrderList.get(0));
		this.mockMvc.perform(post("/salesorder/save").contentType(contentType).content(orderJson))
				.andExpect(jsonPath("$.orderNumber", is("44")));
	}

	@Test
	public void orderDelete() throws Exception {
		mockMvc.perform(get("/salesorder/delete/" + this.salesOrderList.get(1).getOrderNumber()))
				.andExpect(status().isOk()).andExpect(content().string("1"));
	}

}
