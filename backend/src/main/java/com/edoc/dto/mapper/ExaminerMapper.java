package com.edoc.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edoc.dto.CommissionDTO;
import com.edoc.dto.ExaminerDTO;
import com.edoc.models.Examiner;

@Component
public class ExaminerMapper {
    @Autowired
    private CandidatMapper candidatMapper;
    @Autowired
    private SujetMapper sujetMapper;
    @Autowired
    private CommissionMapper commissionMapper;
    
    public ExaminerDTO toDto(Examiner examiner) {
        if (examiner == null) {
            return null;
        }

        ExaminerDTO dto = new ExaminerDTO();
        dto.setId(examiner.getId());
        dto.setDecision(examiner.getDecision());
        dto.setNoteEntretien(examiner.getNoteEntretien());
        dto.setNoteDossier(examiner.getNoteDossier());
        dto.setPublier(examiner.getPublier());
        dto.setValider(examiner.getValider());
        if(examiner.getCommission() != null) {
            dto.setCommission(commissionMapper.toDto(examiner.getCommission()) );
        }
        if(examiner.getCandidat() != null) {
            dto.setCandidat(candidatMapper.toDto(examiner.getCandidat())); //examiner.getCandidat());
        }
        if (examiner.getSujet() != null) {
            dto.setSujet(sujetMapper.toDto(examiner.getSujet()) );
        }
       
        
        return dto;
    }

   /*  public Examiner toEntity(ExaminerDTO dto) {
        if (dto == null) {
            return null;
        }

        Examiner examiner = new Examiner();
        examiner.setId(dto.getId());
        examiner.setDecision(dto.getDecision());
        examiner.setNoteEntretien(dto.getNoteEntretien());
        examiner.setNoteDossier(dto.getNoteDossier());
        examiner.setPublier(dto.getPublier());
        examiner.setValider(dto.getValider());
        examiner.setCommission(dto.getCommission());
        examiner.setCandidat(dto.getCandidat());
        examiner.setSujet(dto.getSujet());
        
        return examiner;
    }

    public List<ExaminerDTO> toDtoList(List<Examiner> examiners) {
        return examiners.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Examiner> toEntityList(List<ExaminerDTO> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    } */
}
