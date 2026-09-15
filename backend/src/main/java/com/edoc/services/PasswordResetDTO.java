package com.edoc.services;

import lombok.Data;

@Data
public class PasswordResetDTO {

    private String password;
    
    private String token;
}