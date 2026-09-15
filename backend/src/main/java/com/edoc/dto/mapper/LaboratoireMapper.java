package com.edoc.dto.mapper;

import org.springframework.stereotype.Component;

import com.edoc.dto.LaboratoireDTO;
import com.edoc.models.Laboratoire;

@Component
public class LaboratoireMapper {
 
  
    public LaboratoireDTO toDto(Laboratoire laboratoire) {
        LaboratoireDTO dto = new LaboratoireDTO();
        dto.setId(laboratoire.getId());
        dto.setNomLaboratoire(laboratoire.getNomLaboratoire());
        return dto;
    }
}

