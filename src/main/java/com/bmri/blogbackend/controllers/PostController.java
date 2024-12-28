package com.bmri.blogbackend.controllers;

import com.bmri.blogbackend.dtos.request.PostCreateDto;
import com.bmri.blogbackend.dtos.response.PostResponseDto;
import com.bmri.blogbackend.services.interfaces.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Validated
@RequestMapping("/posts")
@Slf4j
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
        log.info("getAllPosts: Found {} posts on page {}", postsPage.getTotalElements(), postsPage.getPageable().getPageNumber());
        return ResponseEntity.ok().body(postsPage);
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
        log.info("getAllByPublished: Found {} posts on page {}", postsPage.getTotalElements(), postsPage.getPageable().getPageNumber());
        return ResponseEntity.ok().body(postsPage);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<PostResponseDto> getPostById(
            @PathVariable
            @Positive(message = "Post ID must be a positive integer") Long id) {
        var postDto = postService.getPostById(id);
        log.info("getPostById: Found post with id {}", id);
        return ResponseEntity.ok().body(postDto);
    }

    @GetMapping(value = "/title", produces = "application/json")
    public ResponseEntity<PostResponseDto> getPostByTitle(@RequestParam(value = "title") String title) {
        var postDto = postService.getPostByTitle(title);
        log.info("getPostByTitle: Found post with title {}", title);
        return ResponseEntity.ok().body(postDto);
    }

    @GetMapping(value = "/category", produces = "application/json")
    public ResponseEntity<Page<PostResponseDto>> getPostsByCategory(
            @RequestParam(value = "category") String category, @RequestParam(value = "page", required = false, defaultValue = PAGE_DEFAULT_NUMBER)
            @Min(value = PAGE_MIN_NUMBER, message = "Page number must be greater than or equal to ${PAGE_MIN_NUMBER}") int page,
            @RequestParam(value = "size", required = false, defaultValue = PAGE_DEFAULT_SIZE)
            @Min(value = PAGE_MIN_SIZE, message = "Page size must be greater than or equal to ${PAGE_MIN_SIZE}")
            @Max(value = PAGE_MAX_SIZE, message = "Page size must be less than or equal to ${PAGE_MAX_SIZE}") int size) {
        var postsPage = postService.getPostsByCategory(category, PageRequest.of(page, size));
        log.info("getPostsByCategory: Found {} posts on page {}", postsPage.getTotalElements(), postsPage.getPageable().getPageNumber());
        return ResponseEntity.ok().body(postsPage);
    }

    @GetMapping(value = "/tag", produces = "application/json")
    public ResponseEntity<Page<PostResponseDto>> getPostsByTag(
            @RequestParam(value = "tag") String tag, @RequestParam(value = "page", required = false, defaultValue = PAGE_DEFAULT_NUMBER)
            @Min(value = PAGE_MIN_NUMBER, message = "Page number must be greater than or equal to ${PAGE_MIN_NUMBER}") int page,
            @RequestParam(value = "size", required = false, defaultValue = PAGE_DEFAULT_SIZE)
            @Min(value = PAGE_MIN_SIZE, message = "Page size must be greater than or equal to ${PAGE_MIN_SIZE}")
            @Max(value = PAGE_MAX_SIZE, message = "Page size must be less than or equal to ${PAGE_MAX_SIZE}") int size) {
        var postsPage = postService.getPostsByTag(tag, PageRequest.of(page, size));
        log.info("getPostsByTag: Found {} posts on page {}", postsPage.getTotalElements(), postsPage.getPageable().getPageNumber());
        return ResponseEntity.ok().body(postsPage);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostCreateDto postCreateDto) {
        var newPost = postService.createPost(postCreateDto);

        if (newPost != null) {
            var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newPost.getId()).toUri();
            log.info("createPost: Created post ID {}", newPost.getId());
            return ResponseEntity.created(uri).body(newPost);
        }
        return ResponseEntity.internalServerError().build();
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable @Positive(message = "Post ID must be a positive integer") Long id,
            @Valid @RequestBody PostCreateDto postUpdateDto) {
        var updatedPost = postService.updatePost(id, postUpdateDto);
        log.info("updatePost: Updated post with ID {}", updatedPost.getId());
        return ResponseEntity.ok().body(updatedPost);
    }

    @PatchMapping(value = "/{id}/publish", produces = "application/json")
    public ResponseEntity<PostResponseDto> togglePublish(
            @PathVariable @Positive(message = "Post ID must be a positive integer") Long id,
            @RequestParam(value = "publish") boolean publish) {
        var updatedPost = postService.togglePublish(id, publish);
        log.info("togglePublish: Updated publish field of post ID {} to {}", updatedPost.getId(), publish);
        return ResponseEntity.ok().body(updatedPost);
    }

    @PatchMapping(value = "/{id}/content", produces = "application/json")
    public ResponseEntity<PostResponseDto> updateContent(
            @PathVariable @Positive(message = "Post ID must be a positive integer") Long id,
            @RequestBody String newContent) {
        var updatedPost = postService.updateContent(id, newContent);
        log.info("updateContent: Updated content of post ID {}", updatedPost.getId());
        return ResponseEntity.ok().body(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletePost(
            @PathVariable
            @Positive(message = "Post ID must be a positive integer") Long id) {
        postService.deletePost(id);
        log.info("deletePost: Deleted post ID {}", id);
        return ResponseEntity.noContent().build();
    }

}
