package com.edoc.dto;

import lombok.Data;

@Data
public class LaboratoireDTO {
    private Long id;
    private String nomLaboratoire;
    private String description;
    private String pathImage;
    private String initial;
}
