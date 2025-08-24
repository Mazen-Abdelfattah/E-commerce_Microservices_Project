package com.mazen.ecommerce.shop.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class CartItemResponse {
    private Long id;
    private String sku;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
