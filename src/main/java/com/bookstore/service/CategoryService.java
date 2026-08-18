package com.bookstore.service;

import java.util.List;
import java.util.jar.Attributes.Name;

import org.springframework.stereotype.Service;

import com.bookstore.entity.Category;
import com.bookstore.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	public List<Category> findAllCategories() {
		return categoryRepository.findAll();
	}
	
	public Category findCategoryByid(Long id) {
		return categoryRepository.findById(id).orElseThrow();
	}
	
	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}
	
	public Category updateCategory(Long id, Category newCategory) {
		Category foundCategory = categoryRepository.findById(id).orElseThrow();
		
		foundCategory.setName(newCategory.getName());
		return categoryRepository.save(foundCategory);
	}
	
	public void deleteCategory(Long id) {
		Category foundCategory = categoryRepository.findById(id).orElseThrow();
		categoryRepository.delete(foundCategory);
	}
}
