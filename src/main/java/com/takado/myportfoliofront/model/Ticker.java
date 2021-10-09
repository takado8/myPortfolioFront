package com.takado.myportfoliofront.model;


public enum Ticker {
    BTC("BTC"),
    ETH("ETH"),
    ADA("ADA");

    private final String string;

    Ticker(String tickerString){
        this.string = tickerString;
    }

    public String getString() {
        return string;
    }
}
