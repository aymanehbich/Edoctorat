package com.edoc.dto.mapper;

import org.springframework.stereotype.Component;

import com.edoc.dto.CedDTO;
import com.edoc.models.Ced;

@Component
public class CedMapper {
    
    public CedDTO toDto(Ced ced) {
        if (ced == null) {
            return null;
        }

        CedDTO cedDTO = new CedDTO();
        cedDTO.setId(ced.getId());
        cedDTO.setDescription(ced.getDescription());
        cedDTO.setPathImage(ced.getPathImage());
        cedDTO.setTitre(ced.getTitre());
        cedDTO.setInitiale(ced.getInitiale());
        
        if (ced.getDirecteur() != null) {
            cedDTO.setDirecteurId(ced.getDirecteur().getId());
            cedDTO.setDirecteurNom(ced.getDirecteur().getUser().getLastName());
            cedDTO.setDirecteurPrenom(ced.getDirecteur().getUser().getFirstName());
        }

        return cedDTO;
    }

    public Ced toEntity(CedDTO cedDTO) {
        if (cedDTO == null) {
            return null;
        }

        Ced ced = new Ced();
        ced.setId(cedDTO.getId());
        ced.setDescription(cedDTO.getDescription());
        ced.setPathImage(cedDTO.getPathImage());
        ced.setTitre(cedDTO.getTitre());
        ced.setInitiale(cedDTO.getInitiale());
        
        // Note: Directeur (Professeur) should be set separately
        // as it requires fetching the Professeur entity from the database
        
        return ced;
    }
}