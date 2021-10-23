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

    public UserDto createUser(String email, String nameHash, String displayedName, List<Long> assetsId) {
        return userClient.createUser(email, nameHash, displayedName, assetsId);
    }

    @Nullable
    public UserDto getUser(String email) {
        return userClient.getUser(email);
    }
}