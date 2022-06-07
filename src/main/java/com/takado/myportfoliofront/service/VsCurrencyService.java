package com.takado.myportfoliofront.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VsCurrencyService {
    private final List<String> currencies = new ArrayList<>();

    public VsCurrencyService() {
        fetchCurrencies();
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
