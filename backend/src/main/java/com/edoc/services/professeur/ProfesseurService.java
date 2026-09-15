package com.edoc.services.professeur;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.edoc.dto.CandidatExaminerDTO;
import com.edoc.dto.CandidatInscriptionDTO;
import com.edoc.dto.CandidatProfDTO;
import com.edoc.dto.ExaminerDTO1;
import com.edoc.dto.InscriptionDTO1;
import com.edoc.dto.PostulerProfCandidatDTO;
import com.edoc.dto.ProfesseurDTO;
import com.edoc.dto.Result;
import com.edoc.dto.SujetDTO;
import com.edoc.dto.SujetInscriptionDTO;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.models.Examiner;
import com.edoc.models.Inscription;
import com.edoc.models.Postuler;
import com.edoc.models.Professeur;
import com.edoc.models.Sujet;
import com.edoc.models.User;
import com.edoc.repository.ExaminerRepository;
import com.edoc.repository.InscriptionRepository;
import com.edoc.repository.PostulerRepository;
import com.edoc.repository.ProfesseurRepository;
import com.edoc.repository.SujetRepository;

@Service
public class ProfesseurService {
    @Autowired
    private ProfesseurRepository professeurRepository;

    @Autowired
    private PostulerRepository postulerRepository;

    @Autowired
    private SujetRepository sujetRepository;

    @Autowired
    private ExaminerRepository examinerRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;
    
    public Result<ProfesseurDTO> getProfesseurs() {
      List<Professeur> professeurs = professeurRepository.findAll();
      List<ProfesseurDTO> dtos = professeurs.stream()
          .filter(professeur -> professeur.getUser() != null)
          .map(this::convertToDTO)
          .collect(Collectors.toList());
          
      return new Result<>(
          dtos.size(),
          null,
          null,
          dtos
      );
  }
  

  public ResultDto<PostulerProfCandidatDTO> getCandidatsForProfesseur(Long userId) {
        Professeur professeur = professeurRepository.findByUserId(userId);
        
        List<Sujet> sujets = sujetRepository.findByProfesseur(professeur);
        
        List<Postuler> postulers = postulerRepository.findBySujetIn(sujets);
        
        List<PostulerProfCandidatDTO> postulerDTOs = postulers.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
            ResultDto<PostulerProfCandidatDTO> result = new ResultDto<PostulerProfCandidatDTO>( postulerDTOs.size(), null, null, postulerDTOs);
      
        
        return result;
  }

  // --------------------- dto candidat professeur -----------------------------

     private PostulerProfCandidatDTO convertToDTO(Postuler postuler) {
        PostulerProfCandidatDTO dto = new PostulerProfCandidatDTO();
        dto.setId(postuler.getId());
        dto.setPathFile(postuler.getPathFile());
         dto.setValidé(false);
        
        if (postuler.getCandidat() != null) {
            CandidatProfDTO candidatDTO = new CandidatProfDTO();
            candidatDTO.setId(postuler.getCandidat().getId());
            candidatDTO.setNom(postuler.getCandidat().getUser().getLastName());
            candidatDTO.setPrenom(postuler.getCandidat().getUser().getFirstName());
            candidatDTO.setEmail(postuler.getCandidat().getUser().getEmail());
            candidatDTO.setCne(postuler.getCandidat().getCne());
            dto.setCandidat(candidatDTO);
        }
        
        if (postuler.getSujet() != null) {
            SujetDTO sujetDTO = new SujetDTO();
            sujetDTO.setId(postuler.getSujet().getId());
            sujetDTO.setTitre(postuler.getSujet().getTitre());
            dto.setSujet(sujetDTO);
        }
        
        return dto;
    }

  // --------------------- dto professeurs -----------------------------

    private ProfesseurDTO convertToDTO(Professeur professeur) {
        User user = professeur.getUser();
        return new ProfesseurDTO(
            professeur.getId(),
            user.getLastName(),
            user.getFirstName()
        );
    }

  
      // --------------------- professeur examiner -----------------------------

    public Result<ExaminerDTO1> getResultatsExaminer(Long userId,Integer offset) {
        Professeur professeur = professeurRepository.findByUserId(userId);
      
        PageRequest pageRequest = offset != null 
            ? PageRequest.of(offset / 50, 50) 
            : PageRequest.of(0, 50);
        
        List<Sujet> sujets = sujetRepository.findByProfesseur(professeur);
        
        Page<Examiner> examinerPage = examinerRepository.findBySujetIn(sujets, pageRequest);
        
        List<ExaminerDTO1> examinerDTOs = examinerPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
            return new Result<>(
              examinerPage.getTotalElements(), 
              examinerPage.hasNext() ? "has_next" : null, 
              examinerPage.hasPrevious() ? "has_previous" : null, 
              examinerDTOs
          );
    }


    // --------------------- dto examiner -----------------------------
  private ExaminerDTO1 convertToDTO(Examiner examiner) {
     ExaminerDTO1 dto = new ExaminerDTO1();
        dto.setId(examiner.getId());
        dto.setNoteDossier(examiner.getNoteDossier());
        dto.setNoteEntretien(examiner.getNoteEntretien());
        dto.setDecision(examiner.getDecision());
        dto.setCommission(examiner.getCommission().getId());
        dto.setPublier(examiner.getPublier());
        
        if (examiner.getSujet() != null) {
            SujetDTO sujetDTO = new SujetDTO();
            sujetDTO.setId(examiner.getSujet().getId());
            sujetDTO.setTitre(examiner.getSujet().getTitre());
            dto.setSujet(sujetDTO);
        }
        
        if (examiner.getCandidat() != null) {
            CandidatExaminerDTO candidatDTO = new CandidatExaminerDTO();
            candidatDTO.setId(examiner.getCandidat().getId());
            candidatDTO.setCne(examiner.getCandidat().getCne());
            
            User user = examiner.getCandidat().getUser();
            if (user != null) {
                candidatDTO.setNom(user.getLastName());
                candidatDTO.setPrenom(user.getFirstName());
            }
            
            dto.setCandidat(candidatDTO);
            dto.setCne(examiner.getCandidat().getCne());
        }
        
        return dto;
  }

  // --------------------- professeur inscription -----------------------------

  
    public Result<InscriptionDTO1> getMesInscrits(Long userId, Integer offset) {

        Professeur professeur = professeurRepository.findByUserId(userId);
        
        List<Sujet> sujets = sujetRepository.findByProfesseur(professeur);
        
        PageRequest pageRequest = offset != null 
            ? PageRequest.of(offset / 50, 50) 
            : PageRequest.of(0, 50);
        
        Page<Inscription> inscriptionPage = inscriptionRepository.findBySujetIn(sujets, pageRequest);
        
        List<InscriptionDTO1> inscriptionDTOs = inscriptionPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new Result<>(
            inscriptionPage.getTotalElements(), 
            inscriptionPage.hasNext() ? "has_next" : null, 
            inscriptionPage.hasPrevious() ? "has_previous" : null, 
            inscriptionDTOs
        );
    }

    // --------------------- dto inscription -----------------------------
    private InscriptionDTO1 convertToDTO(Inscription inscription) {
        InscriptionDTO1 dto = new InscriptionDTO1();
        dto.setId(inscription.getId());
        dto.setDateDisposeDossier(inscription.getDateDisposeDossier());
        dto.setRemarque(inscription.getRemarque());
        dto.setValider(Boolean.TRUE.equals(inscription.getValider()));
        
        if (inscription.getCandidat() != null) {
            CandidatInscriptionDTO candidatDTO = new CandidatInscriptionDTO();
            candidatDTO.setId(inscription.getCandidat().getId());
            candidatDTO.setCne(inscription.getCandidat().getCne());
            
            User user = inscription.getCandidat().getUser();
            if (user != null) {
                candidatDTO.setNom(user.getLastName());
                candidatDTO.setPrenom(user.getFirstName());
            }
            
            dto.setCandidat(candidatDTO);
        }
        
        if (inscription.getSujet() != null) {
            SujetInscriptionDTO sujetDTO = new SujetInscriptionDTO();
            sujetDTO.setId(inscription.getSujet().getId());
            sujetDTO.setTitre(inscription.getSujet().getTitre());
            
            dto.setSujet(sujetDTO);
        }
        
        return dto;
    }
}
