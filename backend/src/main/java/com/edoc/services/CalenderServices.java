package com.edoc.services;

import com.edoc.exception.ResourceNotFoundException;
import com.edoc.models.Calendrier;
import com.edoc.repository.CalendrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class CalenderServices {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private CalendrierRepository calendrierRepository;

    // Récupérer tous les calendriers
    public List<Calendrier> getCalendars() {
        return calendrierRepository.findAll();
    }

     
    public Calendrier updateCalendrier(Long id, String dateDebut, String dateFin) throws ResourceNotFoundException {
        Calendrier calendrier = calendrierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Calendrier not found with id: " + id));
            
        // Parse dates from string to appropriate format
        try {
                calendrier.setDateDebut(dateFormat.parse(dateDebut).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            if (dateDebut != null) {
                calendrier.setDateDebut(dateFormat.parse(dateDebut).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            if (dateFin != null) {
                calendrier.setDateFin(dateFormat.parse(dateFin).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd");
        }
        
        return calendrierRepository.save(calendrier);
    }
}
