package com.takado.myportfoliofront.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeDto {
    private Long id;
    private Long userId;
    private Long tickerId;
    private String amount;
    private String valueIn;
    private Trade.Type type;
    private LocalDateTime dateTime;

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
