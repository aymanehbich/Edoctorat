package com.edoc.controllers.candidat;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.dto.candidat.PreRegistrationDTO;
import com.edoc.dto.candidat.RegistrationDTO;
import com.edoc.services.candidat.RegistrationService;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;
    
    @PostMapping("/confirm-email/")
    public ResponseEntity<?> preRegister(@RequestBody PreRegistrationDTO dto) {
        registrationService.preRegister(dto);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/verify-token/")
    public ResponseEntity<PreRegistrationDTO> verifyToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        PreRegistrationDTO dto = registrationService.verifyToken(token);
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/register/candidat/")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO dto) {

        registrationService.registerUser(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Account created successfully");
        return ResponseEntity.ok(response);
    }
}