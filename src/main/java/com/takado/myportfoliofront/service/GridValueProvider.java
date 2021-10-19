package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.PriceClient;
import com.takado.myportfoliofront.model.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.takado.myportfoliofront.service.PriceFormatter.formatPriceString;
import static com.takado.myportfoliofront.service.PriceFormatter.formatProfitString;

@Service
@RequiredArgsConstructor
public class GridValueProvider {
    private final PriceClient priceClient;
    private String currentValueCurrency;
    private String currentPriceCurrency;

    public String getTicker(Asset asset) {
        return asset.getTicker();
    }

    public String getAmount(Asset asset) {
        return formatPriceString(asset.getAmount());
    }

    public String avgPrice(Asset asset) {
        return (currentPriceCurrency.equals("PLN") ? new BigDecimal(asset.getValueIn())
                : new BigDecimal(asset.getValueIn()).divide(priceClient.getExchangeRate(), MathContext.DECIMAL128))
                .divide(new BigDecimal(asset.getAmount()), MathContext.DECIMAL128).toPlainString();
    }

    public String getAvgPrice(Asset asset) {
        return formatPriceString(avgPrice(asset));
    }

    public String getPriceNow(Asset asset) {
        return formatPriceString(
                currentPriceCurrency.equals("USD") ? asset.getPriceNow() :
                        asset.getPriceNow().multiply(priceClient.getExchangeRate()));
    }

    public BigDecimal valueNow(Asset asset) {
        var value = new BigDecimal(asset.getAmount()).multiply(asset.getPriceNow());
        return currentValueCurrency.equals("USD") ? value : value.multiply(priceClient.getExchangeRate());

    }

    public String getValueNow(Asset asset) {
        return formatPriceString(valueNow(asset));
    }

    public String profit(Asset asset) {
        return valueNow(asset)
                .divide(valueIn(asset), MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(100))
                .subtract(BigDecimal.valueOf(100)).toString();
    }

    public String getProfit(Asset asset) {
        return formatProfitString(profit(asset));
    }

    public BigDecimal valueIn(Asset asset) {
        var value = new BigDecimal(asset.getValueIn());
        return currentValueCurrency.equals("PLN") ? value : value.divide(priceClient.getExchangeRate(), MathContext.DECIMAL128);
    }

    public String getValueIn(Asset asset) {
        return formatPriceString(valueIn(asset));
    }

    public String getCurrentValueCurrency() {
        return currentValueCurrency;
    }

    public void setCurrentValueCurrency(String currentValueCurrency) {
        this.currentValueCurrency = currentValueCurrency;
    }

    public String getCurrentPriceCurrency() {
        return currentPriceCurrency;
    }

    public void setCurrentPriceCurrency(String currentPriceCurrency) {
        this.currentPriceCurrency = currentPriceCurrency;
    }
}
