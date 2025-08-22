package com.mazen.ecommerce.wallet.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.mazen.ecommerce.wallet.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByWalletIdOrderByTimestampDesc(Long walletId, Pageable pageable);
}
