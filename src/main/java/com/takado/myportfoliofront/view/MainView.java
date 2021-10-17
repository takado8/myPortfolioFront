package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.model.Asset;
import com.takado.myportfoliofront.service.AssetService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
    private final Grid<Asset> grid = new Grid<>();
    private final FooterRow footerRow;
    private final TextField filter = new TextField();
    private final NewAssetForm newAssetForm;

    public MainView(AssetService assetService) {
        this.assetService = assetService;
        this.newAssetForm = new NewAssetForm(this, assetService);

        grid.addColumn(Asset::getTicker)
                .setHeader("Ticker")
                .setSortable(true)
                .setKey("ticker");
        grid.addColumn(Asset::getAmountFormatted)
                .setHeader("Amount")
                .setKey("amount")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getAmount())));
        grid.addColumn(Asset::avgPriceFormatted)
                .setHeader("Avg Price [$]")
                .setKey("avgPrice")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.avgPrice())));
        grid.addColumn(Asset::getPriceNowFormatted)
                .setHeader("Price Now [$]")
                .setKey("priceNow")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getPriceNow())));
        grid.addColumn(Asset::getValueInFormatted)
                .setHeader("Value In [$]")
                .setKey("valueIn")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getValueIn())));
        grid.addColumn(Asset::valueNowFormatted)
                .setHeader("Value Now [$]")
                .setKey("valueNow")
                .setComparator(Comparator.comparingDouble(asset -> asset.valueNow().doubleValue()));

        grid.addColumn(Asset::profitFormatted)
                .setHeader("Profit [+%]")
                .setKey("profit")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.profit())));
        footerRow = grid.appendFooterRow();

        filter.setPlaceholder("Filter by ticker");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> filtering());
        Button addNewAssetButton = new Button("Add new asset");
        addNewAssetButton.getStyle().set("cursor", "pointer");
        addNewAssetButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout toolbar = new HorizontalLayout(filter, addNewAssetButton);
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
}