package com.mazen.ecommerce.shop.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private String sku;
    private String productName;
    private int quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;

//    It has NO orderId
}