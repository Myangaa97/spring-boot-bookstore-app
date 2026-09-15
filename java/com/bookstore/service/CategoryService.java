package com.bookstore.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.entity.Category;
import com.bookstore.exception.BusinessRuleException;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.exception.ResourceNotfoundException;
import com.bookstore.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	@Transactional(readOnly = true)
	public List<Category> findAllCategories() {
		return categoryRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Category findCategoryByid(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotfoundException("Category not found with ID: " + id));
	}
	
	@Transactional
	public Category createCategory(Category category) {
		if (categoryRepository.existsByName(category.getName())) {
			throw new DuplicateResourceException("Category already exists with name: " + category.getName());
		}
		return categoryRepository.save(category);
	}
	
	@Transactional
	public Category updateCategory(Long id, Category newCategory) {
		Category foundCategory = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotfoundException("Category not found with ID: " + id));
		
		if (categoryRepository.existsByNameAndIdNot(newCategory.getName(), id)) {
			throw new DuplicateResourceException("Category already exists with name: " + newCategory.getName());
		}
		
		foundCategory.setName(newCategory.getName());
		return categoryRepository.save(foundCategory);
	}
	
	@Transactional
	public void deleteCategory(Long id) {
		Category foundCategory = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotfoundException("Category not found with ID: " + id));
		try {
			categoryRepository.delete(foundCategory);
		} catch (DataIntegrityViolationException e) {
			throw new BusinessRuleException("Category cannot be deleted because books reference it");
		}
	}
}
