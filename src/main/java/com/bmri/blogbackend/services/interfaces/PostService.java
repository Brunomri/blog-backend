package com.bmri.blogbackend.services.interfaces;

import com.bmri.blogbackend.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostService {
    Page<PostEntity> getAllPosts(Pageable pageable);

    Page<PostEntity> getAllByPublished(Boolean published, Pageable pageable);

    PostEntity getPostById(Long id);

    PostEntity getPostByTitle(String title);

    Page<PostEntity> getPostsByCategory(String category, Pageable pageable);

    Page<PostEntity> getPostsByTag(String tag, Pageable pageable);

    PostEntity createPost(PostEntity newPost);

    Optional<PostEntity> updatePost(Long id, PostEntity updatedPost);

    boolean deletePost(Long id);
}
