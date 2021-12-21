package com.takado.myportfoliofront.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VsCurrencyServiceTest {
    private final VsCurrencyService vsCurrencyService = VsCurrencyService.getInstance();

    @Test
    void getCurrencyFromLabel() {
        //given
        String label = "Price in USD";
        var expected = "USD";
        //when
        var result = vsCurrencyService.getCurrencyFromLabel(label);
        //then
        assertEquals(expected, result);
    }
}