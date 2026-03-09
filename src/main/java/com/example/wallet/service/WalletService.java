package com.example.wallet.service;

import com.example.wallet.dto.WalletOperationRequest;
import com.example.wallet.dto.WalletResponse;

import java.util.UUID;

public interface WalletService {
    void processOperation(WalletOperationRequest request);

    WalletResponse getBalance(UUID id);
}
