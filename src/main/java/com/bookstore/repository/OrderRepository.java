package com.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.entity.Order;
import com.bookstore.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByUserOrderByCreatedAtDesc(User user);
	
	Optional<Order> findByIdAndUser(Long id, User user);

}
