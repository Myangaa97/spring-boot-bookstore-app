package com.bookstore.service;

import com.bookstore.entity.Author;
import com.bookstore.entity.Category;

import java.util.List;

import org.springframework.stereotype.Service;
import com.bookstore.repository.AuthorRepository;

@Service
public class AuthorService {
	private final AuthorRepository authorRepository;
	
	public AuthorService(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}
	
	public List<Author> findAllAuthors() {
		return authorRepository.findAll();
	}
	
	public Author findAuthorByid(Long id) {
		return authorRepository.findById(id).orElseThrow();
	}
	
	public Author createAuthor(Author author) {
		return authorRepository.save(author);
	}
	
	public Author updateAuthor(Long id, Author newAuthor) {
		Author foundAuthor = authorRepository.findById(id).orElseThrow();
		foundAuthor.setFirstName(newAuthor.getFirstName());
		foundAuthor.setLastName(newAuthor.getLastName());
		foundAuthor.setBio(newAuthor.getBio());
		return authorRepository.save(foundAuthor);
	}
	
	public void deleteAuthor(Long id) {
		Author foundAuthor = authorRepository.findById(id).orElseThrow();
		authorRepository.delete(foundAuthor);
	}
}
