package com.mazen.ecommerce.shop.service;


import com.mazen.ecommerce.shop.dto.order.CreateOrderRequest;
import com.mazen.ecommerce.shop.dto.order.OrderItemResponse;
import com.mazen.ecommerce.shop.dto.order.OrderResponse;
import com.mazen.ecommerce.shop.dto.payment.PaymentResponse;
import com.mazen.ecommerce.shop.model.*;
import com.mazen.ecommerce.shop.model.enums.OrderStatus;
import com.mazen.ecommerce.shop.model.enums.PaymentStatus;
import com.mazen.ecommerce.shop.repository.CartRepository;
import com.mazen.ecommerce.shop.repository.OrderRepository;
import com.mazen.ecommerce.shop.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;

    public OrderResponse createOrder(Long userId, CreateOrderRequest request) throws IllegalAccessException {
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with id: " + request.getCartId()));

        if (!cart.getUserId().equals(userId)) {
            throw new IllegalAccessException("Cart does not belong to user: " + userId);
        }

        if (cart.getCartItems().isEmpty()) {
            throw new EntityNotFoundException("Cannot create order from empty cart");
        }

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
//                .orderItems(new ArrayList<>())
                .build();


        Order finalOrder = order;
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(ci -> OrderItem.builder()
//                        .order(order)
                        .order(finalOrder)
                        .sku(ci.getSku())
                        .productName(ci.getProductName())
                        .quantity(ci.getQuantity())
                        .priceAtPurchase(ci.getUnitPrice())
                        .build())
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);
        order.setCreatedAt(LocalDateTime.now());

        /*
         *  'orderItemRepository.saveAll(orderItems);' WILL MAKE ERROR
         *  Save the order instead of saving orderItems (this will cascade save the items)
         *  Don't Forget: U used 'cascade = CascadeType.ALL' and 'orphanRemoval = true' in model
         */
        order = orderRepository.save(order);


        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        order.setPayment(payment);

        cartRepository.delete(cart);

        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        //TODO Check if the User Exists via Feign Client

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        return toResponse(order);
    }

    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new OrderCancellationException("Cannot cancel shipped order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderCancellationException("Order is already cancelled");
        }

        // Update order status
        order.setStatus(OrderStatus.CANCELLED);

        // Update payment status if exists
        if (order.getPayment() != null) {
            order.getPayment().setStatus(PaymentStatus.FAILED);
            paymentRepository.save(order.getPayment());
        }

        order = orderRepository.save(order);
        return toResponse(order);
    }



    // ====================== HELPER METHODS ======================

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class OrderCancellationException extends RuntimeException {
        public OrderCancellationException(String message) {
            super(message);
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        Integer totalItems = itemResponses.stream()
                .mapToInt(OrderItemResponse::getQuantity)
                .sum();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(itemResponses)
                .payment(order.getPayment() != null ? toResponse(order.getPayment()) : null)
                .createdAt(order.getCreatedAt())
                .totalItems(totalItems)
                .build();
    }

    private OrderItemResponse toResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .sku(orderItem.getSku())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .subtotal(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .build();
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().toString())
                .paidAt(payment.getCreatedAt())
                .transactionId(payment.getTransactionId())
                .build();
    }

}
