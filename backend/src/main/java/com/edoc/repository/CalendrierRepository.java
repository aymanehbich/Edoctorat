package com.edoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edoc.models.Calendrier;

@Repository
public interface CalendrierRepository extends JpaRepository<Calendrier, Long> {

    // Calendrier findByType(String type); 
}
