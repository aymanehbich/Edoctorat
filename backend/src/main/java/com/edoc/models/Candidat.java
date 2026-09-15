package com.edoc.models;



import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "candidat")
public class Candidat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cne;
    private String cin;
    private String nomCandidatAr;
    private String prenomCandidatAr;
    private String adresse;
    private String adresseAr;
    private String sexe;
    private String villeDeNaissance;
    private String academie;
    private String villeDeNaissanceAr;
    private String ville;
    private String dateDeNaissance;
    private String typeDeHandicape;
    private String residence;
    private String telCandidat;
    private String fonctionnaire;
    @Column(name = "path_cv")
    private String pathCv;

    @Column(name = "path_photo")
    private String pathPhoto;
    private Boolean etatDossier;
    private String situation_familiale;
    private String pays;

    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Diplome> diplomes;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    

    

}
