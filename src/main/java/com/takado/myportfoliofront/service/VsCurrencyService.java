package com.takado.myportfoliofront.service;

import com.helger.commons.annotation.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class VsCurrencyService {
    private final static VsCurrencyService instance = new VsCurrencyService();
    private final List<String> currencies = new ArrayList<>();

    private VsCurrencyService() {
        fetchCurrencies();
    }

    public static VsCurrencyService getInstance() {
        return instance;
    }

    private void fetchCurrencies() {
        currencies.add("PLN");
        currencies.add("USD");
    }

    public List<String> getCurrenciesValueLabels() {
        return addPrefixes("Value in ");
    }

    public List<String> getCurrenciesPriceLabels() {
        return addPrefixes("Price in ");
    }

    private List<String> addPrefixes(String prefix) {
        return currencies.stream()
                .map(currency -> prefix + currency)
                .collect(Collectors.toList());
    }

    public String getCurrencyFromLabel(String label) {
        return label.substring(9);
    }

    public void putCurrencyOnTop(String label) {
        String currency = getCurrencyFromLabel(label);
        currencies.remove(currency);
        currencies.add(0, currency);
    }
}
