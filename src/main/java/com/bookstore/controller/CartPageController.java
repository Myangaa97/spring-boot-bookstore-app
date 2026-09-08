package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartPageController {
	
	@GetMapping("/customer/cart")
	public String cart() {
		return "customer/cart";
	}
	
	
}
