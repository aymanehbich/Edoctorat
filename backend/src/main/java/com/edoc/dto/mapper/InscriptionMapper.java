package com.edoc.dto.mapper;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edoc.dto.InscriptionDTO;
import com.edoc.models.Inscription;

@Component
public class InscriptionMapper {
    @Autowired
    private CandidatMapper candidatMapper;
    @Autowired
    private SujetMapper sujetMapper;
    public InscriptionDTO toDto(Inscription inscription) {
        InscriptionDTO dto = new InscriptionDTO();
        dto.setId(inscription.getId());
        dto.setDateDiposeDossier(inscription.getDateDisposeDossier());
        dto.setRemarque(inscription.getRemarque());
        dto.setValider(Boolean.TRUE.equals(inscription.getValider()));
        dto.setCandidat(candidatMapper.toDto(inscription.getCandidat()));
        dto.setSujet(sujetMapper.toDto(inscription.getSujet()));
        return dto;
    }
}
