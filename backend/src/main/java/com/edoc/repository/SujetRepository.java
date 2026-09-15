package com.edoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edoc.models.Professeur;
import com.edoc.models.Sujet;

@Repository
public interface SujetRepository extends JpaRepository<Sujet, Long> {
  List<Sujet> findByProfesseurUserIdOrCoDirecteurUserId(Long professeurUserId, Long coDirecteurUserId);
  List<Sujet> findByProfesseur(Professeur professeur);

  @Query(value = "SELECT DISTINCT s.* FROM sujet s " +
           "JOIN examiner e ON s.id = e.sujet_id " +
           "WHERE e.commission_id = :commissionId", 
           nativeQuery = true)
  List<Sujet> findSujetsByCommissionId(@Param("commissionId") Long commissionId);

  @Query(value = "SELECT s.* FROM sujet s " +
           "JOIN professeur p ON p.id = s.professeur_id " +
           "WHERE p.labo_id = :labo_id",
           nativeQuery = true)
  List<Sujet> findSujetsByLaboId(@Param("labo_id") Long labo_id);

  @Query("SELECT COUNT(i) FROM Inscription i WHERE i.sujet.professeur.id = :professeurId")
    Long countInscriptionsByProfesseurId(@Param("professeurId") Long professeurId);
}
