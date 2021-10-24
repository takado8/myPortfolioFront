package com.takado.myportfoliofront.client;


import com.takado.myportfoliofront.domain.AssetDto;
import com.takado.myportfoliofront.domain.DigitalSignature;
import com.takado.myportfoliofront.service.RequestSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class AssetClient {
    private final static String assetsApiRoot = "http://localhost:8081/v1/assets";

    private final RestTemplate restTemplate;
    private final RequestSignatureService signatureService;

    public List<AssetDto> getAssets(Long userId) {
        String path = assetsApiRoot + "/" + userId;
        byte[] signature;
        DigitalSignature digitalSignature;
        try {
            signature = signatureService.generateSignature(path);
            digitalSignature = new DigitalSignature(signature, path);
        } catch (GeneralSecurityException e) {
            printException(e);
            return Collections.emptyList();
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(path)
                .build()
                .encode()
                .toUri();
        try {
            var result = restTemplate.postForObject(uri.toString(), digitalSignature, AssetDto[].class);
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
        System.out.println("Exception: " + e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
    }
}
