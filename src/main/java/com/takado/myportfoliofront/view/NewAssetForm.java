package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.model.Asset;
import com.takado.myportfoliofront.service.AssetService;
import com.takado.myportfoliofront.service.TickerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;

public class NewAssetForm extends FormLayout {
    private final static String regexValidationPattern = "(?!0\\d)[0-9]*(?<=\\d+)\\.?[0-9]*";

    private final ComboBox<String> tickerBox = new ComboBox<>("Ticker");
    private final TextField amountField = new TextField("Amount");
    private final TextField valueInField = new TextField("Value in");
    private final MainView mainView;
    private final AssetService assetService;
    private final TickerService tickerService;


    public NewAssetForm(MainView mainView, AssetService assetService, TickerService tickerService) {
        this.mainView = mainView;
        this.assetService = assetService;
        this.tickerService = tickerService;
        amountField.setPattern(regexValidationPattern);
        amountField.setPreventInvalidInput(true);
        valueInField.setPattern(regexValidationPattern);
        valueInField.setPreventInvalidInput(true);
        tickerBox.setItems(tickerService.getTickers());
        tickerBox.getStyle().set("cursor", "pointer");
        tickerBox.setAllowCustomValue(false);

        Button addButton = new Button("Add to position", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(event -> addToAssetButtonClicked());
        addButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addButton.getStyle().set("cursor", "pointer");
        Button subtractButton = new Button("Subtract from position", new Icon(VaadinIcon.MINUS));
        subtractButton.addClickListener(event -> subtractFromAssetButtonClicked());
        subtractButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        subtractButton.getStyle().set("cursor", "pointer");
        Button deleteButton = new Button("Delete asset");
        HorizontalLayout buttons = new HorizontalLayout(addButton, subtractButton, deleteButton);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("cursor", "pointer");
        deleteButton.addClickListener(event -> deleteAsset());
        add(tickerBox, amountField, valueInField, buttons);
    }

    private void addToAssetButtonClicked() {
        if (fieldsAreEmpty()) return;

        var ticker = this.tickerBox.getValue();
        var amount = this.amountField.getValue();
        var valueIn = this.valueInField.getValue();
        Asset asset = assetService.findByTicker(ticker);

        if (asset == null) {
            Long userId = mainView.getUser().getId();
            if (userId != null) {
                createAsset(ticker, userId, amount, valueIn);
            }
        } else {
            addToAssetPosition(asset, amount, valueIn);
        }
        refresh();
    }

    private void subtractFromAssetButtonClicked() {
        if (fieldsAreEmpty()) return;

        var ticker = this.tickerBox.getValue();
        Asset asset = assetService.findByTicker(ticker);

        if (asset != null) {
            var amount = this.amountField.getValue();
            var valueIn = this.valueInField.getValue();
            subtractFromAssetPosition(asset, amount, valueIn);
            refresh();
        }
    }

    private void createAsset(String ticker, Long userId, String amount, String valueIn) {
        assetService.createAsset(ticker, userId, amount, valueIn);
    }

    private void deleteAsset() {
        String ticker = this.tickerBox.getValue();
        if (ticker != null && !ticker.isBlank()) {
            assetService.deleteAsset(ticker);
            refresh();
        }
    }

    private void addToAssetPosition(Asset asset, String amount, String valueIn) {
        asset.setAmount((new BigDecimal(asset.getAmount()).add(new BigDecimal(amount))).toString());
        asset.setValueIn((new BigDecimal(asset.getValueIn()).add(new BigDecimal(valueIn))).toString());
        assetService.updateAsset(asset);
    }

    private void subtractFromAssetPosition(Asset asset, String amount, String valueIn) {
        asset.setAmount((new BigDecimal(asset.getAmount()).subtract(new BigDecimal(amount))).toString());
        asset.setValueIn((new BigDecimal(asset.getValueIn()).subtract(new BigDecimal(valueIn))).toString());
        assetService.updateAsset(asset);
    }

    private boolean fieldsAreEmpty() {
        var ticker = this.tickerBox.getValue();
        var amount = this.amountField.getValue();
        var valueIn = this.valueInField.getValue();
        return amount == null || valueIn == null || ticker == null || amount.isBlank() || valueIn.isBlank();
    }

    private void refresh() {
        mainView.refresh();
        setAsset(null);
        this.valueInField.setValue("");
        this.amountField.setValue("");
        this.tickerBox.setValue(null);
    }

    public void setAsset(Asset asset) {
        if (asset == null) {
            setVisible(false);
        } else {
            setVisible(true);
            if (asset.getTicker() != null) {
                tickerBox.setValue(asset.getTicker().getTicker());
                tickerBox.setEnabled(false);
            } else {
                if (!tickerBox.isEnabled()) {
                    tickerBox.setEnabled(true);
                }
            }
            amountField.focus();
        }
    }
}
