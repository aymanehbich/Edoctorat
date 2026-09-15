package com.edoc.dto;

import lombok.Data;

@Data
public class CandidatProfDTO {
    private Long id;
    private String nom;
    private String cne;
    private String prenom;
    private String email;
}