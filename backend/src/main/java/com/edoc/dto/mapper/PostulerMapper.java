package com.edoc.dto.mapper;


import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edoc.dto.PostulerDTO;
import com.edoc.models.Postuler;
@Component
public class PostulerMapper {
    @Autowired
    private CandidatMapper candidatMapper;
    @Autowired
    private SujetMapper sujetMapper;
    
    public PostulerDTO toDto(Postuler postuler) {
        if (postuler == null) {
            return null;
        }
        
        PostulerDTO dto = new PostulerDTO();
        dto.setId(postuler.getId());
        dto.setPathFile(postuler.getPathFile());
        dto.setSujet(sujetMapper.toDto(postuler.getSujet()));
        dto.setCandidat(candidatMapper.toDto(postuler.getCandidat()));
        
        return dto;
    }
    
/*     public static Postuler toEntity(PostulerDTO postulerDto) {
        if (postulerDto == null) {
            return null;
        }
        
        Postuler postuler = new Postuler();
        postuler.setId(postulerDto.getId());
        postuler.setPathFile(postulerDto.getPathFile());
        postuler.setDatePostulation(postulerDto.getDatePostulation());
        
        // Assuming you have methods to fetch Candidat and Sujet by their IDs
        postuler.setCandidat(fetchCandidatById(postulerDto.getCandidatId()));
        postuler.setSujet(fetchSujetById(postulerDto.getSujetId()));
        
        return postuler;
    }
    
    private static Candidat fetchCandidatById(Long id) {
        // Implement this method to fetch Candidat by ID
        return new Candidat(); // Placeholder
    }
    
    private static Sujet fetchSujetById(Long id) {
        // Implement this method to fetch Sujet by ID
        return new Sujet(); // Placeholder
    } */
}
