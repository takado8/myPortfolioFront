package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.TickerClient;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TickerService {
    private final TickerClient tickerClient;
    private final Map<String, String> tickersWithNames;

    public TickerService(TickerClient tickerClient) {
        this.tickerClient = tickerClient;
        this.tickersWithNames = fetchData();
    }

    private HashMap<String, String> fetchData() {
        var map = new HashMap<String, String>();
        for (var ticker : tickerClient.getAllTickers()){
            map.put(ticker.getTicker(), ticker.getCoinId());
        }
        return map;
    }

    public List<String> getTickers() {
        var list = new ArrayList<>(tickersWithNames.keySet());
        Collections.sort(list);
        return list;
    }

    public String getTickerName(String ticker) {
        return tickersWithNames.get(ticker);
    }
}
