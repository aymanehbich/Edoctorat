package com.edoc.dto;

import java.util.List;

import lombok.Data;

@Data
public class CommissionDTO {
    private Long id;
    private String dateCommission;
    private String heure;
    private boolean valider;
    private String lieu;
    private Long labo;
    private List<ProfesseurDTO> participants;
    private List<SujetDTO> sujets; 
}
