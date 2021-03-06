package com.takado.myportfoliofront.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TickerDto {
    private Long id;
    private String ticker;
    private String coinId;
}
