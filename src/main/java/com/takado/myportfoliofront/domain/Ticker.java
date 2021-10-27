package com.takado.myportfoliofront.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ticker {
    private Long id;
    private String ticker;
    private String coinId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticker ticker1 = (Ticker) o;

        return ticker.equals(ticker1.ticker);
    }

    @Override
    public int hashCode() {
        return ticker.hashCode();
    }
}
