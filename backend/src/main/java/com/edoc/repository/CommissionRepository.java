package com.edoc.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edoc.models.Commission;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    @Query("SELECT c FROM Commission c JOIN c.laboratoire l JOIN l.directeur p WHERE p.id = :professeurId")
    List<Commission> findByProfesseurIdCed(@Param("professeurId") Long professeurId);

    List<Commission> findByLaboratoireId(Long laboratoireId);

  
}
