package com.takado.myportfoliofront.mapper;

import com.takado.myportfoliofront.domain.AssetDto;
import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.service.TickerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AssetMapperTest {
    @InjectMocks
    AssetMapper assetMapper;
    @Mock
    TickerService tickerService;

    Ticker ticker = new Ticker(1L, "ABC", "abc");
    Asset asset = new Asset(ticker, 1L, "0.5", "100");
    AssetDto assetDto = new AssetDto(1L, ticker.getId(), 1L, "0.5", "100");

    @Test
    void mapToAssetSet() {
        when(tickerService.getTicker(ticker.getId())).thenReturn(ticker);
        assertTrue(assetMapper.mapToAssetSet(List.of(assetDto)).contains(asset));
    }

    @Test
    void mapToAsset() {
        when(tickerService.getTicker(ticker.getId())).thenReturn(ticker);
        assertEquals(asset, assetMapper.mapToAsset(assetDto));
    }

    @Test
    void mapToDto() {
        assertEquals(assetDto, assetMapper.mapToDto(asset));
    }
}