package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.domain.Trade;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import static com.takado.myportfoliofront.config.Constants.MAIN_VIEW_GRID_HEIGHT;
import static com.takado.myportfoliofront.config.Constants.TRADES_GRID_HEIGHT_MINIMIZED;

@Service
@Getter
@RequiredArgsConstructor
public class GridService {
    private final GridValueProvider valueProvider;

    public FooterRow setupMainViewGrid(Grid<Asset> grid, GridItemSelectedCallback selectable) {
        grid.setClassName("styledBorderCorner");
        grid.addColumn(valueProvider::getTicker)
                .setHeader("Ticker")
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setKey("ticker");
        grid.addColumn(valueProvider::getAmount)
                .setHeader("Amount")
                .setKey("amount")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getAmount())));
        grid.addColumn(valueProvider::getAvgPrice)
                .setHeader("Avg Price [" + valueProvider.getCurrentPriceCurrency() + "]")
                .setKey("avgPrice")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(valueProvider.avgPrice(asset))));
        grid.addColumn(valueProvider::getPriceNow)
                .setHeader("Price Now [" + valueProvider.getCurrentPriceCurrency() + "]")
                .setKey("priceNow")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> asset.getPriceNow().doubleValue()));
        grid.addColumn(valueProvider::getValueIn)
                .setHeader("Value In [" + valueProvider.getCurrentValueCurrency() + "]")
                .setKey("valueIn")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getValueIn())));
        grid.addColumn(valueProvider::getValueNow)
                .setHeader("Value Now [" + valueProvider.getCurrentValueCurrency() + "]")
                .setKey("valueNow")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> valueProvider.valueNow(asset).doubleValue()));
        grid.addColumn(valueProvider.assetProfitComponentRenderer())
                .setHeader("Profit [+%]")
                .setKey("profit")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(valueProvider.profitStr(asset))));
        grid.asSingleSelect().addValueChangeListener(event -> selectable.gridItemSelectedCallback());
        grid.setSizeFull();
        grid.setMaxHeight(MAIN_VIEW_GRID_HEIGHT, Unit.PIXELS);
        var footerRow = grid.appendFooterRow();
        return footerRow;
    }

    public void mainViewGridRestoreProfitColumn(Grid<Asset> grid) {
        grid.addColumn(valueProvider.assetProfitComponentRenderer())
                .setHeader("Profit [+%]")
                .setKey("profit")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setComparator(Comparator.comparingDouble(asset ->
                        Double.parseDouble(valueProvider.profitStr(asset))));
    }

    public void setupTradesGrid(Grid<Trade> tradesGrid) {
        tradesGrid.setClassName("tradesGridStyle");
        tradesGrid.addColumn(Trade::getLocalDateTimeString)
                .setHeader("Date")
                .setTextAlign(ColumnTextAlign.START)
                .setComparator(Comparator.comparing(Trade::getDateTime))
                .setAutoWidth(true);
        tradesGrid.addColumn(valueProvider::getAmount)
                .setHeader("Amount")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(trade -> Double.parseDouble(trade.getAmount())));
        tradesGrid.addColumn(valueProvider::getValueIn)
                .setHeader("Value In")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(trade -> Double.parseDouble(trade.getValueIn())));
        tradesGrid.addColumn(valueProvider::getAvgPrice)
                .setHeader("Price")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(trade ->
                        Double.parseDouble(valueProvider.avgPrice(trade))));
        tradesGrid.addColumn(valueProvider.tradeTypeComponentRenderer())
                .setHeader("Type")
                .setAutoWidth(true)
                .setComparator(Comparator.comparing(trade -> trade.getType().toString()))
                .setTextAlign(ColumnTextAlign.CENTER);
        tradesGrid.setMaxHeight(TRADES_GRID_HEIGHT_MINIMIZED, Unit.PIXELS);
    }

    public void restoreTradesGridValueAndProfitColumns(Grid<Trade> tradesGrid) {
        tradesGrid.addColumn(valueProvider::getValueNow)
                .setHeader("Value Now")
                .setAutoWidth(true)
                .setComparator(Comparator.comparingDouble(trade ->
                        valueProvider.valueNow(trade).doubleValue()))
                .setTextAlign(ColumnTextAlign.END)
                .setKey("value");
        tradesGrid.addColumn(valueProvider.profitComponentRenderer())
                .setHeader("Profit")
                .setAutoWidth(true)
                .setKey("profit")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setComparator(Comparator.comparingDouble(trade ->
                        Double.parseDouble(valueProvider.profitStr(trade))));
    }
}
