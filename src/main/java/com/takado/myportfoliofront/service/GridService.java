package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.domain.Trade;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.Query;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.takado.myportfoliofront.config.Constants.MAIN_VIEW_GRID_HEIGHT;
import static com.takado.myportfoliofront.config.Constants.TRADES_GRID_HEIGHT_MINIMIZED;
import static com.takado.myportfoliofront.service.PriceFormatter.formatPriceString;
import static com.takado.myportfoliofront.service.PriceFormatter.formatProfitString;

@Service
@Getter
@RequiredArgsConstructor
public class GridService {
    private final GridValueProvider valueProvider;
    public Grid<Asset> grid = new Grid<>();
    private FooterRow footerRow;

    public void setupMainViewGrid(GridItemSelectedCallback selectable) {
        grid = new Grid<>();
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
        footerRow = grid.appendFooterRow();
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

    public void refreshFooterRow() {
        footerRow.getCell(grid.getColumnByKey("ticker")).setText("Total:");
        footerRow.getCell(grid.getColumnByKey("valueIn")).setText(formatPriceString(totalValueIn()));
        footerRow.getCell(grid.getColumnByKey("valueNow")).setText(formatPriceString(totalValueNow()));
        var columnProfit = grid.getColumnByKey("profit");
        if (columnProfit != null) {
            footerRow.getCell(columnProfit).setComponent(getTotalProfitBadge());
        }
    }

    public BigDecimal totalValueIn() {
        return getAssetsFromGrid().stream()
                .map(getValueProvider()::valueIn)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalValueNow() {
        return getAssetsFromGrid().stream()
                .map(getValueProvider()::valueNow)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public List<Asset> getAssetsFromGrid() {
        return grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
    }
    private Span getTotalProfitBadge() {
        var totalProfit = totalProfit();
        Span badge = new Span(formatProfitString(totalProfit) + "%");
        badge.getElement().getThemeList().add("badge " + (totalProfit.doubleValue() >= 0 ? "success" : "error"));
        return badge;
    }

    public BigDecimal totalProfit() {
        try {
            return totalValueNow()
                    .divide(totalValueIn(), MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(100))
                    .subtract(BigDecimal.valueOf(100));
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }
}
