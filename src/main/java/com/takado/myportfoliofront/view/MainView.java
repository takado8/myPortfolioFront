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
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
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
    private final VsCurrencyService vsCurrencyService;
    private final GridValueProvider gridValueProvider;
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
            var prices = pricesService.fetchPrices(assetService.getCoinsIds());
            assetService.setPrices(prices);
            grid.setItems(assetService.getAssets());
            refreshFooterRow();
        } catch (NullPointerException ignored) {
        }
    }

    public void refreshFooterRow() {
        footerRow.getCell(grid.getColumnByKey("ticker")).setText("Total:");
        footerRow.getCell(grid.getColumnByKey("valueIn")).setText(formatPriceString(totalValueIn()));
        footerRow.getCell(grid.getColumnByKey("valueNow")).setText(formatPriceString(totalValueNow()));
        footerRow.getCell(grid.getColumnByKey("profit")).setText(formatProfitString(totalProfit()));
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
                .setKey("ticker");
        grid.addColumn(gridValueProvider::getAmount)
                .setHeader("Amount")
                .setKey("amount")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getAmount())));
        grid.addColumn(gridValueProvider::getAvgPrice)
                .setHeader("Avg Price [" + gridValueProvider.getCurrentPriceCurrency() + "]")
                .setKey("avgPrice")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(gridValueProvider.avgPrice(asset))));
        grid.addColumn(gridValueProvider::getPriceNow)
                .setHeader("Price Now [" + gridValueProvider.getCurrentPriceCurrency() + "]")
                .setKey("priceNow")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getPriceNow().toPlainString())));
        grid.addColumn(gridValueProvider::getValueIn)
                .setHeader("Value In [" + gridValueProvider.getCurrentValueCurrency() + "]")
                .setKey("valueIn")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(asset.getValueIn())));
        grid.addColumn(gridValueProvider::getValueNow)
                .setHeader("Value Now [" + gridValueProvider.getCurrentValueCurrency() + "]")
                .setKey("valueNow")
                .setComparator(Comparator.comparingDouble(asset -> gridValueProvider.valueNow(asset).doubleValue()));
        grid.addColumn(gridValueProvider::getProfit)
                .setHeader("Profit [+%]")
                .setKey("profit")
                .setComparator(Comparator.comparingDouble(asset -> Double.parseDouble(gridValueProvider.profit(asset))));
        grid.asSingleSelect().addValueChangeListener(event -> newAssetForm.setAsset(grid.asSingleSelect().getValue()));
        grid.setSizeFull();
        grid.setMaxHeight(476F, Unit.PIXELS);
        footerRow = grid.appendFooterRow();
    }

    private float getGridActualHeight(Grid grid) {
        double[] actualHeight = new double[1];
        grid.getElement()
                .executeJs("return $0.clientHeight", grid.getElement()).then(height -> {
                    Notification.show("grid height " + height.asNumber());
                    actualHeight[0] = height.asNumber();
                });
        return (float) actualHeight[0];
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

    public void displayWelcomeMessage() {
        Dialog dialog = new Dialog();
        dialog.add(new Text("Welcome " + authenticationService.getUserDisplayedName() + "!"));
        dialog.open();
    }

    public UserDto getUser() {
        return user;
    }
}