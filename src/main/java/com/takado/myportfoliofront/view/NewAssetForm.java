package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.domain.Trade;
import com.takado.myportfoliofront.service.AssetService;
import com.takado.myportfoliofront.service.TickerService;
import com.takado.myportfoliofront.service.TradeService;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.takado.myportfoliofront.config.Constants.*;

@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport(include = "tradesGridStyle", value = "./styles.css")
@CssImport(include = "italicText", value = "./styles.css")
@CssImport(include = "labelTradesStyle", value = "./styles.css")
@CssImport(include = "lumo-badge", value = "@vaadin/vaadin-lumo-styles/badge.js")
public class NewAssetForm extends FormLayout implements PageButtonClickedEventListener {

    private final ComboBox<String> tickerBox = new ComboBox<>("Ticker");
    private final TextField amountField = new TextField("Amount");
    private final TextField valueInField = new TextField("Value in");
    private final Grid<Trade> tradesGrid = new Grid<>(Trade.class, false);
    private final HorizontalLayout tradesGridLayout = new HorizontalLayout();
    private final TradesGridNavigationPanel tradesGridNavigationPanel;

    private final MainView mainView;
    private final AssetService assetService;
    private final TickerService tickerService;
    private final TradeService tradeService;
    private boolean isTradesGridMaximized = false;

    private final Button addButton = new Button(ADD_BUTTON_TEXT, new Icon(VaadinIcon.PLUS));
    private final Button subtractButton = new Button(SUBTRACT_BUTTON_TEXT, new Icon(VaadinIcon.MINUS));
    private final Button deleteButton = new Button(DELETE_BUTTON_TEXT);


    public NewAssetForm(MainView mainView, AssetService assetService, TickerService tickerService,
                        TradeService tradeService, TradesGridNavigationPanel tradesGridNavigationPanel) {
        this.mainView = mainView;
        this.assetService = assetService;
        this.tickerService = tickerService;
        this.tradeService = tradeService;
        this.tradesGridNavigationPanel = tradesGridNavigationPanel;
        tradesGridNavigationPanel.addListener(this);
        setupAmountField();
        setupValueField();
        setupTickerBox();
        setupTradesGrid();
        reloadTradesGridContent();
        HorizontalLayout buttons = setupButtonsLayout();
        HorizontalLayout labelTradesLayout = setupLabelTradesLayout();
        Label spacing = setupSpacing();

        tradesGridLayout.add(tradesGrid);
        tradesGridLayout.setSizeFull();
        add(tickerBox, amountField, valueInField, buttons, spacing, labelTradesLayout, tradesGridLayout);
    }

    public void callback() {
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
        labelTradesLayout.setAlignSelf(FlexComponent.Alignment.END, gridLabel);
        labelTradesLayout.setAlignSelf(FlexComponent.Alignment.CENTER, showAllButton);
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
        tickerBox.setItems(tickerService.getTickers());
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
        mainView.gridLayout.add(mainView.grid);
        tradesGridLayout.add(tradesGrid);
    }

    private void moveTradesGridToMainGridPosition() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(tradesGrid);
        layout.add(tradesGridNavigationPanel.initPagesButtonsPanel(countNbOfPages()));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainView.gridLayout.add(layout);
    }

    private int countNbOfPages() {
        var tickerString = this.tickerBox.getValue();
        if (tickerString != null && !tickerString.isBlank()) {
            Ticker ticker = tickerService.getTicker(tickerString);
            var tradeList = tradeService.fetchTradeList(ticker.getCoinId());
            if (tradeList.size() <= TRADE_POSITIONS_PER_PAGE + 1) {
                return 1;
            }
            return (int) Math.ceil((float) tradeList.size() / TRADE_POSITIONS_PER_PAGE);
        }
        return 1;
    }

    private void minimizeTradesGrid() {
        removeAllFromGridsLayouts();
        moveGridsToOriginalPosition();
        setButtonsNormalText();
        tradesGrid.setMinWidth(null);
        tradesGrid.removeColumnByKey("value");
        tradesGrid.removeColumnByKey("profit");
        tradesGrid.setClassName("tradesGridStyle");
        tradesGrid.setMaxHeight(TRADES_GRID_HEIGHT_MINIMIZED, Unit.PIXELS);
    }

    private void maximizeTradesGrid() {
        removeAllFromGridsLayouts();
        moveTradesGridToMainGridPosition();
        setButtonsShortText();
        tradesGrid.setMinWidth(TRADES_GRID_WIDTH_MAXIMIZED, Unit.PIXELS);
        tradesGrid.setClassName("styledBorderCorner");
        tradesGrid.setMaxHeight(MAIN_VIEW_GRID_HEIGHT, Unit.PIXELS);
        mainView.gridService.restoreTradesGridValueAndProfitColumns(tradesGrid);
    }

    private void removeAllFromGridsLayouts() {
        mainView.gridLayout.removeAll();
        tradesGridLayout.removeAll();
    }

    private void setButtonsNormalText() {
        deleteButton.setText(DELETE_BUTTON_TEXT);
        addButton.setText(ADD_BUTTON_TEXT);
        subtractButton.setText(SUBTRACT_BUTTON_TEXT);
    }

    private void setButtonsShortText() {
        deleteButton.setText(DELETE_BUTTON_TEXT_SHORT);
        addButton.setText(ADD_BUTTON_TEXT_SHORT);
        subtractButton.setText(SUBTRACT_BUTTON_TEXT_SHORT);
    }

    private void setupTradesGrid() {
        mainView.gridService.setupTradesGrid(tradesGrid);
    }

    public void reloadTradesGridContent() {
        var tickerString = this.tickerBox.getValue();
        if (tickerString != null && !tickerString.isBlank()) {
            Ticker ticker = tickerService.getTicker(tickerString);
            var tradeList = tradeService.fetchTradeList(ticker.getCoinId());
            List<Trade> itemsToSet;
            if (tradeList == null) {
                itemsToSet = Collections.emptyList();
            } else {
                tradeList.sort(Comparator.comparing(Trade::getDateTime).reversed());
                if (isTradesGridMaximized) {

                    if (tradeList.size() > TRADE_POSITIONS_PER_PAGE + 1) {
                        int currentPageNb = tradesGridNavigationPanel.getCurrentPageNb();
                        int startIdx = (currentPageNb - 1) * TRADE_POSITIONS_PER_PAGE + currentPageNb - 2;
                        int endIdx = currentPageNb * TRADE_POSITIONS_PER_PAGE + currentPageNb - 2;
                        int lastIdx = tradeList.size() - 1;
                        if (endIdx >= lastIdx) {
                            endIdx = lastIdx;
                        }
                        if (startIdx < 0) {
                            startIdx = 0;
                        }
                        itemsToSet = tradeList.subList(startIdx, endIdx + 1);
                    } else {
                        itemsToSet = tradeList;
                    }
                } else {
                    itemsToSet = tradeList.size() > 3 ? tradeList.subList(0, 3) : tradeList;
                }
            }
            tradesGrid.setItems(itemsToSet);
        }
    }

    private void addToAssetButtonClicked() {
        if (fieldsAreEmpty()) return;

        var ticker = this.tickerBox.getValue();
        var amount = this.amountField.getValue();
        var valueIn = this.valueInField.getValue();
        Long userId = mainView.getUser().getId();
        Asset asset = assetService.findByTicker(ticker);
        Trade trade = new Trade(null, userId, tickerService.getTicker(ticker), amount, valueIn, Trade.Type.BID);
        if (asset == null) {
            if (userId != null) {
                createAsset(ticker, userId, amount, valueIn);
            }
        } else {
            addToAssetPosition(asset, amount, valueIn);
        }
        tradeService.saveTrade(trade);
        cleanupInputFields();
        mainView.reloadAssetsAndPrices();
    }

    private void subtractFromAssetButtonClicked() {
        if (fieldsAreEmpty()) return;

        var ticker = tickerBox.getValue();
        Asset asset = assetService.findByTicker(ticker);

        if (asset != null) {
            var amount = amountField.getValue();
            var valueIn = valueInField.getValue();
            Trade trade = new Trade(null, mainView.getUser().getId(), asset.getTicker(), amount, valueIn,
                    Trade.Type.ASK);
            subtractFromAssetPosition(asset, amount, valueIn);
            tradeService.saveTrade(trade);
            cleanupInputFields();
            mainView.reloadAssetsAndPrices();
        }
    }

    private void createAsset(String ticker, Long userId, String amount, String valueIn) {
        assetService.createAsset(ticker, userId, amount, valueIn);
    }

    private void deleteAsset() {
        String ticker = this.tickerBox.getValue();
        if (ticker != null && !ticker.isBlank()) {
            assetService.deleteAsset(ticker);
            mainView.reloadAssetsAndPrices();
            cleanupAll();
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
