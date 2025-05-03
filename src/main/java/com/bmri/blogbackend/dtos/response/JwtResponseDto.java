package com.bmri.blogbackend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtResponseDto {
    private String token;
}
