package com.edoc.dto.mapper;

import com.edoc.dto.candidatDiplomes;
import com.edoc.dto.annexe.AnnexeDTO;
import com.edoc.dto.candidat.CandidatDTO;
import com.edoc.models.Annexe;
import com.edoc.models.Candidat;
import com.edoc.models.Diplome;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class CandidatMapper {
    
    public CandidatDTO toDto(Candidat candidat) {
        CandidatDTO dto = new CandidatDTO();
        dto.setId(candidat.getId());
        dto.setCne(candidat.getCne());
        dto.setPays(candidat.getPays());
        dto.setNom(candidat.getUser().getLastName());
        dto.setPrenom(candidat.getUser().getFirstName());
        dto.setEmail(candidat.getUser().getEmail());
        dto.setCin(candidat.getCin());
        dto.setNomCandidatAr(candidat.getNomCandidatAr());
        dto.setPrenomCandidatAr(candidat.getPrenomCandidatAr());
        dto.setAdresse(candidat.getAdresse());
        dto.setAdresseAr(candidat.getAdresseAr());
        dto.setSexe(candidat.getSexe());
        dto.setVilleDeNaissance(candidat.getVilleDeNaissance());
        dto.setVilleDeNaissanceAr(candidat.getVilleDeNaissanceAr());
        dto.setVille(candidat.getVille());
        dto.setDateDeNaissance(candidat.getDateDeNaissance());
        dto.setTypeDeHandiCape(candidat.getTypeDeHandicape());
        dto.setAcademie(candidat.getAcademie());
        dto.setTelCandidat(candidat.getTelCandidat());
        dto.setPathCv(candidat.getPathCv());
        dto.setPathPhoto(candidat.getPathPhoto());
        dto.setEtatDossier(candidat.getEtatDossier()); 
        dto.setSituation_familiale(candidat.getSituation_familiale());
        dto.setFonctionnaire(candidat.getFonctionnaire() != null ? 
            candidat.getFonctionnaire().toString() : null);
            
        if (candidat.getDiplomes() != null) {
            dto.setDiplomes(candidat.getDiplomes().stream()
                .map(this::diplomeToDto)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public Candidat toEntity(CandidatDTO dto) {
        Candidat candidat = new Candidat();
        candidat.setId(dto.getId());
        candidat.setCne(dto.getCne());
        candidat.setCin(dto.getCin());
        candidat.setPays(dto.getPays());
        candidat.setNomCandidatAr(dto.getNomCandidatAr());
        candidat.setPrenomCandidatAr(dto.getPrenomCandidatAr());
        candidat.setAdresse(dto.getAdresse());
        candidat.setAdresseAr(dto.getAdresseAr());
        candidat.setSexe(dto.getSexe());
        candidat.setVilleDeNaissance(dto.getVilleDeNaissance());
        candidat.setVilleDeNaissanceAr(dto.getVilleDeNaissanceAr());
        candidat.setVille(dto.getVille());
        candidat.setDateDeNaissance(dto.getDateDeNaissance());
        candidat.setTypeDeHandicape(dto.getTypeDeHandiCape());
        candidat.setAcademie(dto.getAcademie());
        candidat.setTelCandidat(dto.getTelCandidat());
        candidat.setPathCv(dto.getPathCv());
        candidat.setPathPhoto(dto.getPathPhoto());
        candidat.setEtatDossier(dto.getEtatDossier());
        candidat.setSituation_familiale(dto.getSituation_familiale());
        if (dto.getFonctionnaire() != null) {
            candidat.setFonctionnaire(dto.getFonctionnaire());
        }
        
        if (dto.getDiplomes() != null) {
            candidat.setDiplomes(dto.getDiplomes().stream()
                .map(diplomeDto -> {
                    Diplome diplome = diplomeToEntity(diplomeDto);
                    diplome.setCandidat(candidat);
                    return diplome;
                })
                .collect(Collectors.toList()));
        }
        
        return candidat;
    }
    
    private candidatDiplomes diplomeToDto(Diplome diplome) {
        candidatDiplomes dto = new candidatDiplomes();
        dto.setId(diplome.getId());
        dto.setIntitule(diplome.getIntitule());
        dto.setType(diplome.getType());
        dto.setDateCommission(diplome.getDateCommission());
        dto.setMention(diplome.getMention());
        dto.setPays(diplome.getPays());
        dto.setEtablissement(diplome.getEtablissement());
        dto.setSpecialite(diplome.getSpecialite());
        dto.setVille(diplome.getVille());
        dto.setProvince(diplome.getProvince());
        dto.setMoyen_generale(diplome.getMoyen_generale());

        if (diplome.getAnnexes() != null) {
          dto.setAnnexes(diplome.getAnnexes().stream()
              .map(this::annexeToDto)
              .collect(Collectors.toList()));
        }
        return dto;
    }
    

    private AnnexeDTO annexeToDto(Annexe annexe) {
      AnnexeDTO dto = new AnnexeDTO();
      dto.setId(annexe.getId());
      dto.setTypeAnnexe(annexe.getTypeAnnexe());
      dto.setTitre(annexe.getTitre());
      dto.setPathFile(annexe.getPathFile());
      return dto;
    }

    private Diplome diplomeToEntity(candidatDiplomes dto) {
        Diplome diplome = new Diplome();
        diplome.setId(dto.getId());
        diplome.setIntitule(dto.getIntitule());
        diplome.setType(dto.getType());
        diplome.setDateCommission(dto.getDateCommission());
        diplome.setMention(dto.getMention());
        diplome.setPays(dto.getPays());
        diplome.setEtablissement(dto.getEtablissement());
        diplome.setSpecialite(dto.getSpecialite());
        diplome.setVille(dto.getVille());
        diplome.setProvince(dto.getProvince());
        diplome.setMoyen_generale(dto.getMoyen_generale());

        if (dto.getAnnexes() != null) {
          diplome.setAnnexes(dto.getAnnexes().stream()
              .map(annexeDto -> {
                  Annexe annexe = annexeToEntity(annexeDto);
                  annexe.setDiplome(diplome);
                  return annexe;
              })
              .collect(Collectors.toList()));
      }
        return diplome;
    }


    private Annexe annexeToEntity(AnnexeDTO dto) {
      Annexe annexe = new Annexe();
      annexe.setId(dto.getId());
      annexe.setTypeAnnexe(dto.getTypeAnnexe());
      annexe.setTitre(dto.getTitre());
      annexe.setPathFile(dto.getPathFile());
      return annexe;
  }
}