package com.mazen.ecommerce.shop.dto.order;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long cartId;
    private String paymentMethod; // e.g., WALLET, CREDIT_CARD, PAY_CASH (It is only wallet right now)
}
