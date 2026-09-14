package com.bookstore.service;

import com.bookstore.repository.BookRepository;
import java.awt.event.ItemEvent;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.dto.OrderItemResponse;
import com.bookstore.dto.OrderResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.OrderStatus;
import com.bookstore.entity.User;
import com.bookstore.exception.BusinessRuleException;
import com.bookstore.exception.ResourceNotfoundException;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class CustomerOrderService {
	private final BookRepository bookRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final CurrentUserService currentUserService;
	public CustomerOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			CurrentUserService currentUserService, BookRepository bookRepository) {
		super();
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.currentUserService = currentUserService;
		this.bookRepository = bookRepository;
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
	
	//cancel order
	
	@Transactional
	public OrderResponse cancelOrder(Long id) {
		User user = currentUserService.getCurrentUser();
		
		Order order = orderRepository.findByIdAndUser(id, user).orElseThrow(() ->
			new ResourceNotfoundException("Order not found" + id));
		
			if(order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
				throw new BusinessRuleException("This order cannot cancelled");
			}
			
			List<OrderItem> orderItems = orderItemRepository.findByOrderOrderByIdAsc(order);
			for(OrderItem item: orderItems) {
				Book book = item.getBook();
				book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
				bookRepository.save(book);
			}
			
			order.setStatus(OrderStatus.CANCELED);
			return toResponse(orderRepository.save(order));
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
