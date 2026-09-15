package com.edoc.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.edoc.models.Inscription;
import com.edoc.models.Sujet;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    Page<Inscription> findBySujetIn(List<Sujet> sujets, Pageable pageable);

     @Query("SELECT i FROM Inscription i " +
           "LEFT JOIN FETCH i.candidat " +
           "LEFT JOIN FETCH i.sujet")
    Page<Inscription> findAllWithRelations(Pageable pageable);

    List<Inscription> findByCandidatId(Long candidatId);
    Optional<Inscription> findByCandidatIdAndSujetId(Long candidatId, Long sujetId);
}