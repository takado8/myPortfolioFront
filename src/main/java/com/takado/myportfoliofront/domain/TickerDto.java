package com.takado.myportfoliofront.domain;

import lombok.Data;

@Data
public class TickerDto {
    private Long id;
    private String ticker;
    private String coinId;
}
