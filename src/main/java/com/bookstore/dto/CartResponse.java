package com.bookstore.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
	Long cart_id,
	List<CartItemResponse> items,
	BigDecimal totalAmount) {
}
