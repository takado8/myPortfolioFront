package com.takado.myportfoliofront.mapper;

import com.takado.myportfoliofront.domain.AssetDto;
import com.takado.myportfoliofront.model.Asset;
import com.takado.myportfoliofront.service.TickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetMapper {
    private final TickerService tickerService;

    public Set<Asset> mapToAssetSet(List<AssetDto> assetDtoList) {
        return assetDtoList.stream()
                .map(this::mapToAsset)
                .collect(Collectors.toSet());
    }

    public Asset mapToAsset(AssetDto assetDto) {
        return new Asset(assetDto.getId(), tickerService.getTicker(assetDto.getTickerId()),
                assetDto.getUserId(), assetDto.getAmount(), assetDto.getValueIn());
    }

    public AssetDto mapToDto(Asset asset) {
        return new AssetDto(asset.getId(),asset.getTicker().getId(),  asset.getUserId(), asset.getAmount(), asset.getValueIn());
    }
}
