package com.mazen.ecommerce.wallet.controller;

import com.mazen.ecommerce.wallet.dto.transaction.CreateTransactionRequest;
import com.mazen.ecommerce.wallet.dto.transaction.TransactionResponse;
import com.mazen.ecommerce.wallet.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{walletId}/deposit")
    private ResponseEntity<TransactionResponse> deposit(@PathVariable Long walletId,
                                                        @Valid @RequestBody CreateTransactionRequest request){

        return ResponseEntity.ok(transactionService.deposit(walletId,request));
    }

    @PostMapping("/{walletId}/withdraw")
    private ResponseEntity<TransactionResponse> withdraw(@PathVariable Long walletId,
                                                        @Valid @RequestBody CreateTransactionRequest request){

        return ResponseEntity.ok(transactionService.withdraw(walletId,request));
    }

    @GetMapping("/{walletId}/history")
    public ResponseEntity<Page<TransactionResponse>> history(
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(transactionService.getTransactions(walletId, page, size));
    }
}
