package com.edoc.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edoc.models.Examiner;
import com.edoc.models.Sujet;

@Repository
public interface ExaminerRepository extends JpaRepository<Examiner, Long> {

    @Query("SELECT e FROM Examiner e " +
       "JOIN e.commission c " +
       "JOIN c.commissionProfesseurs cp " +
       "WHERE cp.professeur.id = :professeurId")
    List<Examiner> findExaminersByProfesseurId(Long professeurId);

    List<Examiner> findByCommissionId(Long id);


      @Query("SELECT e FROM Examiner e JOIN e.commission c JOIN c.laboratoire l JOIN l.directeur p WHERE p.id = :professeurId")
    List<Examiner> findByProfesseurId(@Param("professeurId") Long professeurId);

  Examiner findByCommissionIdAndSujetId(Long commissionId, Long sujetId);
  Page<Examiner> findBySujetIn(List<Sujet> sujets, Pageable pageable);
  List<Examiner> findByCandidatId(Long candidatId);
}
