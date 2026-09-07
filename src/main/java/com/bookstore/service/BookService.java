package com.bookstore.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bookstore.dto.BookRequest;
import com.bookstore.dto.BookResponse;
import com.bookstore.entity.Author;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;

@Service
public class BookService {
	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final AuthorRepository authorRepository;

	public BookService(BookRepository bookRepository, CategoryRepository categoryRepository,
			AuthorRepository authorRepository) {
		this.bookRepository = bookRepository;
		this.categoryRepository = categoryRepository;
		this.authorRepository = authorRepository;
	}

	public List<BookResponse> findAllBooks() {
		return bookRepository.findAll().stream().map(this::toResponse).toList();
	}

	public BookResponse createBook(BookRequest request) {
		/**
		 * { "title": "Clean Code", "isbn": "9780132350884", "price": 95000,
		 * "stockQuantity": 12, "active": true, "categoryId": 1, "authorId": 1 }
		 */

		if (bookRepository.existsByIsbn(request.isbn())) {
			throw new RuntimeException("Book already exists with ISBN: " + request.isbn());
		}

		Category foundCategory = categoryRepository.findById(request.categoryId())
				.orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.categoryId()));

		Author foundAuthor = authorRepository.findById(request.authorId())
				.orElseThrow(() -> new RuntimeException("Author not found with ID: " + request.authorId()));

		Book newBook = new Book();
		newBook.setIsbn(request.isbn());
		newBook.setTitle(request.title());
		newBook.setPrice(request.price());
		newBook.setStockQuantity(request.stockQuantity());
		newBook.setActive(request.active());
		newBook.setCategory(foundCategory);
		newBook.setAuthor(foundAuthor);

		Book savedBook = bookRepository.save(newBook);

		// Book => BookResponse method???
		return toResponse(savedBook);
	}

	public Page<BookResponse> findShopBooks(String keyword, Long categoryId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Book> books;
		boolean hasKeyword = keyword != null && !keyword.isBlank();
		boolean hasCategory = categoryId != null;
		if (hasKeyword && hasCategory) {
			books = bookRepository.findByActiveTrueAndTitleContainingIgnoreCaseAndCategoryId(keyword.trim(), categoryId, pageable);

		} else if (hasKeyword) {
			books = bookRepository.findByActiveTrueAndTitleContainingIgnoreCase(keyword.trim(), pageable);

		} else if (hasCategory) {
			books = bookRepository.findByActiveTrueAndCategoryId(categoryId, pageable);

		} else {
			books = bookRepository.findByActiveTrue(pageable);
		}

		return books.map(this::toResponse);
	}
	
	public BookResponse findActiveBookById(Long id) {
		Book book = bookRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));
		if (!book.isActive()) {
			throw new RuntimeException("Book is not active with ID: " + id);
		}
		return toResponse(book);
	}
	

	public BookResponse updateBook(Long id, BookRequest request) {
//		1. Find existing book for the id

		Book foundBook = bookRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));

//		2. Find category of the request
		Category category = categoryRepository.findById(request.categoryId())
				.orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.categoryId()));

//		3. Find author of the request
		Author author = authorRepository.findById(request.authorId())
				.orElseThrow(() -> new RuntimeException("Author not found with ID: " + request.authorId()));
		// check whether ISBN is used another book with the id
		if (bookRepository.existsByIsbn(request.isbn()) && !foundBook.getIsbn().equals(request.isbn())) {
			throw new RuntimeException("Another book already uses ISBN: " + request.isbn());
		}

		foundBook.setTitle(request.title());
		foundBook.setActive(request.active());
		foundBook.setAuthor(author);
		foundBook.setCategory(category);
		foundBook.setIsbn(request.isbn());
		foundBook.setPrice(request.price());
		foundBook.setStockQuantity(request.stockQuantity());

		Book updatedBook = bookRepository.save(foundBook);
		return toResponse(updatedBook);
	}

	public void deleteBook(Long id) {
		Book book = bookRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));
		bookRepository.delete(book);
	}

//	this is always below the public methods
	private BookResponse toResponse(Book book) {
		return new BookResponse(book.getId(), book.getTitle(), book.getIsbn(), book.getPrice(), book.getStockQuantity(),
				book.isActive(),

				book.getCategory().getId(), book.getCategory().getName(),

				book.getAuthor().getId(), book.getAuthor().getFirstName(), book.getAuthor().getLastName()

		);
	}

}
