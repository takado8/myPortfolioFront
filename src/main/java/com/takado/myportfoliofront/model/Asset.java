package com.takado.myportfoliofront.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Asset {
    private Ticker ticker;
    private String amount;
    private String valueIn;
    private final String valueNow = "0";
    private final String profit = "0";
    private final String avgPrice = "0";
    private final BigDecimal priceNow = BigDecimal.valueOf(54527.47);

    public Asset(Ticker ticker, String amount, String valueIn) {
        this.ticker = ticker;
        this.amount = amount;
        this.valueIn = valueIn;
    }

    public Asset() {

    }

    public Ticker getTicker() {
        return ticker;
    }

    public String getAmount() {
        return amount;
    }

    public String getValueIn() {
        return valueIn;
    }

    public void setTicker(Ticker ticker) {
        if (this.ticker == null) {
            this.ticker = ticker;
        }
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setValueIn(String valueIn) {
        this.valueIn = valueIn;
    }

    public BigDecimal getValueNow(boolean round) {
        return new BigDecimal(amount)
                .multiply(getPriceNow());
    }

    public BigDecimal getValueNow() {
        return new BigDecimal(amount)
                .multiply(getPriceNow())
                .round(new MathContext(2, RoundingMode.HALF_UP));
    }

    public BigDecimal getProfit() {
        return getValueNow(false)
                .divide(new BigDecimal(valueIn), MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(100))
                .subtract(BigDecimal.valueOf(100))
                .round(new MathContext(1, RoundingMode.HALF_UP));
    }

    public BigDecimal getAvgPrice() {
        return new BigDecimal(valueIn)
                .divide(new BigDecimal(amount), MathContext.DECIMAL128)
                .round(new MathContext(2, RoundingMode.HALF_UP));
    }

    public BigDecimal getPriceNow() {
        return priceNow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Asset asset = (Asset) o;

        return ticker == asset.ticker;
    }

    @Override
    public int hashCode() {
        return ticker.hashCode();
    }

    @Override
    public String toString() {
        return "Asset{" +
                "ticker=" + ticker +
                ", amount='" + amount + '\'' +
                ", valueIn='" + valueIn + '\'' +
                '}';
    }
}
