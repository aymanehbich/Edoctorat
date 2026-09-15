package com.edoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edoc.models.Etablissement;

@Repository
public interface EtablissementRepository extends JpaRepository<Etablissement, String> {
  
}
