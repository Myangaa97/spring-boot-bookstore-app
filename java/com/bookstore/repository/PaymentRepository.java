package com.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.entity.Order;
import com.bookstore.entity.Payment;


public interface PaymentRepository extends JpaRepository<Payment, Long>{
	
	Optional<Payment> findByOrder(Order order);
	
	Optional<Payment> findByProviderPaymentId(String providerPaymentId);
}
