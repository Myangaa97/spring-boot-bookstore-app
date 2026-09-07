package com.bookstore.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.dto.BookResponse;
import com.bookstore.service.BookService;
import com.bookstore.service.CategoryService;


@Controller
public class ShopController {
	private final BookService bookService;
	private final CategoryService categoryService;
	
	public ShopController(BookService bookService, CategoryService categoryService) {
		this.bookService = bookService;
		this.categoryService = categoryService;
	}
	
	@GetMapping("/shop")
	public String shop(
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(defaultValue = "0") int page,
			Model model
			) {
		int pageSize = 8;
		Page<BookResponse> books = bookService.findShopBooks(keyword, categoryId, page, pageSize);
		
		model.addAttribute("books", books);
		model.addAttribute("categories", categoryService.findAllCategories());
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryId", categoryId);
		return "shop/list";
	}
	
	@GetMapping("/shop/books/{id}")
	public String bookDetail(@PathVariable Long id, Model model) {
		
		model.addAttribute("book", bookService.findActiveBookById(id));
		return "shop/detail";
	}
	
	
}
