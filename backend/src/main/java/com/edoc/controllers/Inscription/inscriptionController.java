package com.edoc.controllers.Inscription;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.services.inscription.InscriptionService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api")
public class inscriptionController {
    @Autowired
    private InscriptionService inscriptionService;

    @GetMapping("/get-all-inscriptions/")
    public ResponseEntity<?> getMethodName(HttpServletRequest request) {
        return ResponseEntity.ok(inscriptionService.getAllInscriptions());
    }
    
}
