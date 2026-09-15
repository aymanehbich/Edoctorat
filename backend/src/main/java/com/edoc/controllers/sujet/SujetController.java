package com.edoc.controllers.sujet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.dto.Result;
import com.edoc.dto.SujetDTO;
import com.edoc.dto.SujetDTO1;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.exception.AuthenticationException;
import com.edoc.models.Sujet;
import com.edoc.models.User;
import com.edoc.repository.UserRepository;
import com.edoc.services.sujet.SujetService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/api")
public class SujetController {

    @Autowired
    private SujetService sujetService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/sujets/")
    public ResponseEntity<Result<SujetDTO>> getUserSujets(HttpServletRequest ses) {

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

        Result<SujetDTO> sujets = sujetService.getUserSujets(user.getId());
        return ResponseEntity.ok(sujets);
    }

    @PostMapping("/sujets/")
    public ResponseEntity<SujetDTO> createSujet(@RequestBody Map<String, Object> request, HttpServletRequest ses) {
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

          SujetDTO  savedSujet = sujetService.createSujet(request, user.getId());
          
          return ResponseEntity.ok(savedSujet);
        
    }
    //--------------------create sujet by d. labo---------------------------------
    @PostMapping("/sujetslabo/")
    public ResponseEntity<SujetDTO> createSujetFormLabo(
        @RequestParam String titre,
        @RequestParam int professeurId,
        @RequestParam int coDirecteurId,
        @RequestParam int formationDoctoraleId, HttpServletRequest ses) {
            HashMap<String, Object> request = new HashMap<>();
            request.put("titre", titre);
            request.put("professeurId", professeurId);
            request.put("coDirecteurId", coDirecteurId);
            request.put("formationDoctoraleId", formationDoctoraleId);
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

          SujetDTO  savedSujet = sujetService.createSujetByLabo(request, user.getId());
          
          return ResponseEntity.ok(savedSujet);
        
    }
    //----------------update sujet bu d. labo-------------------------------------
    @PutMapping("/sujetslabo/{sujetId}/")
    public ResponseEntity<SujetDTO> updateSujetFormLabo(
        @RequestBody Map<String, Object> request
       , HttpServletRequest ses, @PathVariable Long sujetId) {
           
            
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

          SujetDTO  savedSujet = sujetService.updateSujetByLabo(request,sujetId);
          
          return ResponseEntity.ok(savedSujet);
        
    }
    //------------------------delete sujet by d. labo----------------------------
    

    @DeleteMapping(value={"/sujets/{id}/","/sujetslabo/{id}/"})
    public ResponseEntity<SujetDTO> deleteSujet(@PathVariable Long id) {
        try {
            SujetDTO deletedSujet = sujetService.deleteSujet(id);
            return ResponseEntity.ok(deletedSujet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/sujets/{id}/")
    public ResponseEntity<SujetDTO> updateSujet(@PathVariable Long id,  @RequestBody Map<String, Object> request, HttpServletRequest ses) {
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

        SujetDTO updatedSujet = sujetService.updateSujet(id, request, user.getId());
        
        return ResponseEntity.ok(updatedSujet);
    }


    @GetMapping("/sujets/{id}/")
    public ResponseEntity<SujetDTO> getSujet(@PathVariable Long id) {
        try {
            SujetDTO sujet = sujetService.getSujetById(id);
            return ResponseEntity.ok(sujet);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
     @GetMapping("/get-published-subjects/")
    public ResponseEntity<?> getAllSujet(HttpServletRequest request) {
        Result<SujetDTO> sujets = sujetService.getall();
        
       
        return ResponseEntity.ok(sujets);
    }

    @GetMapping("/get-all-sujets/")
    public ResponseEntity<Result<SujetDTO1>> getAllSujets(HttpServletRequest ses) {

        Result<SujetDTO1> sujets = sujetService.getall1();
        
       
        return ResponseEntity.ok(sujets);
    }
    @PatchMapping("/publier-sujets/")
    public void PublierSujet(HttpServletRequest ses) {

        sujetService.PublierSujets();
        
       
       /*  return ResponseEntity.ok(sujets); */
    }
    @GetMapping("/sujetslabo/")
    public ResponseEntity<Result<SujetDTO1>> getSujetsLabo(HttpServletRequest ses) {

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

        Result<SujetDTO1> sujets = sujetService.getSujetsLabo(user.getId());
        return ResponseEntity.ok(sujets);
    }

  }