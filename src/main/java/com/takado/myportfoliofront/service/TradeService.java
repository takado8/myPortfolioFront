package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.TradeClient;
import com.takado.myportfoliofront.domain.Trade;
import com.takado.myportfoliofront.mapper.TradeMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class TradeService {
    private final Map<String, List<Trade>> tradesMap = new HashMap<>();
    private final TradeClient tradeClient;
    private final TradeMapper tradeMapper;
    private Long userId;

//    public TradeService() {
//        List<Trade> tradeList = new ArrayList<>();
//        Trade trade1 = new Trade(1L, 1L, new Ticker(1L, "ADA", "cardano"),
//                "100", "300", Trade.Type.BID, LocalDateTime.of(2022, 1, 1, 12,
//                9, 43));
//
//        Trade trade2 = new Trade(2L, 1L, new Ticker(1L, "ADA", "cardano"),
//                "120", "200", Trade.Type.BID, LocalDateTime.of(2022, 1, 5, 12,
//                9, 43));
//
//        Trade trade3 = new Trade(3L, 1L, new Ticker(1L, "ADA", "cardano"),
//                "50", "200", Trade.Type.ASK);
//
//        Trade trade4 = new Trade(4L, 1L, new Ticker(1L, "ADA", "cardano"),
//                "505", "400", Trade.Type.BID);
//        Trade trade5 = new Trade(5L, 1L, new Ticker(1L, "ADA", "cardano"),
//                "78.74", "200", Trade.Type.BID);
//        tradeList.add(trade2);
//        tradeList.add(trade4);
//        tradeList.add(trade3);
//        tradeList.add(trade1);
//        tradeList.add(trade5);
//        tradesMap.put(trade1.getTicker().getCoinId(), tradeList);
//    }

    public List<Trade> fetchTradeList(String coinId) {
        if (userId != null) {
            List<Trade> trades;
            if (tradesMap.containsKey(coinId)){
                trades = tradesMap.get(coinId);
            }
            else {
                trades = tradeMapper.mapToTrade(tradeClient.getTrades(userId, coinId));
                tradesMap.put(coinId, trades);
            }
            return trades;
        }
        return Collections.emptyList();
    }

    public void saveTrade(Trade trade) {
        var tradeSavedInDb =  tradeMapper.mapToTrade(tradeClient.createTrade(tradeMapper.mapToDto(trade)));
        if (tradesMap.containsKey(tradeSavedInDb.getTicker().getCoinId())){
            var tradesList = tradesMap.get(tradeSavedInDb.getTicker().getCoinId());
            tradesList.add(tradeSavedInDb);
        }
        else {
            var tradesList = new ArrayList<Trade>();
            tradesList.add(tradeSavedInDb);
            tradesMap.put(tradeSavedInDb.getTicker().getCoinId(), tradesList);
        }
    }

    public void setPrices(Map<String, BigDecimal> prices) {
        for (var entry : tradesMap.entrySet()) {
            var tradesList = entry.getValue();
            if (tradesList.size() > 0) {
                var price = prices.get(tradesList.get(0).getTicker().getCoinId());
                for (Trade trade : tradesList) {
                    trade.setPriceNow(price);
                }
            }
        }
    }
}
