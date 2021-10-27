package com.takado.myportfoliofront.mapper;

import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.domain.TickerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TickerMapperTest {
    @Autowired
    TickerMapper mapper;

    @Test
    void mapToDto() {
        assertEquals(new TickerDto(1L, "ABC", "abc"),
                mapper.mapToDto(new Ticker(1L, "ABC", "abc")));
    }
}