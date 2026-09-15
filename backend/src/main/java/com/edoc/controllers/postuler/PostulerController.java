package com.edoc.controllers.postuler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.edoc.exception.AuthenticationException;
import com.edoc.models.User;
import com.edoc.repository.UserRepository;
import com.edoc.services.postuler.PostulerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PostulerController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostulerService postulerService;

    @GetMapping("/candidat-postules/")
    public ResponseEntity<?> getMethodName(HttpServletRequest request) {
        return postulerService.getPostulerBySession(request);
    }

    @PostMapping("/candidat-postules/")
    public ResponseEntity<?> saveMethodName(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        HttpSession session = request.getSession(false);
        
        Long sujetId = Long.parseLong(payload.get("sujet").toString());
        System.out.println("**************************" + sujetId);
        
        if (session == null) {
            throw new AuthenticationException("No active session found");
        }
        
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext == null || securityContext.getAuthentication() == null) {
            throw new AuthenticationException("No authentication found in session");
        }
        
        String username = securityContext.getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return postulerService.savePostulerBySession(sujetId, user);
    }
    
    
    @DeleteMapping("/candidat-postules/{id}")
    public ResponseEntity<?> deletePostuler(@PathVariable Long id, HttpServletRequest request) {
        return postulerService.deletePostulerById(id);
    }

    @PatchMapping("/candidat-postules/{id}/")
    public ResponseEntity<?> EditePostuler(@PathVariable Long id, MultipartHttpServletRequest request) throws IOException {

        System.out.println("******************************************5dam" + id);
        return postulerService.editPostulerById(id, request);
    }

    @GetMapping("/get-all-candidats/") 
    public ResponseEntity<?> getAllPostulerCandidats() {
        return ResponseEntity.ok(postulerService.getAllPostulerCandidats());
    }
}
