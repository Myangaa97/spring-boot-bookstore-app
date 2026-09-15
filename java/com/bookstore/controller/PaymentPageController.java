package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentPageController {

	@GetMapping("/payment/success")
	public String paymentSuccess() {
		return "customer/payment-success";
	}
}
