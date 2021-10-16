package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.model.Asset;
import com.takado.myportfoliofront.service.AssetService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.Comparator;


@Route
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends VerticalLayout {
    private final AssetService assetService;
    private final Grid<Asset> grid = new Grid<>();
    private final TextField filter = new TextField();
    private final NewAssetForm newAssetForm;

    public MainView(AssetService assetService) {
        this.assetService = assetService;
        this.newAssetForm = new NewAssetForm(this, assetService);

        grid.addColumn(Asset::getTicker).setHeader("Ticker");
        grid.addColumn(Asset::getAmountFormatted).setHeader("Amount").setComparator(
                Comparator.comparingDouble(asset -> Double.parseDouble(asset.getAmount())));
        grid.addColumn(Asset::getValueInFormatted).setHeader("Value In [$]").setComparator(
                Comparator.comparingDouble(asset -> Double.parseDouble(asset.getValueIn())));

        grid.addColumn(Asset::valueNowFormatted).setHeader("Value Now [$]").setComparator(
                Comparator.comparingDouble(asset -> asset.valueNow().doubleValue()));
        grid.addColumn(Asset::profitFormatted).setHeader("Profit [+%]").setComparator(
                Comparator.comparingDouble(asset -> Double.parseDouble(asset.profit())));

        grid.addColumn(Asset::avgPriceFormatted).setHeader("Avg Price [$]").setComparator(
                Comparator.comparingDouble(asset -> Double.parseDouble(asset.avgPrice())));
        grid.addColumn(Asset::getPriceNowFormatted).setHeader("Price Now [$]").setComparator(
                Comparator.comparingDouble(asset -> Double.parseDouble(asset.getPriceNow())));


        filter.setPlaceholder("Filter by ticker");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> update());
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

    private void update() {
        grid.setItems(assetService.findByTicker(filter.getValue()));
    }

    public void refresh() {
        grid.setItems(assetService.getAssets());
    }
}