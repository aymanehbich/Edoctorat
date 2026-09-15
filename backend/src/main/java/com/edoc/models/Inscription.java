package com.edoc.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "inscription")
public class Inscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String dateDisposeDossier;
    private String remarque;
    private Boolean valider;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sujet_id")
    private Sujet sujet;
    
}
