package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.bookstore.dto.RegisterRequest;
import com.bookstore.service.UserService;

import jakarta.validation.Valid;

@Controller
public class RegisterController {
	private final UserService userService;
	
	public RegisterController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute("registerRequest", new RegisterRequest());
		
		return "register";
	}
	
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request, 
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "register";
		}
		
		try {
			userService.registerCustomer(request);
		} catch (Exception e) {
			bindingResult.rejectValue("email", "duplicate", e.getMessage());
			return "register";
		}
		
		return "redirect:/login";
	}
	
}
