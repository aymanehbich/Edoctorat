package com.edoc.services.scolarite;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edoc.dto.inscription.InscriptionScolariteDTO;
import com.edoc.dto.inscription.PostulerScolariteDTO;
import com.edoc.dto.inscription.CandidatScolariteDTO;
import com.edoc.dto.inscription.SujetScolariteDTO;
import com.edoc.dto.inscription.PagedResponse;
import com.edoc.dto.inscription.ValidationRequest;
import com.edoc.exception.ResourceNotFoundException;
import com.edoc.models.Candidat;
import com.edoc.models.Inscription;
import com.edoc.models.Postuler;
import com.edoc.models.Sujet;
import com.edoc.repository.InscriptionRepository;
import com.edoc.repository.PostulerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScolariteService {

    private final InscriptionRepository inscriptionRepository;
    private final PostulerRepository postulerRepository;

    public PagedResponse<InscriptionScolariteDTO> getInscriptions(Pageable pageable) {

        Page<Inscription> inscriptionPage = inscriptionRepository.findAllWithRelations(pageable);
        
        List<Long> candidatIds = inscriptionPage.getContent().stream()
            .map(inscription -> inscription.getCandidat().getId())
            .collect(Collectors.toList());
            
        List<Long> sujetIds = inscriptionPage.getContent().stream()
            .map(inscription -> inscription.getSujet().getId())
            .collect(Collectors.toList());
        
        List<Postuler> postulers = postulerRepository.findByCandidatIdInAndSujetIdIn(candidatIds, sujetIds);
        

        Map<String, PostulerScolariteDTO> postulerMap = postulers.stream()
            .collect(Collectors.toMap(
                p -> p.getCandidat().getId() + "_" + p.getSujet().getId(),
                this::toDTO
            ));
        

          List<InscriptionScolariteDTO> inscriptionDTOs = inscriptionPage.getContent().stream()
            .map(inscription -> {
                InscriptionScolariteDTO dto = toDTO(inscription);
                String key = inscription.getCandidat().getId() + "_" + inscription.getSujet().getId();
                PostulerScolariteDTO postulerDTO = postulerMap.get(key);
               
                return dto;
            })
            .collect(Collectors.toList());
        
        PagedResponse<InscriptionScolariteDTO> response = new PagedResponse<>();
        response.setResults(inscriptionDTOs);
        response.setCount((int) inscriptionPage.getTotalElements());
        
        if (inscriptionPage.hasNext()) {
            response.setNext("/api/scolarite?offset=" + 
                (pageable.getPageNumber() + 1) * pageable.getPageSize());
        }
        if (inscriptionPage.hasPrevious()) {
            response.setPrevious("/api/scolarite?offset=" + 
                (pageable.getPageNumber() - 1) * pageable.getPageSize());
        }
        
        return response;
    }

    @Transactional
    public InscriptionScolariteDTO updateValidation(Long id, ValidationRequest request) {
        Inscription inscription = inscriptionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Inscription not found"));
            
        inscription.setRemarque(request.getRemarque());
        inscription.setValider(request.getValider());
        

        Inscription savedInscription = inscriptionRepository.save(inscription);
        

        Postuler postuler = postulerRepository.findByCandidatIdAndSujetId(
            savedInscription.getCandidat().getId(),
            savedInscription.getSujet().getId()
        ).orElse(null);
        
        InscriptionScolariteDTO dto = toDTO(savedInscription);
        
        
        return dto;
    }

    public InscriptionScolariteDTO toDTO(Inscription inscription) {
        if (inscription == null) return null;
        
        InscriptionScolariteDTO dto = new InscriptionScolariteDTO();
        dto.setId(inscription.getId());
        dto.setDateDisposeDossier(inscription.getDateDisposeDossier());
        dto.setRemarque(inscription.getRemarque());
        dto.setValider(inscription.getValider());
        dto.setCandidat(toDTO(inscription.getCandidat()));
        dto.setSujet(toDTO(inscription.getSujet()));
        return dto;
    }

    public List<InscriptionScolariteDTO> mapInscriptionsToDTOs(List<Inscription> inscriptions) {
      if (inscriptions == null) return null;
      return inscriptions.stream()
          .map(this::toDTO)
          .collect(Collectors.toList());
  }

    public PostulerScolariteDTO toDTO(Postuler postuler) {
        if (postuler == null) return null;
        
        PostulerScolariteDTO dto = new PostulerScolariteDTO();
        dto.setId(postuler.getId());
        dto.setPathFile(postuler.getPathFile());
        dto.setCandidat(toDTO(postuler.getCandidat()));
        dto.setSujet(toDTO(postuler.getSujet()));
        return dto;
    }

    public List<PostulerScolariteDTO> toDTOs(List<Postuler> postulers) {
        if (postulers == null) return null;
        return postulers.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public CandidatScolariteDTO toDTO(Candidat candidat) {
        if (candidat == null) return null;
        
        CandidatScolariteDTO dto = new CandidatScolariteDTO();
        dto.setId(candidat.getId());
        dto.setCne(candidat.getCne());
        dto.setNom(candidat.getUser().getLastName());
        dto.setPrenom(candidat.getUser().getFirstName());
        dto.setPhoto(candidat.getPathPhoto());
        return dto;
    }

    public SujetScolariteDTO toDTO(Sujet sujet) {
        if (sujet == null) return null;
        
        SujetScolariteDTO dto = new SujetScolariteDTO();
        dto.setId(sujet.getId());
        dto.setTitre(sujet.getTitre());
        dto.setFormationDoctorale(sujet.getFormationDoctorale().getTitre());
        dto.setCed(sujet.getFormationDoctorale().getCed().getTitre());
        dto.setLaboratoire(sujet.getProfesseur().getLaboratoire().getNomLaboratoire());
        return dto;
    }
}
