package com.mazen.ecommerce.shop.controller;

import com.mazen.ecommerce.shop.dto.order.CreateOrderRequest;
import com.mazen.ecommerce.shop.dto.order.OrderResponse;
import com.mazen.ecommerce.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable Long userId,
            @Valid @RequestBody CreateOrderRequest request) throws IllegalAccessException {

        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {

        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {

        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {

        OrderResponse response = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }
}
