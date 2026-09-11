package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bookstore.service.CustomerOrderService;



@Controller
@RequestMapping("/customer/orders")
public class CustomerOrderPageController {
	private final CustomerOrderService customerOrderService;

	public CustomerOrderPageController(CustomerOrderService customerOrderService) {
		super();
		this.customerOrderService = customerOrderService;
	}
	
	@GetMapping
	public String orders(Model model) {
		model.addAttribute("orders", customerOrderService.findCurrentUserOrders());
		
		return "/customer/orders";
	}
	
	@GetMapping("{id}")
	public String orderDetail(@PathVariable Long id, Model model) {
		model.addAttribute("order", customerOrderService.findCurrentUserOrderById(id));
		
		return "customer/order-detail";
	}
}
