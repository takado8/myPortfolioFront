package com.takado.myportfoliofront.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetDto {
    private Long id;
    private Long tickerId;
    private Long userId;
    private String amount;
    private String valueIn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetDto assetDto = (AssetDto) o;

        if (!tickerId.equals(assetDto.tickerId)) return false;
        return userId.equals(assetDto.userId);
    }

    @Override
    public int hashCode() {
        return 31 * tickerId.hashCode() + userId.hashCode();
    }
}