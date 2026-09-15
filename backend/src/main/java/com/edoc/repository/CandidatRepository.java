package com.edoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edoc.models.Candidat;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {
    Optional<Candidat> findByUserId(Long userId);

         @Query("SELECT DISTINCT i.candidat FROM Inscription i " +
           "JOIN i.sujet s " +
           "JOIN s.formationDoctorale fd " +
           "JOIN fd.ced c " +
           "JOIN c.directeur p " +
           "WHERE p.user.id = :userId " +
           "AND i.valider = :valider")
    List<Candidat> findCandidatsByCedProfesseurAndValidation(
        @Param("userId") Long userId,
        @Param("valider") Boolean valider
    );
}
