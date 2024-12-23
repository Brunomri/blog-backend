package com.bmri.blogbackend.controllers;

import com.bmri.blogbackend.dtos.request.PostCreateDto;
import com.bmri.blogbackend.dtos.response.PostResponseDto;
import com.bmri.blogbackend.mappers.PostMapper;
import com.bmri.blogbackend.services.interfaces.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Validated
@RequestMapping("/posts")
public class PostController {

    private static final String PAGE_DEFAULT_NUMBER = "0";
    private static final String PAGE_DEFAULT_SIZE = "50";
    private static final int PAGE_MIN_NUMBER = 0;
    private static final int PAGE_MIN_SIZE = 1;
    private static final int PAGE_MAX_SIZE = 500;

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestParam(value = "page", required = false, defaultValue = PAGE_DEFAULT_NUMBER)
            @Min(value = PAGE_MIN_NUMBER, message = "Page number must be greater than or equal to ${PAGE_MIN_NUMBER}") int page,
            @RequestParam(value = "size", required = false, defaultValue = PAGE_DEFAULT_SIZE)
            @Min(value = PAGE_MIN_SIZE, message = "Page size must be greater than or equal to ${PAGE_MIN_SIZE}")
            @Max(value = PAGE_MAX_SIZE, message = "Page size must be less than or equal to ${PAGE_MAX_SIZE}") int size
    ) {
        var postsPage = postService.getAllPosts(PageRequest.of(page, size));

        if (postsPage != null) {
            return ResponseEntity.ok().body(postsPage);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/published", produces = "application/json")
    public ResponseEntity<Page<PostResponseDto>> getAllByPublished(
            @RequestParam(value = "published") boolean published,
            @RequestParam(value = "page", required = false, defaultValue = PAGE_DEFAULT_NUMBER)
            @Min(value = PAGE_MIN_NUMBER, message = "Page number must be greater than or equal to ${PAGE_MIN_NUMBER}") int page,
            @RequestParam(value = "size", required = false, defaultValue = PAGE_DEFAULT_SIZE)
            @Min(value = PAGE_MIN_SIZE, message = "Page size must be greater than or equal to ${PAGE_MIN_SIZE}")
            @Max(value = PAGE_MAX_SIZE, message = "Page size must be less than or equal to ${PAGE_MAX_SIZE}") int size
    ) {
        var postsPage = postService.getAllByPublished(published, PageRequest.of(page, size));

        if (postsPage != null) {
            return ResponseEntity.ok().body(postsPage);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
        var postDto = postService.getPostById(id);

        if (postDto != null) {
            return ResponseEntity.ok().body(postDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/title", produces = "application/json")
    public ResponseEntity<PostResponseDto> getPostByTitle(@RequestParam(value = "title") String title) {
        var postDto = postService.getPostByTitle(title);

        if (postDto != null) {
            return ResponseEntity.ok().body(postDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/category", produces = "application/json")
    public ResponseEntity<Page<PostResponseDto>> getPostsByCategory(
            @RequestParam(value = "category") String category, @RequestParam(value = "page", required = false, defaultValue = PAGE_DEFAULT_NUMBER)
            @Min(value = PAGE_MIN_NUMBER, message = "Page number must be greater than or equal to ${PAGE_MIN_NUMBER}") int page,
            @RequestParam(value = "size", required = false, defaultValue = PAGE_DEFAULT_SIZE)
            @Min(value = PAGE_MIN_SIZE, message = "Page size must be greater than or equal to ${PAGE_MIN_SIZE}")
            @Max(value = PAGE_MAX_SIZE, message = "Page size must be less than or equal to ${PAGE_MAX_SIZE}") int size) {
        var postDto = postService.getPostsByCategory(category, PageRequest.of(page, size));

        if (postDto != null) {
            return ResponseEntity.ok().body(postDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/tag", produces = "application/json")
    public ResponseEntity<Page<PostResponseDto>> getPostsByTag(
            @RequestParam(value = "tag") String tag, @RequestParam(value = "page", required = false, defaultValue = PAGE_DEFAULT_NUMBER)
            @Min(value = PAGE_MIN_NUMBER, message = "Page number must be greater than or equal to ${PAGE_MIN_NUMBER}") int page,
            @RequestParam(value = "size", required = false, defaultValue = PAGE_DEFAULT_SIZE)
            @Min(value = PAGE_MIN_SIZE, message = "Page size must be greater than or equal to ${PAGE_MIN_SIZE}")
            @Max(value = PAGE_MAX_SIZE, message = "Page size must be less than or equal to ${PAGE_MAX_SIZE}") int size) {
        var postDto = postService.getPostsByTag(tag, PageRequest.of(page, size));

        if (postDto != null) {
            return ResponseEntity.ok().body(postDto);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostCreateDto postCreateDto) {
        var newPost = postService.createPost(PostMapper.toEntity(postCreateDto));

        if (newPost != null) {
            var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/posts/{id}").buildAndExpand(newPost.getId()).toUri();
            return ResponseEntity.created(uri).body(newPost);
        }
        return ResponseEntity.internalServerError().build();
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @Valid @RequestBody PostCreateDto postUpdateDto) {
        var updatedPost = postService.updatePost(id, PostMapper.toEntity(postUpdateDto));

        if (updatedPost != null) {
            return ResponseEntity.ok().body(updatedPost);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping(value = "/{id}/publish", produces = "application/json")
    public ResponseEntity<PostResponseDto> togglePublish(@PathVariable Long id, @RequestParam(value = "publish") boolean publish) {
        var updatedPost = postService.togglePublish(id, publish);

        if (updatedPost != null) {
            return ResponseEntity.ok().body(updatedPost);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping(value = "/{id}/content", produces = "application/json")
    public ResponseEntity<PostResponseDto> updateContent(@PathVariable Long id, @RequestBody String newContent) {
        var updatedPost = postService.updateContent(id, newContent);

        if (updatedPost != null) {
            return ResponseEntity.ok().body(updatedPost);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletePost(@PathVariable Long id) {
        var deleted = postService.deletePost(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
