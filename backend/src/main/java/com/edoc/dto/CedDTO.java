package com.edoc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CedDTO {
    private Long id;
    private String description;
    private String pathImage;
    private String titre;
    private String initiale;
    private Long directeurId;
    private String directeurNom;  // Including director's name for display purposes
    private String directeurPrenom;
}