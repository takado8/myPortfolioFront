package com.takado.myportfoliofront.client;

import com.takado.myportfoliofront.domain.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;
    private final static String apiRoot = "http://localhost:8081/v1/users";

    @Nullable
    public UserDto createUser(String email, String nameHash, String displayedName, List<Long> assetsId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(apiRoot).build().encode().toUri();
        UserDto userDto = new UserDto(email, nameHash, displayedName, assetsId);
        try {
            return restTemplate.postForObject(uri, userDto, UserDto.class);
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Nullable
    public UserDto getUser(String email) {
        URI uri = UriComponentsBuilder.fromHttpUrl(apiRoot + "/" + email).build().encode().toUri();
        try {
            return restTemplate.getForObject(uri, UserDto.class);
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
