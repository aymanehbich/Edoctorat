package com.edoc.services.sujet;

import com.edoc.dto.CedDTO;
import com.edoc.dto.FormationDto;
import com.edoc.dto.ProfesseurDTO;
import com.edoc.dto.Result;
import com.edoc.dto.SujetDTO;
import com.edoc.dto.SujetDTO1;
import com.edoc.dto.mapper.CedMapper;
import com.edoc.dto.mapper.SujetMapper;
import com.edoc.models.FormationDoctorale;
import com.edoc.models.Professeur;
import com.edoc.models.Sujet;
import com.edoc.models.User;
import com.edoc.repository.FormationDoctoraleRepository;
import com.edoc.repository.ProfesseurRepository;
import com.edoc.repository.SujetRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SujetService {
    
    @Autowired
    private SujetRepository sujetRepository;
    
    @Autowired
    private ProfesseurRepository professeurRepository;
    
    @Autowired
    private FormationDoctoraleRepository formationDoctoraleRepository;
    @Autowired
    private SujetMapper sujetMapper;


    // -------------------------- get user sujets --------------------------------------------
    public Result<SujetDTO> getUserSujets(Long userId) {
        List<Sujet> sujets = sujetRepository.findByProfesseurUserIdOrCoDirecteurUserId(userId, userId);
  
        return new Result<>(sujets.size(), null, null, sujets.stream().map(this::convertToDTO).collect(Collectors.toList()));
        
    }

    // -------------------------- add sujet --------------------------------------------
    public SujetDTO createSujet(Map<String, Object> request, Long professeurId) {

        Professeur professeur = professeurRepository.findByUserId(professeurId);
        if (professeur == null) {
            throw new RuntimeException("Professeur not found");
        }
    
        int currentNombreProposer;
        currentNombreProposer = professeur.getNombreProposer();

    
        Long inscriptionCount = sujetRepository.countInscriptionsByProfesseurId(professeur.getId());
        int numSom = currentNombreProposer + inscriptionCount.intValue();
        
        professeur.setNumSom(String.valueOf(numSom));
        
        if (numSom >= 12) {
            throw new RuntimeException("Le professeur a atteint la limite maximale de supervision (12). NumSom actuel: " + numSom);
        }
    
        Sujet sujet = new Sujet();
        
        sujet.setTitre((String) request.get("titre"));
        sujet.setDescription((String) request.get("description"));
        sujet.setPublier(false);
        
        Long coDirecteurId = Long.parseLong(request.get("coDirecteurId").toString());
        Professeur coDirecteur = professeurRepository.findById(coDirecteurId)
            .orElseThrow(() -> new RuntimeException("Co-directeur not found"));
        sujet.setCoDirecteur(coDirecteur);
        
        Long formationDoctoraleId = Long.parseLong(request.get("formationDoctoraleId").toString());
        FormationDoctorale formationDoctorale = formationDoctoraleRepository.findById(formationDoctoraleId)
            .orElseThrow(() -> new RuntimeException("Formation Doctorale not found"));
        sujet.setFormationDoctorale(formationDoctorale);
    
        sujet.setProfesseur(professeur);
    
        professeur.setNombreProposer(currentNombreProposer + 1);
        professeurRepository.save(professeur);
    
        Sujet savedSujet = sujetRepository.save(sujet);
        
        return convertToDTO(savedSujet);
    }

    //-----------create sujet by labo--------------------------------
    public SujetDTO createSujetByLabo(Map<String, Object> request, Long professeurId) {

        Sujet sujet = new Sujet();
        
        sujet.setTitre((String) request.get("titre"));
      /*   sujet.setDescription((String) request.get("description"));
         */
        sujet.setPublier(false);
        
        Long coDirecteurId = Long.parseLong(request.get("coDirecteurId").toString());
        Professeur coDirecteur = professeurRepository.findById(coDirecteurId)
            .orElseThrow(() -> new RuntimeException("Co-directeur not found"));
        sujet.setCoDirecteur(coDirecteur);
        

        Long formationDoctoraleId = Long.parseLong(request.get("formationDoctoraleId").toString());
        FormationDoctorale formationDoctorale = formationDoctoraleRepository.findById(formationDoctoraleId)
            .orElseThrow(() -> new RuntimeException("Formation Doctorale not found"));
        sujet.setFormationDoctorale(formationDoctorale);


        Professeur professeur = professeurRepository.findByUserId(Long.parseLong(request.get("professeurId").toString()));
        sujet.setProfesseur(professeur);

        Sujet savedSujet = sujetRepository.save(sujet);
        
        return convertToDTO(savedSujet);
    }
    //-----------update sujet by labo--------------------------------
    public SujetDTO updateSujetByLabo(Map<String, Object> request, Long sujetid) {

        Sujet sujet = sujetRepository.findById(sujetid).get();
        
        sujet.setTitre((String) request.get("titre"));
      /*   sujet.setDescription((String) request.get("description"));
         */
        sujet.setPublier(false);
        
        Long coDirecteurId = Long.parseLong(request.get("coDirecteurId").toString());
        Professeur coDirecteur = professeurRepository.findById(coDirecteurId)
            .orElseThrow(() -> new RuntimeException("Co-directeur not found"));
        sujet.setCoDirecteur(coDirecteur);
        

        Long formationDoctoraleId = Long.parseLong(request.get("formationDoctoraleId").toString());
        FormationDoctorale formationDoctorale = formationDoctoraleRepository.findById(formationDoctoraleId)
            .orElseThrow(() -> new RuntimeException("Formation Doctorale not found"));
        sujet.setFormationDoctorale(formationDoctorale);


        Professeur professeur = professeurRepository.findByUserId(Long.parseLong(request.get("professeurId").toString()));
        sujet.setProfesseur(professeur);

        Sujet savedSujet = sujetRepository.save(sujet);
        
        return convertToDTO(savedSujet);
    }


    // -------------------------- delete sujet --------------------------------------------
    public SujetDTO deleteSujet(Long id) {
      Sujet sujet = sujetRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Sujet with ID " + id + " not found"));
        
        SujetDTO deletedSujetDTO = convertToDTO(sujet);
        
        sujetRepository.deleteById(id);
        
        return deletedSujetDTO;
  }




    // -------------------------- update sujet --------------------------------------------
    public SujetDTO updateSujet(Long id, Map<String, Object> request, Long userId) {

      Sujet sujet = sujetRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Sujet not found"));
      
      sujet.setTitre((String) request.get("titre"));
      sujet.setDescription((String) request.get("description"));
      
      Long coDirecteurId = Long.parseLong(request.get("coDirecteurId").toString());
      Professeur coDirecteur = professeurRepository.findById(coDirecteurId)
          .orElseThrow(() -> new RuntimeException("Co-directeur not found"));
      sujet.setCoDirecteur(coDirecteur);
      
      Long formationDoctoraleId = Long.parseLong(request.get("formationDoctoraleId").toString());
      FormationDoctorale formationDoctorale = formationDoctoraleRepository.findById(formationDoctoraleId)
          .orElseThrow(() -> new RuntimeException("Formation Doctorale not found"));
      sujet.setFormationDoctorale(formationDoctorale);
      
      if (request.containsKey("publier")) {
          sujet.setPublier((Boolean) request.get("publier"));
      }
      
      Professeur professeur = professeurRepository.findByUserId(userId);
      if (!sujet.getProfesseur().getId().equals(professeur.getId())) {
          throw new RuntimeException("You are not authorized to update this sujet");
      }
      
      Sujet updatedSujet = sujetRepository.save(sujet);
      
      return convertToDTO(updatedSujet);
  }


  // -------------------------- get sujet by id --------------------------------------------
  public SujetDTO getSujetById(Long id) {

    Sujet sujet = sujetRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Sujet not found with id: " + id));
    
    return convertToDTO(sujet);
  }
  
  // -------------------------- convert to dto --------------------------------------------
  private SujetDTO convertToDTO(Sujet sujet) {
      SujetDTO dto = new SujetDTO();
      dto.setId(sujet.getId());
      dto.setTitre(sujet.getTitre());
      dto.setDescription(sujet.getDescription());
      dto.setPublier(sujet.isPublier());
      
      

      if (sujet.getProfesseur() != null) {
        ProfesseurDTO professeurDTO = new ProfesseurDTO();
          professeurDTO.setId(sujet.getProfesseur().getId());
          
          User user = sujet.getProfesseur().getUser(); 
          if (user != null) {
              professeurDTO.setNom(user.getLastName());
              professeurDTO.setPrenom(user.getFirstName());
          }
          
          dto.setProfesseur(professeurDTO);
      }
      

      if (sujet.getCoDirecteur() != null) {
          ProfesseurDTO coDirecteurDTO = new ProfesseurDTO();
          coDirecteurDTO.setId(sujet.getCoDirecteur().getId());
          
          User user = sujet.getCoDirecteur().getUser(); 
          if (user != null) {
              coDirecteurDTO.setNom(user.getLastName());
              coDirecteurDTO.setPrenom(user.getFirstName());
          }
          
          dto.setCoDirecteur(coDirecteurDTO);
      }
      
      if (sujet.getFormationDoctorale() != null) {
          FormationDto FormationDto = new FormationDto();
          FormationDto.setId(sujet.getFormationDoctorale().getId());
          FormationDto.setCed(sujet.getFormationDoctorale().getCed().getId());///////////////////////////////////:
          FormationDto.setEtablissement(sujet.getFormationDoctorale().getEtablissement().getId());
          FormationDto.setAxeDeRecherche(sujet.getFormationDoctorale().getAxeDeRecherche());
          FormationDto.setPathImage(sujet.getFormationDoctorale().getPathImage());
          FormationDto.setTitre(sujet.getFormationDoctorale().getTitre());
          FormationDto.setInitiale(sujet.getFormationDoctorale().getInitiale());
          FormationDto.setDateAccreditation(sujet.getFormationDoctorale().getDateAccreditation());
          dto.setFormationDoctorale(FormationDto);
      }
      
      return dto;
  }
  public Result<SujetDTO> getall(){
    List<SujetDTO> sujets = sujetRepository.findAll().stream()
        .filter(Sujet::isPublier)
        .map(this::convertToDTO)
        .collect(Collectors.toList());
    return new Result<SujetDTO>(sujets.size(), null, null, sujets);
}
public Result<SujetDTO1> getall1(){
    List<SujetDTO1> sujets = sujetRepository.findAll().stream().map(sujetMapper::toDto).collect(Collectors.toList());
    return new Result<SujetDTO1>(sujets.size(), null, null, sujets);
}

public void PublierSujets() {
    List<Sujet> sujets = sujetRepository.findAll();
    for (Sujet sujet : sujets) {
        sujet.setPublier(true);
    }
    sujetRepository.saveAll(sujets);
}

public Result<SujetDTO1> getSujetsLabo(Long userId) {
    Professeur professeur = professeurRepository.findByUserId(userId);
    List<Sujet> sujets = sujetRepository.findSujetsByLaboId(professeur.getLaboratoire().getId());
    return new Result<>(sujets.size(), null, null, sujets.stream().map(sujetMapper::toDto).collect(Collectors.toList()));
}
}