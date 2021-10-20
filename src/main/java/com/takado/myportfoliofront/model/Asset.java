package com.takado.myportfoliofront.model;

import com.takado.myportfoliofront.domain.Ticker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private Long id;
    private Ticker ticker;
    private String amount;
    private String valueIn;
    private BigDecimal priceNow = BigDecimal.ONE;

    public Asset(Ticker ticker, String amount, String valueIn) {
        this.ticker = ticker;
        this.amount = amount;
        this.valueIn = valueIn;
    }

    public Asset(Long id, Ticker ticker, String amount, String valueIn) {
        this.id = id;
        this.ticker = ticker;
        this.amount = amount;
        this.valueIn = valueIn;
    }

    public BigDecimal getPriceNow() {
        return priceNow;
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

    public void setPriceNow(BigDecimal priceNow) {
        this.priceNow = priceNow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Asset asset = (Asset) o;
        var ticker = asset.getTicker().getTicker();
        return ticker == null ? asset.ticker == null : ticker.equals(asset.ticker);
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
