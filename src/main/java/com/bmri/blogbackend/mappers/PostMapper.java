package com.bmri.blogbackend.mappers;

import com.bmri.blogbackend.domain.PostEntity;
import com.bmri.blogbackend.dtos.request.PostCreateDto;
import com.bmri.blogbackend.dtos.response.PostResponseDto;
import org.springframework.data.domain.Page;

public class PostMapper {

    public static PostEntity toEntity(PostCreateDto dto) {
        return new PostEntity(dto.getTitle(), dto.getContent(), dto.getCategory(), dto.getTags(), dto.isPublished());
    }

    public static PostEntity toEntity(PostResponseDto dto) {
        var postEntity = new PostEntity(dto.getTitle(), dto.getContent(), dto.getCategory(), dto.getTags(), dto.isPublished());
        postEntity.setId(dto.getId());
        postEntity.setCreatedAt(dto.getCreatedAt());
        postEntity.setUpdatedAt(dto.getUpdatedAt());
        return postEntity;
    }

    public static PostResponseDto toDto(PostEntity entity) {
        return new PostResponseDto(entity.getId(), entity.getTitle(), entity.getContent(), entity.getCategory(),
                entity.getTags(), entity.isPublished(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public static Page<PostResponseDto> toDto(Page<PostEntity> entities) {
        return entities.map(PostMapper::toDto);
    }
}
