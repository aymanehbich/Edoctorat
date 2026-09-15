package com.edoc.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String access;
    private String refresh;
}