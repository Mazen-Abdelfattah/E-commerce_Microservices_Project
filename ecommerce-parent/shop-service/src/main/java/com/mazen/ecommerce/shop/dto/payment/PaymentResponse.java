package com.mazen.ecommerce.shop.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;
}
