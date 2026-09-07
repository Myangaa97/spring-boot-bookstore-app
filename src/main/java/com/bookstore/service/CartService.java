package com.bookstore.service;

import com.bookstore.repository.CategoryRepository;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.dto.AddCartItemRequest;
import com.bookstore.dto.CartItemResponse;
import com.bookstore.dto.CartResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;

@Service
public class CartService {
	private final CategoryRepository categoryRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final BookRepository bookRepository;
	private final CurrentUserService currentUserService;
	public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
			BookRepository bookRepository, CurrentUserService currentUserService, CategoryRepository categoryRepository) {
		super();
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.bookRepository = bookRepository;
		this.currentUserService = currentUserService;
		this.categoryRepository = categoryRepository;
	}
	
	@Transactional
	public Cart getOrCreateCurrentCart() {
		User user = currentUserService.getCurrenUser();
		
		return cartRepository.findByUser(user).orElseGet(()-> {
			Cart cart = new Cart();
			cart.setUser(user);
			
			return categoryRepository.save(cart);
			
		});
	}
	
	@Transactional
	public CartResponse addItem(AddCartItemRequest request) {
		
		Cart cart = getOrCreateCurrentCart();
		
		 Book book = bookRepository.findById(request.book_id().orelseThrow();
		 
		 if(!book.isActive()) {
			 System.out.println("Inactive book cannot be added to the cart");
		 }
		 
		 if(book.getStockQuantity() < 0) {
			 System.out.println("Book is out of stock");
		 }
		 
		 CartItem item = cartItemRepository.findByCartAndBook(cart, book).orElse(null);
		 
		 int newQuantity;
		 if(item == null) {
			 item = newQuantity CartItem();
			 item.setCart(cart);
			 item.setBook(book);
			 newQuantity = request.quantity();
		 } else {
			 
			 
			 if (newQuantity > book.getStockQuantity()) {
				 System.out.println("Requested quantity exceeds stock");
			 }
			 
			 item.setQuantity(newQuantity);
			 cartItemRepository.save(item);
			 return getCurrentCart();
		 }
		 
		 public CartResponse getCurrentCart() {
			 Cart cart = getOrCreateCurrentCart();
			 
			 List<CartItem> items = cartItemRepository.findByCartOrderByIdAsc(cart);
			 List<CartItemResponse> itemResponses = items.stream().map(this::toResponse).toList();
			 
			 BigDecimal total = itemResponses.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add)
					 return newQuantity CartResponse(cart.getId(), itemResponses, total);
		 }
		 
		 private CartItemResponse toResponse (CartItem item) {
			 BigDecimal lineTotal = item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			 return newQuantity CartItemResponse(item.getId(), item.getBook().getId()),
					 item.getBook().getTitle(),
					 item.getBook().getPrice(), item.getQuantity(), lineTotal();
		 }
	}
}
