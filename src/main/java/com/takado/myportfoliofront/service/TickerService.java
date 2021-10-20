package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.TickerClient;
import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.mapper.TickerMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class TickerService {
    private List<Ticker> tickers;
    private final TickerMapper tickerMapper;
    private final TickerClient tickerClient;
    private final Map<String, String> tickersWithNames;


    public TickerService(TickerClient tickerClient, TickerMapper tickerMapper) {
        this.tickerClient = tickerClient;
        this.tickerMapper = tickerMapper;
        this.tickersWithNames = fetchData();
    }

    private HashMap<String, String> fetchData() {
        tickers = tickerMapper.mapToTickerList(tickerClient.getAllTickers());

        var map = new HashMap<String, String>();
        for (var ticker : tickerClient.getAllTickers()){
            map.put(ticker.getTicker(), ticker.getCoinId());
        }
        return map;
    }

    public Ticker getTicker(Long id) {
        return tickers.stream().filter(ticker -> ticker.getId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public Ticker getTicker(String tickerString) {
        return tickers.stream().filter(ticker -> ticker.getTicker().equals(tickerString)).collect(Collectors.toList()).get(0);
    }

    public List<String> getTickers() {
        var list = new ArrayList<>(tickersWithNames.keySet());
        Collections.sort(list);
        return list;
    }

    public String getTickerCoinId(String ticker) {
        return tickersWithNames.get(ticker);
    }
}
