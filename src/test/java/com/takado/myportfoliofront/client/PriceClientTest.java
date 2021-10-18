package com.takado.myportfoliofront.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PriceClientTest {
    @Autowired
    PriceClient priceClient;

    @Test
    void getCoinsPricesTest() {
        System.out.println("\ntest get prices:\n");
        System.out.println(priceClient.getCoinsPrices("usd", "bitcoin", "cardano"));
        System.out.println("\nend test get prices\n");
    }

    @Test
    void getExchangeRateTest() {
        BigDecimal exchangeRate = priceClient.getExchangeRate();
        System.out.println("\ntest exchangeRate:\n");
        System.out.println(exchangeRate);
        System.out.println("\nend test exchangeRate\n");
        assertNotNull(exchangeRate);
        assertTrue(exchangeRate.compareTo(BigDecimal.ZERO) > 0);
    }
}