package com.takado.myportfoliofront.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    public String getUserNameHash() {
        var userDetail = getUserDetail();
        if (userDetail != null) {
            return userDetail.getName();
        } else {
            System.out.println("Exception in getUserName(): null");
            return "<NO USER>";
        }
    }

    public String getUserDisplayedName() {
        var userDetail = getUserDetail();
        if (userDetail != null) {
            return userDetail.getFullName();
        } else {
            System.out.println("Exception in getUserName(): null");
            return "User";
        }
    }

    public String getUserEmail() {
        var userDetail = getUserDetail();
        if (userDetail != null) {
            return userDetail.getEmail();
        } else {
            System.out.println("Exception in getUserEmail(): null");
            return "<NO USER EMAIL>";
        }
    }

    private DefaultOidcUser getUserDetail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            try {
                return (DefaultOidcUser) authentication.getPrincipal();
            } catch (Exception e) {
                System.out.println("Exception in AuthenticationService.getUserDetail: " + e);
            }
        }
        return null;
    }
}
