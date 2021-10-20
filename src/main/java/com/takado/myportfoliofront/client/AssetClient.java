package com.takado.myportfoliofront.client;


import com.takado.myportfoliofront.domain.AssetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class AssetClient {
    private final static String assetsApiRoot = "http://localhost:8081/v1/assets";

    private final RestTemplate restTemplate;

    public List<AssetDto> getAssets() {
        URI uri = UriComponentsBuilder.fromHttpUrl(assetsApiRoot)
                .build()
                .encode()
                .toUri();
        try {
            var result = restTemplate.getForObject(uri, AssetDto[].class);
            if (result != null) {
                return Arrays.stream(result)
                        .filter(assetDto -> assetDto.getTickerId() != null)
                        .collect(Collectors.toList());
            }
        } catch (RestClientException e) {
            printException(e);
        }
        return Collections.emptyList();
    }

    public AssetDto createAsset(AssetDto assetDto) {
        URI uri = UriComponentsBuilder.fromHttpUrl(assetsApiRoot)
                .build()
                .encode()
                .toUri();
        try {
            return restTemplate.postForObject(uri, assetDto, AssetDto.class);
        } catch (RestClientException e) {
            printException(e);
        }
        return null;
    }

    public void deleteAsset(Long assetId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(assetsApiRoot + "/" + assetId)
                .build()
                .encode()
                .toUri();
        try {
            restTemplate.delete(uri);
        } catch (RestClientException e) {
            printException(e);
        }
    }

    public void updateAsset(AssetDto assetDto) {
        URI uri = UriComponentsBuilder.fromHttpUrl(assetsApiRoot)
                .build()
                .encode()
                .toUri();
        try {
            restTemplate.put(uri, assetDto);
        } catch (RestClientException e) {
            printException(e);
        }
    }

    private void printException(Exception e) {
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
    }
}
