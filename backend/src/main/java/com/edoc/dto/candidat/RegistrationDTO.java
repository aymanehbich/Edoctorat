package com.edoc.dto.candidat;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO  {
   private String token;
   private String adresse;
   private String cin;
   private String cne;
   private LocalDate dateDeNaissance;
   private String email;
   private String nom;
   private String nomCandidatAr;
   private String password;
   private String pays;
   private String prenom; 
   private String prenomCandidatAr;
   private String sexe;
   private String situationFamiliale;
   private String typeDehandicape;
   private String villeDeNaissance;
   private String villeDeNaissanceAr;
   private String ville;
   private String telephone;


}