package com.edoc.controllers.reset_password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edoc.dto.reset_password.PasswordResetDTO;
import com.edoc.dto.reset_password.PasswordResetRequestDTO;
import com.edoc.services.reset_password.PasswordRecoveryService;


@RestController
@RequestMapping("/api")
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/request-password-reset/")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequestDTO dto) {
        boolean success = passwordRecoveryService.requestPasswordReset(dto);
        
        return success ? ResponseEntity.ok().build(): ResponseEntity.badRequest().build();
    }

    @PatchMapping("/perform-password-reset/")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetDTO dto) {
        boolean success = passwordRecoveryService.resetPassword(dto);
        
        return success ? ResponseEntity.ok().build(): ResponseEntity.badRequest().build();
    }
}