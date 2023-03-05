package com.takado.myportfoliofront.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Trade implements Priceable {
    public enum Type {
        ASK,
        BID
    }

    private final Long id;
    private final Long userId;
    private final Ticker ticker;
    private final String amount;
    private final String valueIn;
    private final Type type;
    private final LocalDateTime dateTime;
    private BigDecimal priceNow = BigDecimal.valueOf(-1);

    public Trade(Long id, Long userId, Ticker ticker, String amount, String valueIn, Type type) {
        this.id = id;
        this.userId = userId;
        this.ticker = ticker;
        this.amount = amount;
        this.valueIn = valueIn;
        this.type = type;
        this.dateTime = LocalDateTime.now();
    }

    public String getLocalDateTimeString() {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
        return dateTime.format(myFormatObj);
    }

    public void setPriceNow(BigDecimal priceNow) {
        this.priceNow = priceNow;
    }
}
