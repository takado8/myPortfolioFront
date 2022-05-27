package com.takado.myportfoliofront.domain;

import java.math.BigDecimal;

public interface Priceable {
    Ticker getTicker();
    String getAmount();
    String getValueIn();
    BigDecimal getPriceNow();
}
