package com.mazen.ecommerce.shop.dto.cart;

import lombok.Data;

@Data
public class AddToCartRequest {
    private String sku;
    private int quantity;
}
