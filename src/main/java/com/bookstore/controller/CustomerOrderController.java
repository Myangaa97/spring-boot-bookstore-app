package com.bookstore.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.OrderResponse;
import com.bookstore.service.CustomerOrderService;

@RestController
@RequestMapping("/api/customer/orders")
public class CustomerOrderController {
	
	private final CustomerOrderService customerOrderService;

	public CustomerOrderController(CustomerOrderService customerOrderService) {
		this.customerOrderService = customerOrderService;
	}
	
	@GetMapping
	public List<OrderResponse> findAll() {
		return customerOrderService.findCurrentUserOrders();
	}
	
	@GetMapping("/{id}")
	public OrderResponse findById(@PathVariable Long id) {
		return customerOrderService.findCurrentUserOrderById(id);
	}
	
	
}
