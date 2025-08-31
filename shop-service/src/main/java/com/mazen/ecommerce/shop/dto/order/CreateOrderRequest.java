package com.mazen.ecommerce.shop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private Long cartId;
//    private String paymentMethod; // e.g., WALLET, CREDIT_CARD, PAY_CASH (It is only wallet right now)
}
