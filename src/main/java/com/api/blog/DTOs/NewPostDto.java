package com.api.blog.DTOs;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewPostDto {

     String message;
     MultipartFile file;


}
