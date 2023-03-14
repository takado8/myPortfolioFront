package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.UserClient;
import com.takado.myportfoliofront.domain.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;
    private final AuthenticationService authenticationService;

    public UserDto createUser(List<Long> assetsId) {
        return userClient.createUser(authenticationService.getUserEmail(), authenticationService.getUserNameHash(),
                authenticationService.getUserDisplayedName(), assetsId);
    }

    @Nullable
    public UserDto getUser() {
        var user = userClient.getUser(authenticationService.getUserEmail());
        return user == null || user.getId() == null ? null : user;
    }

    public String getUserEmail() {
        return authenticationService.getUserEmail();
    }

    public String getUserDisplayedName(){
        return authenticationService.getUserDisplayedName();
    }
}