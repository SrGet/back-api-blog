package com.api.blog.DTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Value
@Getter
@Setter
public class UserDto {

    String name;
    String lastName;
    String username;
    String password;
    String email;
    int age;


}
