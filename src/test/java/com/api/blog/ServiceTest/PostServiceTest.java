package com.api.blog.ServiceTest;

import com.api.blog.DTOs.EditPostDTO;
import com.api.blog.DTOs.NewPostDto;
import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.ErrorHandling.customExceptions.CreatingResourceException;
import com.api.blog.Mappers.PostMapper;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Repositories.PostRepository;
import com.api.blog.Repositories.UserRepository;
import com.api.blog.Service.CloudinaryService;
import com.api.blog.Service.CommentService;
import com.api.blog.Service.LikeService;
import com.api.blog.Service.PostService;
import static org.junit.jupiter.api.Assertions.*;
import com.api.blog.Utils.RedisKeys;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private LikeService likeService;

    @Mock
    private CommentService commentService;

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
    private Post mockPost;

    private NewPostDto newPostDtoWithImage;
    private NewPostDto newPostDtoWithoutImage;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("pepeTest")
                .profileImgKey(null)
                .build();

        mockPost = Post.builder()
                .id(1L)
                .user(mockUser)
                .imageUrl("https/image-fake")
                .imagePublicId("imagePublicId")
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


        String currentUser = "pepeTest";

        Post postCreated = Post.builder()
                .id(1L)
                .message("New post")
                .user(mockUser)
                .build();

        PostResponseDTO expectedResponse = new PostResponseDTO();



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


        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(postRepository, times(1)).save(any(Post.class));
        verify(valueOperations, times(1)).increment(RedisKeys.postsAmount(mockUser.getId()));
        verify(postMapper, times(1)).toResponseDto(any(Post.class), anyBoolean(), anyBoolean(), any(), anyLong(), anyLong());

    }

    @Test
    @DisplayName("Should create Post with image successfully")
    public void create_shouldCreatePostSuccessfullyWithFile(){

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


        assertNotNull(response);
        assertEquals(expectedResponse, response);

        verify(postRepository, times(1)).save(any(Post.class));
        verify(valueOperations, times(1)).increment(RedisKeys.postsAmount(mockUser.getId()));
        verify(postMapper, times(1)).toResponseDto(any(Post.class), anyBoolean(), anyBoolean(), any(), anyLong(), anyLong());
        verify(cloudinaryService, times(1)).uploadImage(newPostDtoWithImage.getFile());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException")
    public void create_shouldThrowEntityNotFoundException(){

        String currentUser = "pepeTest";

        assertThrows(EntityNotFoundException.class, () -> postService.create(newPostDtoWithImage, currentUser));

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

    @Test
    @DisplayName("Should delete post if current user is owner and post has image")
    public void delete_shouldDeletePostWithImageSuccessfully(){
        String currentUser = "pepeTest";


        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);


        postService.delete(mockPost.getId(), currentUser);


        verify(postRepository,times(1)).findById(anyLong());
        verify(postRepository, times(1)).save(mockPost);
        verify(cloudinaryService,times(1)).deleteImage(anyString());
        verify(valueOperations,times(1)).decrement(RedisKeys.postsAmount(mockPost.getUser().getId()));



    }

    @Test
    @DisplayName("Should delete post if current user is owner and post has no image")
    public void delete_shouldDeletePostWithoutImageSuccessfully(){
        String currentUser = "pepeTest";

        Post postWithoutImage = Post.builder()
                .id(1L)
                .user(mockUser)
                .imageUrl(null)
                .imagePublicId(null)
                .deleted_at(null)
                .build();


        when(postRepository.findById(postWithoutImage.getId())).thenReturn(Optional.of(postWithoutImage));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);


        postService.delete(postWithoutImage.getId(), currentUser);


        verify(postRepository,times(1)).findById(postWithoutImage.getId());
        verify(postRepository, times(1)).save(postWithoutImage);
        verify(cloudinaryService,never()).deleteImage(anyString());
        verify(valueOperations,times(1)).decrement(RedisKeys.postsAmount(mockPost.getUser().getId()));



    }

    @Test
    @DisplayName("Should throw AccessDeniedException if user is not owner")
    public void delete_shouldThrowAccessDeniedExceptionIfUserIsNotOwner(){

        String currentUser = "notPepeTest";

        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(mockPost));

        assertThrows(AccessDeniedException.class, () -> postService.delete(mockPost.getId(), currentUser));

        verify(cloudinaryService,never()).deleteImage(anyString());
        verify(postRepository,never()).save(mockPost);
        verify(valueOperations,never()).decrement(RedisKeys.postsAmount(mockPost.getUser().getId()));




    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if post is already deleted")
    public void delete_shouldThrowIllegalArgumentExceptionIfIsAlreadyDeleted(){

        String currentUser = "pepeTest";

        Post postAlreadyDeleted = Post.builder()
                .id(1L)
                .user(mockUser)
                .imageUrl("https/image-fake")
                .imagePublicId("imagePublicId")
                .deleted_at(LocalDateTime.now())
                .build();

        when(postRepository.findById(mockPost.getId())).thenReturn(Optional.of(postAlreadyDeleted));

        assertThrows(IllegalArgumentException.class, () -> postService.delete(postAlreadyDeleted.getId(),currentUser));

        verify(cloudinaryService,never()).deleteImage(anyString());
        verify(postRepository,never()).save(mockPost);
        verify(valueOperations,never()).decrement(RedisKeys.postsAmount(mockPost.getUser().getId()));




    }


    @Test
    @DisplayName("Should update post successfully")
    public void update_shouldUpdatePostSuccessfully(){

        String currentUser = "pepeTest";
        EditPostDTO editPostDTO = EditPostDTO.builder()
                .postId(1L)
                .newMessage("This is a new message")
                .build();


        when(userRepository.findByUsername(currentUser)).thenReturn(Optional.of(mockUser));
        when(postRepository.findById(editPostDTO.getPostId())).thenReturn(Optional.of(mockPost));
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        when(likeService.isPostLiked(any(User.class), any(Post.class))).thenReturn(false);
        when(likeService.getPostLikesCount(any(Post.class))).thenReturn(0L);
        when(commentService.getCommentsAmount(anyLong())).thenReturn(0L);

        when(postMapper.toResponseDto(any(Post.class), anyBoolean(),anyBoolean(), any(), anyLong(), anyLong())).thenReturn(new PostResponseDTO());

        PostResponseDTO result = postService.update(editPostDTO,currentUser);

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository,times(1)).save(postArgumentCaptor.capture());
        Post postSaved = postArgumentCaptor.getValue();
        assertEquals(editPostDTO.getNewMessage(), postSaved.getMessage());

        assertNotNull(result);


    }






}
