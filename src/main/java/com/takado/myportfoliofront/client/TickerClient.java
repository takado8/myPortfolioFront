package com.takado.myportfoliofront.client;

import com.takado.myportfoliofront.domain.TickerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.takado.myportfoliofront.config.AddressConfig.BACKEND_API_ADDRESS;

@Component
@RequiredArgsConstructor
public class TickerClient {
    private final RestTemplate restTemplate;
    private final static String apiRoot = BACKEND_API_ADDRESS + "/v1/tickers";

    public List<TickerDto> getAllTickers() {
        URI uri = UriComponentsBuilder.fromHttpUrl(apiRoot).build().encode().toUri();
        try {
            return Arrays.stream(Objects.requireNonNull(restTemplate.getForObject(uri, TickerDto[].class)))
                    .collect(Collectors.toList());
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
    }
}
