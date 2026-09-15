package com.edoc.models;

import lombok.Data;
    
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int maxSujetPostuler;
    private Date dateDebutPostulerSujetCandidat;
    private Date dateDebutModifierSujetProf;
    private Date dateFinPostulerSujetCandidat;
    private Date dateFinModifierSujetProf;
}
