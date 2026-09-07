package com.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	List<CartItem> findByCartOrderByIdAsc(Cart cart);
	
	Optional<CartItem> findByCartAndBook(CartItem cart, Book book);
}
