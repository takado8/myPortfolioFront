package com.takado.myportfoliofront.client;

import com.takado.myportfoliofront.domain.AssetDto;
import com.takado.myportfoliofront.domain.DigitalSignature;
import com.takado.myportfoliofront.domain.requests.AssetBodyRequest;
import com.takado.myportfoliofront.service.RequestSignatureService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetClientTest {

    @InjectMocks
    private AssetClient assetClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RequestSignatureService signatureService;

    @Test
    void getAssets() throws GeneralSecurityException {
        //given
        var userId = 123L;
        AssetDto assetDto = new AssetDto(1L, 1L, 1L, "0.5", "100");
        AssetDto[] assetsDtoArr = new AssetDto[1];
        assetsDtoArr[0] = assetDto;
        String uri = "http://localhost:8081/v1/assets/" + userId;
        DigitalSignature signature = new DigitalSignature(new byte[1], uri);
        when(signatureService.generateSignature(uri)).thenReturn(signature);
        when(restTemplate.postForObject(uri, signature, AssetDto[].class)).thenReturn(assetsDtoArr);
        //when
        var result = assetClient.getAssets(userId);
        //then
        assertTrue(result.size() > 0);
        assertEquals(assetDto, result.get(0));
    }

    @Test
    void createAsset() throws GeneralSecurityException {
        //given
        AssetDto assetDto = new AssetDto(1L, 1L, 1L, "0.5", "100");
        String uri = "http://localhost:8081/v1/assets";

        DigitalSignature signature = new DigitalSignature(new byte[1], assetDto.toString());
        AssetBodyRequest bodyRequest = new AssetBodyRequest(assetDto, signature);

        when(signatureService.generateSignature(assetDto.toString())).thenReturn(signature);
        when(restTemplate.postForObject(uri, bodyRequest, AssetDto.class)).thenReturn(assetDto);

        //when
        var result = assetClient.createAsset(assetDto);
        //then
        assertEquals(assetDto, result);
    }

    @Test
    void deleteAsset() throws GeneralSecurityException {
        //given
        String path = "http://localhost:8081/v1/assets/delete/1";
        DigitalSignature signature = new DigitalSignature(new byte[1], path);
        URI uri = UriComponentsBuilder.fromHttpUrl(path)
                .build()
                .encode()
                .toUri();
        when(signatureService.generateSignature(path)).thenReturn(signature);
        //when
        assetClient.deleteAsset(1L);
        // then
        verify(restTemplate, times(1)).postForObject(uri, signature, Object.class);
    }

    @Test
    void updateAsset() throws GeneralSecurityException {
        //given
        AssetDto assetDto = new AssetDto(1L, 1L, 1L, "0.5", "100");
        URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/v1/assets")
                .build()
                .encode()
                .toUri();
        DigitalSignature signature = new DigitalSignature(new byte[1], assetDto.toString());
        AssetBodyRequest bodyRequest = new AssetBodyRequest(assetDto, signature);
        when(signatureService.generateSignature(assetDto.toString())).thenReturn(signature);
        //when
        assetClient.updateAsset(assetDto);
        // then
        verify(restTemplate, times(1)).put(uri, bodyRequest);
    }
}