package com.edoc.dto.mapper;

import com.edoc.dto.diplome.DiplomeDTO;
import com.edoc.models.Diplome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class DiplomeMapper {
    
    @Autowired
    private AnnexeMapper annexeMapper;
    

    
    public DiplomeDTO toDto(Diplome diplome) {
        if (diplome == null) {
            return null;
        }

        return new DiplomeDTO(
            diplome.getId(),
            diplome.getIntitule(),
            diplome.getType(),
            diplome.getDateCommission(),
            diplome.getMention(),
            diplome.getPays(),
            diplome.getEtablissement(),
            diplome.getSpecialite(),
            diplome.getVille(),
            diplome.getProvince(),
            diplome.getMoyen_generale(),
            diplome.getCandidat() != null ? diplome.getCandidat().getId() : null,
            diplome.getAnnexes().stream().map(annexeMapper::toDto).collect(Collectors.toList())
        );
    }
}
