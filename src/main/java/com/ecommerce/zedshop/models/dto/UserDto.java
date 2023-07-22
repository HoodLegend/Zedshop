package com.ecommerce.zedshop.models.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long user_id;
    private String username;
    private String email;

    private String password;

    private String confirmPassword;

}
