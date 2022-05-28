package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.domain.UserDto;
import com.takado.myportfoliofront.service.*;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.takado.myportfoliofront.service.PriceFormatter.formatPriceString;
import static com.takado.myportfoliofront.service.PriceFormatter.formatProfitString;

@Push
@Route("")
@PageTitle("myPortfolio")
@CssImport(include = "styledBorderCorner", value = "./styles.css")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends VerticalLayout {
    private final AssetService assetService;
    private final PricesService pricesService;
    private final TradeService tradeService;
    private final VsCurrencyService vsCurrencyService;
    protected GridValueProvider gridValueProvider;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    public final Grid<Asset> grid = new Grid<>();
    public HorizontalLayout gridLayout = new HorizontalLayout();
    private FooterRow footerRow;
    private final TextField filter = new TextField();
    private final NewAssetForm newAssetForm;
    private final Select<String> priceCurrency = new Select<>();
    private final Select<String> valueCurrency = new Select<>();
    private boolean lockPriceCurrencyChanged = false;
    private boolean lockValueCurrencyChanged = false;
    private UserDto user;

    public MainView(AssetService assetService, GridValueProvider gridValueProvider,
                    AuthenticationService authenticationService, UserService userService,
                    TickerService tickerService, TradeService tradeService, PricesService pricesService) {
        this.assetService = assetService;
        this.tradeService = tradeService;
        this.pricesService = pricesService;
        this.vsCurrencyService = VsCurrencyService.getInstance();
        this.gridValueProvider = gridValueProvider;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.newAssetForm = new NewAssetForm(this, assetService, tickerService, tradeService);

        makeGrid();
        HorizontalLayout toolbar = makeToolbar();
        gridLayout.add(grid);
        gridLayout.setSizeFull();
        HorizontalLayout mainContent = new HorizontalLayout(gridLayout, newAssetForm);
        mainContent.setSizeFull();
        add(toolbar, mainContent);
        setSizeFull();
        newAssetForm.setAsset(null);

        user = fetchUser();
        if (user == null) {
            user = createUserAccount();
            displayWelcomeMessage();
        }
        assetService.fetchAssets(user.getId());
        refresh();
    }

    public UserDto fetchUser() {
        var user = userService.getUser(authenticationService.getUserEmail());
        return user == null || user.getId() == null ? null : user;
    }

    public UserDto createUserAccount() {
        return userService.createUser(authenticationService.getUserEmail(), authenticationService.getUserNameHash(),
                authenticationService.getUserDisplayedName(),
                assetService.getAssets().stream().map(Asset::getId).collect(Collectors.toList()));
    }

    public void valueCurrencyChanged() {
        String valueCurrency = this.valueCurrency.getValue();
        if (valueCurrency == null || lockValueCurrencyChanged) return;
        vsCurrencyService.putCurrencyOnTop(valueCurrency);
        gridValueProvider.setCurrentValueCurrency(vsCurrencyService.getCurrencyFromLabel(valueCurrency));

        grid.getColumnByKey("valueIn")
                .setHeader("Value In [" + gridValueProvider.getCurrentValueCurrency() + "]");

        grid.getColumnByKey("valueNow")
                .setHeader("Value Now [" + gridValueProvider.getCurrentValueCurrency() + "]");

        lockValueCurrencyChanged = true;
        this.valueCurrency.setItems(vsCurrencyService.getCurrenciesValueLabels());
        this.valueCurrency.setValue(valueCurrency);
        lockValueCurrencyChanged = false;
        refresh();
    }

    public void priceCurrencyChanged() {
        String priceCurrency = this.priceCurrency.getValue();
        if (priceCurrency == null || lockPriceCurrencyChanged) return;
        vsCurrencyService.putCurrencyOnTop(priceCurrency);
        gridValueProvider.setCurrentPriceCurrency(vsCurrencyService.getCurrencyFromLabel(priceCurrency));
        grid.getColumnByKey("avgPrice")
                .setHeader("Avg Price [" + gridValueProvider.getCurrentPriceCurrency() + "]");
        grid.getColumnByKey("priceNow")
                .setHeader("Price Now [" + gridValueProvider.getCurrentPriceCurrency() + "]");

        lockPriceCurrencyChanged = true;
        this.priceCurrency.setItems(vsCurrencyService.getCurrenciesPriceLabels());
        this.priceCurrency.setValue(priceCurrency);
        lockPriceCurrencyChanged = false;
        refresh();
    }

    public void filtering() {
        grid.setItems(assetService.filterByTicker(filter.getValue()));
        refreshFooterRow();
    }

    @Scheduled(fixedDelay = 20000L)
    public void scheduledRefresh() {
        if (newAssetForm.isVisible() || !filter.getValue().isBlank()) return;
        try {
            getUI().ifPresent(ui -> {
                if (ui.isAttached())
                    ui.access(() -> {
                        refresh();
                        try {
                            Thread.sleep(300L);
                        } catch (InterruptedException ignored) {
                        }
                    });
            });
        } catch (IllegalStateException | NullPointerException ignored) {
        } catch (UIDetachedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void refresh() {
        try {
            var newAssetFormVisible = newAssetForm.isVisible();
            var prices = pricesService.fetchPrices(assetService.getCoinsIds());
            assetService.setPrices(prices);
            grid.setItems(assetService.getAssets());
            refreshFooterRow();
            tradeService.setPrices(prices);
            newAssetForm.refreshTradesGrid();
            newAssetForm.setVisible(newAssetFormVisible);
        } catch (NullPointerException ignored) {
        }
    }

    public void refreshFooterRow() {
        footerRow.getCell(grid.getColumnByKey("ticker")).setText("Total:");
        footerRow.getCell(grid.getColumnByKey("valueIn")).setText(formatPriceString(totalValueIn()));
        footerRow.getCell(grid.getColumnByKey("valueNow")).setText(formatPriceString(totalValueNow()));
        var columnProfit = grid.getColumnByKey("profit");
        if (columnProfit != null) {
            footerRow.getCell(columnProfit).setComponent(getTotalProfitBadge());
        }
    }

    private Span getTotalProfitBadge() {
        var totalProfit = totalProfit();
        Span badge = new Span(formatProfitString(totalProfit) + "%");
        badge.getElement().getThemeList().add("badge " + (totalProfit.doubleValue() >= 0 ? "success" : "error"));
        return badge;
    }

    public List<Asset> getAssetsFromGrid() {
        return grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
    }

    public BigDecimal totalValueIn() {
        return getAssetsFromGrid().stream()
                .map(gridValueProvider::valueIn)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalValueNow() {
        return getAssetsFromGrid().stream()
                .map(gridValueProvider::valueNow)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalProfit() {
        try {
            return totalValueNow()
                    .divide(totalValueIn(), MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(100))
                    .subtract(BigDecimal.valueOf(100));
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }

    public void makeGrid() {
        grid.setClassName("styledBorderCorner");
        grid.addColumn(gridValueProvider::getTicker)
                .setHeader("Ticker")
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setKey("ticker");
        grid.addColumn(gridValueProvider::getAmount)
                .setHeader("Amount")
                .setKey("amount")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getAmount())));
        grid.addColumn(gridValueProvider::getAvgPrice)
                .setHeader("Avg Price [" + gridValueProvider.getCurrentPriceCurrency() + "]")
                .setKey("avgPrice")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(gridValueProvider.avgPrice(asset))));
        grid.addColumn(gridValueProvider::getPriceNow)
                .setHeader("Price Now [" + gridValueProvider.getCurrentPriceCurrency() + "]")
                .setKey("priceNow")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> asset.getPriceNow().doubleValue()));
        grid.addColumn(gridValueProvider::getValueIn)
                .setHeader("Value In [" + gridValueProvider.getCurrentValueCurrency() + "]")
                .setKey("valueIn")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getValueIn())));
        grid.addColumn(gridValueProvider::getValueNow)
                .setHeader("Value Now [" + gridValueProvider.getCurrentValueCurrency() + "]")
                .setKey("valueNow")
                .setTextAlign(ColumnTextAlign.END)
                .setComparator(Comparator.comparingDouble(asset -> gridValueProvider.valueNow(asset).doubleValue()));
        grid.addColumn(profitComponentRenderer())
                .setHeader("Profit [+%]")
                .setKey("profit")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(gridValueProvider.profit(asset))));
        grid.asSingleSelect().addValueChangeListener(event -> gridItemSelected());
        grid.setSizeFull();
        grid.setMaxHeight(476F, Unit.PIXELS);
        footerRow = grid.appendFooterRow();
    }

    private void switchProfitColumnVisibility() {
        var profitColumn = grid.getColumnByKey("profit");
        if (newAssetForm.isVisible() && profitColumn != null) {
            grid.removeColumn(profitColumn);
        } else if (profitColumn == null && !newAssetForm.isVisible()) {
            grid.addColumn(profitComponentRenderer())
                    .setHeader("Profit [+%]")
                    .setKey("profit")
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setComparator(Comparator.comparingDouble(asset ->
                            Double.parseDouble(gridValueProvider.profit(asset))));
        }
        refreshFooterRow();
    }

    private void gridItemSelected() {
        newAssetForm.setAsset(grid.asSingleSelect().getValue());
        switchProfitColumnVisibility();
    }

    public HorizontalLayout makeToolbar() {
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
        addNewAssetButton.addClickListener(e -> {
            grid.asSingleSelect().clear();
            newAssetForm.setAsset(new Asset());
            switchProfitColumnVisibility();
        });

        Button logoutButton = new Button("Logout");
        logoutButton.getStyle().set("cursor", "pointer");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);

        logoutButton.addClickListener(e ->
                getUI().ifPresent(page -> page.getPage().setLocation("http://localhost:8080/logout")));

        String userName = authenticationService.getUserEmail();
        Dialog dialog = new Dialog();
        dialog.add(new Text("User email: " + userName));

        Button showUserButton = new Button("Show user");
        showUserButton.getStyle().set("cursor", "pointer");
        showUserButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
        showUserButton.addClickListener(e -> dialog.open());

        return new HorizontalLayout(filter, priceCurrency, valueCurrency,
                addNewAssetButton, logoutButton, showUserButton);
    }

    private final SerializableBiConsumer<Span, Asset> profitComponentUpdater = (span, asset) -> {
        String theme = String
                .format("badge %s", Double.parseDouble(gridValueProvider.profit(asset)) >= 0 ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(gridValueProvider.getProfit(asset) + "%");
    };

    private ComponentRenderer<Span, Asset> profitComponentRenderer() {
        return new ComponentRenderer<>(Span::new, profitComponentUpdater);
    }

    public void displayWelcomeMessage() {
        Dialog dialog = new Dialog();
        dialog.add(new Text("Welcome " + authenticationService.getUserDisplayedName() + "!"));
        dialog.open();
    }

    public UserDto getUser() {
        return user;
    }
}