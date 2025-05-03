package com.bmri.blogbackend.services.implementations;

import com.bmri.blogbackend.dtos.request.PostCreateDto;
import com.bmri.blogbackend.dtos.response.PostResponseDto;
import com.bmri.blogbackend.exceptions.ObjectNotFoundException;
import com.bmri.blogbackend.mappers.PostMapper;
import com.bmri.blogbackend.repositories.PostRepository;
import com.bmri.blogbackend.services.interfaces.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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
    public Page<PostResponseDto> getAllByPublished(boolean published, Pageable pageable) {
        return PostMapper.toDto(postRepository.getByPublished(published, pageable));
    }

    @Override
    public PostResponseDto getPostById(Long id) {
        var postEntity = postRepository.findById(id);
        return postEntity.map(PostMapper::toDto).orElseThrow(() -> new ObjectNotFoundException("Post not found with ID = " + id));
    }

    @Override
    public PostResponseDto getPostByTitle(String title) {
        var postEntity = postRepository.getByTitle(title);
        return postEntity.map(PostMapper::toDto).orElseThrow(() -> new ObjectNotFoundException("Post not found with title = " + title));
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
    @Transactional(readOnly = false)
    public PostResponseDto createPost(PostCreateDto newPost) {
        return PostMapper.toDto(postRepository.save(PostMapper.toEntity(newPost)));
    }

    @Override
    @Transactional(readOnly = false)
    public PostResponseDto updatePost(Long id, PostCreateDto updatedPost) {
        var currentPost = getPostById(id);

        if (updatedPost.getTitle() != null) {
            currentPost.setTitle(updatedPost.getTitle());
        }
        if (updatedPost.getContent() != null) {
            currentPost.setContent(updatedPost.getContent());
        }
        if (updatedPost.getCategory() != null) {
            currentPost.setCategory(updatedPost.getCategory());
        }
        if (updatedPost.getTags() != null) {
            currentPost.setTags(updatedPost.getTags());
        }
        currentPost.setPublished(updatedPost.isPublished());

        return PostMapper.toDto(postRepository.save(PostMapper.toEntity(currentPost)));
    }

    @Override
    @Transactional(readOnly = false)
    public PostResponseDto togglePublish(Long id, boolean publish) {
        var currentPost = getPostById(id);
        currentPost.setPublished(publish);
        return PostMapper.toDto(postRepository.save(PostMapper.toEntity(currentPost)));
    }

    @Override
    @Transactional(readOnly = false)
    public PostResponseDto updateContent(Long id, String content) {
        var currentPost = getPostById(id);
        currentPost.setContent(content);
        return PostMapper.toDto(postRepository.save(PostMapper.toEntity(currentPost)));
    }

    @Override
    @Transactional(readOnly = false)
    public boolean deletePost(Long id) {
        var currentPost = getPostById(id);
        postRepository.delete(PostMapper.toEntity(currentPost));
        return true;
    }

}
