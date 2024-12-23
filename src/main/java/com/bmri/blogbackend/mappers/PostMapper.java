package com.bmri.blogbackend.mappers;

import com.bmri.blogbackend.domain.PostEntity;
import com.bmri.blogbackend.dtos.request.PostCreateDto;
import com.bmri.blogbackend.dtos.response.PostResponseDto;
import org.springframework.data.domain.Page;

public class PostMapper {

    public static PostEntity toEntity(PostCreateDto dto) {
        var postEntity = new PostEntity();
        postEntity.setTitle(dto.getTitle());
        postEntity.setContent(dto.getContent());
        postEntity.setCategory(dto.getCategory());
        postEntity.setTags(dto.getTags());
        postEntity.setPublished(dto.isPublished());
        return postEntity;
    }

    public static PostResponseDto toDto(PostEntity entity) {
        var postResponseDto = new PostResponseDto();
        postResponseDto.setTitle(entity.getTitle());
        postResponseDto.setContent(entity.getContent());
        postResponseDto.setCategory(entity.getCategory());
        postResponseDto.setTags(entity.getTags());
        postResponseDto.setPublished(entity.isPublished());
        return postResponseDto;
    }

    public static Page<PostResponseDto> toDto(Page<PostEntity> entities) {
        return entities.map(PostMapper::toDto);
    }
}
