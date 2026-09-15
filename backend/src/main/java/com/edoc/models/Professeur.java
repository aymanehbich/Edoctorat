package com.edoc.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "professeur")
public class Professeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cin;

    @Column(name = "tel_professeur")
    private String telProfesseur;
    
    @Column(name = "path_photo")
    private String pathPhoto;
    
    private String grade;
    
    @Column(name = "num_som")
    private String numSom;
    
    @Column(name = "nombre_proposer")
    private int nombreProposer = 0;
    
    @Column(name = "nombre_encadrer")
    private int nombreEncadrer = 0;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etablissement_id")
    private Etablissement etablissement;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "labo_id")
    private Laboratoire laboratoire;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    
    @OneToOne(mappedBy = "directeur", cascade = CascadeType.ALL)
    private Ced ced;

    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL)
    private Set<CommissionProfesseur> commissionProfesseurs = new HashSet<>();
    
}
