package com.takado.myportfoliofront.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VsCurrencyServiceTest {
    @Autowired
    private VsCurrencyService vsCurrencyService;

    @Test
    void getCurrencyFromLabel() {
        String label = "Price in USD";
        var expected = "USD";
        var result = vsCurrencyService.getCurrencyFromLabel(label);
        assertEquals(expected, result);
    }
}