package com.edoc.models;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Calendrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  private LocalDate dateDebut; // Maps to a 'date' column in the database

    private LocalDate dateFin; // Maps to a 'date' column in the database

    private String pour;

    private String action;
}
