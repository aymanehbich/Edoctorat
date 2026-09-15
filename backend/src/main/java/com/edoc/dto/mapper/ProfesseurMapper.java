package com.edoc.dto.mapper;

import org.springframework.stereotype.Component;

import com.edoc.dto.ProfesseurDTO;
import com.edoc.models.Professeur;

@Component
public class ProfesseurMapper {
    
    public  ProfesseurDTO toDto(Professeur professeur) {
        if (professeur == null) {
            return null;
        }
        
        ProfesseurDTO dto = new ProfesseurDTO();
        dto.setId(professeur.getId());
        dto.setNom(professeur.getUser().getLastName());
        dto.setPrenom(professeur.getUser().getFirstName());
        
        return dto;
    }
    
/*     public static Professeur toEntity(ProfesseurDTO professeurDto) {
        if (professeurDto == null) {
            return null;
        }
        
        Professeur professeur = new Professeur();
        professeur.setId(professeurDto.getId());
        professeur.setNom(professeurDto.getNom());
        professeur.setPrenom(professeurDto.getPrenom());
        
        return professeur;
    } */
}
