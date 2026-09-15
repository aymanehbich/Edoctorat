package com.edoc.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edoc.dto.FormationDoctoraleDTO;
import com.edoc.dto.Result;
import com.edoc.dto.mapper.FormatioDoctoraleMapper;
import com.edoc.models.FormationDoctorale;

import com.edoc.repository.FormationDoctoraleRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FormationDoctoraleService {
    @Autowired
    private FormationDoctoraleRepository formationDoctoraleRepository;
    @Autowired
    private FormatioDoctoraleMapper formationDoctoraleMapper;

    public Result<FormationDoctoraleDTO> getFormationDoctorales() {
      List<FormationDoctoraleDTO> formations = formationDoctoraleRepository.findAll().stream().map(formationDoctoraleMapper::toDto)
            .collect(Collectors.toList());;

      int count = formations.size();
      
      return new Result<>(count, null, null, formations);

      }

}