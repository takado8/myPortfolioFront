package com.takado.myportfoliofront.client;


import com.takado.myportfoliofront.domain.AssetDto;
import com.takado.myportfoliofront.domain.DigitalSignature;
import com.takado.myportfoliofront.domain.requests.AssetBodyRequest;
import com.takado.myportfoliofront.service.RequestSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class AssetClient {
    private final static String assetsApiRoot = "http://localhost:8081/v1/assets";

    private final RestTemplate restTemplate;
    private final RequestSignatureService signatureService;

    public List<AssetDto> getAssets(Long userId) {
        String path = assetsApiRoot + "/" + userId;
        DigitalSignature digitalSignature;
        try {
            digitalSignature = signatureService.generateSignature(path);
        } catch (GeneralSecurityException e) {
            printException(e);
            return Collections.emptyList();
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(path)
                .build()
                .encode()
                .toUri();
        try {
            String uriStr = uri.toString();
            var result = restTemplate.postForObject(uriStr, digitalSignature, AssetDto[].class);
            if (result != null) {
                return Arrays.stream(result)
//                        .filter(assetDto -> assetDto.getTickerId() != null)
                        .collect(Collectors.toList());
            }
        } catch (RestClientException e) {
            printException(e);
        }
        return Collections.emptyList();
    }

    public AssetDto createAsset(AssetDto assetDto) {
        String assetString = assetDto.toString();
        DigitalSignature digitalSignature;
        try {
            digitalSignature = signatureService.generateSignature(assetString);
        } catch (GeneralSecurityException e) {
            printException(e);
            return new AssetDto();
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(assetsApiRoot)
                .build()
                .encode()
                .toUri();
        try {
            AssetBodyRequest bodyRequest = new AssetBodyRequest(assetDto, digitalSignature);
            return restTemplate.postForObject(uri.toString(), bodyRequest, AssetDto.class);
        } catch (RestClientException e) {
            printException(e);
        }
        return null;
    }

    public void deleteAsset(Long assetId) {
        String path = assetsApiRoot + "/delete/" + assetId;
        DigitalSignature digitalSignature;
        try {
            digitalSignature = signatureService.generateSignature(path);
        } catch (GeneralSecurityException e) {
            printException(e);
            return;
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(path)
                .build()
                .encode()
                .toUri();
        try {
            restTemplate.postForObject(uri, digitalSignature, Object.class);
        } catch (RestClientException e) {
            printException(e);
        }
    }

    public void updateAsset(AssetDto assetDto) {
        String assetString = assetDto.toString();
        DigitalSignature digitalSignature;
        try {
            digitalSignature = signatureService.generateSignature(assetString);
        } catch (GeneralSecurityException e) {
            printException(e);
            return;
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(assetsApiRoot)
                .build()
                .encode()
                .toUri();
        try {
            AssetBodyRequest bodyRequest = new AssetBodyRequest(assetDto, digitalSignature);
            restTemplate.put(uri, bodyRequest);
        } catch (RestClientException e) {
            printException(e);
        }
    }

    public void printException(Exception e) {
        System.out.println("\n\nEXCEPTION: ");
        System.out.println("Exception: " + e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
        System.out.println("\n\n\n\n");
    }
}
