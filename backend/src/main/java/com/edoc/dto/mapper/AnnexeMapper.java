package com.edoc.dto.mapper;

import com.edoc.dto.annexe.AnnexeDTO;
import com.edoc.models.Annexe;
import org.springframework.stereotype.Component;

@Component
public class AnnexeMapper {
    
    public AnnexeDTO toDto(Annexe annexe) {
        if (annexe == null) {
            return null;
        }

        return new AnnexeDTO(
            annexe.getId(),
            annexe.getTypeAnnexe(),
            annexe.getTitre(),
            annexe.getPathFile()
        );
    }
}
