package com.mazen.ecommerce.shop.service;

import com.mazen.ecommerce.shop.dto.order.OrderItemResponse;
import com.mazen.ecommerce.shop.dto.order.OrderResponse;
import com.mazen.ecommerce.shop.dto.order.UpdateOrderStatusRequest;
import com.mazen.ecommerce.shop.dto.payment.PaymentResponse;
import com.mazen.ecommerce.shop.model.Order;
import com.mazen.ecommerce.shop.model.OrderItem;
import com.mazen.ecommerce.shop.model.Payment;
import com.mazen.ecommerce.shop.model.enums.OrderStatus;
import com.mazen.ecommerce.shop.model.enums.PaymentStatus;
import com.mazen.ecommerce.shop.repository.OrderRepository;
import com.mazen.ecommerce.shop.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        // Business rules for status transitions
        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());

        // Update payment status based on order status
        // PENDING -> PAID -> SHIPPED -> CANCELLED
        if (order.getPayment() != null) {
            updatePaymentStatusBasedOnOrder(order.getPayment(), request.getStatus());
        }

        order = orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    public List<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> ordersPage = orderRepository.findAll(pageable);

        return ordersPage.getContent().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getAllPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Payment> paymentsPage = paymentRepository.findAll(pageable);

        return paymentsPage.getContent().stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    // ====================== HELPER METHODS ======================

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        Map<OrderStatus, Set<OrderStatus>> validTransitions = Map.of(
                OrderStatus.PENDING, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
                OrderStatus.PAID, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
                OrderStatus.SHIPPED, Set.of(), // Cannot change from shipped
                OrderStatus.CANCELLED, Set.of() // Cannot change from cancelled
        );

        Set<OrderStatus> allowedTransitions = validTransitions.get(currentStatus);
        if (allowedTransitions == null || !allowedTransitions.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    String.format("Cannot transition from %s to %s", currentStatus, newStatus));
        }
    }

    private void updatePaymentStatusBasedOnOrder(Payment payment, OrderStatus orderStatus) {
        switch (orderStatus) {
            case PAID:
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setCreatedAt(LocalDateTime.now());
                break;
            case CANCELLED:
                payment.setStatus(PaymentStatus.FAILED);
                break;
            // PENDING and SHIPPED don't change payment status
        }
        paymentRepository.save(payment);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        Integer totalItems = itemResponses.stream()
                .mapToInt(OrderItemResponse::getQuantity)
                .sum();

        PaymentResponse paymentResponse = null;
        if (order.getPayment() != null) {
            paymentResponse = mapToPaymentResponse(order.getPayment());
        }

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(itemResponses)
                .payment(paymentResponse)
                .createdAt(order.getCreatedAt())
                .totalItems(totalItems)
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        BigDecimal itemTotal = orderItem.getPriceAtPurchase()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .sku(orderItem.getSku())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .subtotal(itemTotal)
                .build();
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().toString())
                .paidAt(payment.getCreatedAt())
                .transactionId(payment.getTransactionId())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class InvalidStatusTransitionException extends RuntimeException {
        public InvalidStatusTransitionException(String message) {
            super(message);
        }
    }
}

