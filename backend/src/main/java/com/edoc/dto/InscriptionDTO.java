package com.edoc.dto;

import com.edoc.dto.candidat.CandidatDTO;

import lombok.Data;

@Data
public class InscriptionDTO {
    private Long id;
    private String dateDiposeDossier;
    private String remarque;
    private boolean valider;
    private String pathFile;
    private CandidatDTO candidat;
    private SujetDTO1 sujet; 


}