package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.model.Asset;
import com.takado.myportfoliofront.model.Ticker;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.takado.myportfoliofront.model.Ticker.*;

public class AssetService {
    private final Set<Asset> assets;
    private static AssetService assetService;

    private AssetService() {
        this.assets = exampleData();
    }

    public static AssetService getInstance() {
        if (assetService == null) {
            assetService = new AssetService();
        }
        return assetService;
    }

    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    public Set<Asset> getAssets() {
        return assets;
    }

    private Set<Asset> exampleData() {
        Set<Asset> assets = new HashSet<>();
        assets.add(new Asset(BTC, "0.01", "1000"));
        assets.add(new Asset(ETH, "0.5", "500"));
        assets.add(new Asset(ADA, "1000", "300"));
        return assets;
    }

    public Asset findByTicker(String ticker) {
        Asset result;
        try {
            result = assets.stream()
                    .filter(asset -> asset.getTicker().getString().toUpperCase().contains(ticker.toUpperCase()))
                    .collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException ex) {
            result = null;
        }
        return result;
    }

    public void delete(Ticker assetTicker) {
        this.assets.remove(new Asset(assetTicker, "", ""));
    }
}
