package com.edoc.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edoc.dto.ExaminerDTO;
import com.edoc.dto.ProfesseurDTO;
import com.edoc.dto.candidat.CandidatDTO;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.dto.mapper.ExaminerMapper;
import com.edoc.dto.mapper.ProfesseurMapper;
import com.edoc.models.Candidat;
import com.edoc.models.Examiner;
import com.edoc.models.Laboratoire;
import com.edoc.models.Professeur;
import com.edoc.repository.CandidatRepository;
import com.edoc.repository.CommissionRepository;
import com.edoc.repository.ExaminerRepository;
import com.edoc.repository.LaboratoireRepository;
import com.edoc.repository.ProfesseurRepository;
import com.edoc.services.examiner.ExaminerService;

@Service
public class labratoireService {
    @Autowired
    private ExaminerMapper examinerMapper;
    @Autowired
    private LaboratoireRepository laboratoireRepository;
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private ProfesseurRepository professeurRepository;
    @Autowired
    private ProfesseurMapper professeurMapper;
    @Autowired
    private ExaminerRepository examinerRepository;
    @Autowired
    private ExaminerService examinerService;

    public ResultDto<ExaminerDTO> getLaboCandidat(Long userId) {
        Laboratoire labo = professeurRepository.findByUserId(userId).getLaboratoire();
        List<Examiner> Examiner = commissionRepository.findByLaboratoireId(labo.getId())
            .stream()
            .flatMap(commission -> commission.getExaminers().stream())
            .collect(Collectors.toList());
        List<ExaminerDTO> Examiners= Examiner.stream().map(examinerMapper::toDto).collect(Collectors.toList());
        ResultDto<ExaminerDTO> result = new ResultDto<>();
        result.setResults(Examiners);
        return result;
    }


    /** Bulk-publishes every examiner row of the given decision (e.g. "Liste principale" or "Liste d'attente") held at the director's laboratoire, so results become visible to candidats. */
    public ResultDto<ExaminerDTO> publierResultatsLabo(Long userId, String decision) {
        Laboratoire labo = professeurRepository.findByUserId(userId).getLaboratoire();
        List<Examiner> examiners = commissionRepository.findByLaboratoireId(labo.getId())
            .stream()
            .flatMap(commission -> commission.getExaminers().stream())
            .filter(examiner -> decision.equals(examiner.getDecision()))
            .collect(Collectors.toList());
        examiners.forEach(examiner -> examiner.setPublier(true));
        examinerRepository.saveAll(examiners);
        examiners.forEach(examinerService::createInscriptionIfAdmittedAndPublished);
        List<ExaminerDTO> examinerDTOs = examiners.stream().map(examinerMapper::toDto).collect(Collectors.toList());
        ResultDto<ExaminerDTO> result = new ResultDto<>();
        result.setResults(examinerDTOs);
        return result;
    }

    public ResultDto<ProfesseurDTO> getLaboProfesseur(Long userId) {
        Laboratoire labo = professeurRepository.findByUserId(userId).getLaboratoire();
        List<Professeur> professeurs = professeurRepository.findByLaboratoireId(labo.getId());
        List<ProfesseurDTO> professeurDTOs = professeurs.stream().map(professeurMapper::toDto).collect(Collectors.toList());
        ResultDto<ProfesseurDTO> result = new ResultDto<>();
        result.setResults(professeurDTOs);
        return result;
    }
    
}
