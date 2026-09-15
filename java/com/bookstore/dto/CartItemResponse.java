package com.bookstore.dto;

import java.math.BigDecimal;

public record CartItemResponse(
		Long id,
		Long bookId,
		String title,
		BigDecimal unitPrice,
        Integer quantity,
		BigDecimal lineTotal) {

}
