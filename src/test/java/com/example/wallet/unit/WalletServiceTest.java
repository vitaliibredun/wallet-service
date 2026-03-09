package com.example.wallet.unit;

import com.example.wallet.dto.OperationType;
import com.example.wallet.dto.WalletOperationRequest;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.WalletNotFoundException;
import com.example.wallet.integration.AbstractIntegrationTest;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class WalletServiceTest extends AbstractIntegrationTest {

    @Autowired
    WalletService service;

    @Autowired
    WalletRepository repository;

    UUID walletId;

    @BeforeEach
    void setup() {
        walletId = UUID.randomUUID();
        repository.save(new Wallet(walletId, BigDecimal.valueOf(1000)));
    }

    @Test
    void shouldDeposit() {
        WalletOperationRequest request =
                new WalletOperationRequest(
                        walletId,
                        OperationType.DEPOSIT,
                        BigDecimal.valueOf(200));

        service.processOperation(request);

        Wallet wallet = repository.findById(walletId).orElseThrow();

        assertThat(wallet.getBalance())
                .isEqualByComparingTo("1200");
    }

    @Test
    void shouldWithdraw() {
        WalletOperationRequest request =
                new WalletOperationRequest(
                        walletId,
                        OperationType.WITHDRAW,
                        BigDecimal.valueOf(300)
                );

        service.processOperation(request);

        Wallet wallet = repository.findById(walletId).orElseThrow();

        assertThat(wallet.getBalance())
                .isEqualByComparingTo("700");
    }

    @Test
    void shouldThrowInsufficientFunds() {
        WalletOperationRequest request =
                new WalletOperationRequest(
                        walletId,
                        OperationType.WITHDRAW,
                        BigDecimal.valueOf(5000)
                );

        assertThatThrownBy(() ->
                service.processOperation(request))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void shouldThrowWalletNotFound() {
        WalletOperationRequest request =
                new WalletOperationRequest(
                        UUID.randomUUID(),
                        OperationType.DEPOSIT,
                        BigDecimal.TEN
                );

        assertThatThrownBy(() ->
                service.processOperation(request)
        ).isInstanceOf(WalletNotFoundException.class);
    }

    @Test
    void shouldThrowInvalidAmount() {
        WalletOperationRequest request =
                new WalletOperationRequest(
                        walletId,
                        OperationType.DEPOSIT,
                        BigDecimal.valueOf(-10)
                );

        assertThatThrownBy(() ->
                service.processOperation(request)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldReturnBalance() {
        WalletResponse response = service.getBalance(walletId);

        assertThat(response.walletId()).isEqualTo(walletId);
        assertThat(response.balance())
                .isEqualByComparingTo("1000");
    }
}
