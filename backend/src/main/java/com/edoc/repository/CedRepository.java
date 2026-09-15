package com.edoc.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edoc.models.Ced;


@Repository
public interface CedRepository extends JpaRepository<Ced, Long> {
  
}
