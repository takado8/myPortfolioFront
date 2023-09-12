package com.takado.myportfoliofront.view;

import com.takado.myportfoliofront.domain.Asset;
import com.takado.myportfoliofront.service.*;
import com.takado.myportfoliofront.service.grid.GridItemSelectedCallback;
import com.takado.myportfoliofront.service.grid.GridLayoutManager;
import com.takado.myportfoliofront.service.grid.GridService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;

import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;

import java.util.ConcurrentModificationException;

//
//@Push
//@Route("home")
@Component
//@PageTitle("myPortfolio")
//@Scope("prototype")
@RequestScope
@CssImport(include = "styledBorderCorner", value = "./styles.css")
@RequiredArgsConstructor
public class MainView extends VerticalLayout implements GridItemSelectedCallback {
    private final AssetService assetService;
    private final PricesService pricesService;
    private final TradeService tradeService;
    private final VsCurrencyService vsCurrencyService;
    private final UserService userService;
    private final GridService gridService;
    private final SchedulerService schedulerService;
    private final GridLayoutManager gridLayoutManager;
    private final TextField filter = new TextField();
    private final NewAssetForm newAssetForm;
    private final Select<String> priceCurrency = new Select<>();
    private final Select<String> valueCurrency = new Select<>();
    private boolean lockPriceCurrencyChanged = false;
    private boolean lockValueCurrencyChanged = false;
    private boolean isGuest = true;

    @PostConstruct
    private void initialize() {
        setupGrid();
        HorizontalLayout toolbar = makeToolbar();
        gridLayoutManager.initNewLayout();
        gridLayoutManager.gridLayoutAdd(gridService.grid);
        gridLayoutManager.gridLayoutSetSizeFull();
        HorizontalLayout mainContent = new HorizontalLayout(gridLayoutManager.getGridLayout(), newAssetForm);
        mainContent.setSizeFull();
        add(toolbar, mainContent);
        setSizeFull();
        newAssetForm.setAsset(null);

        var user = userService.fetchUser();
        if (user == null) {
            user = userService.createUser();
            userService.displayWelcomeMessage();
        }
        assetService.fetchAssets(user.getId());
        schedulerService.setScheduledRefresh(this::scheduledRefresh);
        reloadAssetsAndPrices();
    }

    public void valueCurrencyChanged() {
        String valueCurrency = this.valueCurrency.getValue();
        if (valueCurrency == null || lockValueCurrencyChanged) return;
        vsCurrencyService.putCurrencyOnTop(valueCurrency);
        gridService.getValueProvider().setCurrentValueCurrency(vsCurrencyService.getCurrencyFromLabel(valueCurrency));
        gridService.grid.getColumnByKey("valueIn")
                .setHeader("Value In [" + gridService.getValueProvider().getCurrentValueCurrency() + "]");
        gridService.grid.getColumnByKey("valueNow")
                .setHeader("Value Now [" + gridService.getValueProvider().getCurrentValueCurrency() + "]");
        lockValueCurrencyChanged = true;
        this.valueCurrency.setItems(vsCurrencyService.getCurrenciesValueLabels());
        this.valueCurrency.setValue(valueCurrency);
        lockValueCurrencyChanged = false;
        reloadAssetsAndPrices();
    }

    public void priceCurrencyChanged() {
        String priceCurrency = this.priceCurrency.getValue();
        if (priceCurrency == null || lockPriceCurrencyChanged) return;
        vsCurrencyService.putCurrencyOnTop(priceCurrency);
        gridService.getValueProvider().setCurrentPriceCurrency(vsCurrencyService.getCurrencyFromLabel(priceCurrency));
        gridService.grid.getColumnByKey("avgPrice")
                .setHeader("Avg Price [" + gridService.getValueProvider().getCurrentPriceCurrency() + "]");
        gridService.grid.getColumnByKey("priceNow")
                .setHeader("Price Now [" + gridService.getValueProvider().getCurrentPriceCurrency() + "]");
        lockPriceCurrencyChanged = true;
        this.priceCurrency.setItems(vsCurrencyService.getCurrenciesPriceLabels());
        this.priceCurrency.setValue(priceCurrency);
        lockPriceCurrencyChanged = false;
        reloadAssetsAndPrices();
    }

    public void filtering() {
        gridService.grid.setItems(assetService.filterByTicker(filter.getValue()));
        gridService.refreshFooterRow();
    }

    public void scheduledRefresh() {
        if (!filter.getValue().isBlank()) return;
        try {
            reloadData();
            getUI().ifPresent(ui -> {
                if (ui.isAttached())
                    ui.access(this::reloadUI);
            });
        } catch (IllegalStateException | NullPointerException ignored) {
        } catch (UIDetachedException | ConcurrentModificationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void reloadData() {
        var prices = pricesService.fetchPrices(assetService.getCoinsIds());
        assetService.setPrices(prices);
        tradeService.setPrices(prices);
    }
    private void reloadUI() {
        var newAssetFormVisible = newAssetForm.isVisible();
        gridService.setItems(assetService.getAssets());
        gridService.refreshFooterRow();
        newAssetForm.reloadTradesGridContent();
        newAssetForm.setVisible(newAssetFormVisible);

    }

    public void reloadAssetsAndPrices() {
        try {
            reloadData();
            reloadUI();
        } catch (NullPointerException ignored) {
        }
    }

    public void setupGrid() {
        gridService.setupMainViewGrid(this);
    }

    @Override
    public void gridItemSelectedCallback() {
        var asset = gridService.grid.asSingleSelect().getValue();
//        System.out.println("main:");
//
//        gridService.setSelected(asset == null ? null : asset.getTicker());
        newAssetForm.setAsset(asset);
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
            gridService.grid.asSingleSelect().clear();
            newAssetForm.setAsset(new Asset());
        });

        Button logoutButton = new Button("Logout");
        logoutButton.getStyle().set("cursor", "pointer");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
        logoutButton.addClickListener(e -> getUI().ifPresent(page -> page.getPage().setLocation("/logout")));

        String userName = userService.getUserEmail();
        Dialog dialog = new Dialog();
        dialog.add(new Text("User email: " + userName));

        Button showUserButton = new Button("Show user");
        showUserButton.getStyle().set("cursor", "pointer");
        showUserButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
        showUserButton.addClickListener(e -> dialog.open());

        return new HorizontalLayout(filter, priceCurrency, valueCurrency,
                addNewAssetButton, logoutButton, showUserButton);
    }

    public void setIsGuest(boolean isGuest) {
        this.isGuest = isGuest;
    }

    public boolean isGuest() {
        return isGuest;
    }
}