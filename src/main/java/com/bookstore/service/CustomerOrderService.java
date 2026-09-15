package com.bookstore.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.OrderItemResponse;
import com.bookstore.dto.OrderResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.OrderStatus;
import com.bookstore.entity.User;
import com.bookstore.exception.BusinessRuleException;
import com.bookstore.exception.ResourceNotfoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;

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
	
	@Transactional(readOnly = true)
	public List<OrderResponse> findCurrentUserOrders() {
		
		User user = currentUserService.getCurrentUser();
		
		List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
		
		if (orders.isEmpty()) {
			return List.of();
		}
		
		List<OrderItem> items = orderItemRepository
				.findByOrderIdInOrderByIdAsc(orders.stream().map(Order::getId).toList());
		
		Map<Long, List<OrderItem>> itemsByOrder = items.stream()
				.collect(Collectors.groupingBy(orderItem -> orderItem.getOrder().getId()));
		
		return orders.stream()
				.map(order -> toResponse(order, itemsByOrder.getOrDefault(order.getId(), List.of())))
				.toList();
	}
	
	
	@Transactional(readOnly = true)
	public OrderResponse findCurrentUserOrderById(Long id) {
		
		User user = currentUserService.getCurrentUser();
		
		Order order = orderRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new ResourceNotfoundException("Order not found: " + id));
		
		List<OrderItem> items = orderItemRepository.findByOrderOrderByIdAsc(order);
		return toResponse(order, items);
	}
	
	//cancel order
	
	@Transactional
	public OrderResponse cancelOrder(Long id) {
		User user = currentUserService.getCurrentUser();
		
		Order order = orderRepository.findByIdAndUser(id, user).orElseThrow(() ->
			new ResourceNotfoundException("Order not found: " + id));
		
			if(order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
				throw new BusinessRuleException("This order cannot be cancelled");
			}
			
			List<OrderItem> orderItems = orderItemRepository.findByOrderOrderByIdAsc(order);
			for(OrderItem item: orderItems) {
				Book book = item.getBook();
				int restoredStock = book.getStockQuantity() + item.getQuantity();
				book.setStockQuantity(restoredStock);
				bookRepository.save(book);
			}
			
			order.setStatus(OrderStatus.CANCELED);
			return toResponse(orderRepository.save(order), orderItems);
	}
	
	private OrderResponse toResponse(Order order, List<OrderItem> items) {
		List<OrderItemResponse> itemResponses = items
				.stream()
				.map(this::toItemResponse)
				.toList();
		
		return new OrderResponse(
				order.getId(),
				order.getStatus().name(),
				order.getTotalAmount(),
				order.getCreatedAt(),
				itemResponses);
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
