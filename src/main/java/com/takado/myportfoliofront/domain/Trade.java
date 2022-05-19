package com.takado.myportfoliofront.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
//@NoArgsConstructor
public class Trade {
    public enum Type {
        ASK,
        BID
    }

    private Long id;
    private Long userId;
    private Ticker ticker;
    private String amount;
    private String value;
    private Type type;
}
