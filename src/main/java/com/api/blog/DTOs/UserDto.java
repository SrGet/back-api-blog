package com.api.blog.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Value
@Getter
@Setter
public class UserDto {

    @NotBlank(message = "Username is required")
    String username;

    String profileImgUrl;

    @NotBlank(message = "Password is required")
    String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    String email;



}
