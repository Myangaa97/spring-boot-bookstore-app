package com.bookstore.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.entity.Order;
import com.bookstore.entity.OrderStatus;
import com.bookstore.entity.Payment;
import com.bookstore.entity.PaymentStatus;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;

@Service
public class StripeWebhookService {
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;

	public StripeWebhookService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
		super();
		this.paymentRepository = paymentRepository;
		this.orderRepository = orderRepository;
	}

	@Transactional
	public void handleEvent(Event event) {
		if ("checkout.session.completed".equals(event.getType())) {
			handleCheckoutComplete(event);
		}
		
		if ("checkout.session.expired".equals(event.getType())) {
			handleCheckoutExpired(event);
		}
	}
	
	

	private void handleCheckoutExpired(Event event) {
		Session session = getSession(event);
		Payment payment = paymentRepository.findByProviderPaymentId(session.getId()).orElse(null);
		if (payment == null) {
			return;
		}
		
		if (payment.getStatus() == PaymentStatus.PENDING) {
			payment.setStatus(PaymentStatus.CANCELED);
			paymentRepository.save(payment);
		}
	}

	private void handleCheckoutComplete(Event event) {
		Session session = getSession(event);

		if (!"paid".equals(session.getPaymentStatus())) {
			return;
		}

		Payment payment = paymentRepository.findByProviderPaymentId(session.getId()).orElse(null);
		if (payment == null) {
			return;
		}

		if (payment.getStatus() == PaymentStatus.PAID) {
			return;
		}

		payment.setStatus(PaymentStatus.PAID);
		payment.setPaidAt(LocalDateTime.now());
		Order order = payment.getOrder();

		if (order.getStatus() == OrderStatus.PENDING) {
			order.setStatus(OrderStatus.CONFIRMED);
		}

		paymentRepository.save(payment);
		orderRepository.save(order);

	}

	private Session getSession(Event event) {
		StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

		if (!(stripeObject instanceof Session session)) {
			throw new IllegalStateException("Stripe session data missing");
		}

		return session;
	}

}
