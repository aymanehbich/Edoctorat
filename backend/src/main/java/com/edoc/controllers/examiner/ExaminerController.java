package com.edoc.controllers.examiner;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.dto.ExaminerDTO;
import com.edoc.exception.AuthenticationException;
import com.edoc.services.examiner.ExaminerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ExaminerController {

    @Autowired
    private ExaminerService examinerService;

    @PutMapping("/examiner/{id}/")
    public ResponseEntity<ExaminerDTO> updateExaminer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest ses) {
        HttpSession session = ses.getSession(false);
        if (session == null) {
            throw new AuthenticationException("No active session found");
        }

        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext == null || securityContext.getAuthentication() == null) {
            throw new AuthenticationException("No authentication found in session");
        }

        ExaminerDTO updated = examinerService.updateExaminer(id, request);
        return ResponseEntity.ok(updated);
    }
}
