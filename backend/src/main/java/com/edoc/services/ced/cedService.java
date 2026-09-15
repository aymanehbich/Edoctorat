package com.edoc.services.ced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edoc.dto.ExaminerDTO;
import com.edoc.dto.SujetDTO;
import com.edoc.dto.SujetDTO1;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.dto.mapper.ExaminerMapper;
import com.edoc.dto.mapper.SujetMapper;
import com.edoc.models.Candidat;
import com.edoc.models.Examiner;
import com.edoc.models.FormationDoctorale;
import com.edoc.models.Professeur;
import com.edoc.models.Sujet;
import com.edoc.models.User;
import com.edoc.repository.CandidatRepository;
import com.edoc.repository.CedRepository;
import com.edoc.repository.ExaminerRepository;
import com.edoc.repository.FormationDoctoraleRepository;
import com.edoc.repository.ProfesseurRepository;
import com.edoc.services.sujet.SujetService;

@Service

public class cedService {
    @Autowired
    private CedRepository cedRepository;
    @Autowired
    private SujetService sujetService;
    @Autowired
    private ExaminerRepository examinerRepository;
    @Autowired
    private ProfesseurRepository professeurRepository;
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private SujetMapper sujetMapper;
    @Autowired
    private FormationDoctoraleRepository formationDoctoraleRepository;
    @Autowired
    private ExaminerMapper examinerMapper;


    public List<SujetDTO1> getSujetByCedFromproffeseur(User user, Integer offset, Integer limit) {
        Professeur professeur = professeurRepository.findByUserId(user.getId());
        List<FormationDoctorale> formationDoctorales =formationDoctoraleRepository.findByCedId(professeur.getCed().getId());

        List<Sujet> cedSujets = new ArrayList<>();
        for (FormationDoctorale formationDoctorale : formationDoctorales) {
            List<Sujet> sujets = formationDoctorale.getSujets();
            cedSujets.addAll(sujets);
        }
        List<SujetDTO1> sujetDTOs = new ArrayList<>();
        sujetDTOs = cedSujets.stream().map(sujetMapper::toDto).collect(Collectors.toList());

        

        // If offset or limit is null, return all subjects
        if (offset == null || limit == null) {
            return sujetDTOs;
        }

        // Apply pagination
        int startIndex = Math.min(offset, cedSujets.size());
        int endIndex = Math.min(startIndex + limit, cedSujets.size());
        return sujetDTOs.subList(startIndex, endIndex);
    }

     public ResultDto<Candidat> getCandidatByCed(User user, Integer offset, Integer limit) {
        List<Candidat> candidats = candidatRepository.findCandidatsByCedProfesseurAndValidation(user.getId(), true);
        if (offset == null || limit == null) {
            return new ResultDto<Candidat>(candidats.size(), null, null, candidats); 
        }

        // Apply pagination
        int startIndex = Math.min(offset, candidats.size());
        int endIndex = Math.min(startIndex + limit, candidats.size());
        return new ResultDto<Candidat>(candidats.size(), null, null, candidats.subList(startIndex, endIndex)); 

    }

    public ResultDto<ExaminerDTO> getExaminerCandidatByCed(User user, Integer offset, Integer limit) {
        Professeur professeur = professeurRepository.findByUserId(user.getId());
        List<Examiner> examiners = examinerRepository.findExaminersByProfesseurId(professeur.getId());
        if (offset == null || limit == null) {
            return new ResultDto<ExaminerDTO>(examiners.size(), null, null, examiners.stream().map(examinerMapper::toDto).collect(Collectors.toList()));
        }

        // Apply pagination
        int startIndex = Math.min(offset, examiners.size());
        int endIndex = Math.min(startIndex + limit, examiners.size());
        return new ResultDto<ExaminerDTO>(examiners.size(), null, null, examiners.stream().map(examinerMapper::toDto).collect(Collectors.toList()).subList(startIndex, endIndex)); 
    }

    public ResultDto<ExaminerDTO> getResultatAsExaminerCedFromDirecteur(User user, Integer offset, Integer limit) {
        Professeur professeur = professeurRepository.findByUserId(user.getId());
        List<Examiner> examiners = examinerRepository.findByProfesseurId(professeur.getId());
        if (offset == null || limit == null) {
            return new ResultDto<ExaminerDTO>(examiners.size(), null, null, examiners.stream().map(examinerMapper::toDto).collect(Collectors.toList()));
        }

        // Apply pagination
        int startIndex = Math.min(offset, examiners.size());
        int endIndex = Math.min(startIndex + limit, examiners.size());
        return new ResultDto<ExaminerDTO>(examiners.size(), null, null, examiners.stream().map(examinerMapper::toDto).collect(Collectors.toList()).subList(startIndex, endIndex)); 
    }

}
