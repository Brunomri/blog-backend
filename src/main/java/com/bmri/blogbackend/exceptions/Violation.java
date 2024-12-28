package com.bmri.blogbackend.exceptions;

import lombok.Getter;

@Getter
public class Violation extends StandardErrorResponse {

    private final String fieldName;
    private final Object value;

    public Violation(String timestamp, Integer status, String detail, String fieldName,
                     Object value, String message) {
        super(timestamp, status, detail, message);
        this.fieldName = fieldName;
        this.value = value;
    }
}
