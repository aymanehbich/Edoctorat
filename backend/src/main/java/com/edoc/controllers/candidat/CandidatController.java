package com.edoc.controllers.candidat;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.edoc.models.User;
import com.edoc.services.JwtService;
import com.edoc.services.candidat.CandidatService;
import com.edoc.repository.UserRepository;
import com.edoc.dto.CandidatResultatsDTO;
import com.edoc.dto.candidat.CandidatDTO;
import com.edoc.exception.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class CandidatController {
    @Autowired
    private  CandidatService candidatService;

    @Autowired
    private  JwtService jwtService;

    @Autowired
    private  UserRepository userRepository;
    


    // ----------------------------------- get candidat info -----------------------
    
    @GetMapping("/candidat-info/")
    public ResponseEntity<CandidatDTO> getCandidatInfo(HttpServletRequest request) {
          HttpSession session = request.getSession(false);
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
              
          return ResponseEntity.ok(candidatService.getCandidatInfo(user.getId()));
    }

  // ------------------- get candidat by id -----------------------------------------

  @GetMapping("/get-candidat-profile/{id}")
  public ResponseEntity<CandidatDTO> getCandidatProfile(@PathVariable Long id) {
        return ResponseEntity.ok(candidatService.getCandidatProfile(id));
  }
  
  // ------------------- get candidat's own exam results & enrollment status ---------------

  @GetMapping("/candidat-resultats/")
  public ResponseEntity<CandidatResultatsDTO> getMesResultats(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
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

        return ResponseEntity.ok(candidatService.getMesResultats(user.getId()));
  }

  // --------------- update candidat info ---------------------------------------------

  @PutMapping("/candidat-info/")
  public ResponseEntity<CandidatDTO> updateCandidatInfo(MultipartHttpServletRequest request) throws IOException {
    HttpSession session = request.getSession(false);
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
    
    return ResponseEntity.ok(candidatService.updateCandidatInfo(request,user.getId()));
    
  }
}