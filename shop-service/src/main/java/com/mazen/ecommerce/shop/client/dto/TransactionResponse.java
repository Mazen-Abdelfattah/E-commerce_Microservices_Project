package com.mazen.ecommerce.shop.client.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class TransactionResponse {

    private Long id;

    private TransactionType type;

    private BigDecimal amount;

    private LocalDateTime timestamp = LocalDateTime.now();

}