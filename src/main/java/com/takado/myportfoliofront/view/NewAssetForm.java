package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.model.Asset;
import com.takado.myportfoliofront.model.Ticker;
import com.takado.myportfoliofront.service.AssetService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class NewAssetForm extends FormLayout {
    private ComboBox<Ticker> ticker = new ComboBox<>("Ticker");
    private TextField amount = new TextField("Amount");
    private TextField valueIn = new TextField("Value in");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Binder<Asset> binder = new Binder<>(Asset.class);

    private final MainView mainView;
    private final AssetService assetService = AssetService.getInstance();


    public NewAssetForm(MainView mainView) {
        this.mainView = mainView;
        amount.setPattern("[0-9]*(?<=\\d+)\\.?[0-9]*");
        amount.setPreventInvalidInput(true);
        ticker.setItems(Ticker.values());
        save.addClickListener(event -> save());
        delete.addClickListener(event -> delete());
        HorizontalLayout buttons = new HorizontalLayout(save, delete);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        binder.bindInstanceFields(this);
        add(ticker, amount, valueIn, buttons);

    }

    private void save() {
        var amount = this.amount.getValue();
        var valueIn = this.valueIn.getValue();
        var ticker = this.ticker.getValue();
        if (amount == null || valueIn == null || ticker == null) return;

        Asset asset = new Asset(ticker, amount, valueIn);
        assetService.addAsset(asset);
        mainView.refresh();
        setAsset(null);
    }

    private void delete() {
        var ticker = this.ticker.getValue();
        if (ticker == null) return;

        Asset asset = binder.getBean();
        assetService.delete(asset);
        mainView.refresh();
        setAsset(null);
    }

    public void setAsset(Asset asset) {
        binder.setBean(asset);

        if (asset == null) {
            setVisible(false);
        } else {
            setVisible(true);

            if (asset.getTicker() != null){
                ticker.setEnabled(false);
            }
            else {
                if (!ticker.isEnabled()){
                    ticker.setEnabled(true);
                }
            }
            amount.focus();
        }
    }
}
