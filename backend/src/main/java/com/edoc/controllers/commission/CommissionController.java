package com.edoc.controllers.commission;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.edoc.dto.CommissionDTO;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.exception.AuthenticationException;
import com.edoc.models.User;
import com.edoc.repository.UserRepository;
import com.edoc.services.commission.CommissionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class CommissionController {

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/commissions/")
    public ResponseEntity<CommissionDTO> createCommission(@RequestBody Map<String, Object> request, HttpServletRequest ses) {
        HttpSession session = ses.getSession(false);
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

        CommissionDTO commission = commissionService.createCommission(request, user.getId());
        return ResponseEntity.ok(commission);
    }

    @GetMapping("/get-all-commissions/")
    public ResponseEntity<?> getAllCommissions() {
        try {
            ResultDto<CommissionDTO> commissions = commissionService.getAllCommissions();
            return ResponseEntity.ok(commissions);
        } catch (Exception e) {
            return ResponseEntity.ok("Error retrieving commissions: " + e.getMessage());
        }
    }
    @GetMapping("/participant/")
    public ResponseEntity<?> getCommissions(HttpServletRequest ses) {

        HttpSession session = ses.getSession(false);
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

        List<CommissionDTO> commissions = commissionService.getCommissionsForProfesseur(user.getId());
        ResultDto<CommissionDTO> result = new ResultDto<>();
        result.setResults(commissions);
        return ResponseEntity.ok(commissions);
    }


    @GetMapping("/commission/")
    public ResponseEntity<?> getCommissionsLabo(HttpServletRequest ses) {

        HttpSession session = ses.getSession(false);
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

         commissionService.getCommissionsOfLabo(user.getId());
        
        return ResponseEntity.ok( commissionService.getCommissionsOfLabo(user.getId()));
    }
}