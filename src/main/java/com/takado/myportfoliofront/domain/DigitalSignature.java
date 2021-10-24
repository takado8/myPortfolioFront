package com.takado.myportfoliofront.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DigitalSignature {
    private final byte[] signature;
    private final String message;
}
