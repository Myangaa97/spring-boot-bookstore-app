package com.bookstore.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.RegisterRequest;
import com.bookstore.dto.UserCreateRequest;
import com.bookstore.dto.UserResponse;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.DuplicateResourceException;
import com.bookstore.repository.UserRepository;


@Service
public class UserService {
	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public void registerCustomer(RegisterRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Registration request cannot be null");
		}
		String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
		
		if(userRepository.existsByEmail(email)) {
			throw new DuplicateResourceException("Email is already registered: " + email);
		}
		
		User user = new User();
		user.setFirstName(request.getFirstName().trim());
		user.setLastName(request.getLastName().trim());
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.CUSTOMER);
		user.setEnabled(true);
		
		userRepository.save(user);
	}

	@Transactional
	public UserResponse createUser(UserCreateRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("User create request cannot be null");
		}

		String email = request.email().trim().toLowerCase(Locale.ROOT);

		if (userRepository.existsByEmail(email)) {
			throw new DuplicateResourceException("Email already exists: " + email);
		}

		User user = new User();

		user.setFirstName(request.firstName().trim());

		user.setLastName(request.lastName().trim());
		user.setEmail(email);

		String encryptedPassword = passwordEncoder.encode(request.password());
		user.setPassword(encryptedPassword);

		user.setRole(request.role());

		user.setEnabled(true);

		User savedUser = userRepository.save(user);

		return toResponse(savedUser);
	}

	@Transactional(readOnly = true)
	public List<UserResponse> findAllUsers() {
		return userRepository.findAll().stream()
				.map(this::toResponse).toList();
	}

//	/api/users
	/**
	 * {
		  "firstName": "Bookstore",
		  "lastName": "Admin",
		  "email": "admin@bookstore.com",
		  "password": "admin123",
		  "role": "ADMIN"
		}
		{
		  "firstName": "Bat",
		  "lastName": "Bold",
		  "email": "bat@example.com",
		  "password": "customer123",
		  "role": "CUSTOMER"
		}
	 * @param user
	 * @return
	 */
//	User -> UserResponse
	private UserResponse toResponse(User user) {

		return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(),
				user.isEnabled());

	}
}
