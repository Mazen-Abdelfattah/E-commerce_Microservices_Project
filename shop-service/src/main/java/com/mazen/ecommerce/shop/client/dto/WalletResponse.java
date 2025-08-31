package com.mazen.ecommerce.shop.client.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletResponse {
    private Long id;
    private String walletType;
    private String walletName;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
