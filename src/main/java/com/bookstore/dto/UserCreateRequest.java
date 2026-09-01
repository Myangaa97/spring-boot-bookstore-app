package com.bookstore.dto;


import com.bookstore.entity.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(@NotBlank @Size(max = 100) String firstName,

		@NotBlank @Size(max = 100) String lastName,

		@NotBlank @Size(max = 150) String email,

		@NotBlank @Size(min = 6, max = 100) String password,

		@NotNull Role role)

{}
