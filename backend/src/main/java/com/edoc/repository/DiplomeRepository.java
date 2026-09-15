package com.edoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edoc.models.Diplome;

@Repository
public interface DiplomeRepository extends JpaRepository<Diplome, Long> {
  
}