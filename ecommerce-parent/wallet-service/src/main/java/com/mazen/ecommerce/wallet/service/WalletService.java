package com.mazen.ecommerce.wallet.service;

import com.mazen.ecommerce.wallet.dto.wallet.CreateWalletRequest;
import com.mazen.ecommerce.wallet.dto.wallet.WalletResponse;
import com.mazen.ecommerce.wallet.model.User;
import com.mazen.ecommerce.wallet.model.Wallet;
import com.mazen.ecommerce.wallet.repository.UserRepository;
import com.mazen.ecommerce.wallet.repository.WalletRepository;
import com.mazen.ecommerce.wallet.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletResponse createWallet(Long userId, CreateWalletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Wallet wallet = Wallet.builder()
                .user(user)
                .walletType(request.getWalletType())
                .walletName(request.getWalletName())
                .balance(BigDecimal.ZERO)
                .build();

        System.out.println("Received REQUEST walletName = " + request.getWalletName());
        System.out.println("Received WALLET walletName = " + wallet.getWalletName());


        Wallet saved = walletRepository.save(wallet);
        return toResponse(saved);
    }

    public List<WalletResponse> getUserWallets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String currentUser = SecurityUtils.getCurrentUserEmail();
        if (!SecurityUtils.hasRole("ADMIN") &&
                !user.getEmail().equals(currentUser)) {
            throw new SecurityException("You are not allowed to view these wallets");
        }

        return walletRepository.findWalletsByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WalletResponse getWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        String currentUser = SecurityUtils.getCurrentUserEmail();
        if (!SecurityUtils.hasRole("ADMIN") &&
                !wallet.getUser().getEmail().equals(currentUser)) {
            throw new SecurityException("You are not allowed to view this wallet");
        }
        return toResponse(wallet);
    }

    public BigDecimal getBalance(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        String currentUser = SecurityUtils.getCurrentUserEmail();
        if (!SecurityUtils.hasRole("ADMIN") &&
                !wallet.getUser().getEmail().equals(currentUser)) {
            throw new SecurityException("You are not allowed to view this wallet balance");
        }

        return wallet.getBalance();
    }

    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .walletType(wallet.getWalletType())
                .walletName(wallet.getWalletName())
                .balance(wallet.getBalance())
                .createdAt(wallet.getCreatedAt())
                .build();
    }
}
