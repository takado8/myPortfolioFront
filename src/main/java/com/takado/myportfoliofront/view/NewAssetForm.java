package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.model.Asset;
import com.takado.myportfoliofront.model.Ticker;
import com.takado.myportfoliofront.service.AssetService;
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

    private final ComboBox<Ticker> ticker = new ComboBox<>("Ticker");
    private final TextField amount = new TextField("Amount");
    private final TextField valueIn = new TextField("Value in");
    //    private Binder<Asset> binder = new Binder<>(Asset.class);
    private final MainView mainView;
    private final AssetService assetService = AssetService.getInstance();


    public NewAssetForm(MainView mainView) {
        this.mainView = mainView;
        amount.setPattern(regexValidationPattern);
        amount.setPreventInvalidInput(true);
        valueIn.setPattern(regexValidationPattern);
        valueIn.setPreventInvalidInput(true);
        ticker.setItems(Ticker.values());
        ticker.getStyle().set("cursor", "pointer");
        Button addButton = new Button("Add to asset", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(event -> addToAsset());
        addButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addButton.getStyle().set("cursor", "pointer");
        Button subtractButton = new Button("Subtract from asset", new Icon(VaadinIcon.MINUS));
        subtractButton.addClickListener(event -> subtract());
        subtractButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        subtractButton.getStyle().set("cursor", "pointer");
        Button deleteButton = new Button("Delete asset");
        HorizontalLayout buttons = new HorizontalLayout(addButton, subtractButton, deleteButton);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("cursor", "pointer");
        deleteButton.addClickListener(event -> deleteAsset());
//        binder.bindInstanceFields(this);
        add(ticker, amount, valueIn, buttons);

    }

    private void addToAsset() {
        var ticker = this.ticker.getValue();
        var amount = this.amount.getValue();
        var valueIn = this.valueIn.getValue();
        if (amount == null || valueIn == null || ticker == null || amount.isEmpty() || valueIn.isEmpty()) return;
        Asset asset = assetService.findByTicker(ticker.getString());
        if (asset == null) {
            asset = new Asset(ticker, amount, valueIn);
            assetService.addAsset(asset);
        } else {
            asset.setAmount((new BigDecimal(asset.getAmount()).add(new BigDecimal(amount))).toString());
            asset.setValueIn((new BigDecimal(asset.getValueIn()).add(new BigDecimal(valueIn))).toString());
        }
        mainView.refresh();
        setAsset(null);
        this.valueIn.setValue("");
        this.amount.setValue("");
        this.ticker.setValue(null);
    }

    private void subtract() {
        var ticker = this.ticker.getValue();
        var amount = this.amount.getValue();
        var valueIn = this.valueIn.getValue();
        if (amount == null || valueIn == null || ticker == null || amount.isEmpty() || valueIn.isEmpty()) return;
        Asset asset = assetService.findByTicker(ticker.getString());
        if (asset == null) {
            return;
        } else {
            asset.setAmount((new BigDecimal(asset.getAmount()).subtract(new BigDecimal(amount))).toString());
            asset.setValueIn((new BigDecimal(asset.getValueIn()).subtract(new BigDecimal(valueIn))).toString());
        }
        mainView.refresh();
        setAsset(null);
        this.valueIn.setValue("");
        this.amount.setValue("");
        this.ticker.setValue(null);
    }

    private void deleteAsset() {
        var ticker = this.ticker.getValue();
        if (ticker != null)
            assetService.delete(ticker);
        mainView.refresh();
        setAsset(null);
        this.valueIn.setValue("");
        this.amount.setValue("");
        this.ticker.setValue(null);
    }

    public void setAsset(Asset asset) {
//        binder.setBean(asset);
        if (asset == null) {
            setVisible(false);
        } else {
            setVisible(true);
            if (asset.getTicker() != null) {
                ticker.setValue(asset.getTicker());
                ticker.setEnabled(false);
            } else {
                if (!ticker.isEnabled()) {
                    ticker.setEnabled(true);
                }
            }
            amount.focus();
        }
    }
}
