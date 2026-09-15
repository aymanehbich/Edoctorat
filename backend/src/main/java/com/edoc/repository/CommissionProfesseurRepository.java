package com.edoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edoc.models.CommissionProfesseur;

@Repository
public interface CommissionProfesseurRepository extends JpaRepository<CommissionProfesseur, Long> {
    List<CommissionProfesseur> findByCommissionId(Long commissionId);
    List<CommissionProfesseur> findByProfesseurId(Long professeurId);
}
