package com.takado.myportfoliofront.domain.requests;

import com.takado.myportfoliofront.domain.DigitalSignature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BodyRequest {
    private DigitalSignature digitalSignature;
}
