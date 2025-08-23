package com.mazen.ecommerce.wallet.controller;

import com.mazen.ecommerce.wallet.dto.wallet.CreateWalletRequest;
import com.mazen.ecommerce.wallet.dto.wallet.WalletResponse;
import com.mazen.ecommerce.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<WalletResponse> createWallet(@PathVariable Long userId, @Valid @RequestBody CreateWalletRequest request){
        return ResponseEntity.ok(walletService.createWallet(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WalletResponse>> getUserWallets(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getUserWallets(userId));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWallet(walletId));
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }

}
