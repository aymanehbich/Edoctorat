package com.edoc.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "etablissement")
@NoArgsConstructor
@AllArgsConstructor
public class Etablissement {
    @Id
    @Column(name = "idEtablissement")
    private String id;

    @Column(name = "nom_etablissement")
    private String nomEtablissement;
    
}
