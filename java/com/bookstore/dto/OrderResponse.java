package com.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
		Long id,
		String status,
		BigDecimal totalAmount,
		String paymentStatus,
		LocalDateTime createdAt,
		List<OrderItemResponse> items
		) {

}
