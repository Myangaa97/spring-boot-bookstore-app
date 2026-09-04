package com.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
	@NotBlank(message = "first name is required")
	@Size(max = 100, message = "first name is too long")
	private String firstName;
	
	@NotBlank(message = "last name is required")
	@Size(max = 100, message = "last name is too long")
	private String lastName;
	
	@NotBlank(message = "email is required")
	@Email(message = "Please enter a valid email")
	private String email;
	
	@NotBlank(message = "pass is required")
	@Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
	private String password;
	
	public RegisterRequest(){}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
