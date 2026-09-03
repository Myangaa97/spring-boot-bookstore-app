package com.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookstore.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
	
}