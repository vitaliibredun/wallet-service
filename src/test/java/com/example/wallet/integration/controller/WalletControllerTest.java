package com.example.wallet.integration.controller;

import com.example.wallet.integration.AbstractIntegrationTest;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository repository;

    private UUID walletId;

    @BeforeEach
    void setup() {
        walletId = UUID.randomUUID();
        repository.save(new Wallet(walletId, BigDecimal.valueOf(1000)));
    }

//    ===========================
//    GET BALANCE
//    ===========================

    @Test
    void shouldReturnBalance() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/" + walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void shouldReturn404IfWalletNotExists() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WALLET_NOT_FOUND"));
    }

//    ===========================
//    DEPOSIT
//    ===========================

    @Test
    void shouldDepositSuccessfully() throws Exception {
        String json = """
                {
                  "walletId":"%s",
                  "operationType":"DEPOSIT",
                  "amount":500
                }
                """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Wallet updated = repository.findById(walletId).get();
        assertThat(updated.getBalance()).isEqualByComparingTo("1500");
    }

//    ===========================
//    WITHDRAW
//    ===========================

    @Test
    void shouldWithdrawSuccessfully() throws Exception {
        String json = """
                {
                  "walletId":"%s",
                  "operationType":"WITHDRAW",
                  "amount":200
                }
                """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Wallet updated = repository.findById(walletId).get();
        assertThat(updated.getBalance()).isEqualByComparingTo("800");
    }

    @Test
    void shouldReturn400IfInsufficientFunds() throws Exception {
        String json = """
                {
                  "walletId":"%s",
                  "operationType":"WITHDRAW",
                  "amount":5000
                }
                """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void shouldReturn404IfWalletNotExistsOnOperation() throws Exception {
        String json = """
                {
                  "walletId":"%s",
                  "operationType":"DEPOSIT",
                  "amount":100
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WALLET_NOT_FOUND"));
    }

//    ===========================
//    VALIDATION
//    ===========================

    @Test
    void shouldReturn400IfAmountNegative() throws Exception {
        String json = """
                {
                  "walletId":"%s",
                  "operationType":"DEPOSIT",
                  "amount":-10
                }
                """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void shouldReturn400IfInvalidJson() throws Exception {
        String json = "{ invalid json }";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_JSON"));
    }
}
