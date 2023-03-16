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
    private Long lastFetchedUserId = -1L;

    public List<Trade> fetchTradeList(String coinId, Long userId) {
        if (userId != null) {
            if (!lastFetchedUserId.equals(userId)) {
                tradesMap.clear();
                lastFetchedUserId = userId;
            }
            List<Trade> trades;
            if (tradesMap.containsKey(coinId)) {
                trades = tradesMap.get(coinId);
            } else {
                trades = tradeMapper.mapToTrade(tradeClient.getTrades(userId, coinId));
                tradesMap.put(coinId, trades);
            }
            return trades;
        }
        return Collections.emptyList();
    }

    public void saveTrade(Trade trade) {
        var tradeSavedInDb = tradeMapper.mapToTrade(tradeClient.createTrade(tradeMapper.mapToDto(trade)));
        if (tradesMap.containsKey(tradeSavedInDb.getTicker().getCoinId())) {
            var tradesList = tradesMap.get(tradeSavedInDb.getTicker().getCoinId());
            tradesList.add(tradeSavedInDb);
        } else {
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
