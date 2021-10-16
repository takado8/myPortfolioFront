package com.takado.myportfoliofront.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private Long id;
    private String ticker;
    private String amount;
    private String valueIn;

    private final BigDecimal priceNow = BigDecimal.valueOf(60950.0);

    public Asset(String ticker, String amount, String valueIn) {
        this.ticker = ticker;
        this.amount = amount;
        this.valueIn = valueIn;
    }

    public BigDecimal valueNow() {
        return new BigDecimal(amount).multiply(priceNow);
    }

    public String profit() {
        return valueNow()
                .divide(new BigDecimal(valueIn), MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(100))
                .subtract(BigDecimal.valueOf(100)).toString();
    }

    public String avgPrice() {
        return new BigDecimal(valueIn).divide(new BigDecimal(amount), MathContext.DECIMAL128).toPlainString();
    }

    public String valueNowFormatted() {
        return formatPriceString(valueNow());
    }
    public String getPriceNow() {
        return priceNow.toString();
    }

    public String getPriceNowFormatted() {
        return formatPriceString(priceNow);
    }

    public String getAmountFormatted() {
        return formatPriceString(amount);
    }

    public String getValueInFormatted() {
        return formatPriceString(valueIn);
    }

    public String profitFormatted() {
        return Double.parseDouble(valueIn) <= 0 ? "..." : formatProfitString(new BigDecimal(profit()));
    }

    public String avgPriceFormatted() {
        return formatPriceString(avgPrice());
    }

    public void setTicker(String ticker) {
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

    private String formatProfitString(BigDecimal profit) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(profit);
    }

    private String formatPriceString(BigDecimal price) {
        DecimalFormat formatter = price.compareTo(BigDecimal.ONE) >= 0 ?
                new DecimalFormat("#,###.##") : new DecimalFormat("0.########");
        return formatter.format(price);
    }

    private String formatPriceString(String price) {
        return formatPriceString(new BigDecimal(price));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Asset asset = (Asset) o;
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
