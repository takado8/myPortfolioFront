package com.takado.myportfoliofront.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TradeDto {
    private final Long id;
    private final Long userId;
    private final Long tickerId;
    private final String amount;
    private final String valueIn;
    private final Trade.Type type;
    private final LocalDateTime dateTime;

    @Override
    public String toString() {
        return "TradeDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", tickerId=" + tickerId +
                ", amount='" + amount + '\'' +
                ", valueIn='" + valueIn + '\'' +
                ", type=" + type +
                ", dateTime=" + dateTime +
                '}';
    }
}
