package com.edoc.dto.inscription;

import lombok.Data;

@Data
public class CandidatScolariteDTO {
  private Long id;
  private String cne;
  private String nom;
  private String prenom;
  private String photo;
}