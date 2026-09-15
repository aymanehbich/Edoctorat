package com.edoc.dto;



import lombok.Data;

@Data
public class SujetDTO{
  private Long id;
  private ProfesseurDTO professeur;
  private ProfesseurDTO coDirecteur;
  private String titre;
  private String description;
  private FormationDto formationDoctorale;
  private boolean publier;
  private boolean valider;
  
}
