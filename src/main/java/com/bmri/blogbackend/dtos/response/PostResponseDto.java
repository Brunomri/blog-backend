package com.bmri.blogbackend.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String category;
    private List<String> tags;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
