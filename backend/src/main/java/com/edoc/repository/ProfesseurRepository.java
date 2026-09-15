package com.edoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edoc.models.Laboratoire;
import com.edoc.models.Professeur;

@Repository
public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {
  Professeur findByUserId(Long userId);
  List<Professeur> findByLaboratoire(Laboratoire laboratoire);
  List<Professeur> findByLaboratoireId(Long laboratoireId);

}
