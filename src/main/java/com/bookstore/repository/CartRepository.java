package com.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookstore.entity.Cart;
import com.bookstore.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long>{
	Optional<Cart> findByUser(User user);
}
