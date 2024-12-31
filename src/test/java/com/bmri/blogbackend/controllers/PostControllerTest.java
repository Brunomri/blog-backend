package com.bmri.blogbackend.controllers;

import com.bmri.blogbackend.dtos.request.PostCreateDto;
import com.bmri.blogbackend.dtos.response.PostResponseDto;
import com.bmri.blogbackend.exceptions.ObjectNotFoundException;
import com.bmri.blogbackend.services.interfaces.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PostController.class)
@WithMockUser(username = "user", roles = {"USER"})
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    private static List<PostResponseDto> postResponseDtos;
    private static Page<PostResponseDto> postResponseDtoPage;
    private static Page<PostResponseDto> publishedPostResponseDtoPage;
    private static Page<PostResponseDto> categoryPostResponseDtoPage;
    private static Page<PostResponseDto> tagPostResponseDtoPage;
    private static Pageable pageRequest;
    private static PostCreateDto postCreateDto;
    private static PostCreateDto invalidPostCreateDto;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        var post1 = new PostResponseDto(1L, "post1", "content1", "category1",
                List.of("tag1", "tag2"), false, LocalDateTime.now(), LocalDateTime.now());

        var post2 = new PostResponseDto(2L, "post2", "content2", "category2",
                List.of("tag3", "tag4"), true, LocalDateTime.now(), LocalDateTime.now());

        postResponseDtos = List.of(post1, post2);
        pageRequest = PageRequest.of(0, 2);
        postResponseDtoPage = new PageImpl<>(postResponseDtos, pageRequest, postResponseDtos.size());
        publishedPostResponseDtoPage = new PageImpl<>(postResponseDtos.stream()
                .filter(PostResponseDto::isPublished).toList(), pageRequest, postResponseDtos.size());
        categoryPostResponseDtoPage = new PageImpl<>(postResponseDtos.stream()
                .filter(postResponseDto -> postResponseDto.getCategory().equals("category1")).toList(), pageRequest, postResponseDtos.size());
        tagPostResponseDtoPage = new PageImpl<>(postResponseDtos.stream()
                .filter(postResponseDto -> postResponseDto.getTags().equals(List.of("tag1", "tag2"))).toList(), pageRequest, postResponseDtos.size());
        postCreateDto = new PostCreateDto("newPost", "newContent", "newCategory",
                List.of("newTag1", "newTag2"), false);
        invalidPostCreateDto = new PostCreateDto(null, "newContent", "newCategory",
                List.of("newTag1", "newTag2"), false);
    }

    @Test
    void testGetAllPosts() throws Exception {
        when(postService.getAllPosts(any(Pageable.class))).thenReturn(postResponseDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()").value(postResponseDtoPage.getTotalElements()));

        verify(postService, times(1)).getAllPosts(any(Pageable.class));
    }

    @Test
    void testGetAllByPublished() throws Exception {
        when(postService.getAllByPublished(anyBoolean(), any(Pageable.class))).thenReturn(publishedPostResponseDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/published").param("published", "true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()").value(publishedPostResponseDtoPage.getNumberOfElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].published").value(true));

        verify(postService, times(1)).getAllByPublished(anyBoolean(), any(Pageable.class));
    }

    @Test
    void testGetPostById() throws Exception {
        var expectedPost = postResponseDtos.getFirst();

        when(postService.getPostById(anyLong())).thenReturn(postResponseDtos.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedPost.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(expectedPost.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(expectedPost.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value(expectedPost.getCategory()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]").value(expectedPost.getTags().get(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[1]").value(expectedPost.getTags().get(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.published").value(expectedPost.isPublished()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(expectedPost.getCreatedAt().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(expectedPost.getUpdatedAt().toString()));

        verify(postService, times(1)).getPostById(anyLong());
    }

    @Test
    void testGetPostByIdNotFound() throws Exception {
        when(postService.getPostById(anyLong())).thenThrow(new ObjectNotFoundException("Post not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertThatThrownBy(() -> postService.getPostById(anyLong()))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Post not found");

        verify(postService, times(2)).getPostById(anyLong());
    }

    @Test
    void testGetPostByTitle() throws Exception {
        var expectedPost = postResponseDtos.getFirst();

        when(postService.getPostByTitle(anyString())).thenReturn(postResponseDtos.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/title").param("title", expectedPost.getTitle()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedPost.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(expectedPost.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(expectedPost.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value(expectedPost.getCategory()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]").value(expectedPost.getTags().get(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[1]").value(expectedPost.getTags().get(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.published").value(expectedPost.isPublished()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(expectedPost.getCreatedAt().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(expectedPost.getUpdatedAt().toString()));

        verify(postService, times(1)).getPostByTitle(anyString());
    }

    @Test
    void testGetPostByTitleNotFound() throws Exception {
        when(postService.getPostByTitle(anyString())).thenThrow(new ObjectNotFoundException("Post not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/title").param("title", "title1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertThatThrownBy(() -> postService.getPostByTitle(anyString()))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Post not found");

        verify(postService, times(2)).getPostByTitle(anyString());
    }

    @Test
    void testGetPostsByCategory() throws Exception {
        when(postService.getPostsByCategory(anyString(), any(Pageable.class))).thenReturn(categoryPostResponseDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/category").param("category", "category1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()").value(publishedPostResponseDtoPage.getNumberOfElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].category").value("category1"));

        verify(postService, times(1)).getPostsByCategory(anyString(), any(Pageable.class));
    }

    @Test
    void testGetPostsByTag() throws Exception {
        when(postService.getPostsByTag(anyString(), any(Pageable.class))).thenReturn(tagPostResponseDtoPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/tag").param("tag", "tag1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()").value(publishedPostResponseDtoPage.getNumberOfElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].tags[0]").value("tag1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].tags[1]").value("tag2"));

        verify(postService, times(1)).getPostsByTag(anyString(), any(Pageable.class));
    }

    @Test
    void testCreatePost() throws Exception {
        var expectedPost = postResponseDtos.getFirst();

        when(postService.createPost(any(PostCreateDto.class))).thenReturn(expectedPost);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Location", "http://localhost/posts/1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedPost.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(expectedPost.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(expectedPost.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value(expectedPost.getCategory()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]").value(expectedPost.getTags().get(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[1]").value(expectedPost.getTags().get(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.published").value(expectedPost.isPublished()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(expectedPost.getCreatedAt().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(expectedPost.getUpdatedAt().toString()));

        verify(postService, times(1)).createPost(any(PostCreateDto.class));
    }

    @Test
    void testCreatePostInvalidRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(invalidPostCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].status").value("400"));

        verify(postService, never()).createPost(any(PostCreateDto.class));
    }

    @Test
    void testCreatePostDataIntegrityViolation() throws Exception {
        when(postService.createPost(any(PostCreateDto.class))).thenThrow(new DataIntegrityViolationException("Database constraint violated"));

        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("409"));

        verify(postService, times(1)).createPost(any(PostCreateDto.class));
    }

    @Test
    void testUpdatePost() throws Exception {
        var expectedPost = postResponseDtos.getFirst();

        when(postService.updatePost(anyLong(), any(PostCreateDto.class))).thenReturn(expectedPost);

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedPost.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(expectedPost.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(expectedPost.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value(expectedPost.getCategory()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]").value(expectedPost.getTags().get(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[1]").value(expectedPost.getTags().get(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.published").value(expectedPost.isPublished()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(expectedPost.getCreatedAt().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(expectedPost.getUpdatedAt().toString()));

        verify(postService, times(1)).updatePost(anyLong(), any(PostCreateDto.class));
    }

    @Test
    void testUpdatePostInvalidRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(invalidPostCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.violations[0].status").value("400"));

        verify(postService, never()).updatePost(anyLong(), any(PostCreateDto.class));
    }

    @Test
    void testUpdatePostDataIntegrityViolation() throws Exception {
        when(postService.updatePost(anyLong(), any(PostCreateDto.class))).thenThrow(new DataIntegrityViolationException("Database constraint violated"));

        mockMvc.perform(MockMvcRequestBuilders.put("/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("409"));

        verify(postService, times(1)).updatePost(anyLong(), any(PostCreateDto.class));
    }

    @Test
    void testTogglePublish() throws Exception {
        when(postService.togglePublish(anyLong(), anyBoolean())).thenReturn(postResponseDtos.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/1/publish").param("publish", "false")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.published").value(false));

        verify(postService, times(1)).togglePublish(anyLong(), anyBoolean());
    }

    @Test
    void testTogglePublishNotFound() throws Exception {
        when(postService.togglePublish(anyLong(), anyBoolean())).thenThrow(new ObjectNotFoundException("Post not found"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/1/publish").param("publish", "false")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));

        verify(postService, times(1)).togglePublish(anyLong(), anyBoolean());
    }

    @Test
    void testUpdateContent() throws Exception {
        when(postService.updateContent(anyLong(), anyString())).thenReturn(postResponseDtos.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/1/content")
                        .content(objectMapper.writeValueAsString("test"))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(postResponseDtos.getFirst().getContent()));

        verify(postService, times(1)).updateContent(anyLong(), anyString());
    }

    @Test
    void testUpdateContentNotFound() throws Exception {
        when(postService.updateContent(anyLong(), anyString())).thenThrow(new ObjectNotFoundException("Post not found"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/1/content")
                        .content(objectMapper.writeValueAsString("test"))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));

        verify(postService, times(1)).updateContent(anyLong(), anyString());
    }

    @Test
    void testDeletePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/1")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(postService, times(1)).deletePost(anyLong());
    }

    @Test
    void testDeletePostNotFound() throws Exception {
        when(postService.deletePost(anyLong())).thenThrow(new ObjectNotFoundException("Post not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/1")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));

        verify(postService, times(1)).deletePost(anyLong());
    }

}
