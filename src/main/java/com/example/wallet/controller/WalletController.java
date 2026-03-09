package com.example.wallet.controller;

import com.example.wallet.dto.WalletOperationRequest;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService service;

    public ResponseEntity<Void> operate(
            @Valid @RequestBody WalletOperationRequest request) {
        service.processOperation(request);
        return ResponseEntity.ok().build();
    }

    public WalletResponse get(@PathVariable UUID id) {
        return service.getBalance(id);
    }
}
