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
import lombok.EqualsAndHashCode;

@Data
@Entity

@Table(name = "ced")
public class Ced {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String description;
    
    @Column(name = "path_image")
    private String pathImage;
    
    private String titre;
    
    @Column(name = "initiale")
    private String initiale;
    
    @ManyToOne
    @JoinColumn(name = "directeur_id")
    private Professeur directeur;
    
}