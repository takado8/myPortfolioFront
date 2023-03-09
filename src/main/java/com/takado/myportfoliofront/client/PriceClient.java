package com.takado.myportfoliofront.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.takado.myportfoliofront.config.AddressConfig.BACKEND_API_ADDRESS;

@Component
@RequiredArgsConstructor
public class PriceClient {
    private final static String pricesApiRoot = BACKEND_API_ADDRESS + "/v1/prices";

    private final RestTemplate restTemplate;

    @Nullable
    public Map<String, HashMap<String, BigDecimal>> getCoinsPrices(String vs_currency, String... coinsIds) {
        URI uri = UriComponentsBuilder.fromHttpUrl(pricesApiRoot + "/" + vs_currency + "/"
                        + String.join(",", coinsIds))
                .build().encode().toUri();
        try {
            return Objects.requireNonNull(
                    restTemplate.exchange(uri, HttpMethod.GET, null,
                            new ParameterizedTypeReference<HashMap<String, HashMap<String, BigDecimal>>>() {
                            }).getBody());
        } catch (RestClientException e) {
            return null;
        }
    }

    public BigDecimal getExchangeRate() {
        URI uri = UriComponentsBuilder.fromHttpUrl(pricesApiRoot + "/exchangeRate").build().encode().toUri();
        try {
            BigDecimal response = restTemplate.getForObject(uri, BigDecimal.class);
            return response == null ? BigDecimal.ONE : response;
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            return BigDecimal.ONE;
        }
    }
}
