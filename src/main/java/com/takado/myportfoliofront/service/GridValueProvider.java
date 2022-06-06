package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.PriceClient;
import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.domain.Priceable;
import com.takado.myportfoliofront.domain.Trade;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
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

    public String getTicker(Priceable asset) {
        return asset.getTicker().getTicker();
    }

    public String getAmount(Priceable asset) {
        return formatPriceString(asset.getAmount());
    }

    public String avgPrice(Priceable asset) {
        return (currentPriceCurrency.equals("PLN") ? new BigDecimal(asset.getValueIn())
                : new BigDecimal(asset.getValueIn()).divide(priceClient.getExchangeRate(), MathContext.DECIMAL128))
                .divide(new BigDecimal(asset.getAmount()), MathContext.DECIMAL128).toPlainString();
    }

    public String getAvgPrice(Priceable asset) {
        return formatPriceString(avgPrice(asset));
    }

    public String getPriceNow(Priceable asset) {
        return formatPriceString(
                currentPriceCurrency.equals("USD") ? asset.getPriceNow() :
                        asset.getPriceNow().multiply(priceClient.getExchangeRate()));
    }

    public BigDecimal valueNow(Priceable asset) {
        var value = new BigDecimal(asset.getAmount()).multiply(asset.getPriceNow());
        return currentValueCurrency.equals("USD") ? value : value.multiply(priceClient.getExchangeRate());

    }

    public String getValueNow(Priceable asset) {
        return formatPriceString(valueNow(asset));
    }

    public BigDecimal profit(Priceable asset) {
        var valueIn = valueIn(asset);
        if (valueIn.doubleValue() > 0) {
            return valueNow(asset)
                    .divide(valueIn, MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(100))
                    .subtract(BigDecimal.valueOf(100));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public String profitStr(Priceable asset) {
        return profit(asset).toPlainString();
    }

    public String getProfit(Priceable asset) {
        return formatProfitString(profitStr(asset));
    }

    public BigDecimal valueIn(Priceable asset) {
        var value = new BigDecimal(asset.getValueIn());
        return currentValueCurrency.equals("PLN") ? value : value.divide(priceClient.getExchangeRate(), MathContext.DECIMAL128);
    }

    public String getValueIn(Priceable asset) {
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

    private final SerializableBiConsumer<Span, Asset> assetProfitComponentUpdater = (span, asset) -> {
        String theme = String
                .format("badge %s", Double.parseDouble(profitStr(asset)) >= 0 ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(getProfit(asset) + "%");
    };

    public ComponentRenderer<Span, Asset> assetProfitComponentRenderer() {
        return new ComponentRenderer<>(Span::new, assetProfitComponentUpdater);
    }


    private final SerializableBiConsumer<Span, Trade> typeComponentUpdater = (span, trade) -> {
        String theme = String
                .format("badge %s", trade.getType() == Trade.Type.BID ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(trade.getType().toString());
    };

    public ComponentRenderer<Span, Trade> tradeTypeComponentRenderer() {
        return new ComponentRenderer<>(Span::new, typeComponentUpdater);
    }

    private final SerializableBiConsumer<Span, Trade> profitComponentUpdater = (span, trade) -> {
        var profit = trade.getType().equals(Trade.Type.ASK) ?
                profit(trade).multiply(BigDecimal.valueOf(-1)) : profit(trade);
        var profitStr = formatProfitString(profit.toPlainString()) + "%";
        String theme = String
                .format("badge %s", profit.doubleValue() >= 0 ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(profitStr);
    };

    public ComponentRenderer<Span, Trade> profitComponentRenderer() {
        return new ComponentRenderer<>(Span::new, profitComponentUpdater);
    }
}
