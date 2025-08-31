package com.mazen.ecommerce.wallet.dto.wallet;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateWalletRequest {
    @NotBlank
    private String walletType;  // e.g. MAIN, BONUS, CRYPTO

    @NotBlank
    private String walletName;
}
