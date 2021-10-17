package com.takado.myportfoliofront.mapper;

import com.takado.myportfoliofront.domain.AssetDto;
import com.takado.myportfoliofront.model.Asset;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AssetMapper {
    public Set<Asset> mapToAssetSet(List<AssetDto> assetDtoList) {
        return assetDtoList.stream()
                .map(this::mapToAsset)
                .collect(Collectors.toSet());
    }

    public Asset mapToAsset(AssetDto assetDto) {
        return new Asset(assetDto.getId(), assetDto.getCoinId(), assetDto.getTicker(), assetDto.getAmount(), assetDto.getValueIn());
    }

    public AssetDto mapToDto(Asset asset) {
        return new AssetDto(asset.getId(), asset.getCoinId(), asset.getTicker(), asset.getAmount(), asset.getValueIn());
    }
}
