package com.edoc.dto.reset_password;

import lombok.Data;

@Data
public class PasswordResetDTO {

    private String password;
    
    private String token;
}
