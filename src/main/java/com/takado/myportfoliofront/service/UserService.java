package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.client.UserClient;
import com.takado.myportfoliofront.domain.UserDto;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;
    private final AuthenticationService authenticationService;
    private UserDto user;

    public UserDto createUser() {
        user = userClient.createUser(authenticationService.getUserEmail(), authenticationService.getUserNameHash(),
                authenticationService.getUserDisplayedName(), Collections.emptyList());
        return user;
    }

    @Nullable
    public UserDto fetchUser() {
        var fetchedUser = userClient.getUser(authenticationService.getUserEmail());
        if (fetchedUser == null || fetchedUser.getId() == null) {
            return null;
        }
        user = fetchedUser;
        return user;
    }

    @Nullable
    public Long getUserId() {
        return user == null || user.getId() == null ? null : user.getId();
    }

    public String getUserEmail() {
        return authenticationService.getUserEmail();
    }

    public String getUserDisplayedName() {
        return authenticationService.getUserDisplayedName();
    }

    public void displayWelcomeMessage() {
        Dialog dialog = new Dialog();
        dialog.add(new Text("Welcome " + getUserDisplayedName() + "!"));
        dialog.open();
    }
}