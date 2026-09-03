package com.bookstore.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.bookstore.entity.Author;
import com.bookstore.service.AuthorService;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
	private final AuthorService authorService;
	public AuthorController(AuthorService authorService) {
		this.authorService = authorService;
	}
	
	@GetMapping
	public List<Author> findAll() {
		return authorService.findAllAuthors();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Author create(@RequestBody Author author) {
		return authorService.createAuthor(author);
	}
	
	@GetMapping("/{id}")
	public Author findById(@PathVariable Long id) {
		return authorService.findAuthorByid(id);
	}
	
	@PutMapping("/{id}")
	public Author update(@PathVariable Long id, @RequestBody Author author) {
		return authorService.updateAuthor(id, author);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		authorService.deleteAuthor(id);
	}
	
	
	
	
}
