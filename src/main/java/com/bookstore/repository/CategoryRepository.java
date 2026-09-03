package com.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
	
}
