package com.edoc.dto;

import com.edoc.models.Ced;

import lombok.Data;

@Data
public class FormationDoctoraleDTO {
  private Long id;
  private String pathImage;
  private String initiale;
  private String titre;
  private String axeDeRecherche;
  private String dateAccreditation;
  private CedDTO ced;
  private EtablissementDTO etablissement;

}


