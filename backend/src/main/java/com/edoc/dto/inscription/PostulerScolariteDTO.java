package com.edoc.dto.inscription;



import lombok.Data;

@Data
public class PostulerScolariteDTO {
    private Long id;
    private String pathFile;
    private SujetScolariteDTO sujet;
    private CandidatScolariteDTO candidat;
}

