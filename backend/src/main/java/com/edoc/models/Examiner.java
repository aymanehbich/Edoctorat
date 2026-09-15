package com.edoc.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "examiner")
public class Examiner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String decision;
    private Double noteEntretien;
    private Double noteDossier;
    private Boolean publier;
    private Boolean valider;

    @ManyToOne
    @JoinColumn(name = "commission_id")
    private Commission commission;
    
    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;
    
    @ManyToOne
    @JoinColumn(name = "sujet_id")
    private Sujet sujet;

}
