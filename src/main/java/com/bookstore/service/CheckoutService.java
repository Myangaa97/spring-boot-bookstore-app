package com.bookstore.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.CheckoutResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.OrderStatus;
import com.bookstore.entity.User;
import com.bookstore.exception.BusinessRuleException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.OrderItemRepository;
import com.bookstore.repository.OrderRepository;



@Service
public class CheckoutService {
	private final CurrentUserService currentUserService;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderItemRepository orderItemRepository;
	private final OrderRepository orderRepository;
	private final BookRepository bookRepository;
	
	// Spring Dependency injection
	public CheckoutService(CurrentUserService currentUserService, CartRepository cartRepository,
			CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository,
			OrderRepository orderRepository, BookRepository bookRepository) {
		super();
		this.currentUserService = currentUserService;
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.orderItemRepository = orderItemRepository;
		this.orderRepository = orderRepository;
		this.bookRepository = bookRepository;
	}
	
	@Transactional
	public CheckoutResponse checkout() //throws Exception hiij bolno yrunhii//
		{
		
		User user = currentUserService.getCurrentUser();
		
		Cart cart = cartRepository.findByUser(user).orElseThrow(() -> {
			throw new BusinessRuleException("Card does not exist");
		});
		
		List<CartItem> cartItems = cartItemRepository.findByCartOrderByIdAsc(cart);
		if(cartItems.isEmpty()) {
			throw new BusinessRuleException("Card is empty");
		}
		
// 		Validate all cart items
		for(CartItem item : cartItems) {
			Book book = item.getBook();
			if(!book.isActive()) {
				throw new BusinessRuleException("Book is not available" + book.getTitle());
			}
			
			if(item.getQuantity() > book.getStockQuantity()) {
				throw new BusinessRuleException("Not enough book is stock" + book.getTitle());
			}
		}
		
		
		//Calculate total amount of cart items
		// for loop calculate amount of cart items
//		BigDecimal total = BigDecimal.ZERO;
//		for(CartItem item: cartItems) {
//			Book book = item.getBook();
//			BigDecimal totalAmountPrice = BigDecimal.valueOf(item.getQuantity()).multiply(book.getPrice());
//			total.add(totalAmountPrice);			
//		}
		
		// stream -> map -> reduce
		BigDecimal totalAmount = cartItems
				.stream()
					.map(ItemEvent -> ItemEvent.getBook().getPrice()
						.multiply(BigDecimal.valueOf(ItemEvent.getQuantity())))
						.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		// 3. Create order
		Order order = new Order();
		
		order.setUser(user);
		order.setStatus(OrderStatus.PENDING);
		order.setTotalAmout(totalAmount);
		order.setCreatedAt(LocalDateTime.now());
		
		Order savedOrder = orderRepository.save(order);
		
		// 4. create order
		for(CartItem item: cartItems) {
			Book book = item.getBook();
			BigDecimal unitPrice = book.getPrice();
			BigDecimal totalLine = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
			
			OrderItem orderItem = new OrderItem();
			
			orderItem.setOrder(savedOrder);
			orderItem.setBook(book);
			orderItem.setBookTile(book.getTitle());
			orderItem.setUnitPrice(unitPrice);
			orderItem.setQuantity(item.getQuantity());
			orderItem.setLineTotal(totalLine);
			
			orderItemRepository.save(orderItem);
			
			// 5. reduce book stock
			book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
			bookRepository.save(book);
		}
		
		cartItemRepository.deleteAll(cartItems);
		
		
		
		
		return new CheckoutResponse(savedOrder.getId(),
					savedOrder.getStatus().name(),
					savedOrder.getTotalAmout(),
					savedOrder.getCreatedAt());
	}
}
