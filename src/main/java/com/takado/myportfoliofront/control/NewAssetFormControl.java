package com.takado.myportfoliofront.control;

import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.domain.Trade;
import com.takado.myportfoliofront.service.*;
import com.takado.myportfoliofront.service.grid.GridService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.takado.myportfoliofront.config.Constants.*;

@Service
@RequiredArgsConstructor
public class NewAssetFormControl {
    private final AssetService assetService;
    private final TickerService tickerService;
    private final TradeService tradeService;
    private final UserService userService;
    private final PricesService pricesService;
    private final GridService gridService;
    private int nbOfTradesPerPage = 0;
    private int nbOfRecentTradesDisplayed = 0;

    public List<String> getTickers() {
        return tickerService.getTickers();
    }

    public void setNbOfTradesDisplayed() {
        try {
            UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
                int screenHeight = receiver.getScreenHeight();
                int positionsPerPage = 0;
                int nbOfRecentTrades = 0;
                if (getScreenSizeNbOfTradesDisplayed().containsKey(screenHeight)) {
                    positionsPerPage = getScreenSizeNbOfTradesDisplayed().get(screenHeight)[0];
                    nbOfRecentTrades = getScreenSizeNbOfTradesDisplayed().get(screenHeight)[1];
                } else {
                    System.out.println("\nScreen height of " + screenHeight + " not supported. Setting closest known value.");
                    int closestScreenHeightDiff = Integer.MAX_VALUE;
                    for (var entry : getScreenSizeNbOfTradesDisplayed().entrySet()) {
                        int height = entry.getKey();
                        int diff = screenHeight - height;
                        if (diff > 0 && diff < closestScreenHeightDiff) {
                            closestScreenHeightDiff = diff;
                            positionsPerPage = entry.getValue()[0];
                            nbOfRecentTrades = entry.getValue()[1];
                        }
                    }
                    if (positionsPerPage == 0) {
                        positionsPerPage = DEFAULT_TRADE_POSITIONS_PER_PAGE;
                        nbOfRecentTrades = DEFAULT_NB_OF_RECENT_TRADES_DISPLAYED;
                    }
                }
                nbOfTradesPerPage = positionsPerPage;
                nbOfRecentTradesDisplayed = nbOfRecentTrades;
            });
        } catch (Exception ignore) {
            nbOfTradesPerPage = DEFAULT_TRADE_POSITIONS_PER_PAGE;
            nbOfRecentTradesDisplayed = DEFAULT_NB_OF_RECENT_TRADES_DISPLAYED;
        }
    }

    public int countNbOfPagesInTradesGrid(String tickerString) {
        if (tickerString != null && !tickerString.isBlank()) {
            Ticker ticker = tickerService.getTicker(tickerString);
            var tradeList = tradeService.fetchTradeList(ticker.getCoinId(), userService.getUserId());
            if (tradeList.size() <= nbOfTradesPerPage + 1) {
                return 1;
            }
            return (int) Math.ceil((float) tradeList.size() / nbOfTradesPerPage);
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
                    if (tradeList.size() > nbOfTradesPerPage + 1) {
                        int startIdx = (currentPageNb - 1) * nbOfTradesPerPage;
                        int endIdx = currentPageNb * nbOfTradesPerPage - 1;
                        int lastIdx = tradeList.size() - 1;
                        if (endIdx >= lastIdx) {
                            endIdx = lastIdx;
                        }
                        itemsToSet = tradeList.subList(startIdx, endIdx + 1);
                    } else {
                        itemsToSet = tradeList;
                    }
                } else {
                    itemsToSet = tradeList.size() > nbOfRecentTradesDisplayed ?
                            tradeList.subList(0, nbOfRecentTradesDisplayed) : tradeList;
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

    public void reloadAssets() {
        gridService.grid.setItems(assetService.getAssets());
        gridService.refreshFooterRow();
    }

    public void restoreTradesGridValueAndProfitColumns(Grid<Trade> tradesGrid) {
        gridService.restoreTradesGridValueAndProfitColumns(tradesGrid);
    }

    public void setupTradesGrid(Grid<Trade> tradesGrid) {
        gridService.setupTradesGrid(tradesGrid);
    }

    public Long getUserId() {
        return userService.getUserId();
    }

    public void deselectMainGridItem() {
        gridService.deselectMainGridItem();
    }

    public void reselectMainGridItem() {
        gridService.reselectMainGridItem();
    }
}