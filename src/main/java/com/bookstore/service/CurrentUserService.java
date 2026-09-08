package com.bookstore.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bookstore.entity.User;
import com.bookstore.repository.UserRepository;

@Service
public class CurrentUserService {
	private final UserRepository userRepository;
	
	public CurrentUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public User getCurrenUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		String email = authentication.getName();
		
		User foundUser = userRepository.findByEmail(email).orElseThrow();
		return foundUser;
	}
}
