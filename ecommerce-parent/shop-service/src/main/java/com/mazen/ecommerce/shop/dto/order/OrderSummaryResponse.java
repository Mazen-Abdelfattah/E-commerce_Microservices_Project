package com.mazen.ecommerce.shop.dto.order;

import com.mazen.ecommerce.shop.model.enums.OrderStatus;
import com.mazen.ecommerce.shop.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    // Lightweight version for order lists
    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Integer totalItems;
    private LocalDateTime createdAt;
    private PaymentStatus paymentStatus;

    /*
     It is just without:
        userId
        payment
        orderItems
     */
}