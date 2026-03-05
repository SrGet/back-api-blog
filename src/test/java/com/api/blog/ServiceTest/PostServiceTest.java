package com.api.blog.ServiceTest;

import com.api.blog.DTOs.NewPostDto;
import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.ErrorHandling.customExceptions.CreatingResourceException;
import com.api.blog.Mappers.PostMapper;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Repositories.PostRepository;
import com.api.blog.Repositories.UserRepository;
import com.api.blog.Service.CloudinaryService;
import com.api.blog.Service.PostService;
import static org.junit.jupiter.api.Assertions.*;
import com.api.blog.Utils.RedisKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private PostMapper postMapper;


    @InjectMocks
    private PostService postService;

    private User mockUser;

    private NewPostDto newPostDtoWithImage;
    private NewPostDto newPostDtoWithoutImage;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("pepeTest")
                .build();

        newPostDtoWithImage = NewPostDto.builder()
                .message("Test message")
                .file(mock(MultipartFile.class))
                .build();

        newPostDtoWithoutImage = NewPostDto.builder()
                .message("Test message")
                .file(null)
                .build();
    }


    @Test
    @DisplayName("Should create Post without image successfully")
    public void create_shouldCreatePostSuccessfullyWithoutFile(){

        // GIVEN
        String currentUser = "pepeTest";

        Post postCreated = Post.builder()
                .id(1L)
                .message("New post")
                .user(mockUser)
                .build();

        PostResponseDTO expectedResponse = new PostResponseDTO();


        // WHEN
        when(userRepository.findByUsername(currentUser)).thenReturn(Optional.of(mockUser));
        when(postRepository.save(any(Post.class))).thenReturn(postCreated);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(postMapper.toResponseDto(
                eq(postCreated),
                eq(false),
                eq(true),
                eq(null),
                eq(0L),
                eq(0L)
        )).thenReturn(expectedResponse);

        PostResponseDTO response = postService.create(newPostDtoWithoutImage, currentUser);

        // THEN
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(postRepository, times(1)).save(any(Post.class));
        verify(valueOperations, times(1)).increment(RedisKeys.postsAmount(mockUser.getId()));
        verify(postMapper, times(1)).toResponseDto(any(Post.class), anyBoolean(), anyBoolean(), any(), anyLong(), anyLong());

    }

    @Test
    @DisplayName("Should create Post with image successfully")
    public void create_shouldCreatePostSuccessfullyWithFile(){

        // GIVEN
        String currentUser = "pepeTest";

        Map<String,String> uploadImageResponse = new HashMap<>();
        uploadImageResponse.put("secureUrl","https:secure:link");
        uploadImageResponse.put("imagePublicId","image-public-id");


        Post postCreated = Post.builder()
                .id(1L)
                .message("New post")
                .user(mockUser)
                .build();

        PostResponseDTO expectedResponse =  new PostResponseDTO();


        // WHEN
        when(userRepository.findByUsername(currentUser)).thenReturn(Optional.of(mockUser));
        when(cloudinaryService.uploadImage(newPostDtoWithImage.getFile())).thenReturn(uploadImageResponse);
        when(postRepository.save(any(Post.class))).thenReturn(postCreated);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(postMapper.toResponseDto(
                eq(postCreated),
                eq(false),
                eq(true),
                eq(null),
                eq(0L),
                eq(0L)
        )).thenReturn(expectedResponse);

        PostResponseDTO response = postService.create(newPostDtoWithImage, currentUser);

        // THEN
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(postRepository, times(1)).save(any(Post.class));
        verify(valueOperations, times(1)).increment(RedisKeys.postsAmount(mockUser.getId()));
        verify(postMapper, times(1)).toResponseDto(any(Post.class), anyBoolean(), anyBoolean(), any(), anyLong(), anyLong());
        verify(cloudinaryService, times(1)).uploadImage(newPostDtoWithImage.getFile());
    }

    @Test
    @DisplayName("Should throw CreatingResourceException and delete image when create post fails")
    public void create_shouldThrowCreatingResourceExceptionAndDeleteImage(){


        String currentUser = "pepeTest";

        Map<String,String> uploadImageResponse = new HashMap<>();
        uploadImageResponse.put("secureUrl","https:secure:link");
        uploadImageResponse.put("imagePublicId","image-public-id");


        when(userRepository.findByUsername(currentUser)).thenReturn(Optional.of(mockUser));
        when(cloudinaryService.uploadImage(newPostDtoWithImage.getFile())).thenReturn(uploadImageResponse);
        when(postRepository.save(any(Post.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(CreatingResourceException.class, () -> postService.create(newPostDtoWithImage, currentUser));
        verify(cloudinaryService,times(1)).deleteImage(uploadImageResponse.get("imagePublicId"));


    }

    @Test
    @DisplayName("Should throw CreatingResourceException and not delete image when create post fails")
    public void create_shouldThrowCreatingResourceExceptionAndNotDeleteImage(){

        String currentUser = "pepeTest";

        when(userRepository.findByUsername(currentUser)).thenReturn(Optional.of(mockUser));
        when(postRepository.save(any(Post.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(CreatingResourceException.class, () -> postService.create(newPostDtoWithoutImage, currentUser));

        verifyNoInteractions(postMapper, redisTemplate, cloudinaryService);

    }


}
