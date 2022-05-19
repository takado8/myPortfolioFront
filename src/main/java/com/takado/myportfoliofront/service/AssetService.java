package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.AssetClient;
import com.takado.myportfoliofront.client.PriceClient;
import com.takado.myportfoliofront.domain.Ticker;
import com.takado.myportfoliofront.mapper.AssetMapper;
import com.takado.myportfoliofront.domain.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AssetService {
    private final static String USD = "usd";
    private final TickerService tickerService;
    private final AssetClient assetClient;
    private final PriceClient priceClient;
    private final AssetMapper assetMapper;
    private final Set<Asset> assets;

    public void fetchAssets(Long userId) {
        assets.clear();
        assets.addAll(assetMapper.mapToAssetSet(assetClient.getAssets(userId)));
    }

    public void fetchPrices() {
        String[] coinsIds = assets.stream().map(asset -> asset.getTicker().getCoinId()).toArray(String[]::new);
        var prices = priceClient.getCoinsPrices(USD, coinsIds);

        for (Asset asset : assets) {
            asset.setPriceNow(prices.get(asset.getTicker().getCoinId()).get(USD));
        }
    }

    public void createAsset(String tickerString, Long userId, String amount, String valueIn) {
        Ticker ticker = tickerService.getTicker(tickerString);
        Asset asset = new Asset(ticker, userId, amount, valueIn);
        Asset newAsset = assetMapper.mapToAsset(assetClient.createAsset(assetMapper.mapToDto(asset)));
        assets.add(newAsset);
    }

    public void updateAsset(Asset asset) {
        assetClient.updateAsset(assetMapper.mapToDto(asset));
    }

    public void deleteAsset(String assetTicker) {
        var asset = assets.stream()
                .filter(asset1 -> asset1.getTicker().getTicker().equals(assetTicker))
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
                .filter(asset -> asset.getTicker().getTicker().toUpperCase().contains(ticker.toUpperCase()))
                .collect(Collectors.toSet());
    }

    public Asset findByTicker(String ticker) {
        Asset result;
        try {
            result = assets.stream()
                    .filter(asset -> asset.getTicker().getTicker().toUpperCase().contains(ticker.toUpperCase()))
                    .collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException ex) {
            result = null;
        }
        return result;
    }
}
