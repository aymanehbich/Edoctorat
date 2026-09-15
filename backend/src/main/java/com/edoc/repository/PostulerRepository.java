package com.edoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.edoc.models.Postuler;
import com.edoc.models.Sujet;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostulerRepository extends JpaRepository<Postuler, Long> {
        List<Postuler> findByCandidatId(Long candidatId);
        Optional<Postuler> findBySujetIdAndCandidatId(Long sujetId, Long candidatId);
        List<Postuler> findBySujetIn(List<Sujet> sujets);

        
    @Query(value = "SELECT p FROM Postuler p ORDER BY p.id ASC")
    List<Postuler> findAllWithPagination(@Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(p) FROM Postuler p WHERE p.candidat.id = :candidatId")
    Long countByCandidatId(@Param("candidatId") Long candidatId);

    @Query("SELECT c.maxSujetPostuler FROM Config c ORDER BY c.id DESC LIMIT 1")
    Integer findMaxSujetPostuler();

    @Query("SELECT p FROM Postuler p " +
           "LEFT JOIN FETCH p.candidat " +
           "LEFT JOIN FETCH p.sujet")
    List<Postuler> findAllWithRelations();


    @Query("SELECT p FROM Postuler p " +
        "LEFT JOIN FETCH p.candidat " +
        "LEFT JOIN FETCH p.sujet " +
        "WHERE p.candidat.id IN :candidatIds " +
        "AND p.sujet.id IN :sujetIds")
    List<Postuler> findByCandidatIdInAndSujetIdIn(List<Long> candidatIds, List<Long> sujetIds);

    @Query("SELECT p FROM Postuler p " +
        "LEFT JOIN FETCH p.candidat " +
        "LEFT JOIN FETCH p.sujet " +
        "WHERE p.candidat.id = :candidatId " +
        "AND p.sujet.id = :sujetId")
    Optional<Postuler> findByCandidatIdAndSujetId(Long candidatId, Long sujetId);

}
