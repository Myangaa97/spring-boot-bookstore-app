package com.bookstore.dto;

import java.math.BigDecimal;

public record PaymentResponse(
		Long id,
		Long orderId,
		String status,
		BigDecimal amount
		) {

}
