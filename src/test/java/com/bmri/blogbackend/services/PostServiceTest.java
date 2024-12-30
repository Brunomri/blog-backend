package com.bmri.blogbackend.services;

import com.bmri.blogbackend.domain.PostEntity;
import com.bmri.blogbackend.dtos.request.PostCreateDto;
import com.bmri.blogbackend.exceptions.ObjectNotFoundException;
import com.bmri.blogbackend.repositories.PostRepository;
import com.bmri.blogbackend.services.implementations.PostServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private static List<PostEntity> postEntities;
    private static Page<PostEntity> postEntitiesPage;
    private static Page<PostEntity> publishedPostEntitiesPage;
    private static Page<PostEntity> categoryPostEntitiesPage;
    private static Page<PostEntity> tagPostEntitiesPage;
    private static Pageable pageRequest;
    private static PostCreateDto postCreateDto;

    @BeforeAll
    static void setUp() {
        var post1 = new PostEntity("post1", "content1", "category1", List.of("tag1", "tag2"), false);
        post1.setId(1L);
        post1.setCreatedAt(LocalDateTime.now());
        post1.setUpdatedAt(LocalDateTime.now());

        var post2 = new PostEntity("post2", "content2", "category2", List.of("tag3", "tag4"), true);
        post2.setId(2L);
        post2.setCreatedAt(LocalDateTime.now());
        post2.setUpdatedAt(LocalDateTime.now());

        postEntities = List.of(post1, post2);
        pageRequest = PageRequest.of(0, 2);
        postEntitiesPage = new PageImpl<>(postEntities, pageRequest, postEntities.size());
        publishedPostEntitiesPage = new PageImpl<>(postEntities.stream()
                .filter(PostEntity::isPublished).toList(), pageRequest, postEntities.size());
        categoryPostEntitiesPage = new PageImpl<>(postEntities.stream()
                .filter(postEntity -> postEntity.getCategory().equals("category1")).toList(), pageRequest, postEntities.size());
        tagPostEntitiesPage = new PageImpl<>(postEntities.stream()
                .filter(postEntity -> postEntity.getTags().contains("tag1")).toList(), pageRequest, postEntities.size());
        postCreateDto = new PostCreateDto("post1", "content1", "category1", List.of("tag1", "tag2"), false);
    }

    @Test
    void testGetAllPosts() {
        when(postRepository.findAll(any(Pageable.class))).thenReturn(postEntitiesPage);

        var result = postService.getAllPosts(pageRequest);

        assertEquals(postEntities.size(), result.getTotalElements());

        assertEquals(postEntities.getFirst().getId(), result.getContent().get(0).getId());
        assertEquals(postEntities.get(1).getId(), result.getContent().get(1).getId());

        verify(postRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllByPublished() {
        when(postRepository.getByPublished(anyBoolean(), any(Pageable.class)))
                .thenReturn(publishedPostEntitiesPage);

        var result = postService.getAllByPublished(true, pageRequest);

        assertEquals(publishedPostEntitiesPage.getTotalElements(), result.getTotalElements());
        assertEquals(publishedPostEntitiesPage.getContent().getFirst().isPublished(), result.getContent().getFirst().isPublished());

        verify(postRepository, times(1)).getByPublished(anyBoolean(), any(Pageable.class));
    }

    @Test
    void testPostById() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(postEntities.getFirst()));

        var result = postService.getPostById(1L);

        assertEquals(postEntities.getFirst().getId(), result.getId());

        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    void testPostByIdNotFound() {
        when(postRepository.findById(anyLong())).thenThrow(new ObjectNotFoundException("Post not found"));

        assertThrows(ObjectNotFoundException.class, () -> postService.getPostById(1L));

        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    void testPostByTitle() {
        when(postRepository.getByTitle(anyString())).thenReturn(Optional.ofNullable(postEntities.getFirst()));

        var result = postService.getPostByTitle("post1");

        assertEquals(postEntities.getFirst().getTitle(), result.getTitle());

        verify(postRepository, times(1)).getByTitle(anyString());
    }

    @Test
    void testPostByTitleNotFound() {
        when(postRepository.getByTitle(anyString())).thenThrow(new ObjectNotFoundException("Post not found"));

        assertThrows(ObjectNotFoundException.class, () -> postService.getPostByTitle("post1"));

        verify(postRepository, times(1)).getByTitle(anyString());
    }

    @Test
    void testGetPostsByCategory() {
        when(postRepository.getByCategory(anyString(), any(Pageable.class)))
                .thenReturn(categoryPostEntitiesPage);

        var result = postService.getPostsByCategory("category1", pageRequest);

        assertEquals(categoryPostEntitiesPage.getTotalElements(), result.getTotalElements());
        assertEquals(categoryPostEntitiesPage.getContent().getFirst().getCategory(), result.getContent().getFirst().getCategory());

        verify(postRepository, times(1)).getByCategory(anyString(), any(Pageable.class));
    }

    @Test
    void testGetPostsByTag() {
        when(postRepository.getByTagsContaining(anyString(), any(Pageable.class)))
                .thenReturn(tagPostEntitiesPage);

        var result = postService.getPostsByTag("tag1", pageRequest);

        assertEquals(categoryPostEntitiesPage.getTotalElements(), result.getTotalElements());
        assertEquals(categoryPostEntitiesPage.getContent().getFirst().getTags().getFirst(), result.getContent().getFirst().getTags().getFirst());

        verify(postRepository, times(1)).getByTagsContaining(anyString(), any(Pageable.class));
    }

    @Test
    void testCreatePost() {
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntities.getFirst());

        var result = postService.createPost(postCreateDto);

        assertEquals(postEntities.getFirst().getId(), result.getId());
        assertEquals(postCreateDto.getTitle(), result.getTitle());
        assertEquals(postCreateDto.getContent(), result.getContent());
        assertEquals(postCreateDto.getCategory(), result.getCategory());
        assertEquals(postCreateDto.getTags(), result.getTags());
        assertEquals(postCreateDto.isPublished(), result.isPublished());

        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void testCreatePostConflict() {
        when(postRepository.save(any(PostEntity.class))).thenThrow(new DataIntegrityViolationException("Title already exists"));

        assertThrows(DataIntegrityViolationException.class, () -> postService.createPost(postCreateDto));

        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void testCreatePostViolation() {
        when(postRepository.save(any(PostEntity.class))).thenThrow(new ConstraintViolationException("Constraint violated", new HashSet<>()));

        assertThrows(ConstraintViolationException.class, () -> postService.createPost(postCreateDto));

        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void testUpdatePost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(postEntities.getFirst()));
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntities.getFirst());

        var result = postService.updatePost(1L, postCreateDto);

        assertEquals(postEntities.getFirst().getId(), result.getId());
        assertEquals(postCreateDto.getTitle(), result.getTitle());
        assertEquals(postCreateDto.getContent(), result.getContent());
        assertEquals(postCreateDto.getCategory(), result.getCategory());
        assertEquals(postCreateDto.getTags(), result.getTags());
        assertEquals(postCreateDto.isPublished(), result.isPublished());
        
        verify(postRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void testUpdatePostConflict() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(postEntities.getFirst()));
        when(postRepository.save(any(PostEntity.class))).thenThrow(new DataIntegrityViolationException("Title already exists"));

        assertThrows(DataIntegrityViolationException.class, () -> postService.updatePost(1L, postCreateDto));

        verify(postRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void testUpdatePostViolation() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(postEntities.getFirst()));
        when(postRepository.save(any(PostEntity.class))).thenThrow(new ConstraintViolationException("Constraint violated", new HashSet<>()));

        assertThrows(ConstraintViolationException.class, () -> postService.updatePost(1L, postCreateDto));

        verify(postRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void testTogglePublish() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(postEntities.getFirst()));
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntities.getFirst());

        var result = postService.togglePublish(postEntities.getFirst().getId(), false);

        assertFalse(result.isPublished());

        verify(postRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void testUpdateContent() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(postEntities.getFirst()));
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntities.getFirst());

        var result = postService.updateContent(postEntities.getFirst().getId(), "new content");

        assertEquals(postEntities.getFirst().getContent(), result.getContent());

        verify(postRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).save(any(PostEntity.class));
    }

    @Test
    void deletePost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(postEntities.getFirst()));

        assertTrue(postService.deletePost(postEntities.getFirst().getId()));

        verify(postRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).delete(any(PostEntity.class));
    }

}
