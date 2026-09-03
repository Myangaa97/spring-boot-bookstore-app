package com.bookstore.dto;

import com.bookstore.entity.*;

public record UserResponse(Long id, String firstName, String lastName, String email, Role role, boolean enabled) {
}
