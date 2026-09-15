package com.edoc.controllers.professeur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.dto.ExaminerDTO1;
import com.edoc.dto.InscriptionDTO1;
import com.edoc.dto.PostulerProfCandidatDTO;
import com.edoc.dto.ProfesseurDTO;
import com.edoc.dto.Result;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.exception.AuthenticationException;
import com.edoc.models.User;
import com.edoc.repository.UserRepository;
import com.edoc.services.professeur.ProfesseurService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ProfesseurController {
    @Autowired
    private ProfesseurService professeurService;

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/get-professeurs/")
    public ResponseEntity<Result<ProfesseurDTO>> getProfesseurs() {
      Result<ProfesseurDTO> result = professeurService.getProfesseurs();
      return ResponseEntity.ok(result);
    }

    @GetMapping("/get-professeur-candidats/")
    public ResponseEntity<?> getProfesseurCandidats(HttpServletRequest ses) {

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

        ResultDto<PostulerProfCandidatDTO> result = professeurService.getCandidatsForProfesseur(user.getId());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/examiner/")
    public ResponseEntity<Result<ExaminerDTO1>> getResultats(@RequestParam(required = false) Integer offset, HttpServletRequest ses) {
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



        Result<ExaminerDTO1> result = professeurService.getResultatsExaminer(user.getId(), offset);
        return ResponseEntity.ok(result);
    }



    @GetMapping("/inscrits/")
    public ResponseEntity<Result<InscriptionDTO1>> getMesInscrits(HttpServletRequest ses,@RequestParam(required = false) Integer offset){
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

        Result<InscriptionDTO1> result = professeurService.getMesInscrits(user.getId(), offset);
        return ResponseEntity.ok(result);
    }
}
