package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.PriceClient;
import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.model.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import static com.takado.myportfoliofront.service.PriceFormatter.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class GridValueProviderTest {
    @InjectMocks
    private GridValueProvider gridValueProvider;

    @Mock
    private PriceClient priceClient;

    @Mock
    private Ticker ticker;

    private final Asset asset = new Asset(1L, ticker,1L,"10","10000", BigDecimal.valueOf(10000));

    @BeforeEach
    void beforeEach () {
        when(priceClient.getExchangeRate()).thenReturn(BigDecimal.valueOf(4.0));
    }

    @Test
    void avgPrice() {
        gridValueProvider.setCurrentValueCurrency("USD");
        gridValueProvider.setCurrentPriceCurrency("USD");
        assertEquals( "250", gridValueProvider.avgPrice(asset));

        gridValueProvider.setCurrentValueCurrency("PLN");
        gridValueProvider.setCurrentPriceCurrency("PLN");
        assertEquals( "1000", gridValueProvider.avgPrice(asset));

        gridValueProvider.setCurrentValueCurrency("USD");
        gridValueProvider.setCurrentPriceCurrency("PLN");
        assertEquals( "1000", gridValueProvider.avgPrice(asset));

        gridValueProvider.setCurrentValueCurrency("PLN");
        gridValueProvider.setCurrentPriceCurrency("USD");
        assertEquals("250", gridValueProvider.avgPrice(asset));
    }

    @Test
    void getPriceNow() {
        gridValueProvider.setCurrentValueCurrency("PLN");
        gridValueProvider.setCurrentPriceCurrency("USD");
        assertEquals(formatPriceString("10000"), gridValueProvider.getPriceNow(asset));
        gridValueProvider.setCurrentPriceCurrency("PLN");
        assertEquals(formatPriceString("40000"), gridValueProvider.getPriceNow(asset));
    }

    @Test
    void valueNow() {
        gridValueProvider.setCurrentValueCurrency("PLN");
        gridValueProvider.setCurrentPriceCurrency("USD");
        assertEquals(new BigDecimal("400000.0"), gridValueProvider.valueNow(asset));
        gridValueProvider.setCurrentValueCurrency("USD");
        assertEquals(BigDecimal.valueOf(100000), gridValueProvider.valueNow(asset));
    }

    @Test
    void profit() {
        gridValueProvider.setCurrentValueCurrency("PLN");
        gridValueProvider.setCurrentPriceCurrency("USD");
        assertEquals("3900.0", gridValueProvider.profit(asset));

        gridValueProvider.setCurrentValueCurrency("USD");
        gridValueProvider.setCurrentPriceCurrency("PLN");
        assertEquals("3900.0", gridValueProvider.profit(asset));
    }

    @Test
    void valueIn () {
        gridValueProvider.setCurrentValueCurrency("PLN");
        gridValueProvider.setCurrentPriceCurrency("USD");
        assertEquals(new BigDecimal("10000"), gridValueProvider.valueIn(asset));
        gridValueProvider.setCurrentValueCurrency("USD");
        assertEquals(BigDecimal.valueOf(2500).toPlainString(), gridValueProvider.valueIn(asset).toPlainString());
    }
}