package com.mazen.ecommerce.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private String walletName;

    @Column(nullable = false, length = 50)
    private String walletType;  // e.g., savings, spending, business, crypto, ...

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
    }
}

