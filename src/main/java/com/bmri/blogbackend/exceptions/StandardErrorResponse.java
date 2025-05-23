package com.bmri.blogbackend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StandardErrorResponse {

    private final String timestamp;
    private final Integer status;
    private final String detail;
    private final String message;
}
