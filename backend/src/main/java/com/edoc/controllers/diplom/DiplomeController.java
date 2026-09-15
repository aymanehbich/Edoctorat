package com.edoc.controllers.diplom;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.edoc.dto.candidat.ResultDto;
import com.edoc.dto.diplome.DiplomeDTO;
import com.edoc.dto.mapper.DiplomeMapper;
import com.edoc.exception.AuthenticationException;
import com.edoc.models.Annexe;
import com.edoc.models.Candidat;
import com.edoc.models.Diplome;
import com.edoc.models.User;
import com.edoc.repository.AnnexeRepository;
import com.edoc.repository.CandidatRepository;
import com.edoc.repository.DiplomeRepository;
import com.edoc.repository.UserRepository;
import com.edoc.services.AnnexeService;
import com.edoc.services.DiplomeService;
import com.edoc.services.candidat.CandidatService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DiplomeController {

    @Autowired
    private DiplomeService diplomeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private AnnexeRepository annexeRepository;

    @Autowired
    private AnnexeService annexeService;

    @Autowired
    private DiplomeMapper diplomeMapper;


    @GetMapping("/diplomes")
    public List<?> getAllDiplomes() {
        List<?> diplomes = diplomeService.getAllDiplomes();
        return diplomes;
    }
    
    @GetMapping("/candidat-parcours/")
    public ResponseEntity<?> getAllDiplomes(HttpServletRequest request) {
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
        
        return ResponseEntity.ok(diplomeService.getAllDiplomesByCandidatId(user.getId()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Diplome> getDiplomeById(@PathVariable Long id) {
        Optional<Diplome> diplome = diplomeService.getDiplomeById(id);
        return diplome.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

 
    @PostMapping("/candidat-parcours/")
    public ResponseEntity<?> addDiplome(MultipartHttpServletRequest request) {
       try {
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
              
            // Extract fields
            

            /* Long candidatId = Long.parseLong(request.getParameter("candidat_id")); */
            Candidat candidat = candidatRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Candidat not found"));;
                    

            
            return ResponseEntity.ok(diplomeService.addDiplome(request,candidat));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    

    
    @PutMapping("/{id}")
    public ResponseEntity<Diplome> updateDiplome(@PathVariable Long id, @RequestBody Diplome diplome) {
        Diplome updatedDiplome = diplomeService.updateDiplome(diplome, id);
        return updatedDiplome != null ? ResponseEntity.ok(updatedDiplome) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiplome(@PathVariable Long id) {
        boolean deleted = diplomeService.deleteDiplome(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @PatchMapping("/candidat-parcours/{diplomeId}/")
public ResponseEntity<?> updateDiplome(@PathVariable Long diplomeId, MultipartHttpServletRequest request) {
    
    try {
        // Session validation
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new AuthenticationException("No active session found");
        }
        
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext == null || securityContext.getAuthentication() == null) {
            throw new AuthenticationException("No authentication found in session");
        }
        
        // User validation
        String username = securityContext.getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
        Candidat candidat = candidatRepository.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Candidat not found"));
            
        

        return ResponseEntity.ok( diplomeService.updateDiplome(diplomeId, request,candidat));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

}
