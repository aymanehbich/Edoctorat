package com.edoc.services.examiner;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edoc.dto.ExaminerDTO;
import com.edoc.dto.mapper.ExaminerMapper;
import com.edoc.models.Examiner;
import com.edoc.models.Inscription;
import com.edoc.repository.ExaminerRepository;
import com.edoc.repository.InscriptionRepository;

@Service
public class ExaminerService {

    @Autowired
    private ExaminerRepository examinerRepository;

    @Autowired
    private ExaminerMapper examinerMapper;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    /**
     * Grades an Examiner row (created when the commission was scheduled). A
     * decision of "Liste principale" auto-creates the Inscription once the
     * result is also published (publier=true) — there is no separate
     * admission step anywhere else in the backend, so this is the only place
     * an Inscription can come into existence outside direct DB seeding.
     * Gating on publier too (not just decision) matters because the candidat
     * reads their inscription status unconditionally (no publier filter,
     * unlike the exam results list) — creating the Inscription before
     * publication would leak the admission decision early.
     */
    public ExaminerDTO updateExaminer(Long id, Map<String, Object> request) {
        Examiner examiner = examinerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Examiner not found"));

        if (request.containsKey("noteDossier")) {
            examiner.setNoteDossier(Double.valueOf(request.get("noteDossier").toString()));
        }
        if (request.containsKey("noteEntretien")) {
            examiner.setNoteEntretien(Double.valueOf(request.get("noteEntretien").toString()));
        }
        if (request.containsKey("decision")) {
            examiner.setDecision((String) request.get("decision"));
        }
        if (request.containsKey("valider")) {
            examiner.setValider((Boolean) request.get("valider"));
        }
        if (request.containsKey("publier")) {
            examiner.setPublier((Boolean) request.get("publier"));
        }

        Examiner savedExaminer = examinerRepository.save(examiner);
        createInscriptionIfAdmittedAndPublished(savedExaminer);

        return examinerMapper.toDto(savedExaminer);
    }

    /**
     * Also used by the labo director's bulk-publish action: an examiner row
     * can reach decision="Liste principale" with publier=false (a draft
     * grade) and only get publier=true later via that bulk action, which
     * saves the Examiner directly rather than going through updateExaminer —
     * so the auto-inscription check has to be reachable from both places.
     */
    public void createInscriptionIfAdmittedAndPublished(Examiner examiner) {
        if (!"Liste principale".equals(examiner.getDecision())
                || !Boolean.TRUE.equals(examiner.getPublier())
                || examiner.getCandidat() == null
                || examiner.getSujet() == null) {
            return;
        }

        boolean alreadyInscrit = inscriptionRepository
            .findByCandidatIdAndSujetId(examiner.getCandidat().getId(), examiner.getSujet().getId())
            .isPresent();

        if (!alreadyInscrit) {
            Inscription inscription = new Inscription();
            inscription.setCandidat(examiner.getCandidat());
            inscription.setSujet(examiner.getSujet());
            inscription.setDateDisposeDossier(LocalDate.now().toString());
            inscription.setRemarque("");
            inscription.setValider(false);
            inscriptionRepository.save(inscription);
        }
    }
}
