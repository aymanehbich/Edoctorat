package com.edoc.dto;

import com.edoc.dto.annexe.AnnexeDTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class candidatDiplomes {
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
    private List<AnnexeDTO> annexes; 
}
