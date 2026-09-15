package com.edoc.dto;

import com.edoc.dto.candidat.CandidatDTO;


import lombok.Data;

@Data
public class ExaminerDTO {
    private Long id;
    private String decision;
    private Double noteEntretien;
    private Double noteDossier;
    private Boolean publier;
    private Boolean valider;
    
    private CommissionDTO commission;
    private CandidatDTO candidat;
    private SujetDTO1 sujet;
}


