package com.edoc.dto.reset_password;
import lombok.Data;

@Data
public class PasswordResetRequestDTO {

    private String email;
    
    private String redirectUrl;
}
