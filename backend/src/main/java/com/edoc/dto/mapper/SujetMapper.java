package com.edoc.dto.mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edoc.dto.FormationDoctoraleDTO;
import com.edoc.dto.SujetDTO;
import com.edoc.dto.SujetDTO1;
import com.edoc.models.Sujet;
@Component
public class SujetMapper {
    @Autowired
    private LaboratoireMapper laboratoireMapper;
    
    public SujetDTO1 toDto(Sujet sujet) {
        if (sujet == null) {
            return null;
        }
        
        SujetDTO1 dto = new SujetDTO1();
        dto.setId(sujet.getId());
        dto.setTitre(sujet.getTitre());
        dto.setDescription(sujet.getDescription());
        dto.setPublier(sujet.isPublier());
        dto.setFormationDoctorale(new FormatioDoctoraleMapper().toDto(sujet.getFormationDoctorale()));
        dto.setCoDirecteur(new ProfesseurMapper().toDto(sujet.getCoDirecteur()));
        dto.setProfesseur(new ProfesseurMapper().toDto(sujet.getProfesseur()));
        if(sujet.getProfesseur().getLaboratoire() != null){
        dto.setLabo(sujet.getProfesseur().getLaboratoire().getNomLaboratoire());
        dto.setLaboratoire(laboratoireMapper.toDto(sujet.getProfesseur().getLaboratoire()));
    }

        
        return dto;
    }

    public SujetDTO toDto0(Sujet sujet) {
        if (sujet == null) {
            return null;
        }
        
        SujetDTO dto = new SujetDTO();
        dto.setId(sujet.getId());
        dto.setTitre(sujet.getTitre());
        dto.setDescription(sujet.getDescription());
        dto.setPublier(sujet.isPublier());
        /* dto.setFormationDoctorale(new FormatioDoctoraleMapper().toDto(sujet.getFormationDoctorale())); */
        dto.setCoDirecteur(new ProfesseurMapper().toDto(sujet.getCoDirecteur()));
        dto.setProfesseur(new ProfesseurMapper().toDto(sujet.getProfesseur()));
        /* dto.setLabo(sujet.getProfesseur().getLaboratoire().getNomLaboratoire());} */
        return dto;
    }
    
/*     public static Sujet toEntity(SujetDTO sujetDto) {
        if (sujetDto == null) {
            return null;
        }
        
        Sujet sujet = new Sujet();
        sujet.setId(sujetDto.getId());
        sujet.setTitre(sujetDto.getTitre());
        sujet.setDescription(sujetDto.getDescription());
        sujet.setDateCreation(sujetDto.getDateCreation());
        sujet.setDateModification(sujetDto.getDateModification());
        
        return sujet;
    } */
}