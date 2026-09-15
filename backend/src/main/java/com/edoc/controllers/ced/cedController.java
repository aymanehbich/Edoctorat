package com.edoc.controllers.ced;

import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties.Servlet;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.dto.SujetDTO1;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.exception.AuthenticationException;
import com.edoc.models.Candidat;
import com.edoc.models.Examiner;
import com.edoc.models.Sujet;
import com.edoc.models.User;

import com.edoc.repository.UserRepository;
import com.edoc.services.ced.cedService;
import com.edoc.services.commission.CommissionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class cedController {
    @Autowired
    private cedService cedService;
    @Autowired
    private CommissionService commissionService;
    
    @Autowired
    private UserRepository userRepository;
@GetMapping("/get-ced-sujets/")
public ResponseEntity<?> getCedSujets(
        HttpServletRequest request,
        @RequestParam(required = false) Integer offset,
        @RequestParam(required = false) Integer limit) {
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
              List<SujetDTO1> Sujets=cedService.getSujetByCedFromproffeseur(user, offset, limit);
          ResultDto<SujetDTO1> result = new ResultDto<>();
            result.setResults(Sujets);
            result.setCount(Sujets.size());
    return ResponseEntity.ok(result);
}
/* @GetMapping("/get-ced-candidats/")
public ResponseEntity<?> getCedCandidat(
        HttpServletRequest request,
        @RequestParam(required = false) Integer offset,
        @RequestParam(required = false) Integer limit) {
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
                
    return ResponseEntity.ok(cedService.getCandidatByCed(user, offset, limit));
} */

@GetMapping("/get-ced-candidats/")
public ResponseEntity<?> getCedCandidat(
        HttpServletRequest request,
        @RequestParam(required = false) Integer offset,
        @RequestParam(required = false) Integer limit) {
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
             /*  List<Examiner> candidats=cedService.getExaminerCandidatByCed(user, offset, limit);
          ResultDto<Examiner> result = new ResultDto<>();
            result.setResults(candidats);
            result.setCount(candidats.size()); */
    return ResponseEntity.ok(cedService.getExaminerCandidatByCed(user, offset, limit) /* result */);
}
@GetMapping("/get-ced-commissions/")
public ResponseEntity<?> getCedExaminer(
        HttpServletRequest request,
        @RequestParam(required = false) Integer offset,
        @RequestParam(required = false) Integer limit) {
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
                
    return ResponseEntity.ok(commissionService.getCommissionsOfCedFromProfesseur(user, offset, limit));

        }



        @GetMapping("/get-ced-resultats/")
public ResponseEntity<?> getResultat(
        HttpServletRequest request,
        @RequestParam(required = false) Integer offset,
        @RequestParam(required = false) Integer limit) {
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
                
    return ResponseEntity.ok(cedService.getResultatAsExaminerCedFromDirecteur(user, offset, limit));

        }
}
