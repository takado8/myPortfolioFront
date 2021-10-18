package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.AssetClient;
import com.takado.myportfoliofront.client.PriceClient;
import com.takado.myportfoliofront.mapper.AssetMapper;
import com.takado.myportfoliofront.model.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AssetService {
    private final static String USD = "usd";
    private final static String PLN = "pln";

    private String vsCurrency = PLN;
    private final AssetClient assetClient;
    private final PriceClient priceClient;
    private final AssetMapper assetMapper;
    private final Set<Asset> assets;

    public void fetchAssets() {
        assets.clear();
        assets.addAll(assetMapper.mapToAssetSet(assetClient.getAssets()));
    }

    public void fetchPrices() {
        String[] coinsIds = assets.stream().map(Asset::getCoinId).toArray(String[]::new);
        var prices = priceClient.getCoinsPrices(vsCurrency, coinsIds);

        for (Asset asset : assets) {
            asset.setPriceNow(prices.get(asset.getCoinId()).get(vsCurrency));
        }
    }

    public void createAsset(Asset asset) {
        Asset newAsset = assetMapper.mapToAsset(assetClient.createAsset(assetMapper.mapToDto(asset)));
        assets.add(newAsset);
    }

    public void updateAsset(Asset asset) {
        assetClient.updateAsset(assetMapper.mapToDto(asset));
    }

    public void deleteAsset(String assetTicker) {
        var asset = assets.stream()
                .filter(asset1 -> asset1.getTicker().equals(assetTicker))
                .findFirst()
                .orElse(null);
        if (asset != null) {
            assets.remove(asset);
            assetClient.deleteAsset(asset.getId());
        }
    }

    public Set<Asset> getAssets() {
        return assets;
    }

    public Set<Asset> filterByTicker(String ticker){
        return assets.stream()
                .filter(asset -> asset.getTicker().toUpperCase().contains(ticker.toUpperCase()))
                .collect(Collectors.toSet());
    }

    public Asset findByTicker(String ticker) {
        Asset result;
        try {
            result = assets.stream()
                    .filter(asset -> asset.getTicker().toUpperCase().contains(ticker.toUpperCase()))
                    .collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException ex) {
            result = null;
        }
        return result;
    }

    public String getVsCurrency() {
        return vsCurrency.toUpperCase();
    }

    public void setVsCurrency(String vsCurrency) {
        this.vsCurrency = vsCurrency;
    }
}
