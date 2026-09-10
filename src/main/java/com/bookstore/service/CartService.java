package com.bookstore.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.dto.AddCartItemRequest;
import com.bookstore.dto.CartItemResponse;
import com.bookstore.dto.CartResponse;
import com.bookstore.dto.UpdateCartItemRequest;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {
	
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final BookRepository bookRepository;
	private final CurrentUserService currentUserService;
	
	public CartService(
			CartRepository cartRepository, 
			CartItemRepository cartItemRepository,
			BookRepository bookRepository, 
			CurrentUserService currentUserService) {

				this.cartRepository = cartRepository;
				this.cartItemRepository = cartItemRepository;
				this.bookRepository = bookRepository;
				this.currentUserService = currentUserService;
	}
	
	@Transactional
	public Cart getOrCreateCurrentCart() {
		
		User user = currentUserService.getCurrentUser();
		
		return cartRepository.findByUser(user).orElseGet(()-> {
			Cart cart = new Cart();
			cart.setUser(user);
			
			return cartRepository.save(cart);
		});
	}
	
	@Transactional
	public CartResponse addItem(AddCartItemRequest request) {
		
		Cart cart = getOrCreateCurrentCart();
		
		Book book = bookRepository.findById(request.bookId()).orElseThrow(() -> new RuntimeException("Book not found"));
		 
		if(!book.isActive()) {
			throw new RuntimeException("Inactive book cannot be added to the cart");
		}
		 
		if(book.getStockQuantity() <= 0) {
			throw new RuntimeException("Book is out of stock");
		}
		 
		CartItem item = cartItemRepository.findByCartAndBook(cart, book).orElse(null);
		 
		int newQuantity;
		
		if(item == null) {
			item = new CartItem();
			item.setCart(cart);
			item.setBook(book);
			newQuantity = request.quantity();
		} else {
			newQuantity = item.getQuantity() + request.quantity();
		}
		 
		if (newQuantity > book.getStockQuantity()) {
			throw new RuntimeException("Requested quantity exceeds stock");
		}
			 
		item.setQuantity(newQuantity);
		cartItemRepository.save(item);
			 
		return getCurrentCart();
	}
		 
	public CartResponse getCurrentCart() {
		Cart cart = getOrCreateCurrentCart();
			 
		List<CartItem> items = cartItemRepository.findByCartOrderByIdAsc(cart);
		List<CartItemResponse> itemResponses = items.stream().map(this::toResponse).toList();
			 
		BigDecimal total = itemResponses.stream()
				.map(CartItemResponse::lineTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
			 
		return new CartResponse(cart.getId(), itemResponses, total);
	}
	
	public CartResponse updateQuantity(Long itemId, UpdateCartItemRequest request) {
		
		Cart cart = getOrCreateCurrentCart();
		
		CartItem item = cartItemRepository.findById(itemId).orElseThrow();
		if(!item.getCart().getId().equals(cart.getId())) {
			System.out.println("Cart item not found");
		}
		
		if(request.quantity() > item.getBook().getStockQuantity()) {
			System.out.println("Request quantity exceeds stock");
		}
		
		item.setQuantity(request.quantity());
		
		cartItemRepository.save(item);
		
		return getCurrentCart();
	}
	
	@Transactional
	public CartResponse removeItem(Long itemId) {
		Cart cart = getOrCreateCurrentCart();
		
		CartItem item = cartItemRepository.findById(itemId).orElseThrow();
		if(!item.getCart().getId().equals(cart.getId())) {
			System.out.println("Cart item not found");
		}
		
		cartItemRepository.delete(item);
		return getCurrentCart();
	}
		 
	private CartItemResponse toResponse (CartItem item) {
		BigDecimal lineTotal = item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
		return new CartItemResponse(
			item.getId(),
			item.getBook().getId(),
			item.getBook().getTitle(),
			item.getBook().getPrice(),
			item.getQuantity(),
			lineTotal
		);
	}
}