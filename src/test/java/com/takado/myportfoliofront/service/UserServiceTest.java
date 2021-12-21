package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.UserClient;
import com.takado.myportfoliofront.domain.DigitalSignature;
import com.takado.myportfoliofront.domain.UserDto;
import com.takado.myportfoliofront.domain.requests.UserBodyRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserService userService = new UserService(null);
    @InjectMocks
    UserClient userClient;
    @Mock
    RestTemplate restTemplate;
    @Mock
    RequestSignatureService signatureService;


    @Test
    void createUser() {
        //given
        userService.setUserClient(userClient);
        UserDto userDto = new UserDto("mail", "123",
                "aa", Collections.emptyList());
        when(restTemplate.postForObject(any(URI.class), any(UserBodyRequest.class), eq(UserDto.class)))
                .thenReturn(userDto);
        //when
        var result = userService.createUser("mail", "123",
                "aa", Collections.emptyList());
        //then
        assertEquals(userDto, result);

    }

    @Test
    void getUser() throws GeneralSecurityException {
        //given
        userService.setUserClient(userClient);
        UserDto userDto = new UserDto("mail", "123",
                "aa", Collections.emptyList());
        String uriStr = "http://localhost:8081/v1/users/mail";
        DigitalSignature signature = new DigitalSignature(new byte[1], uriStr);
        when(signatureService.generateSignature(uriStr)).thenReturn(signature);
        URI uri = UriComponentsBuilder.fromHttpUrl(uriStr).build().encode().toUri();

        when(restTemplate.postForObject(uri, signature, UserDto.class))
                .thenReturn(userDto);
        //when
        var result = userService.getUser("mail");
        //then
        assertEquals(userDto, result);
    }
}