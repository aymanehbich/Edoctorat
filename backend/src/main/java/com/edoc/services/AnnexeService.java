package com.edoc.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.edoc.models.Annexe;
import com.edoc.models.Diplome;
import com.edoc.repository.AnnexeRepository;

import jakarta.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnnexeService {

    @Autowired
    private AnnexeRepository annexeRepository;

    
    public Annexe addAnnexe(Annexe annexe) {
        return annexeRepository.save(annexe);
    }

    
    public List<Annexe> getAllAnnexes() {
        return annexeRepository.findAll();
    }

   
    public Optional<Annexe> getAnnexeById(Long id) {
        return annexeRepository.findById(id);
    }

    public void deleteAnnexe(Long id) {
        annexeRepository.deleteById(id);
    }

   
    public Annexe updateAnnexe(Long id, Annexe annexeDetails) {
        if(annexeRepository.existsById(id)) {
            annexeDetails.setId(id);
            return annexeRepository.save(annexeDetails);
        } else {
            return null;
        }
    }






    
    public Annexe createAnnexe(String typeAnnexe, MultipartFile file, Diplome diplome) throws IOException {
    if (file != null && !file.isEmpty()) {
        String uploadDir = "uploads/candidates/annexes";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = uploadDir + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        //of
        Path path = Paths.get(filePath); // Convert filePath to Path

        // Copy file using Files.copy
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return new Annexe(null, typeAnnexe, file.getOriginalFilename(), "/"+filePath, diplome);
    }
    return null;
}
    public void saveAll(List<Annexe> annexes){
        for (Annexe annexe : annexes){
            annexeRepository.save(annexe);
        }
    }

    @Transactional
    public void deleteByDiplomeAndType(Diplome diplome, String type) {
        System.out.println(type + diplome.getId());
        List<Annexe> annexesToDelete = annexeRepository.findByDiplomeId(diplome.getId());
        for (Annexe annexe : annexesToDelete) {
            // Delete physical file
            try {
                Files.deleteIfExists(Paths.get(annexe.getPathFile()));
            } catch (IOException e) {
                System.out.println("Error deleting file: " + annexe.getPathFile() + e);
            }
        }
        // Delete database records
        /* annexeRepository.findByDiplomeAndTypeAnnexe(diplome, type); */
        for(Annexe annexe : annexesToDelete) {
            
            if(annexe.getTypeAnnexe().equals(type)) {
                System.out.println(annexe.getId()+"*******************l9a ma ym7i");
               annexeRepository.deleteByDiplomeIdAndType(annexe.getDiplome().getId(), annexe.getTypeAnnexe()); 
               annexeRepository.flush();
            }
           
        }

      
    }



}


