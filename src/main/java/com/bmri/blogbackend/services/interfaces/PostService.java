package com.bmri.blogbackend.services.interfaces;

import com.bmri.blogbackend.dtos.request.PostCreateDto;
import com.bmri.blogbackend.dtos.response.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Page<PostResponseDto> getAllPosts(Pageable pageable);

    Page<PostResponseDto> getAllByPublished(boolean published, Pageable pageable);

    PostResponseDto getPostById(Long id);

    PostResponseDto getPostByTitle(String title);

    Page<PostResponseDto> getPostsByCategory(String category, Pageable pageable);

    Page<PostResponseDto> getPostsByTag(String tag, Pageable pageable);

    PostResponseDto createPost(PostCreateDto newPost);

    PostResponseDto updatePost(Long id, PostCreateDto updatedPost);

    PostResponseDto togglePublish(Long id, boolean publish);

    PostResponseDto updateContent(Long id, String content);

    boolean deletePost(Long id);

}
