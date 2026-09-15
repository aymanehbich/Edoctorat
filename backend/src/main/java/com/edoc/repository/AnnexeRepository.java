package com.edoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edoc.models.Annexe;
import com.edoc.models.Diplome;



@Repository
public interface AnnexeRepository extends JpaRepository<Annexe, Long> {

    void deleteByDiplomeAndTypeAnnexe(Diplome diplome, String typeAnnexe);

    @Query("delete from Annexe a where a.diplome.id = :diplomeId and a.typeAnnexe = :typeAnnexe")
    void deleteByDiplomeIdAndTypeId0(@Param("diplomeId") Long diplomeId,@Param("typeAnnexe") String typeAnnexe);
    

    List<Annexe> findByDiplomeAndTypeAnnexe(Diplome diplome, String typeAnnexe);

    List<Annexe> findByDiplomeId(Long id);

    @Modifying
    @Query(value = "DELETE FROM annexe WHERE diplome_id = :diplomeId AND type_annexe = :type", nativeQuery = true)
    void deleteByDiplomeIdAndType(@Param("diplomeId") Long diplomeId, @Param("type") String type);
   
}