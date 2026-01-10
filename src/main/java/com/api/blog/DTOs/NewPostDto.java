package com.api.blog.DTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

@Value
@Getter
@Setter
public class NewPostDto {

     String message;
     MultipartFile file;


}
