package com.edoc.controllers;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }
    
    @PostMapping("/login_scolarite/")
    public ResponseEntity<UserInfo> loginScolarite(@RequestBody LoginRequest loginRequest) {
      UserInfo userInfo = authService.authenticateScolarite(loginRequest);
      return ResponseEntity.ok(userInfo);
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
