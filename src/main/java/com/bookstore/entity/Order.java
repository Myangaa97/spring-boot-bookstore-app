package com.bookstore.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;
	
	@Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmout;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	public Order() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalAmout() {
		return totalAmout;
	}

	public void setTotalAmout(BigDecimal totalAmout) {
		this.totalAmout = totalAmout;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}
