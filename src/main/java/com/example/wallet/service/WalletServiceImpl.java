package com.example.wallet.service;

import com.example.wallet.dto.OperationType;
import com.example.wallet.dto.WalletOperationRequest;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.InvalidRequestException;
import com.example.wallet.exception.WalletNotFoundException;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository repository;

    @Override
    @Transactional
    public void processOperation(WalletOperationRequest request) {
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Amount must be positive");
        }

        if (!repository.existsById(request.walletId())) {
            throw new WalletNotFoundException("The wallet doesn't exist");
        }

        int balance;

        if (request.operationType() == OperationType.DEPOSIT) {
            balance = repository.deposit(request.walletId(), request.amount());
        } else {
            balance = repository.withdraw(request.walletId(), request.amount());
        }

        if (balance == 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getBalance(UUID id) {
        Wallet wallet = repository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + id));

        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }
}
