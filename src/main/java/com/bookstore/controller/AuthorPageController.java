package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthorPageController {
	@GetMapping("/authors")
	public String authorPage() {
		return "authors";
	}
	
}
