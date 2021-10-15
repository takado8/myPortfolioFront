package com.takado.myportfoliofront.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Asset {
    private Long id;
    private String ticker;
    private String amount;
    private String valueIn;
    private final String valueNow = "0";
    private final String profit = "0";
    private final String avgPrice = "0";
    private final BigDecimal priceNow = BigDecimal.valueOf(54527.47);

    public Asset(String ticker, String amount, String valueIn) {
        this.ticker = ticker;
        this.amount = amount;
        this.valueIn = valueIn;
    }

    public Asset(Long id, String ticker, String amount, String valueIn) {
        this.id = id;
        this.ticker = ticker;
        this.amount = amount;
        this.valueIn = valueIn;
    }

    public Asset() {

    }

    public String getTicker() {
        return ticker;
    }

    public String getAmount() {
        return amount;
    }

    public String getValueIn() {
        return valueIn;
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

    public BigDecimal getValueNow(boolean round) {
        var result = new BigDecimal(amount).multiply(priceNow);
        return round ? result.round(new MathContext(2, RoundingMode.HALF_UP)) : result;
    }

    public String getValueNow() {
        return formatPrice(new BigDecimal(amount)
                .multiply(priceNow)
                .round(new MathContext(2, RoundingMode.HALF_UP)));
    }

    public String getProfit() {
        return formatPrice(getValueNow(false)
                .divide(new BigDecimal(valueIn), MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(100))
                .subtract(BigDecimal.valueOf(100))
                .round(new MathContext(1, RoundingMode.HALF_UP)));
    }

    public String getAvgPrice() {
        return formatPrice(new BigDecimal(valueIn)
                .divide(new BigDecimal(amount), MathContext.DECIMAL128)
                .round(new MathContext(2, RoundingMode.HALF_UP)));
    }

    public String getPriceNow() {
        return formatPrice(priceNow);
    }

    public Long getId() {
        return id;
    }

    private String formatPrice(BigDecimal price) {
        String plainStringPrice = price.toPlainString();

        DecimalFormat formatter = new DecimalFormat("#,###.##");

        var formatted = formatter.format(price);
//        if (formatted.contains(".")) {
//            formatted = formatted.replace('.', ',');
        if (formatted.startsWith(","))
            formatted = "0" + formatted;
//        }
        return formatted;

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
