package com.edoc.dto.candidat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreRegistrationDTO  {
    private String email;
    private String nom;
    private String prenom;
    private String origin;  
}