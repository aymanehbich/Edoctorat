package com.edoc.services;

import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    
    private String email;
    
    private String redirectUrl;
}
