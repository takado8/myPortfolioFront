package com.takado.myportfoliofront.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TickerClientTest {
    @Autowired
    TickerClient tickerClient;

    @Test
    void testGetAllTickers() {
        var tickers = tickerClient.getAllTickers();
        System.out.println("\n\n getting tickers:\n");
        System.out.println(tickers);
        System.out.println("\nend getting tickers\n");
    }
}