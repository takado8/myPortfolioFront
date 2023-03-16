package com.takado.myportfoliofront.control;

import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.domain.Trade;
import com.takado.myportfoliofront.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.takado.myportfoliofront.config.Constants.TRADE_POSITIONS_PER_PAGE;

@Service
@RequiredArgsConstructor
public class NewAssetFormControl {
    private final AssetService assetService;
    private final TickerService tickerService;
    private final TradeService tradeService;
    private final UserService userService;
    private final PricesService pricesService;
    private final GridService gridService;

    public List<String> getTickers() {
        return tickerService.getTickers();
    }

    public int countNbOfPagesInTradesGrid(String tickerString) {
        if (tickerString != null && !tickerString.isBlank()) {
            Ticker ticker = tickerService.getTicker(tickerString);
            var tradeList = tradeService.fetchTradeList(ticker.getCoinId(), userService.getUserId());
            if (tradeList.size() <= TRADE_POSITIONS_PER_PAGE + 1) {
                return 1;
            }
            return (int) Math.ceil((float) tradeList.size() / TRADE_POSITIONS_PER_PAGE);
        }
        return 1;
    }

    public List<Trade> getTradeItemsToSet(String tickerString, boolean isTradesGridMaximized, int currentPageNb) {
        if (tickerString != null && !tickerString.isBlank()) {
            Ticker ticker = tickerService.getTicker(tickerString);
            var tradeList = tradeService.fetchTradeList(ticker.getCoinId(), userService.getUserId());
            List<Trade> itemsToSet;
            if (tradeList == null) {
                itemsToSet = Collections.emptyList();
            } else {
                tradeList.sort(Comparator.comparing(Trade::getDateTime).reversed());
                if (isTradesGridMaximized) {
                    if (tradeList.size() > TRADE_POSITIONS_PER_PAGE + 1) {
                        int startIdx = (currentPageNb - 1) * TRADE_POSITIONS_PER_PAGE + currentPageNb - 2;
                        int endIdx = currentPageNb * TRADE_POSITIONS_PER_PAGE + currentPageNb - 2;
                        int lastIdx = tradeList.size() - 1;
                        if (endIdx >= lastIdx) {
                            endIdx = lastIdx;
                        }
                        if (startIdx < 0) {
                            startIdx = 0;
                        }
                        itemsToSet = tradeList.subList(startIdx, endIdx + 1);
                    } else {
                        itemsToSet = tradeList;
                    }
                } else {
                    itemsToSet = tradeList.size() > 3 ? tradeList.subList(0, 3) : tradeList;
                }
            }
            return itemsToSet;
        }
        return Collections.emptyList();
    }

    public void addToAsset(String ticker, String amount, String valueIn, Long userId) {

        Asset asset = assetService.findByTicker(ticker);
        Trade trade = new Trade(null, userId, tickerService.getTicker(ticker), amount, valueIn, Trade.Type.BID);
        if (asset == null) {
            if (userId != null) {
                assetService.createAsset(ticker, userId, amount, valueIn);
            }
        } else {
            addToAssetPosition(asset, amount, valueIn);
        }
        tradeService.saveTrade(trade);
    }

    private void addToAssetPosition(Asset asset, String amount, String valueIn) {
        asset.setAmount((new BigDecimal(asset.getAmount()).add(new BigDecimal(amount))).toString());
        asset.setValueIn((new BigDecimal(asset.getValueIn()).add(new BigDecimal(valueIn))).toString());
        assetService.updateAsset(asset);
    }

    public boolean subtractFromAsset(String ticker, String amount, String valueIn, Long userId) {
        Asset asset = assetService.findByTicker(ticker);

        if (asset != null) {

            Trade trade = new Trade(null, userId, asset.getTicker(), amount, valueIn,
                    Trade.Type.ASK);
            subtractFromAssetPosition(asset, amount, valueIn);
            tradeService.saveTrade(trade);
            return true;
        }
        return false;
    }

    private void subtractFromAssetPosition(Asset asset, String amount, String valueIn) {
        asset.setAmount((new BigDecimal(asset.getAmount()).subtract(new BigDecimal(amount))).toString());
        asset.setValueIn((new BigDecimal(asset.getValueIn()).subtract(new BigDecimal(valueIn))).toString());
        assetService.updateAsset(asset);
    }


    public boolean deleteAsset(String ticker) {
        if (ticker != null && !ticker.isBlank()) {
            assetService.deleteAsset(ticker);
            return true;
        }
        return false;
    }

    public void setupTradesPrices() {
        var prices = pricesService.fetchPrices(assetService.getCoinsIds());
        tradeService.setPrices(prices);
    }

    public void setupTradesAndAssetsPrices() {
        var prices = pricesService.fetchPrices(assetService.getCoinsIds());
        assetService.setPrices(prices);
        tradeService.setPrices(prices);
    }

    public void reloadAssets(){
        gridService.grid.setItems(assetService.getAssets());
        gridService.refreshFooterRow();
    }
}