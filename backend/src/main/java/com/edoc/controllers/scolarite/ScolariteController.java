package com.edoc.controllers.scolarite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edoc.dto.inscription.InscriptionScolariteDTO;
import com.edoc.dto.inscription.PagedResponse;
import com.edoc.dto.inscription.ValidationRequest;
import com.edoc.services.scolarite.ScolariteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScolariteController {

    @Autowired
    private  ScolariteService scolariteService;
    private static final int PAGE_SIZE = 50;

    @GetMapping("/scolarite/")
    public ResponseEntity<PagedResponse<InscriptionScolariteDTO>> getInscriptions(
            @RequestParam(required = false) Integer offset) {
        int page = offset != null ? offset / PAGE_SIZE : 0;
        return ResponseEntity.ok(scolariteService.getInscriptions(PageRequest.of(page, PAGE_SIZE)));
    }

    @PatchMapping("/scolarite/{id}/")
    public ResponseEntity<InscriptionScolariteDTO> updateValidation(
            @PathVariable Long id,
            @RequestBody ValidationRequest request) {
        return ResponseEntity.ok(scolariteService.updateValidation(id, request));
    }
}
