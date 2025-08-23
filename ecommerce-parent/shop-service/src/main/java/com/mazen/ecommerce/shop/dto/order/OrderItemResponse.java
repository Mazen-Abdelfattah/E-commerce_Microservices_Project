package com.mazen.ecommerce.shop.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long id;
    private String sku;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}