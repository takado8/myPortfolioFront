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


@Route
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends VerticalLayout {
    private final AssetService assetService;
    private final Grid<Asset> grid = new Grid<>(Asset.class);
    private final TextField filter = new TextField();
    private final NewAssetForm newAssetForm;

    public MainView(AssetService assetService) {
        this.assetService = assetService;
        this.newAssetForm = new NewAssetForm(this, assetService);

        grid.setColumns("ticker", "amount", "valueIn", "valueNow", "profit", "avgPrice", "priceNow");
        grid.getColumnByKey("valueIn").setHeader("Value In [$]");
        grid.getColumnByKey("valueNow").setHeader("Value Now [$]");
        grid.getColumnByKey("profit").setHeader("Profit [+%]");
        grid.getColumnByKey("avgPrice").setHeader("Avg Price [$]");
        grid.getColumnByKey("priceNow").setHeader("Price Now [$]");


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