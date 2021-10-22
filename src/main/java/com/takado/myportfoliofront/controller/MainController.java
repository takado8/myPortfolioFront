package com.takado.myportfoliofront.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("")
public class MainController {
    @RequestMapping("/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response) {
        // token can be revoked here if needed
//        new SecurityContextLogoutHandler().logout(request, null, null);
//        try {
//            //sending back to client app
//            response.sendRedirect(request.getHeader("referer"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        SecurityContextHolder.getContext().setAuthentication(null);
        request.getSession(true).invalidate();
    }

}
