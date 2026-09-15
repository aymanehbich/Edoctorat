package com.edoc.dto.mapper;

import org.springframework.stereotype.Component;

import com.edoc.dto.EtablissementDTO;
import com.edoc.dto.FormationDoctoraleDTO;
import com.edoc.dto.FormationDto;
import com.edoc.models.Ced;

import com.edoc.models.FormationDoctorale;
@Component
public class FormatioDoctoraleMapper {
    
    public FormationDoctoraleDTO toDto(FormationDoctorale formation) {
        if (formation == null) {
            return null;
        }
        
        FormationDoctoraleDTO dto = new FormationDoctoraleDTO();
        dto.setId(formation.getId());
        dto.setCed(new CedMapper().toDto(formation.getCed())); // Assuming you have a CedMapper formation.getCed().getId());
        dto.setEtablissement(new EtablissementDTO(formation.getEtablissement().getId(), formation.getEtablissement().getNomEtablissement()) );
        dto.setAxeDeRecherche(formation.getAxeDeRecherche());
        dto.setPathImage(formation.getPathImage());
        dto.setTitre(formation.getTitre());
        dto.setInitiale(formation.getInitiale());
        dto.setDateAccreditation(formation.getDateAccreditation());
        
        return dto;
    }
    
    /* public static Formation toEntity(FormationDto formationDto) {
        if (formationDto == null) {
            return null;
        }
        
        Formation formation = new Formation();
        formation.setId(formationDto.getId());
        formation.setEtablissement(formationDto.getEtablissement());
        formation.setAxeDeRecherche(formationDto.getAxeDeRecherche());
        formation.setPathImage(formationDto.getPathImage());
        formation.setTitre(formationDto.getTitre());
        formation.setInitiale(formationDto.getInitiale());
        formation.setDateAccreditation(formationDto.getDateAccreditation());
        
        // Assuming you have a method to fetch Ced by its ID
        formation.setCed(fetchCedById(formationDto.getCed()));
        
        return formation;
    }
    
    private static Ced fetchCedById(Long id) {
        // Implement this method to fetch Ced by ID
        return new Ced(); // Placeholder
    } */
}
