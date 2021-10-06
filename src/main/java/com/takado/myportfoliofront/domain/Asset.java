package com.takado.myportfoliofront.domain;

public class Asset {
    private Ticker ticker;
    private String amount;
    private String valueIn;

    public Asset(Ticker ticker, String amount, String valueIn) {
        this.ticker = ticker;
        this.amount = amount;
        this.valueIn = valueIn;
    }

    public Asset () {

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
}
