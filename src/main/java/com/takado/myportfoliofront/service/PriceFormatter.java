package com.takado.myportfoliofront.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Service
public class PriceFormatter {
    public static String formatProfitString(BigDecimal profit) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(profit);
    }

    public static String formatProfitString(String profit){
        return formatProfitString(new BigDecimal(profit));
    }

    public static String formatPriceString(BigDecimal price) {
        DecimalFormat formatter = price.compareTo(BigDecimal.ONE) >= 0 ?
                new DecimalFormat("#,###.00") : new DecimalFormat("0.0000");
        return formatter.format(price);
    }

    public static String formatPriceString(String price) {
        return formatPriceString(new BigDecimal(price));
    }
}
