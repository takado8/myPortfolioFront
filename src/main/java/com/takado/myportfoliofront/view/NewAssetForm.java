package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.control.NewAssetFormControl;
import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.domain.Trade;
import com.takado.myportfoliofront.service.grid.GridLayoutManager;
import com.takado.myportfoliofront.service.UserService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.takado.myportfoliofront.config.Constants.*;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.*;

@UIScope
@Component
@RequiredArgsConstructor
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport(include = "tradesGridStyle", value = "./styles.css")
@CssImport(include = "italicText", value = "./styles.css")
@CssImport(include = "noPaddingOrMargin", value = "./styles.css")
@CssImport(include = "labelTradesStyle", value = "./styles.css")
@CssImport(include = "lumo-badge", value = "@vaadin/vaadin-lumo-styles/badge.js")
public class NewAssetForm extends FormLayout implements PageButtonClickedEventListener {

    private final ComboBox<String> tickerBox = new ComboBox<>("Ticker");
    private final TextField amountField = new TextField("Amount");
    private final TextField valueInField = new TextField("Value in");
    private final Button addButton = new Button(ADD_BUTTON_TEXT_SHORT, new Icon(VaadinIcon.PLUS));
    private final Button subtractButton = new Button(SUBTRACT_BUTTON_TEXT_SHORT, new Icon(VaadinIcon.MINUS));
    private final Button deleteButton = new Button(DELETE_BUTTON_TEXT_SHORT);
    private final Grid<Trade> tradesGrid = new Grid<>(Trade.class, false);
    private final HorizontalLayout tradesGridLayout = new HorizontalLayout();
    private final VerticalLayout mainLayout = new VerticalLayout();

    private final TradesGridNavigationPanel tradesGridNavigationPanel;
    private final NewAssetFormControl control;
    private final GridLayoutManager mainViewGridLayoutManager;

    private boolean isTradesGridMaximized = false;

    @PostConstruct
    private void initialize() {
        tradesGridNavigationPanel.addListener(this);
        setupAmountField();
        setupValueField();
        setupTickerBox();
        setupTradesGrid();
        setMaxWidth(NEW_ASSET_FORM_MAX_WIDTH, Unit.VW);
        setMinWidth(NEW_ASSET_FORM_MIN_WIDTH, Unit.VW);
        reloadTradesGridContent();
        HorizontalLayout buttons = setupButtonsLayout();
        HorizontalLayout labelTradesLayout = setupLabelTradesLayout();
        Label spacing = setupSpacing();
        tradesGridLayout.add(tradesGrid);
        tradesGridLayout.setSizeFull();
        mainLayout.add(tickerBox, amountField, valueInField, buttons, spacing, labelTradesLayout, tradesGridLayout);
        mainLayout.setJustifyContentMode(JustifyContentMode.START);
        mainLayout.setSpacing(false);
        mainLayout.setAlignItems(Alignment.STRETCH);
        mainLayout.setClassName("noPaddingOrMargin");
        mainLayout.setMaxWidth(NEW_ASSET_FORM_MAX_WIDTH, Unit.VW);
        mainLayout.setMinWidth(NEW_ASSET_FORM_MIN_WIDTH, Unit.VW);
        add(mainLayout);
    }

    @Override
    public void pageButtonClickedCallback() {
        reloadTradesGridContent();
    }

    private Label setupSpacing() {
        return new Label() {{
            setHeight(6F, Unit.PIXELS);
        }};
    }

    private HorizontalLayout setupLabelTradesLayout() {
        HorizontalLayout labelTradesLayout = new HorizontalLayout();
        Label gridLabel = new Label();
        gridLabel.setText("Recent History");
        gridLabel.setHeight(30F, Unit.PIXELS);

        Span showAllButton = new Span("Show All");
        showAllButton.setClassName("italicText");
        showAllButton.getElement().getThemeList().add("badge");
        showAllButton.getStyle().set("cursor", "pointer");
        showAllButton.addClickListener(event -> showAllTradesButtonClicked());
        showAllButton.setMaxHeight(22F, Unit.PIXELS);

        labelTradesLayout.setClassName("labelTradesStyle");
        labelTradesLayout.add(gridLabel, showAllButton);
        labelTradesLayout.setAlignSelf(Alignment.END, gridLabel);
        labelTradesLayout.setAlignSelf(Alignment.CENTER, showAllButton);
        labelTradesLayout.setSizeFull();
        return labelTradesLayout;
    }

    private HorizontalLayout setupButtonsLayout() {
        addButton.addClickListener(event -> addToAssetButtonClicked());
        addButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addButton.getStyle().set("cursor", "pointer");
        subtractButton.addClickListener(event -> subtractFromAssetButtonClicked());
        subtractButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        subtractButton.getStyle().set("cursor", "pointer");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("cursor", "pointer");
        deleteButton.addClickListener(event -> deleteAsset());
        return new HorizontalLayout(addButton, subtractButton, deleteButton);
    }

    private void setupTickerBox() {
        tickerBox.setItems(control.getTickers());
        tickerBox.getStyle().set("cursor", "pointer");
        tickerBox.setAllowCustomValue(false);
    }

    private void setupAmountField() {
        amountField.setPattern(REGEX_VALIDATION_PATTERN);
        amountField.setPreventInvalidInput(true);
    }

    private void setupValueField() {
        valueInField.setPattern(REGEX_VALIDATION_PATTERN);
        valueInField.setPreventInvalidInput(true);
    }

    void showAllTradesButtonClicked() {
        if (isTradesGridMaximized) {
            isTradesGridMaximized = false;
            minimizeTradesGrid();
        } else {
            isTradesGridMaximized = true;
            maximizeTradesGrid();
        }
        reloadTradesGridContent();
    }

    private void moveGridsToOriginalPosition() {
        mainViewGridLayoutManager.gridLayoutBringBackMainGrid();
        tradesGridLayout.add(tradesGrid);
    }

    private void moveTradesGridToMainGridPosition() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(tradesGrid);
        layout.add(tradesGridNavigationPanel.initPagesButtonsPanel(countNbOfPages()));
        layout.setAlignItems(Alignment.CENTER);
        mainViewGridLayoutManager.gridLayoutAdd(layout);
    }

    private int countNbOfPages() {
        var tickerString = this.tickerBox.getValue();
        return control.countNbOfPagesInTradesGrid(tickerString);
    }

    private void minimizeTradesGrid() {
        removeAllFromGridsLayouts();
        moveGridsToOriginalPosition();
        tradesGrid.setMinWidth(null);
        tradesGrid.removeColumnByKey("value");
        tradesGrid.removeColumnByKey("profit");
        tradesGrid.setClassName("tradesGridStyle");
        tradesGrid.setMaxHeight(TRADES_GRID_HEIGHT_MINIMIZED, Unit.VH);
        mainLayout.setMinWidth(NEW_ASSET_FORM_MIN_WIDTH, Unit.VW);
        mainLayout.setMaxWidth(NEW_ASSET_FORM_MAX_WIDTH, Unit.VW);
        setMinWidth(NEW_ASSET_FORM_MIN_WIDTH, Unit.VW);
        setMaxWidth(NEW_ASSET_FORM_MAX_WIDTH, Unit.VW);
    }

    private void maximizeTradesGrid() {
        removeAllFromGridsLayouts();
        moveTradesGridToMainGridPosition();
        tradesGrid.setClassName("styledBorderCorner");
        tradesGrid.setMinWidth(TRADES_GRID_WIDTH_MAXIMIZED, Unit.VW);
        tradesGrid.setMaxWidth(TRADES_GRID_MAX_WIDTH_MAXIMIZED, Unit.VW);
        tradesGrid.setMaxHeight(MAIN_VIEW_GRID_MAX_HEIGHT, Unit.VH);
        setMaxWidth("unset");
        setMinWidth(NEW_ASSET_FORM_MIN_WIDTH_SHORT, Unit.VW);
        mainLayout.setMaxWidth("unset");
        mainLayout.setMinWidth(NEW_ASSET_FORM_MIN_WIDTH_SHORT, Unit.VW);
        control.restoreTradesGridValueAndProfitColumns(tradesGrid);
        control.setupTradesPrices();
        reloadTradesGridContent();
    }

    private void removeAllFromGridsLayouts() {
        tradesGridLayout.removeAll();
        mainViewGridLayoutManager.gridLayoutRemoveAll();
    }

    private void setupTradesGrid() {
        control.setupTradesGrid(tradesGrid);
    }

    public void reloadTradesGridContent() {
        var tickerString = this.tickerBox.getValue();
        List<Trade> itemsToSet = control.getTradeItemsToSet(tickerString, isTradesGridMaximized,
                tradesGridNavigationPanel.getCurrentPageNb());
        tradesGrid.setItems(itemsToSet);
    }

    private void refreshAfterAssetChanges(){
        control.setupTradesAndAssetsPrices();
        control.reloadAssets();
        reloadTradesGridContent();
        setVisible(true);
    }

    private void addToAssetButtonClicked() {
        if (fieldsAreEmpty()) return;
        var ticker = this.tickerBox.getValue();
        var amount = this.amountField.getValue();
        var valueIn = this.valueInField.getValue();
        Long userId = control.getUserId();
        if (userId != null) {
            control.addToAsset(ticker, amount, valueIn, userId);
            cleanupInputFields();
            refreshAfterAssetChanges();
        } else {
            Notification.show("User id is unknown.");
        }
    }

    private void subtractFromAssetButtonClicked() {
        if (fieldsAreEmpty()) return;
        var ticker = tickerBox.getValue();
        var amount = amountField.getValue();
        var valueIn = valueInField.getValue();
        var userId = control.getUserId();
        if (userId != null) {
            var result = control.subtractFromAsset(ticker, amount, valueIn, userId);
            if (result) {
                cleanupInputFields();
                refreshAfterAssetChanges();
            }
        } else {
            Notification.show("User id is unknown.");
        }
    }

    private void deleteAsset() {
        String ticker = this.tickerBox.getValue();
        boolean result = control.deleteAsset(ticker);
        if (result) {
            refreshAfterAssetChanges();
            cleanupAll();
        }
    }

    private boolean fieldsAreEmpty() {
        var ticker = tickerBox.getValue();
        var amount = amountField.getValue();
        var valueIn = valueInField.getValue();
        return amount == null || valueIn == null || ticker == null || amount.isBlank() || valueIn.isBlank();
    }

    private void cleanupInputFields() {
        valueInField.clear();
        amountField.clear();
        valueInField.setInvalid(false);
        amountField.setInvalid(false);
    }

    private void cleanupAll() {
        setAsset(null);
        cleanupInputFields();
        tickerBox.setValue(null);
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
        reloadTradesGridContent();
    }
}
