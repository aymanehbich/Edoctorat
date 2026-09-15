package com.edoc.dto.diplome;

import java.util.Date;
import java.util.List;

import com.edoc.dto.annexe.AnnexeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiplomeDTO {
    private Long id;
    private String intitule;
    private String type;
    private Date dateCommission;
    private String mention;
    private String pays;
    private String etablissement;
    private String specialite;
    private String ville;
    private String province;
    private Double moyen_generale;
    private Long candidatId;
    private List<AnnexeDTO> annexes;
}
