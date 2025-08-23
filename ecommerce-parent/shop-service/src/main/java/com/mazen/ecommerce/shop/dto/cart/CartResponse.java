package com.mazen.ecommerce.shop.dto.cart;

import com.mazen.ecommerce.shop.dto.cart.CartItemResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;
    int itemCount;
}