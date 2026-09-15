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
@Table(name = "sujet")
public class Sujet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titre;
    private String description;
    private boolean publier;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professeur_id")
    private Professeur professeur;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "co_directeur_id")
    private Professeur coDirecteur;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formation_doctorale_id")
    private FormationDoctorale formationDoctorale;
    
}