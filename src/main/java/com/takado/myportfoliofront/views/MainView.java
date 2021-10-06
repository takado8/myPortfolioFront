package com.takado.myportfoliofront.views;

import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.service.AssetService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;


@Route
public class MainView extends VerticalLayout {
    private final AssetService assetService = AssetService.getInstance();
    private Grid<Asset> grid = new Grid<>(Asset.class);
    private TextField filter = new TextField();
    private NewAssetForm form = new NewAssetForm(this);
    private Button addNewAssetButton = new Button("Add new asset");

    public MainView() {
        grid.setColumns("ticker", "amount", "valueIn");
        filter.setPlaceholder("Filter by ticker");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> update());
        HorizontalLayout toolbar = new HorizontalLayout(filter, addNewAssetButton);
        HorizontalLayout mainContent = new HorizontalLayout(grid, form);
        mainContent.setSizeFull();
        grid.setSizeFull();
        add(toolbar, mainContent);
        setSizeFull();
        form.setAsset(null);
        grid.asSingleSelect().addValueChangeListener(event -> form.setAsset(grid.asSingleSelect().getValue()));
        addNewAssetButton.addClickListener(e -> {
            grid.asSingleSelect().clear(); //"czyścimy" zaznaczenie
            form.setAsset(new Asset());      //dodajemy nowy obiekt do formularza
        });

        refresh();
    }
    private void update() {
        grid.setItems(assetService.findByTicker(filter.getValue()));
    }
    public void refresh() {
        grid.setItems(assetService.getAssets());
    }
}