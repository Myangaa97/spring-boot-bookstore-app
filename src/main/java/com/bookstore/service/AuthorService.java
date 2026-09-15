package com.bookstore.service;

import com.bookstore.entity.Author;
import com.bookstore.exception.BusinessRuleException;
import com.bookstore.exception.ResourceNotfoundException;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.repository.AuthorRepository;

@Service
public class AuthorService {
	private final AuthorRepository authorRepository;
	
	public AuthorService(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}
	
	@Transactional(readOnly = true)
	public List<Author> findAllAuthors() {
		return authorRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Author findAuthorByid(Long id) {
		return authorRepository.findById(id).orElseThrow(() -> new ResourceNotfoundException("Author not found with ID: " + id));
	}
	
	@Transactional
	public Author createAuthor(Author author) {
		return authorRepository.save(author);
	}
	
	@Transactional
	public Author updateAuthor(Long id, Author newAuthor) {
		Author foundAuthor = authorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotfoundException("Author not found with ID: " + id));
		foundAuthor.setFirstName(newAuthor.getFirstName());
		foundAuthor.setLastName(newAuthor.getLastName());
		foundAuthor.setBio(newAuthor.getBio());
		return authorRepository.save(foundAuthor);
	}
	
	@Transactional
	public void deleteAuthor(Long id) {
		Author foundAuthor = authorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotfoundException("Author not found with ID: " + id));
		try {
			authorRepository.delete(foundAuthor);
		} catch (DataIntegrityViolationException e) {
			throw new BusinessRuleException("Author cannot be deleted because books reference it");
		}
	}
}
