package com.takado.myportfoliofront.domain.requests;

import com.takado.myportfoliofront.domain.AssetDto;
import com.takado.myportfoliofront.domain.DigitalSignature;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssetBodyRequest extends BodyRequest {
    private AssetDto assetDto;

    public AssetBodyRequest(AssetDto assetDto, DigitalSignature digitalSignature) {
        super(digitalSignature);
        this.assetDto = assetDto;
    }
}

