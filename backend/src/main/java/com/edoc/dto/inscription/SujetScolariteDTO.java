package com.edoc.dto.inscription;

import lombok.Data;

@Data
public class SujetScolariteDTO {
  private Long id;
  private String titre;
  private String formationDoctorale;
  private String ced;
  private String laboratoire;
}
