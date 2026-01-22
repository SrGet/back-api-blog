package com.api.blog.Controllers;

import com.api.blog.Service.MinIOService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Slf4j
public class FileStreamController {

    private final MinIOService minIOService;

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName){

        try{
            InputStream inputStream = minIOService.getImg(fileName);

            if(inputStream == null){
                log.warn("Image not found. fileName: {}", fileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Resource resource = new InputStreamResource(inputStream);
            String contentType = MediaTypeFactory.getMediaType(fileName)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM)
                    .toString();

            log.info("Getting image successful. Returning resource");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; fileName=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);

        } catch (Exception e) {
            log.error("Getting image failed. Reason: {}", e.getMessage());
            throw new RuntimeException(e);
        }







    }
}
