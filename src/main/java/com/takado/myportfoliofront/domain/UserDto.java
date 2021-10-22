package com.takado.myportfoliofront.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String nameHash;
    private String displayedName;
    private List<Long> assetsId;

    public UserDto(String email, String nameHash, String displayedName, List<Long> assetsId) {
        this.email = email;
        this.nameHash = nameHash;
        this.displayedName = displayedName;
        this.assetsId = assetsId;
    }
}