package com.takado.myportfoliofront.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SignatureKeysConfiguration {
    @Value("${signature.privateKeyString}")
    private String privateKeyString;
    @Value("${signature.publicKeyString}")
    private String publicKeyString;
}
