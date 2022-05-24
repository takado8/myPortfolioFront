package com.takado.myportfoliofront.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.takado.myportfoliofront.service.PriceFormatter.formatPriceString;

@Getter
@AllArgsConstructor
public class Trade {
    public enum Type {
        ASK,
        BID
    }

    private final Long id;
    private final Long userId;
    private final Ticker ticker;
    private final String amount;
    private final String value;
    private final Type type;
    private final LocalDateTime dateTime;

    public Trade(Long id, Long userId, Ticker ticker, String amount, String value, Type type) {
        this.id = id;
        this.userId = userId;
        this.ticker = ticker;
        this.amount = amount;
        this.value = value;
        this.type = type;
        this.dateTime = LocalDateTime.now();
    }

    public String getLocalDateTimeString() {
//        LocalDateTime myDateObj = LocalDateTime.now();
//        System.out.println("Before formatting: " + myDateObj);
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(myFormatObj);
    }

    public String getPrice() {
        return formatPriceString(new BigDecimal(amount).multiply(new BigDecimal(value)));
    }
}
