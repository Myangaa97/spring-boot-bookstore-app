package com.bookstore.controller;

import java.security.Principal;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CustomerController {

	@GetMapping("/customer")
	public String adminDashboard(Principal principal, Model model) {

		model.addAttribute("email", principal.getName());
		return "customer/dashboard";
	}

}
