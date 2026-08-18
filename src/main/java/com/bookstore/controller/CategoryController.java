package com.bookstore.controller;

import com.bookstore.repository.CategoryRepository;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.bookstore.entity.Category;
import com.bookstore.service.CategoryService;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	private final CategoryRepository categoryRepository;
	
	private final CategoryService categoryService;
	public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
		this.categoryService = categoryService;
		this.categoryRepository = categoryRepository;
	}
	
	@GetMapping
	public List<Category> findAll() {
		return categoryService.findAllCategories();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Category create(@RequestBody Category category) {
		return categoryService.createCategory(category);
	}
	
	@GetMapping("/{id}")
	public Category findById(@PathVariable Long id) {
		return categoryService.findCategoryByid(id);
	}
	
	@PutMapping("/{id}")
	public Category update(@PathVariable Long id, @RequestBody Category category) {
		return categoryService.updateCategory(id, category);
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id){
			categoryService.deleteCategory(id);
	}

}
