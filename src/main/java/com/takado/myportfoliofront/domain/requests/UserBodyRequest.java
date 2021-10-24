package com.takado.myportfoliofront.domain.requests;

import com.takado.myportfoliofront.domain.DigitalSignature;
import com.takado.myportfoliofront.domain.UserDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserBodyRequest extends BodyRequest{
    private UserDto userDto;

    public UserBodyRequest(DigitalSignature digitalSignature, UserDto userDto) {
        super(digitalSignature);
        this.userDto = userDto;
    }
}
