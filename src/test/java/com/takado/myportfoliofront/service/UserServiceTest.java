package com.takado.myportfoliofront.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    void userExistsTest() {
        var respNo = userService.userExists("nononono12311");
        var respYes = userService.userExists("alala@gm.com");
        assertNotNull(respNo);
        assertNotNull(respYes);
        assertFalse(respNo);
        assertTrue(respYes);
    }
}