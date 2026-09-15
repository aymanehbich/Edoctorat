package com.edoc.dto;

import lombok.Data;

@Data
public class InscriptionDTO1 {
  private Long id;
  private String dateDisposeDossier;
  private String remarque;
  private boolean valider;
  private CandidatInscriptionDTO candidat;
  private SujetInscriptionDTO sujet;
}
