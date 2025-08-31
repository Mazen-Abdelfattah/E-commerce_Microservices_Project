package com.mazen.ecommerce.shop.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WithdrawRequest {
    private BigDecimal amount;
    private String description;
}
