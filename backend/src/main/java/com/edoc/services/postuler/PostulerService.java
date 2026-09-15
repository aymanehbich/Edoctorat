package com.edoc.services.postuler;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import com.edoc.dto.PostulerDTO;
import com.edoc.dto.Result;
import com.edoc.dto.mapper.PostulerMapper;
import com.edoc.exception.AuthenticationException;
import com.edoc.models.Candidat;
import com.edoc.models.Postuler;
import com.edoc.models.Sujet;
import com.edoc.models.User;
import com.edoc.repository.CandidatRepository;
import com.edoc.repository.PostulerRepository;
import com.edoc.repository.SujetRepository;
import com.edoc.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class PostulerService {
    @Autowired
    private PostulerRepository postulerRepository;
    @Autowired 
    private UserRepository userRepository;
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private SujetRepository sujetRepository;
    @Autowired
    private PostulerMapper postulerMapper;

    @Transactional
    public ResponseEntity<?> getPostulerBySession(HttpServletRequest request){
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
        Optional<Candidat> candidat = candidatRepository.findByUserId(user.getId());
        System.out.println(candidat.get().getDiplomes().size()+"******************************************");
        List<Postuler> postuler = postulerRepository.findByCandidatId(candidat.get().getId());
        List<PostulerDTO> postulers = postuler.stream().map(postulerMapper::toDto).toList();
        Result<PostulerDTO> result = new Result<>();
        result.setCount(postuler.size());
        result.setResults(postulers);

        return ResponseEntity.ok(result);
    }

    @Transactional
    public ResponseEntity<?> deletePostulerById(Long id) {
        
        
        Optional<Postuler> postuler = postulerRepository.findById(id);
        if (!postuler.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        postulerRepository.delete(postuler.get());
        return ResponseEntity.ok().build();
    }
    
    @Transactional
    public ResponseEntity<?> editPostulerById(Long id, MultipartHttpServletRequest request) throws IOException {
    
        Optional<Postuler> postuler = postulerRepository.findById(id);
       
        if (postuler.isEmpty()) {
            System.out.println("mal9ach");
            return ResponseEntity.notFound().build();
        }
        
        Postuler existingPostuler = postuler.get();
        MultipartFile file = request.getFile("pathFile");
         
        if (file != null && !file.isEmpty()) {
    try {
        // Generate a new file name with a timestamp
        String originalFilename = file.getOriginalFilename();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        String newFilename = timestamp + "_" + sanitizedFilename;

        // Validate file size and type
      

        

        // Create and verify upload directory
        Path uploadDir = Paths.get("uploads/candidates/annexes")/* .toAbsolutePath() */;
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(newFilename);

        // Save the file to the data repository
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update the pathFile attribute
        existingPostuler.setPathFile("/"+filePath.toString());
        
        return ResponseEntity.ok().body("File uploaded successfully");

    } catch (IOException e) {
        return ResponseEntity.ok().body("Failed to upload file: " + e.getMessage());
    }
} else {
    return ResponseEntity.ok().body("No file provided");
}
}
    
    public ResponseEntity<?>  savePostulerBySession(Long sujetId, User user){
        Optional<Candidat> candidat = candidatRepository.findByUserId(user.getId());
        if (!candidat.isPresent()) {
            return ResponseEntity.badRequest().body("Candidat not found");
        }

        Optional<Sujet> sujet = sujetRepository.findById(sujetId);
        if (!sujet.isPresent()) {
            return ResponseEntity.badRequest().body("Sujet not found");
        }

        Integer maxSujetPostuler = postulerRepository.findMaxSujetPostuler();
        if (maxSujetPostuler == null) {
            return ResponseEntity.badRequest().body("Configuration for max_sujet_postuler not found");
        }

        Long currentPostulations = postulerRepository.countByCandidatId(candidat.get().getId());
        
        if (currentPostulations >= maxSujetPostuler) {
            return ResponseEntity.badRequest().body("Maximum number of postulations (" + maxSujetPostuler + ") reached");
        }

        Postuler postuler = new Postuler();
        postuler.setCandidat(candidat.get());
        postuler.setSujet(sujet.get());
        
        Postuler savedPostuler = postulerRepository.save(postuler);
        return ResponseEntity.ok(postulerMapper.toDto(savedPostuler));
    }

    public Result<PostulerDTO>  getAllPostulerCandidats(){
     
        List<Postuler> postuler = postulerRepository.findAll();
        List<PostulerDTO> postulers = postuler.stream().map(postulerMapper::toDto).toList();
        Result<PostulerDTO> result = new Result<>();
        result.setCount(postuler.size());
        result.setResults(postulers);
         return result;
    }

}
