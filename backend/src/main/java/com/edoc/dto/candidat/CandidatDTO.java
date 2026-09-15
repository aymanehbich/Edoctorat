package com.edoc.dto.candidat;

import java.util.List;

import com.edoc.dto.candidatDiplomes;

import lombok.Data;

@Data
public class CandidatDTO {
    private Long id;
    private String cne;
    private String pays;
    private String nom;
    private String prenom;
    private String email;
    private String cin;
    private String nomCandidatAr;
    private String prenomCandidatAr;
    private String adresse;
    private String adresseAr;
    private String sexe;
    private String villeDeNaissance;
    private String villeDeNaissanceAr;
    private String ville;
    private String dateDeNaissance;
    private String typeDeHandiCape;
    private String academie;
    private String telCandidat;
    private String pathCv;
    private String pathPhoto;
    private Boolean etatDossier;
    private String situation_familiale;
    private String fonctionnaire;
    private List<candidatDiplomes> diplomes;
}