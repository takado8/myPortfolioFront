package com.takado.myportfoliofront.client;

import com.takado.myportfoliofront.domain.DigitalSignature;
import com.takado.myportfoliofront.domain.TradeDto;
import com.takado.myportfoliofront.domain.requests.TradeBodyRequest;
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

import static com.takado.myportfoliofront.config.AddressConfig.BACKEND_API_ADDRESS;

@Component
@RequiredArgsConstructor
public class TradeClient {
    private final static String tradesApiRoot = BACKEND_API_ADDRESS + "/v1/trades";

    private final RestTemplate restTemplate;
    private final RequestSignatureService signatureService;

    public List<TradeDto> getTrades(Long userId, String tickerCoinId) {
        String path = tradesApiRoot + "/" + userId + "/" + tickerCoinId;
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
            var result = restTemplate.postForObject(uriStr, digitalSignature, TradeDto[].class);
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

    public TradeDto createTrade(TradeDto tradeDto) {
        String tradeString = tradeDto.toString();
        DigitalSignature digitalSignature;
        try {
            digitalSignature = signatureService.generateSignature(tradeString);
        } catch (GeneralSecurityException e) {
            printException(e);
            return null;
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(tradesApiRoot)
                .build()
                .encode()
                .toUri();

        try {
            TradeBodyRequest bodyRequest = new TradeBodyRequest(tradeDto, digitalSignature);
            return restTemplate.postForObject(uri.toString(), bodyRequest, TradeDto.class);
        } catch (RestClientException e) {
            printException(e);
        }
        return null;
    }

    public void printException(Exception e) {
        System.out.println("\n\nEXCEPTION: ");
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
        System.out.println("\n\n");
    }
}
