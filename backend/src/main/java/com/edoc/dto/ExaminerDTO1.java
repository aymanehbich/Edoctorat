package com.edoc.dto;

import lombok.Data;

@Data
public class ExaminerDTO1 {
  private Long id;
  private SujetDTO sujet;
  private String cne;
  private Double noteDossier;
  private Double noteEntretien;
  private String decision;
  private Long commission;
  private CandidatExaminerDTO candidat;
  private boolean publier;
}
