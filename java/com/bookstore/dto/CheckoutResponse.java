package com.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CheckoutResponse (
		Long orderId,
		String status,
		BigDecimal totalAmount,
		LocalDateTime createdAt
){

}
