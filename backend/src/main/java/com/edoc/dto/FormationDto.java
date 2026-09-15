
package com.edoc.dto;

import lombok.Data;

@Data
public class FormationDto {
    private Long id;
    private Long ced;
    private String etablissement;
    private String axeDeRecherche;
    private String pathImage;
    private String titre;
    private String initiale;
    private String dateAccreditation;
}
