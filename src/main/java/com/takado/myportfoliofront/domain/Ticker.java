package com.takado.myportfoliofront.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ticker {
    private Long id;
    private String ticker;
    private String coinId;
}
