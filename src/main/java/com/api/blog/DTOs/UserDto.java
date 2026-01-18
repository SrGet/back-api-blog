package com.api.blog.DTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Value
@Getter
@Setter
public class UserDto {

    String username;
    String password;
    String email;



}
