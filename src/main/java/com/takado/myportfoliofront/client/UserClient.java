package com.takado.myportfoliofront.client;

import com.takado.myportfoliofront.domain.DigitalSignature;
import com.takado.myportfoliofront.domain.UserDto;
import com.takado.myportfoliofront.domain.requests.UserBodyRequest;
import com.takado.myportfoliofront.service.RequestSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final RestTemplate restTemplate;
    private final RequestSignatureService signatureService;
    private final static String apiRoot = "http://localhost:8081/v1/users";

    @Nullable
    public UserDto createUser(String email, String nameHash, String displayedName, List<Long> assetsId) {
        UserDto userDto = new UserDto(email, nameHash, displayedName, assetsId);
        String userDtoString = userDto.toString();
        DigitalSignature digitalSignature;
        try {
            digitalSignature = signatureService.generateSignature(userDtoString);
        } catch (GeneralSecurityException e) {
            printException(e);
            return null;
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(apiRoot).build().encode().toUri();
        try {
            UserBodyRequest bodyRequest = new UserBodyRequest(digitalSignature, userDto);
            return restTemplate.postForObject(uri, bodyRequest, UserDto.class);
        } catch (RestClientException e) {
            printException(e);
            return null;
        }
    }

    @Nullable
    public UserDto getUser(String email) {
        String path = apiRoot + "/" + email;
        DigitalSignature digitalSignature;
        try {
            digitalSignature = signatureService.generateSignature(path);
        } catch (GeneralSecurityException e) {
            printException(e);
            return null;
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(path).build().encode().toUri();
        try {
            return restTemplate.postForObject(uri, digitalSignature, UserDto.class);
        } catch (RestClientException e) {
            printException(e);
            return null;
        }
    }

    private void printException(Exception e) {
        System.out.println("Exception: " + e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
    }
}
