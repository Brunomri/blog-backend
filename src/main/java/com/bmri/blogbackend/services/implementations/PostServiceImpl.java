package com.bmri.blogbackend.services.implementations;

import com.bmri.blogbackend.domain.PostEntity;
import com.bmri.blogbackend.dtos.response.PostResponseDto;
import com.bmri.blogbackend.mappers.PostMapper;
import com.bmri.blogbackend.repositories.PostRepository;
import com.bmri.blogbackend.services.interfaces.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        return PostMapper.toDto(postRepository.findAll(pageable));
    }

    @Override
    public Page<PostResponseDto> getAllByPublished(Boolean published, Pageable pageable) {
        return PostMapper.toDto(postRepository.findAll(pageable));
    }

    @Override
    public PostResponseDto getPostById(Long id) {
        var postEntity = postRepository.findById(id);
        return postEntity.map(PostMapper::toDto).orElse(null);
    }

    @Override
    public PostResponseDto getPostByTitle(String title) {
        var postEntity = postRepository.getByTitle(title);
        return postEntity.map(PostMapper::toDto).orElse(null);
    }

    @Override
    public Page<PostResponseDto> getPostsByCategory(String category, Pageable pageable) {
        return PostMapper.toDto(postRepository.getByCategory(category, pageable));
    }

    @Override
    public Page<PostResponseDto> getPostsByTag(String tag, Pageable pageable) {
        return PostMapper.toDto(postRepository.getByTagsContaining(tag, pageable));
    }

    @Override
    public PostResponseDto createPost(PostEntity newPost) {
        return PostMapper.toDto(postRepository.save(newPost));
    }

    @Override
    public PostResponseDto updatePost(Long id, PostEntity updatedPost) {
        var currentPost = postRepository.findById(id);
        if (currentPost.isPresent()) {
            var postEntity = currentPost.get();

            postEntity.setTitle(updatedPost.getTitle());
            postEntity.setContent(updatedPost.getContent());
            postEntity.setCategory(updatedPost.getCategory());
            postEntity.setTags(updatedPost.getTags());
            postEntity.setPublished(updatedPost.isPublished());

            return PostMapper.toDto(postRepository.save(postEntity));
        }
        return null;
    }

    @Override
    public PostResponseDto togglePublish(Long id, boolean publish) {
        var currentPost = postRepository.findById(id);
        if (currentPost.isPresent()) {
            var postEntity = currentPost.get();
            postEntity.setPublished(publish);
            return PostMapper.toDto(postRepository.save(postEntity));
        }
        return null;
    }

    @Override
    public PostResponseDto updateContent(Long id, String content) {
        var currentPost = postRepository.findById(id);
        if (currentPost.isPresent()) {
            var postEntity = currentPost.get();
            postEntity.setContent(content);
            return PostMapper.toDto(postRepository.save(postEntity));
        }
        return null;
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
