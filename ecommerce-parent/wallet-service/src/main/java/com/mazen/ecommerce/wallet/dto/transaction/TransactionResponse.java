package com.mazen.ecommerce.wallet.dto.transaction;

import com.mazen.ecommerce.wallet.model.Wallet;
import com.mazen.ecommerce.wallet.model.enums.TransactionType;
import jakarta.persistence.*;
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
