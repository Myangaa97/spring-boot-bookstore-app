package com.bookstore.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;
	
	@Column(name = "book_title", nullable = false)
	private String bookTile;
	
	@Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal unitPrice;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@Column(name = "line_total", nullable = false, precision = 12, scale = 2)
	private BigDecimal lineTotal;
	
	public OrderItem() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public String getBookTile() {
		return bookTile;
	}

	public void setBookTile(String bookTile) {
		this.bookTile = bookTile;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}
	
}
