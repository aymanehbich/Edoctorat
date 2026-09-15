package com.edoc.controllers;


import com.edoc.services.CalenderServices;
import com.edoc.exception.ResourceNotFoundException;
import com.edoc.models.Calendrier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CalendrierContr {
    @Autowired
    private CalenderServices calendarService;

    // Endpoint pour récupérer les calendriers
    @GetMapping("/get-calendrier/")
    public List<Calendrier> getCalendars() {
        return calendarService.getCalendars();
    }
    @PatchMapping("/update-canlendrier/{id}/")
    public ResponseEntity<Calendrier> updateCalendrier(
            @PathVariable("id") Long id,
            @RequestBody HashMap<String, String> request) {
        try {
            Calendrier updatedCalendrier = calendarService.updateCalendrier(
                id, 
                request.get("dateDebut"), 
                request.get("dateFin")
            );
            return ResponseEntity.ok(updatedCalendrier);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
