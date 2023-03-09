package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.PriceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PricesService {
    private final static String USD = "usd";
    private final static int PRICES_FETCH_TIME_DELAY = 10;
    private final PriceClient priceClient;
    private final Map<String, BigDecimal> lastFetchedPrices = new HashMap<>();
    private long pricesLastFetchTime = 0;

    public Map<String, BigDecimal> fetchPrices(String[] coinsIds) {
        var currentTime = currentTimeSeconds();
        if (isTimeToFetchFreshData(currentTime)) {
            var newPrices = priceClient.getCoinsPrices(USD, coinsIds);
            if (newPrices != null) {
                lastFetchedPrices.clear();
                for (var entry : newPrices.entrySet()) {
                    lastFetchedPrices.put(entry.getKey(), entry.getValue().get(USD));
                }
                pricesLastFetchTime = currentTime;
            }
        }
        return lastFetchedPrices;
    }

    boolean isTimeToFetchFreshData(long currentTimeSeconds) {
        return currentTimeSeconds - pricesLastFetchTime > PRICES_FETCH_TIME_DELAY;
    }

    long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
