package com.edoc.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.dto.FormationDoctoraleDTO;
import com.edoc.dto.Result;
import com.edoc.models.FormationDoctorale;
import com.edoc.services.FormationDoctoraleService;

@RestController
@RequestMapping("/api")
public class FormationDoctoraleController {
    @Autowired
    private FormationDoctoraleService formationDoctoraleService;
    
    @GetMapping("/formation-doctorale/")
    public ResponseEntity<Result<FormationDoctoraleDTO>> getFormationDoctorales() {
      Result<FormationDoctoraleDTO> result = formationDoctoraleService.getFormationDoctorales();
      return ResponseEntity.ok(result);
    }
}