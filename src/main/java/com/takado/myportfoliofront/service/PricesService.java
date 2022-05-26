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
    private final PriceClient priceClient;

    public Map<String, BigDecimal> fetchPrices(String[] coinsIds) {
        var prices = priceClient.getCoinsPrices(USD, coinsIds);
        Map<String, BigDecimal> pricesFlattenMap = new HashMap<>();
        for (var entry : prices.entrySet()) {
            pricesFlattenMap.put(entry.getKey(), entry.getValue().get(USD));
        }
        return pricesFlattenMap;
    }
}
