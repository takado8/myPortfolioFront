package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.domain.Trade;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
public class TradeService {
    private final List<Trade> tradeList;

    public TradeService() {
        tradeList = new ArrayList<>();
        Trade trade1 = new Trade(1L,1L, new Ticker(1L, "ADA", "cardano"),
                "100", "300", Trade.Type.BID);

//        Trade trade2 = new Trade(2L,1L, new Ticker(1L, "ADA", "cardano"),
//                "120", "200", Trade.Type.BID);

        Trade trade3 = new Trade(3L,1L, new Ticker(1L, "ADA", "cardano"),
                "50", "200", Trade.Type.ASK);

        Trade trade4 = new Trade(4L,1L, new Ticker(1L, "ADA", "cardano"),
                "505", "400", Trade.Type.BID);
        Trade trade5 = new Trade(5L,1L, new Ticker(1L, "ADA", "cardano"),
                "125", "130", Trade.Type.ASK);
        tradeList.add(trade1);
//        tradeList.add(trade2);
        tradeList.add(trade3);
        tradeList.add(trade4);
        tradeList.add(trade5);
    }

}
