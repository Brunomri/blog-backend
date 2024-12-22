package com.bmri.blogbackend.services.implementations;

import com.bmri.blogbackend.domain.PostEntity;
import com.bmri.blogbackend.repositories.PostRepository;
import com.bmri.blogbackend.services.interfaces.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Page<PostEntity> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<PostEntity> getAllByPublished(Boolean published, Pageable pageable) {
        return postRepository.getByPublishedIsTrue(pageable);
    }

    @Override
    public PostEntity getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public PostEntity getPostByTitle(String title) {
        return postRepository.getByTitle(title).orElse(null);
    }

    @Override
    public Page<PostEntity> getPostsByCategory(String category, Pageable pageable) {
        return postRepository.getByCategory(category, pageable);
    }

    @Override
    public Page<PostEntity> getPostsByTag(String tag, Pageable pageable) {
        return postRepository.getByTagsContaining(tag, pageable);
    }

    @Override
    public PostEntity createPost(PostEntity newPost) {
        return postRepository.save(newPost);
    }

    @Override
    public Optional<PostEntity> updatePost(Long id, PostEntity updatedPost) {
        var currentPost = postRepository.findById(id);
        if (currentPost.isPresent()) {
            var postEntity = currentPost.get();

            postEntity.setTitle(updatedPost.getTitle());
            postEntity.setContent(updatedPost.getContent());
            postEntity.setCategory(updatedPost.getCategory());
            postEntity.setTags(updatedPost.getTags());
            postEntity.setPublished(updatedPost.isPublished());

            return Optional.of(postRepository.save(postEntity));
        }
        return Optional.empty();
    }

    @Override
    public boolean deletePost(Long id) {
        var currentPost = postRepository.findById(id);
        if (currentPost.isPresent()) {
            postRepository.delete(currentPost.get());
            return true;
        }
        return false;
    }
}
