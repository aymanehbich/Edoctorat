package com.edoc.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.edoc.dto.Result;
import com.edoc.dto.candidat.ResultDto;
import com.edoc.dto.diplome.DiplomeDTO;
import com.edoc.dto.mapper.DiplomeMapper;
import com.edoc.models.Annexe;
import com.edoc.models.Candidat;
import com.edoc.models.Diplome;
import com.edoc.repository.AnnexeRepository;
import com.edoc.repository.CandidatRepository;
import com.edoc.repository.DiplomeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiplomeService {
    @Autowired
    private AnnexeRepository annexeRepository;
    
    @Autowired
    private DiplomeMapper diplomeMapper;
    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private DiplomeRepository diplomeRepository;
    @Autowired
    public AnnexeService annexeService;

    @PersistenceContext
    private EntityManager entityManager;
    public List<Diplome> getAllDiplomes() {
        return diplomeRepository.findAll();
    }

    public Optional<Diplome> getDiplomeById(Long id) {
        return diplomeRepository.findById(id);
    }
    

 
    public Diplome addDiplome(Diplome diplome) {
        
        return diplomeRepository.save(diplome);
    }
    public Diplome save(Diplome diplome) {
        return diplomeRepository.save(diplome);
    }

 
    public Diplome updateDiplome(Diplome diplome, Long id) {
        Optional<Diplome> existingDiplomeOpt = diplomeRepository.findById(id);
        if (existingDiplomeOpt.isPresent()) {
            Diplome existingDiplome = existingDiplomeOpt.get();
           
            existingDiplome.setIntitule(diplome.getIntitule());
            existingDiplome.setType(diplome.getType());
            existingDiplome.setDateCommission(diplome.getDateCommission());
            existingDiplome.setMention(diplome.getMention());
            existingDiplome.setPays(diplome.getPays());
            existingDiplome.setEtablissement(diplome.getEtablissement());
            existingDiplome.setSpecialite(diplome.getSpecialite());
            existingDiplome.setVille(diplome.getVille());
            existingDiplome.setProvince(diplome.getProvince());
            existingDiplome.setMoyen_generale(diplome.getMoyen_generale());

            return diplomeRepository.save(existingDiplome);
        }
        return null; 
    }

   
    /**
     * Diplome<->Candidat is a bidirectional, eager, cascade=ALL relationship
     * (Diplome.candidat is an eager @ManyToOne back to Candidat.diplomes, an
     * eager @OneToMany). Deleting a Diplome through the JPA repository
     * (delete()/deleteById()) silently no-ops — no exception, no row removed —
     * confirmed by direct testing. A native bulk delete bypasses that broken
     * cascade path, matching the existing native-query workaround already used
     * by AnnexeRepository.deleteByDiplomeIdAndType for the same reason.
     */
    @Transactional
    public boolean deleteDiplome(Long id) {
        if (!diplomeRepository.existsById(id)) {
            return false;
        }

        for (Annexe annexe : annexeRepository.findByDiplomeId(id)) {
            if (annexe.getPathFile() != null) {
                try {
                    Files.deleteIfExists(Paths.get(annexe.getPathFile().replaceFirst("^/", "")));
                } catch (IOException e) {
                    // Best-effort disk cleanup; an orphaned file must not block the DB delete.
                }
            }
        }
        entityManager.createNativeQuery("DELETE FROM annexe WHERE diplome_id = ?1")
                .setParameter(1, id)
                .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM diplome WHERE id = ?1")
                .setParameter(1, id)
                .executeUpdate();
        return true;
    }

    public static String saveFile(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String uploadDir = "data";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String filePath = uploadDir + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            return filePath;
        }
        return null;
    }

    public Result<DiplomeDTO> getAllDiplomesByCandidatId(Long userId) {
    
        Optional<Candidat> candidat = candidatRepository.findByUserId(userId);
        
        List<Diplome> diplomes = candidat.get().getDiplomes();

        List<DiplomeDTO> diplomeDTOs = diplomes.stream()
            .map(diplomeMapper::toDto)
            .collect(Collectors.toList());

        Result<DiplomeDTO> result = new Result<>();
        result.setCount(diplomeDTOs.size());
        result.setResults(diplomeDTOs);
        return result;
    }
    
    //--------------------------------update Diplime--------------------------------
    @Transactional
    public DiplomeDTO updateDiplome(Long diplomeId, MultipartHttpServletRequest request,Candidat candidat) throws ParseException, IOException{
        // Fetch existing diploma
        Diplome existingDiplome = getDiplomeById(diplomeId).orElseThrow(() -> new RuntimeException("Diploma not found"));
            System.out.println("******************************************"+diplomeId);
        

        // Update basic fields if provided
        if (request.getParameter("intitule") != null) {
            existingDiplome.setIntitule(request.getParameter("intitule"));
        }
        if (request.getParameter("type") != null) {
            existingDiplome.setType(request.getParameter("type"));
        }
        if (request.getParameter("dateCommission") != null) {
            existingDiplome.setDateCommission(
                new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("dateCommission"))
            );
        }
        if (request.getParameter("mention") != null) {
            existingDiplome.setMention(request.getParameter("mention"));
        }
        if (request.getParameter("pays") != null) {
            existingDiplome.setPays(request.getParameter("pays"));
        }
        if (request.getParameter("etablissement") != null) {
            existingDiplome.setEtablissement(request.getParameter("etablissement"));
        }
        if (request.getParameter("specialite") != null) {
            existingDiplome.setSpecialite(request.getParameter("specialite"));
        }
        if (request.getParameter("ville") != null) {
            existingDiplome.setVille(request.getParameter("ville"));
        }
        if (request.getParameter("province") != null) {
            existingDiplome.setProvince(request.getParameter("province"));
        }
        if (request.getParameter("moyen_generale") != null) {
            existingDiplome.setMoyen_generale(
                Double.parseDouble(request.getParameter("moyen_generale"))
            );
        }

        // Save updated diploma
        Diplome savedDiplome = save(existingDiplome);



        // Handle file updates
        if (request.getFile("diplomeFile") != null && !request.getFile("diplomeFile").isEmpty()) {
            // Delete existing diploma annexe if it exists
            annexeService.deleteByDiplomeAndType(savedDiplome, "Diplôme");
            // Create new one
            annexeRepository.save(annexeService.createAnnexe("Diplôme", request.getFile("diplomeFile"), savedDiplome)) ;
        }

        if (request.getFile("releveFile") != null && !request.getFile("releveFile").isEmpty()) {
            // Delete existing releve annexe if it exists
            annexeService.deleteByDiplomeAndType(savedDiplome, "Relevé");
            // Create new one
            annexeRepository.save(annexeService.createAnnexe("Relevé", request.getFile("releveFile"), savedDiplome));
        }
/* 
        // Handle multiple releve files if present
        List<MultipartFile> releveFiles = request.getFiles("relevefile");
        if (releveFiles != null && !releveFiles.isEmpty()) {
            // Delete existing releve annexes
            annexeService.deleteByDiplomeAndType(savedDiplome, "Relevé");
            // Create new ones
            List<Annexe> newAnnexes = new ArrayList<>();
            for (MultipartFile file : releveFiles) {
                if (!file.isEmpty()) {
                    newAnnexes.add(annexeService.createAnnexe("Relevé", file, savedDiplome));
                }
            }
            annexeService.saveAll(newAnnexes);

        }

        // Map to DTO and return
       */
        DiplomeDTO diplomeDTO = diplomeMapper.toDto(savedDiplome);
        return diplomeDTO; 
}
//-----------------------ajouter diplome with annex--------------------------------

public DiplomeDTO addDiplome(MultipartHttpServletRequest request,Candidat candidat) throws ParseException, IOException{
    // Create Diplome entity
    String intitule = request.getParameter("intitule");
            String type = request.getParameter("type");
            Date dateCommission = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("dateCommission"));
            String mention = request.getParameter("mention");
            String pays = request.getParameter("pays");
            String etablissement = request.getParameter("etablissement");
            String specialite = request.getParameter("specialite");
            String ville = request.getParameter("ville");
            String province = request.getParameter("province");
            Double moyenGenerale = Double.parseDouble(request.getParameter("moyen_generale"));
    Diplome diplome = new Diplome(null, intitule, type, dateCommission, mention, pays, etablissement, specialite,
    ville, province, moyenGenerale, candidat, new ArrayList<>());

Diplome savedDiplome = save(diplome);

// Process uploaded files as annexes (createAnnexe returns null when no file was provided, so
// each result must be filtered before saving, otherwise annexeRepository.save(null) throws)
List<Annexe> annexes = new ArrayList<>();
for (MultipartFile file : request.getFiles("relevefile")) {
    Annexe releveAnnexe = annexeService.createAnnexe("Relevé", file, savedDiplome);
    if (releveAnnexe != null) {
        annexes.add(releveAnnexe);
    }
}
Annexe diplomeAnnexe = annexeService.createAnnexe("Diplôme", request.getFile("diplomeFile"), savedDiplome);
if (diplomeAnnexe != null) {
    annexes.add(diplomeAnnexe);
}
Annexe releveFileAnnexe = annexeService.createAnnexe("Relevé", request.getFile("releveFile"), savedDiplome);
if (releveFileAnnexe != null) {
    annexes.add(releveFileAnnexe);
}

annexeService.saveAll(annexes); // Save all annexes

return diplomeMapper.toDto(savedDiplome);
}
        
}









