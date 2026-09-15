package com.edoc.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/* @Data
@Entity
@Table(name = "commission")
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String dateCommission;
    private String heure;
    private String lieu;
    
    @ManyToOne
    @JoinColumn(name = "labo_id")
    private Laboratoire laboratoire;

    @ManyToMany
    @JoinTable(
        name = "commission_professeur",
        joinColumns = @JoinColumn(name = "commission_id"),
        inverseJoinColumns = @JoinColumn(name = "professeur_id")
    )
    private Set<Professeur> professeurs = new HashSet<>();
    
}
 */


 @Entity
@Table(name = "commission")
@Data
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String dateCommission;
    private String heure;
    private String lieu;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labo_id")
    private Laboratoire laboratoire;

    /* @OneToMany(mappedBy = "commission", cascade = CascadeType.ALL)
    private List<Sujet> sujets; */
    @OneToMany(mappedBy = "commission", cascade = CascadeType.ALL)
    private Set<CommissionProfesseur> commissionProfesseurs = new HashSet<>();

    
//new
    @OneToMany(mappedBy = "commission", cascade = CascadeType.ALL)
    private List<Examiner> examiners;
}