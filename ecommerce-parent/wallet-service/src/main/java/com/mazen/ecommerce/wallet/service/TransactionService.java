package com.mazen.ecommerce.wallet.service;

import com.mazen.ecommerce.wallet.dto.transaction.CreateTransactionRequest;
import com.mazen.ecommerce.wallet.dto.transaction.TransactionResponse;
import com.mazen.ecommerce.wallet.model.Transaction;
import com.mazen.ecommerce.wallet.model.Wallet;
import com.mazen.ecommerce.wallet.model.enums.TransactionType;
import com.mazen.ecommerce.wallet.repository.TransactionRepository;
import com.mazen.ecommerce.wallet.repository.WalletRepository;
import com.mazen.ecommerce.wallet.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransactionResponse deposit(Long walletId, CreateTransactionRequest request) {
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        String currentUser = SecurityUtils.getCurrentUserEmail();
        if (!SecurityUtils.hasRole("ADMIN") &&
                !wallet.getUser().getEmail().equals(currentUser)) {
            throw new SecurityException("You are not allowed to deposit into this wallet");
        }

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    public TransactionResponse withdraw(Long walletId, CreateTransactionRequest request) {
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        String currentUser = SecurityUtils.getCurrentUserEmail();
        if (!SecurityUtils.hasRole("ADMIN") &&
                !wallet.getUser().getEmail().equals(currentUser)) {
            throw new SecurityException("You are not allowed to withdraw from this wallet");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(request.getAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    public Page<TransactionResponse> getTransactions(Long walletId, Pageable pageable) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        String currentUser = SecurityUtils.getCurrentUserEmail();
        if (!SecurityUtils.hasRole("ADMIN") &&
                !wallet.getUser().getEmail().equals(currentUser)) {
            throw new SecurityException("You are not allowed to view this wallet's transactions");
        }

        return transactionRepository.findByWalletIdOrderByTimestampDesc(walletId, pageable)
                .map(this::toResponse);
    }

    private TransactionResponse toResponse(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .type(tx.getType())
                .amount(tx.getAmount())
                .timestamp(tx.getTimestamp())
                .build();
    }
}
