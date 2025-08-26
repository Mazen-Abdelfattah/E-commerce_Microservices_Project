package com.mazen.ecommerce.shop.client;

import com.mazen.ecommerce.shop.client.dto.CreateTransactionRequest;
import com.mazen.ecommerce.shop.client.dto.TransactionResponse;
import com.mazen.ecommerce.shop.client.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @GetMapping("/api/wallets/user/{userId}")
    List<WalletResponse> getUserWallet(@PathVariable("userId") Long userId);

    @PostMapping("/api/transactions/{walletId}/withdraw")
    TransactionResponse withdraw(
            @PathVariable("walletId") Long walletId,
            @RequestBody CreateTransactionRequest request
    );

}
