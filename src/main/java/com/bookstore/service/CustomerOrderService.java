package com.bookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.dto.OrderItemResponse;
import com.bookstore.dto.OrderResponse;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.User;
import com.bookstore.exception.ResourceNotfoundException;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;

@Service
public class CustomerOrderService {
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final CurrentUserService currentUserService;
	public CustomerOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			CurrentUserService currentUserService) {
		super();
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.currentUserService = currentUserService;
	}
	
	public List<OrderResponse> findCurrentUserOrders() {
		
		User user = currentUserService.getCurrentUser();
		
		
		return orderRepository
				.findByUserOrderByCreatedAtDesc(user)
				.stream()
				.map(this::toResponse)
				.toList();
	}
	
	
	public OrderResponse findCurrentUserOrderById(Long id) {
		
		User user = currentUserService.getCurrentUser();
		
		Order order = orderRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> {
					throw new ResourceNotfoundException("Order not found" + id);
				});
		
		return toResponse(order);
	}
	
	private OrderResponse toResponse(Order order) {
		List<OrderItemResponse> items = orderItemRepository
				.findByOrderOrderByIdAsc(order)
				.stream()
				.map(this::toItemResponse)
				.toList();
		
		return new OrderResponse(
				order.getId(),
				order.getStatus().name(),
				order.getTotalAmount(),
				order.getCreatedAt(),
				items);
	}
	
	private OrderItemResponse toItemResponse(OrderItem item) {
		return new OrderItemResponse(
				item.getId(),
				item.getBook().getId(),
				item.getBookTitle(),
				item.getUnitPrice(),
				item.getQuantity(),
				item.getLineTotal()
				);
	}
}
