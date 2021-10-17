package com.takado.myportfoliofront.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PriceClient {
    private final static String pricesApiRoot = "http://localhost:8081/v1/prices";

    private final RestTemplate restTemplate;

    public Map<String, HashMap<String, BigDecimal>> getCoinsPrices(String vs_currency, String... coinsIds) {
        URI uri = UriComponentsBuilder.fromHttpUrl(pricesApiRoot + "/" + vs_currency + "/"
                        + String.join(",", coinsIds))
                .build().encode().toUri();
        try {
            return Objects.requireNonNull(
                    restTemplate.exchange(uri, HttpMethod.GET, null,
                            new ParameterizedTypeReference<HashMap<String, HashMap<String, BigDecimal>>>() {
                            }).getBody());
        } catch (RestClientException e){
            return null;
        }
    }
}
