package com.edoc.dto;

import com.edoc.models.Laboratoire;

import lombok.Data;

@Data
public class SujetDTO1{
  private Long id;
  private ProfesseurDTO professeur;
  private ProfesseurDTO coDirecteur;
  private String titre;
  private String description;
  private FormationDoctoraleDTO formationDoctorale;
  private boolean publier;
  private String Labo;
  private LaboratoireDTO laboratoire;
  
}
