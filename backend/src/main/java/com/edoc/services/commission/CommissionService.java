package com.edoc.services.commission;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edoc.dto.CommissionDTO;
import com.edoc.dto.ProfesseurDTO;
import com.edoc.dto.SujetDTO;
import com.edoc.dto.SujetDTO1;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.dto.mapper.CommissionMapper;
import com.edoc.dto.mapper.SujetMapper;
import com.edoc.models.Commission;
import com.edoc.models.CommissionProfesseur;
import com.edoc.models.Examiner;
import com.edoc.models.Laboratoire;
import com.edoc.models.Postuler;
import com.edoc.models.Professeur;
import com.edoc.models.Sujet;
import com.edoc.models.User;
import com.edoc.repository.CommissionProfesseurRepository;
import com.edoc.repository.CommissionRepository;
import com.edoc.repository.ExaminerRepository;
import com.edoc.repository.PostulerRepository;
import com.edoc.repository.ProfesseurRepository;
import com.edoc.repository.SujetRepository;


import jakarta.persistence.EntityNotFoundException;

@Service
public class CommissionService {
    @Autowired
    private SujetMapper sujetMapper;
    @Autowired
    private CommissionRepository commissionRepository;
    
    @Autowired
    private CommissionProfesseurRepository commissionProfesseurRepository;
    
    @Autowired
    private ProfesseurRepository professeurRepository;

    @Autowired
    private SujetRepository sujetRepository;

    @Autowired
    private ExaminerRepository examinerRepository;
    @Autowired
    private CommissionMapper commissionMapper;
    @Autowired
    private PostulerRepository postulerRepository;

    // --------------------- create commission (schedule a jury session) -----------------------

    public CommissionDTO createCommission(Map<String, Object> request, Long userId) {
        Professeur organisateur = professeurRepository.findByUserId(userId);

        Commission commission = new Commission();
        commission.setDateCommission((String) request.get("dateCommission"));
        commission.setHeure((String) request.get("heure"));
        commission.setLieu((String) request.get("lieu"));
        if (organisateur != null) {
            commission.setLaboratoire(organisateur.getLaboratoire());
        }
        Commission savedCommission = commissionRepository.save(commission);

        List<?> professeurIds = (List<?>) request.get("professeurIds");
        if (professeurIds != null) {
            for (Object professeurIdObj : professeurIds) {
                Long professeurId = Long.parseLong(professeurIdObj.toString());
                Professeur professeur = professeurRepository.findById(professeurId)
                    .orElseThrow(() -> new RuntimeException("Professeur not found: " + professeurId));
                CommissionProfesseur commissionProfesseur = new CommissionProfesseur();
                commissionProfesseur.setCommission(savedCommission);
                commissionProfesseur.setProfesseur(professeur);
                commissionProfesseurRepository.save(commissionProfesseur);
            }
        }

        List<?> postulerIds = (List<?>) request.get("postulerIds");
        if (postulerIds != null) {
            for (Object postulerIdObj : postulerIds) {
                Long postulerId = Long.parseLong(postulerIdObj.toString());
                Postuler postuler = postulerRepository.findById(postulerId)
                    .orElseThrow(() -> new RuntimeException("Postuler not found: " + postulerId));

                Examiner examiner = new Examiner();
                examiner.setCommission(savedCommission);
                examiner.setCandidat(postuler.getCandidat());
                examiner.setSujet(postuler.getSujet());
                examiner.setNoteDossier(0.0);
                examiner.setNoteEntretien(0.0);
                examiner.setDecision("");
                examiner.setPublier(false);
                examiner.setValider(false);
                examinerRepository.save(examiner);
            }
        }

        return convertToDTO(savedCommission);
    }

    public List<CommissionDTO> getCommissionsForProfesseur(Long userId) {
        
        Professeur professeur = professeurRepository.findByUserId(userId);
        
        List<CommissionProfesseur> commissionProfesseurs = commissionProfesseurRepository.findByProfesseurId(professeur.getId());
        
        return commissionProfesseurs.stream()
            .map(cp -> {
                Commission commission = commissionRepository.findById(cp.getCommission().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Commission not found"));
                return convertToDTO(commission);
            })
            .collect(Collectors.toList());
    }
    public ResultDto<CommissionDTO> getCommissionsOfCedFromProfesseur(User user, Integer offset, Integer limit) {
        Professeur professeur = professeurRepository.findByUserId(user.getId());
        List<Commission> commissions = commissionRepository.findByProfesseurIdCed(professeur.getId());
        return new ResultDto<CommissionDTO>(commissions.size(), null, null, commissions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
    }
    public ResultDto<CommissionDTO> getAllCommissions() {
      
        List<Commission> commissions = commissionRepository.findAll();
        return new ResultDto<CommissionDTO>(commissions.size(), null, null, commissions.stream()
            .map(this::ToDtoCommussionall)
            .collect(Collectors.toList()));
    }


    private CommissionDTO convertToDTO(Commission commission) {
      CommissionDTO dto = new CommissionDTO();
      dto.setId(commission.getId());
      dto.setDateCommission(commission.getDateCommission());
      dto.setHeure(commission.getHeure());
      dto.setLieu(commission.getLieu());
      
      if (commission.getLaboratoire() != null) {
          dto.setLabo(commission.getLaboratoire().getId());
      }
      
      List<CommissionProfesseur> commissionProfesseurs = commissionProfesseurRepository
      .findByCommissionId(commission.getId());
  
      dto.setParticipants(commissionProfesseurs.stream()
          .map(cp -> {
              Professeur professeur = professeurRepository.findById(cp.getProfesseur().getId())
                  .orElseThrow(() -> new EntityNotFoundException("Professeur not found"));
              return convertProfesseurToDTO(professeur);
          })
          .collect(Collectors.toList()));
      
      List<SujetDTO> sujets = sujetRepository.findSujetsByCommissionId(commission.getId()).stream()
          .map(sujet -> convertSujetToDTO(sujet, commission))
          .collect(Collectors.toList());
              
      dto.setSujets(sujets);
      
      return dto;
  }
  
  private SujetDTO convertSujetToDTO(Sujet sujet, Commission commission) {
      SujetDTO dto = new SujetDTO();
      dto.setId(sujet.getId());
      dto.setTitre(sujet.getTitre());
      dto.setDescription(sujet.getDescription());
      dto.setPublier(sujet.isPublier());
      
      Examiner examiner = examinerRepository.findByCommissionIdAndSujetId(
          commission.getId(), 
          sujet.getId()
      );
      
      dto.setValider(examiner != null && examiner.getValider());
  
      if (sujet.getProfesseur() != null) {
          ProfesseurDTO professeurDTO = convertProfesseurToDTO(sujet.getProfesseur());
          dto.setProfesseur(professeurDTO);
      }
  
      return dto;
  }

  
    private ProfesseurDTO convertProfesseurToDTO(Professeur professeur) {
        ProfesseurDTO dto = new ProfesseurDTO();
        dto.setId(professeur.getId());
        
        User user = professeur.getUser();
        if (user != null) {
            dto.setNom(user.getLastName());
            dto.setPrenom(user.getFirstName());
        }
        
        return dto;
    }
    private CommissionDTO ToDtoCommussionall(Commission commission) {
        CommissionDTO dto = new CommissionDTO();
        dto.setId(commission.getId());
        dto.setDateCommission(commission.getDateCommission());
        dto.setHeure(commission.getHeure());
        dto.setLieu(commission.getLieu());
        List<Examiner> examiners = examinerRepository.findByCommissionId(commission.getId()); 
        dto.setSujets(examiners.stream()
            .map(examiner -> {
                Sujet sujet = sujetRepository.findById(examiner.getSujet().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Sujet not found"));
                return sujetMapper.toDto0(sujet);
            })
            .collect(Collectors.toList()));
        
        if (commission.getLaboratoire() != null) {
            dto.setLabo(commission.getLaboratoire().getId());
        }
        
        return dto;
    }
    public ResultDto<CommissionDTO> getCommissionsOfLabo(Long userId) {
        Laboratoire laboratoire = professeurRepository.findByUserId(userId).getLaboratoire();
        List<Commission> commissions = commissionRepository.findByLaboratoireId(laboratoire.getId());
        ResultDto<CommissionDTO> result = new ResultDto<>();
        result.setResults(commissions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
        return result;
    }
}

