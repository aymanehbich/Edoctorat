package com.edoc.controllers;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.edoc.dto.LoginRequest;
import com.edoc.dto.TokenResponse;
import com.edoc.dto.UserInfo;
import com.edoc.dto.UserInfoResponse;
import com.edoc.services.AuthService;

@RestController
@RequestMapping("/api")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/token/")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.authenticate(loginRequest));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Identifiants invalides ou compte inactif."));
        }
    }
    
    @PostMapping("/login_scolarite/")
    public ResponseEntity<?> loginScolarite(@RequestBody LoginRequest loginRequest) {
      try {
        UserInfo userInfo = authService.authenticateScolarite(loginRequest);
        return ResponseEntity.ok(userInfo);
      } catch (AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", "Identifiants invalides ou compte inactif."));
      }
    }
    
    @PostMapping("/verify-is-prof/")
    public ResponseEntity<?> verifyProf(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.verifyAndAuthenticateProf(request.get("token")));
    }
    
    @GetMapping("/get-user-info/")
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.getUserInfo(token.substring(7)));
    }
    
    @PostMapping("/token/refresh/")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.refreshToken(request.get("refresh")));
    }

    
}
