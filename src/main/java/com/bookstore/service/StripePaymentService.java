package com.bookstore.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.dto.StripeCheckoutResponse;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderStatus;
import com.bookstore.entity.Payment;
import com.bookstore.entity.PaymentStatus;
import com.bookstore.entity.User;
import com.bookstore.exception.BusinessRuleException;
import com.bookstore.exception.ResourceNotfoundException;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripePaymentService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final CurrentUserService currentUserService;

	public StripePaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository,
			CurrentUserService currentUserService) {
		super();
		this.orderRepository = orderRepository;
		this.paymentRepository = paymentRepository;
		this.currentUserService = currentUserService;
	}

	@Value("${app.base-url}")
	private String baseUrl;

	@Transactional
	public StripeCheckoutResponse createCheckoutSession(Long orderId) throws StripeException {

		User user = currentUserService.getCurrentUser();
		Order order = orderRepository.findByIdAndUser(orderId, user)
				.orElseThrow(() -> new ResourceNotfoundException("order not found" + orderId));

		if (order.getStatus() == OrderStatus.CANCELED) {
			throw new BusinessRuleException("Canceled order cannot to paid");
		}

		if (order.getStatus() != OrderStatus.PENDING) {
			throw new BusinessRuleException("This order cannot be paid");
		}

		Payment payment = paymentRepository.findByOrder(order).orElseGet(() -> {
			Payment newPayment = new Payment();

			newPayment.setOrder(order);

			newPayment.setAmount(order.getTotalAmount());

			newPayment.setProvider("STRIPE");

			newPayment.setStatus(PaymentStatus.PENDING);

			newPayment.setCreatedAt(LocalDateTime.now());

			return paymentRepository.save(newPayment);

		});

		if (payment.getStatus() == PaymentStatus.PAID) {
			throw new BusinessRuleException("Order is already paid");
		}

		long stripeAmount = order.getTotalAmount().multiply(new BigDecimal("100")).longValueExact();

		SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(baseUrl + "/payment/success" + "?session_id" + "{CHECKOUT_SESSION_ID}")

				.setCancelUrl(baseUrl + "/customer/order" + order.getId() + "?payment_cancelled")
				.setCustomerEmail(user.getEmail()).putMetadata("order_id", order.getId().toString())
				.putMetadata("payment_id", payment.getId().toString())
				.addLineItem(
						SessionCreateParams.LineItem.builder().setQuantity(1L)
								.setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency("mnt")
										.setUnitAmount(stripeAmount)
										.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()

												.setName("Bookstore #" + order.getId()).build())
										.build())
								.build())
				.build();

		Session session = Session.create(params);

		payment.setProviderPaymentId(session.getId());

		paymentRepository.save(payment);

		return new StripeCheckoutResponse(session.getUrl());
	}

}
