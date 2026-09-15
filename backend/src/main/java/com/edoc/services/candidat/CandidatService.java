package com.edoc.services.candidat;

import com.edoc.dto.CandidatResultatsDTO;
import com.edoc.dto.ExaminerDTO;
import com.edoc.dto.InscriptionDTO;
import com.edoc.dto.candidat.CandidatDTO;
import com.edoc.dto.mapper.CandidatMapper;
import com.edoc.dto.mapper.ExaminerMapper;
import com.edoc.dto.mapper.InscriptionMapper;
import com.edoc.models.Candidat;
import com.edoc.models.User;
import com.edoc.repository.CandidatRepository;
import com.edoc.repository.ExaminerRepository;
import com.edoc.repository.InscriptionRepository;
import com.edoc.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class CandidatService {

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private CandidatMapper candidatMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExaminerRepository examinerRepository;

    @Autowired
    private ExaminerMapper examinerMapper;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Autowired
    private InscriptionMapper inscriptionMapper;

    private final String uploadDir = "uploads/candidates/images";


    public CandidatDTO getCandidatInfo(Long userId) {
        Candidat candidat = candidatRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Candidat not found"));
        return candidatMapper.toDto(candidat);
    }

    public CandidatDTO getCandidatProfile(Long id) {
        Candidat candidat = candidatRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Candidat not found"));
        return candidatMapper.toDto(candidat);
    }

    /**
     * Only Examiner rows with publier=true are surfaced — same "hidden until
     * the professeur releases it" gate as Sujet.publier, so a candidat never
     * sees a decision before it is officially published.
     */
    public CandidatResultatsDTO getMesResultats(Long userId) {
        Candidat candidat = candidatRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Candidat not found"));

        List<ExaminerDTO> examens = examinerRepository.findByCandidatId(candidat.getId()).stream()
            .filter(examiner -> Boolean.TRUE.equals(examiner.getPublier()))
            .map(examinerMapper::toDto)
            .collect(Collectors.toList());

        List<InscriptionDTO> inscriptions = inscriptionRepository.findByCandidatId(candidat.getId()).stream()
            .map(inscriptionMapper::toDto)
            .collect(Collectors.toList());

        return new CandidatResultatsDTO(examens, inscriptions);
    }

    public CandidatDTO updateCandidatInfo(MultipartHttpServletRequest request, Long userId) throws IOException {
        CandidatDTO candidatDTO = extractFormData(request);
        
        handleFileUploads(request, candidatDTO);
        
        return updateCandidatEntity(candidatDTO, userId);
    }

    private CandidatDTO extractFormData(MultipartHttpServletRequest request) {
        CandidatDTO candidatDTO = new CandidatDTO();
        candidatDTO.setNom(request.getParameter("nom"));
        candidatDTO.setPrenom(request.getParameter("prenom"));
        candidatDTO.setCne(request.getParameter("cne"));
        candidatDTO.setPays(request.getParameter("pays"));
        candidatDTO.setEmail(request.getParameter("mailCandidat"));
        candidatDTO.setCin(request.getParameter("cin"));
        candidatDTO.setNomCandidatAr(request.getParameter("nomCandidatAr"));
        candidatDTO.setPrenomCandidatAr(request.getParameter("prenomCandidatAr"));
        candidatDTO.setAdresse(request.getParameter("adresse"));
        candidatDTO.setAdresseAr(request.getParameter("adresseAr"));
        candidatDTO.setSexe(request.getParameter("sexe"));
        candidatDTO.setVilleDeNaissance(request.getParameter("villeDeNaissance"));
        candidatDTO.setVilleDeNaissanceAr(request.getParameter("villeDeNaissanceAr"));
        candidatDTO.setVille(request.getParameter("ville"));
        candidatDTO.setDateDeNaissance(request.getParameter("dateDeNaissance"));
        candidatDTO.setAcademie(request.getParameter("academie"));
        candidatDTO.setTelCandidat(request.getParameter("telCandidat"));
        candidatDTO.setSituation_familiale(request.getParameter("situation_familiale"));
        candidatDTO.setFonctionnaire(request.getParameter("fonctionnaire"));
        
        return candidatDTO;
    }

    private void handleFileUploads(MultipartHttpServletRequest request, CandidatDTO candidatDTO) throws IOException {
      System.out.println("Starting file upload handling...");
      
      Iterator<String> fileNames = request.getFileNames();
      while (fileNames.hasNext()) {
          String fileName = fileNames.next();
          System.out.println("Processing file with name: " + fileName);
          
          MultipartFile file = request.getFile(fileName);
          if (file != null && !file.isEmpty()) {
          
              
              String originalFilename = file.getOriginalFilename();
              String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
              String newFileName = UUID.randomUUID().toString() + fileExtension;
              String fullPath = "/"+uploadDir +"/"+ newFileName;
              
              
              File directory = new File(uploadDir);
              if (!directory.exists()) {
                  System.out.println("Creating directory: " + uploadDir);
                  directory.mkdirs();
              }
              

              Path path = Paths.get(uploadDir, newFileName);
              Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
              System.out.println("File saved to: " + path.toString());
              
          

              if (fileName.toLowerCase().contains("cv")) {
                  candidatDTO.setPathCv(fullPath);
                  System.out.println("CV path set to: " + fullPath);
              } else if (fileName.toLowerCase().contains("photo") || 
                        fileName.toLowerCase().contains("image") || 
                        fileName.toLowerCase().contains("picture")) {
                  candidatDTO.setPathPhoto(fullPath);
                  System.out.println("Photo path set to: " + fullPath);
              } else {
                  System.out.println("WARNING: Unrecognized file type: " + fileName);
              }
          } else {
              System.out.println("File is null or empty for: " + fileName);
          }
      }
  
  }

    private CandidatDTO updateCandidatEntity(CandidatDTO candidatDTO, Long userId) {
        Candidat candidat = candidatRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Candidat not found"));
        
        candidatDTO.setId(candidat.getId());
        
        User user = candidat.getUser();
        user.setFirstName(candidatDTO.getPrenom());
        user.setLastName(candidatDTO.getNom());
        user.setEmail(candidatDTO.getEmail());
        userRepository.save(user);
        
      
        Candidat updatedCandidat = candidatMapper.toEntity(candidatDTO);
        updatedCandidat.setUser(user);
        
        if (candidatDTO.getPathCv() == null) {
            updatedCandidat.setPathCv(candidat.getPathCv());
        }
        if (candidatDTO.getPathPhoto() == null) {
            updatedCandidat.setPathPhoto(candidat.getPathPhoto());
        }
        
        Candidat savedCandidat = candidatRepository.save(updatedCandidat);
        return candidatMapper.toDto(savedCandidat);
    }
}