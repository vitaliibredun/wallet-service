package com.example.wallet.integration.concurrency;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WalletConcurrencyTest extends AbstractIntegrationTest {

    @Autowired
    WalletRepository repository;

    @Autowired
    MockMvc mockMvc;

    UUID walletId;

    @BeforeEach
    void setup() {
        walletId = UUID.randomUUID();
        repository.save(new Wallet(walletId, BigDecimal.valueOf(1000)));
    }

    @Test
    void concurrentWithdrawShouldNeverGoNegative() throws Exception {
        int threads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        Runnable task=()->{
            try {
                String json = """
                        {
                          "walletId":"%s",
                          "operationType":"WITHDRAW",
                          "amount":10
                        }
                        """.formatted(walletId);

                mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));
            } catch (Exception ignored) {}
        };

        for (int i = 0; i < 200; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        Wallet updated = repository.findById(walletId).get();

        assertThat(updated.getBalance()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }
}
