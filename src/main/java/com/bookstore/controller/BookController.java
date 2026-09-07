package com.bookstore.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.dto.BookRequest;
import com.bookstore.dto.BookResponse;
import com.bookstore.service.BookService;


@RestController
@RequestMapping("/api/books")
public class BookController {
	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping
	public List<BookResponse> findAll() {
		return bookService.findAllBooks();
	}

	@PostMapping
	public BookResponse create(@RequestBody BookRequest request) {
		return bookService.createBook(request);
	}

	@PutMapping("/{id}")
	public BookResponse update(@PathVariable Long id, @RequestBody BookRequest request) {
		return bookService.updateBook(id, request);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		bookService.deleteBook(id);
	}

}
