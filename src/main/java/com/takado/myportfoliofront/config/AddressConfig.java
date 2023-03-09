package com.takado.myportfoliofront.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AddressConfig {
    public static final String BACKEND_API_ADDRESS = "http://localhost:8081";
    public static String SERVER_ADDRESS;

    @Value("${serverAddress}")
    public void setServerAddress(String serverAddress){
        SERVER_ADDRESS = serverAddress;
    }
}
