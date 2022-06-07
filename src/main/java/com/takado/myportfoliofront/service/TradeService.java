package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.domain.Trade;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
public class TradeService {
    private final Map<String, List<Trade>> tradesMap = new HashMap<>();

    public TradeService() {
        List<Trade> tradeList = new ArrayList<>();
        Trade trade1 = new Trade(1L, 1L, new Ticker(1L, "ADA", "cardano"),
                "100", "300", Trade.Type.BID, LocalDateTime.of(2022, 1, 1, 12,
                9, 43));

        Trade trade2 = new Trade(2L, 1L, new Ticker(1L, "ADA", "cardano"),
                "120", "200", Trade.Type.BID, LocalDateTime.of(2022, 1, 5, 12,
                9, 43));

        Trade trade3 = new Trade(3L, 1L, new Ticker(1L, "ADA", "cardano"),
                "50", "200", Trade.Type.ASK);

        Trade trade4 = new Trade(4L, 1L, new Ticker(1L, "ADA", "cardano"),
                "505", "400", Trade.Type.BID);
        Trade trade5 = new Trade(5L, 1L, new Ticker(1L, "ADA", "cardano"),
                "78.74", "200", Trade.Type.BID);
        tradeList.add(trade2);
        tradeList.add(trade4);
        tradeList.add(trade3);
        tradeList.add(trade1);
        tradeList.add(trade5);
        tradesMap.put(trade1.getTicker().getCoinId(), tradeList);
    }

    public List<Trade> getTradeList(String coinId) {
        return tradesMap.get(coinId);
    }

    public void setPrices(Map<String, BigDecimal> prices) {
        for (var entry : tradesMap.entrySet()) {
            var tradesList = entry.getValue();
            var price = prices.get(tradesList.get(0).getTicker().getCoinId());
            for (Trade trade : tradesList) {
                trade.setPriceNow(price);
            }
        }
    }
}
