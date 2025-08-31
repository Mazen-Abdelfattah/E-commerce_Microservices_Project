//package com.mazen.ecommerce.shop.service;
//
//import com.mazen.ecommerce.shop.client.InventoryClient;
//import com.mazen.ecommerce.shop.client.WalletClient;
//import com.mazen.ecommerce.shop.client.dto.CreateTransactionRequest;
//import com.mazen.ecommerce.shop.client.dto.TransactionResponse;
//import com.mazen.ecommerce.shop.client.dto.TransactionType;
//import com.mazen.ecommerce.shop.client.dto.WalletResponse;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//import io.github.resilience4j.retry.annotation.Retry;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@RequiredArgsConstructor
//@Slf4j
//@Component
//public class WalletServiceAdapter {
//    private final WalletClient walletClient;
//
//    // ====================== RESILIENCE METHODS ======================
//
//    // Wrap Feign call with resilience annotations
//    @CircuitBreaker(name = "walletService", fallbackMethod = "walletFallback")
//    @Retry(name = "walletService")
//    public List<WalletResponse> getWallets(Long userId) {
//        return walletClient.getUserWallet(userId);
//    }
//
//    @Retry(name = "walletService")
//    @CircuitBreaker(name = "walletService", fallbackMethod = "withdrawFallback")
//    public TransactionResponse withdrawFromWallet(Long walletId, BigDecimal amount) {
//        return walletClient.withdraw(walletId,
//                new CreateTransactionRequest(TransactionType.WITHDRAW, amount));
//    }
//
//    // === Fallbacks ===
//    public List<WalletResponse> walletFallback(Long userId, Throwable t) {
//        log.error("Wallet service unavailable for user {}", userId, t);
//        log.error("Fallback triggered: forcing order cancellation");
//        return List.of(); // empty list
//    }
//
//    public TransactionResponse withdrawFallback(Long walletId, BigDecimal amount, Throwable t) {
//        log.error("Wallet service unavailable, cannot withdraw from wallet {}", walletId, t);
//        return null; // gracefully handle
//    }
//}
