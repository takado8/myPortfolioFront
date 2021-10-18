package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.model.Asset;
import com.takado.myportfoliofront.service.AssetService;
import com.takado.myportfoliofront.service.VsCurrencyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Route
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends VerticalLayout {
    private final AssetService assetService;
    private final VsCurrencyService vsCurrencyService;
    private final Grid<Asset> grid = new Grid<>();
    private FooterRow footerRow;
    private final TextField filter = new TextField();
    private final NewAssetForm newAssetForm;
    private final Select<String> priceCurrency = new Select<>();
    private final Select<String> valueCurrency = new Select<>();
    private boolean lockPriceCurrencyChanged = false;
    private boolean lockValueCurrencyChanged = false;

    public MainView(AssetService assetService, VsCurrencyService vsCurrencyService) {
        this.assetService = assetService;
        this.vsCurrencyService = vsCurrencyService;
        this.newAssetForm = new NewAssetForm(this, assetService);

        makeGrid();

        filter.setPlaceholder("Filter by ticker");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> filtering());

        priceCurrency.setItems(vsCurrencyService.getCurrenciesPriceLabels());
        valueCurrency.setItems(vsCurrencyService.getCurrenciesValueLabels());
        priceCurrency.addValueChangeListener(event -> priceCurrencyChanged());
        valueCurrency.addValueChangeListener(event -> valueCurrencyChanged());
        priceCurrency.setValue("Price in USD");
        valueCurrency.setValue("Value in PLN");
        priceCurrency.getStyle().set("cursor", "pointer");
        valueCurrency.getStyle().set("cursor", "pointer");

        Button addNewAssetButton = new Button("Add new asset");
        addNewAssetButton.getStyle().set("cursor", "pointer");
        addNewAssetButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(filter, priceCurrency, valueCurrency, addNewAssetButton);

        HorizontalLayout mainContent = new HorizontalLayout(grid, newAssetForm);
        mainContent.setSizeFull();
        grid.setSizeFull();
        add(toolbar, mainContent);
        setSizeFull();
        newAssetForm.setAsset(null);
        grid.asSingleSelect().addValueChangeListener(event -> newAssetForm.setAsset(grid.asSingleSelect().getValue()));

        addNewAssetButton.addClickListener(e -> {
            grid.asSingleSelect().clear();
            newAssetForm.setAsset(new Asset());
        });
        assetService.fetchAssets();
        refresh();
    }

    private void valueCurrencyChanged() {
        var value = valueCurrency.getValue();
        if (value == null || lockValueCurrencyChanged) return;
        vsCurrencyService.putCurrencyOnTop(value);
        vsCurrencyService.setCurrentValueCurrency(value);
        grid.getColumnByKey("valueIn")
                .setHeader("Value In [" + vsCurrencyService.getCurrentValueCurrency() + "]");

        grid.getColumnByKey("valueNow")
                .setHeader("Value Now [" + vsCurrencyService.getCurrentValueCurrency() + "]");

        lockValueCurrencyChanged = true;
        valueCurrency.setItems(vsCurrencyService.getCurrenciesValueLabels());
        valueCurrency.setValue(value);
        lockValueCurrencyChanged = false;
    }

    private void priceCurrencyChanged() {
        var value = priceCurrency.getValue();
        if (value == null || lockPriceCurrencyChanged) return;
        vsCurrencyService.putCurrencyOnTop(value);
        vsCurrencyService.setCurrentPriceCurrency(value);
        grid.getColumnByKey("avgPrice")
                .setHeader("Avg Price [" + vsCurrencyService.getCurrentPriceCurrency() + "]");
        grid.getColumnByKey("priceNow")
                .setHeader("Price Now [" + vsCurrencyService.getCurrentPriceCurrency() + "]");

        lockPriceCurrencyChanged = true;
        priceCurrency.setItems(vsCurrencyService.getCurrenciesPriceLabels());
        priceCurrency.setValue(value);
        lockPriceCurrencyChanged = false;
    }

    private void filtering() {
        grid.setItems(assetService.filterByTicker(filter.getValue()));
        refreshFooterRow();
    }

    public void refresh() {
        assetService.fetchPrices();
        grid.setItems(assetService.getAssets());
        refreshFooterRow();
    }

    public void refreshFooterRow() {
        footerRow.getCell(grid.getColumnByKey("ticker")).setText("Total:");
        footerRow.getCell(grid.getColumnByKey("valueIn")).setText(Asset.formatPriceString(totalValueIn()));
        footerRow.getCell(grid.getColumnByKey("valueNow")).setText(Asset.formatPriceString(totalValueNow()));
        footerRow.getCell(grid.getColumnByKey("profit")).setText(Asset.formatProfitString(totalProfit()));
    }

    private List<Asset> getAssetsFromGrid() {
        return grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
    }

    private BigDecimal totalValueIn() {
        return getAssetsFromGrid().stream()
                .map(asset -> new BigDecimal(asset.getValueIn()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalValueNow() {
        return getAssetsFromGrid().stream()
                .map(Asset::valueNow)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalProfit() {
        try {
            return totalValueNow()
                    .divide(totalValueIn(), MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(100))
                    .subtract(BigDecimal.valueOf(100));
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }

    private void makeGrid() {
        grid.addColumn(Asset::getTicker)
                .setHeader("Ticker")
                .setSortable(true)
                .setKey("ticker");
        grid.addColumn(Asset::getAmountFormatted)
                .setHeader("Amount")
                .setKey("amount")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getAmount())));
        grid.addColumn(Asset::avgPriceFormatted)
                .setHeader("Avg Price [" + vsCurrencyService.getCurrentPriceCurrency() + "]")
                .setKey("avgPrice")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.avgPrice())));
        grid.addColumn(Asset::getPriceNowFormatted)
                .setHeader("Price Now [" + vsCurrencyService.getCurrentPriceCurrency() + "]")
                .setKey("priceNow")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getPriceNow())));
        grid.addColumn(Asset::getValueInFormatted)
                .setHeader("Value In [" + vsCurrencyService.getCurrentValueCurrency() + "]")
                .setKey("valueIn")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getValueIn())));
        grid.addColumn(Asset::valueNowFormatted)
                .setHeader("Value Now [" + vsCurrencyService.getCurrentValueCurrency() + "]")
                .setKey("valueNow")
                .setComparator(Comparator.comparingDouble(asset -> asset.valueNow().doubleValue()));
        grid.addColumn(Asset::profitFormatted)
                .setHeader("Profit [+%]")
                .setKey("profit")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.profit())));
        footerRow = grid.appendFooterRow();
    }
}