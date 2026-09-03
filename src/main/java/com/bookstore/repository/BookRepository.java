package com.bookstore.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{
	
	boolean existsByIsbn(String isbn);
	
	boolean existsByIsbnAndIdNot(String isbn, Long id);
	
	Page<Book> findByActiveTrue(Pageable pageable);
	
	Page<Book> findByActiveTrueAndTitleContainingIgnoreCase(String keyword, Pageable pageable);
 	
	Page<Book> findByActiveTrueAndCategoryId(Long categoryId, Pageable pageable);
	
	Page<Book> findByActiveTrueAndTitleContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId, Pageable pageable);
}
