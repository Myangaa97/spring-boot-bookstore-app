package com.bookstore.dto;

import java.math.BigDecimal;

public record BookRequest(String title, String isbn, BigDecimal price, 
		Integer stockQuantity, boolean active,
		Long categoryId, Long authorId) {

}
