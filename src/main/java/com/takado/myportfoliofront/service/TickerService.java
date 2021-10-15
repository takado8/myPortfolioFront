package com.takado.myportfoliofront.service;

import java.util.Arrays;
import java.util.List;

public class TickerService {
    private static TickerService instance;
    private final List<String> tickers;

    private TickerService() {
        this.tickers = exampleData();
    }

    public static TickerService getInstance() {
        if (instance == null) {
            instance = new TickerService();
        }
        return instance;
    }

    private List<String> exampleData() {
        return Arrays.asList("ADA", "BTC", "ETH");
    }

    public List<String> getTickers() {
        return tickers;
    }
}
