package com.edoc.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edoc.models.FormationDoctorale;

@Repository
public interface FormationDoctoraleRepository extends JpaRepository<FormationDoctorale, Long> {
 List<FormationDoctorale> findByCedId(Long cedId);
}

