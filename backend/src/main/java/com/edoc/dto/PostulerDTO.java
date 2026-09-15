package com.edoc.dto;


import com.edoc.dto.candidat.CandidatDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostulerDTO {
    private Long id;
    private String pathFile;
    private SujetDTO1 sujet;
    private String sujetTitre;
    private CandidatDTO candidat;
}
