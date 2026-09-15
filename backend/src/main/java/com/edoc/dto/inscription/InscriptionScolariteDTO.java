package com.edoc.dto.inscription;



import lombok.Data;

@Data
public class InscriptionScolariteDTO {
   private Long id;
    private String dateDisposeDossier;
    private String remarque;
    private Boolean valider;
    private CandidatScolariteDTO candidat;
    private SujetScolariteDTO sujet;
}
