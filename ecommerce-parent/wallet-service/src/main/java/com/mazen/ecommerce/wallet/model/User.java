package com.mazen.ecommerce.wallet.model;

import com.mazen.ecommerce.wallet.model.enums.Role;
import com.mazen.ecommerce.wallet.repository.WalletRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Wallet> wallets = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

//    @OneToMany(mappedBy = "user")
//    private List<Transaction> transactions;

    public void addWallet(Wallet wallet) {
        if (this.wallets == null) {
            this.wallets = new ArrayList<>();
        }
        wallets.add(wallet);
    }
}
