package com.mazen.ecommerce.shop.controller;

import com.mazen.ecommerce.shop.dto.order.OrderResponse;
import com.mazen.ecommerce.shop.dto.order.UpdateOrderStatusRequest;
import com.mazen.ecommerce.shop.dto.payment.PaymentResponse;
import com.mazen.ecommerce.shop.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse response = adminService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<OrderResponse> orders = adminService.getAllOrders(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<PaymentResponse> payments = adminService.getAllPayments(page, size);
        return ResponseEntity.ok(payments);

    }
}
