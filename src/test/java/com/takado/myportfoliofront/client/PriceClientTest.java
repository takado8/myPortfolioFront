package com.takado.myportfoliofront.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PriceClientTest {
    @Autowired
    PriceClient priceClient;

    @Test
    void getCoinsPricesTest() {
        System.out.println("\n\ntest get prices:\n\n");
        System.out.println(priceClient.getCoinsPrices("usd", "bitcoin", "cardano"));
        System.out.println("\n\nend test get prices\n\n");
    }
}