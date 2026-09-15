package com.edoc.dto.annexe;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnexeDTO {
    private Long id;
    private String typeAnnexe;
    private String titre;
    private String pathFile;
}


