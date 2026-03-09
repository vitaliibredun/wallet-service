package com.example.wallet.repository;

import com.example.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    @Modifying
    @Query("""
            UPDATE Wallet w
            SET w.balance = w.balance + :amount
            WHERE w.id = :walletId
            """)
    int deposit(UUID walletId, BigDecimal amount);

    @Modifying
    @Query("""
            UPDATE Wallet w
            SET w.balance = w.balance - :amount
            WHERE w.id = :walletId AND w.balance >= :amount
            """)
    int withdraw(UUID walletId, BigDecimal amount);
}
