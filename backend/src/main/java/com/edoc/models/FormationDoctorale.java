package com.edoc.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "formation_doctorale")
public class FormationDoctorale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
   
    private String pathImage;
    private String initiale;
    private String titre;
    private String axeDeRecherche;
    private String dateAccreditation;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ced_id")
    private Ced ced;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etablissement_id")
    private Etablissement etablissement;
    
    @OneToMany(mappedBy = "formationDoctorale")
    private List<Sujet> sujets;

}
