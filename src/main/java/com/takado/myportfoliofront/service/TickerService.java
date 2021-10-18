package com.takado.myportfoliofront.service;


import java.util.*;

public class TickerService {
    private static TickerService instance;
    private final Map<String, String> tickersWithNames;

    private TickerService() {
        this.tickersWithNames = exampleData();
    }

    public static TickerService getInstance() {
        if (instance == null) {
            instance = new TickerService();
        }
        return instance;
    }

    private HashMap<String,String> exampleData() {
        var map = new HashMap<String, String>();
        map.put("ADA", "cardano");
        map.put("BTC", "bitcoin");
        map.put("ETH", "ethereum");
        map.put("LINK", "chainlink");
        return map;
    }

    public List<String> getTickers() {
        return new ArrayList<>(tickersWithNames.keySet());
    }

    public String getTickerName(String ticker) {
        return tickersWithNames.get(ticker);
    }
}
