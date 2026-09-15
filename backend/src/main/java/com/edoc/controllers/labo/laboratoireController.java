package com.edoc.controllers.labo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.exception.AuthenticationException;
import com.edoc.models.Sujet;
import com.edoc.models.User;
import com.edoc.repository.LaboratoireRepository;
import com.edoc.repository.SujetRepository;
import com.edoc.repository.UserRepository;
import com.edoc.services.labratoireService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class laboratoireController {
    @Autowired
    private labratoireService labratoireService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SujetRepository sujetRepository;
    
    

    @GetMapping("/labo_candidat/")
    public ResponseEntity<?> getLaboCandidat( HttpServletRequest ses) {
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
          

        return ResponseEntity.ok(labratoireService.getLaboCandidat(user.getId()));
    }
    @DeleteMapping("/labo_candidat/{id}/")
    public ResponseEntity<?> deleteSujet(@PathVariable Long id) {
        
          
          

        sujetRepository.deleteById(id);
        return ResponseEntity.ok("Sujet deleted successfully");
    }


    @GetMapping("/labo_professeur/")
    public ResponseEntity<?> getProfesseur( HttpServletRequest ses) {
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
          

        return ResponseEntity.ok(labratoireService.getLaboProfesseur(user.getId()));
    }

    @PatchMapping("/labo_publier_resultats/")
    public ResponseEntity<?> publierResultats(HttpServletRequest ses, @RequestParam String decision) {
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

        return ResponseEntity.ok(labratoireService.publierResultatsLabo(user.getId(), decision));
    }
}
