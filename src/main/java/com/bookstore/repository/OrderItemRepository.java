package com.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	List<OrderItem> findByOrderOrderByIdAsc(Order order);
}
