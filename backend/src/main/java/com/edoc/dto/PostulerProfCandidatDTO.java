package com.edoc.dto;

import lombok.Data;

@Data
public class PostulerProfCandidatDTO {
    private Long id;
    private CandidatProfDTO candidat;
    private SujetDTO sujet;
    private String pathFile;
    private boolean validé;

}