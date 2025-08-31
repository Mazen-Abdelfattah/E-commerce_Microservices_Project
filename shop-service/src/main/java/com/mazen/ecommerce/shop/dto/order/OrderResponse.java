package com.mazen.ecommerce.shop.dto.order;

import com.mazen.ecommerce.shop.dto.payment.PaymentResponse;
import com.mazen.ecommerce.shop.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentResponse payment;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> orderItems;
    private Integer totalItems;
}