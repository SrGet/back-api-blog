package com.api.blog.Service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<String,String> uploadImage(MultipartFile file) {


        try {

            if (file != null){
                Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of("folder", "social-app"));
                log.info("UploadResult: {}", uploadResult);

                return Map.of("secureUrl",uploadResult.get("secure_url").toString(),
                        "publicId", uploadResult.get("public_id").toString());
            }
            return null;
            

        } catch (IOException e) {
            throw new RuntimeException("Upload file failed.", e);
        }
    }


    public void deleteImage(String publicId){
        try {
            cloudinary.uploader().destroy(publicId, Map.of("invalidate",true));
        } catch (IOException e) {
            throw new RuntimeException("Delete image failed.", e);
        }
    }
}
