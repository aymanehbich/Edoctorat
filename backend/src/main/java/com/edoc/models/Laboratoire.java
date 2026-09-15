package com.edoc.models;

import jakarta.persistence.Column;
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
@Table(name = "laboratoire")
public class Laboratoire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
   
    @Column(name = "nom_laboratoire")
    private String nomLaboratoire;
    
    private String description;
    
    @Column(name = "path_image")
    private String pathImage;
    
    private String initial;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "directeur_id")
    private Professeur directeur;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etablissement_id")
    private Etablissement etablissement;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ced_id")
    private Ced ced;
}
