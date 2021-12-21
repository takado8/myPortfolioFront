package com.takado.myportfoliofront.model;

import com.takado.myportfoliofront.domain.Ticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AssetTest {
    @Mock
    private Ticker ticker;
    private final Long id = 5L;
    private final String amount = "0.5";
    private final String valueIn = "100.50";

    private Asset asset;

    @BeforeEach
    void beforeEach() {
        asset = new Asset(ticker, id, amount, valueIn);
    }

    @Test
    void getPriceNow() {
        asset.setPriceNow(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, asset.getPriceNow());
    }

    @Test
    void setTicker() {
        ticker.setTicker("AAA");
        asset.setTicker(ticker);
        assertEquals(ticker, asset.getTicker());
    }

    @Test
    void setAmount() {
        asset.setAmount("12");
        assertEquals("12", asset.getAmount());
    }

    @Test
    void setValueIn() {
        asset.setValueIn("12");
        assertEquals("12", asset.getValueIn());
    }

    @Test
    void setPriceNow() {
        asset.setPriceNow(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, asset.getPriceNow());
    }

    @Test
    void testEquals() {
        var ticker1 = new Ticker(1L, "ABC", "abc");
        var ticker2 = new Ticker(2L, "ABCD", "abcdfg");
        var newAsset = new Asset(ticker1, id, amount, valueIn);
        var newAsset2 = new Asset(ticker2, id, amount, valueIn);
        assertFalse(newAsset.equals(newAsset2));
        newAsset2.getTicker().setTicker("ABC");
        assertTrue(newAsset.equals(newAsset2));
    }

    @Test
    void testHashCode() {
        assertEquals(asset.hashCode(), asset.getTicker().hashCode());
    }

    @Test
    void testToString() {
        assertEquals("Asset{ticker=ticker, amount='0.5', valueIn='100.50'}", asset.toString());
    }

    @Test
    void getId() {
        assertEquals(id, asset.getUserId());
    }

    @Test
    void getTicker() {
        assertEquals(ticker, asset.getTicker());
    }

    @Test
    void getUserId() {
        assertEquals(id, asset.getUserId());
    }

    @Test
    void getAmount() {
        assertEquals(amount, asset.getAmount());
    }

    @Test
    void getValueIn() {
        assertEquals(valueIn, asset.getValueIn());
    }

    @Test
    void testConstructors() {
        var asset1 = new Asset(id,ticker,id, amount,valueIn);
        assertEquals(asset1, asset);
        var assetEmpty = new Asset();
        assertNull(assetEmpty.getId());
        assertNull(assetEmpty.getTicker());
        assetEmpty.setTicker(ticker);
    }
}