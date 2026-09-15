package com.edoc.dto.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.edoc.dto.CommissionDTO;
import com.edoc.models.Commission;


import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommissionMapper {
    
/*     @Autowired
    private LaboratoireRepository laboratoireRepository;
    
    @Autowired
    private CommissionProfesseurMapper commissionProfesseurMapper; */

    public CommissionDTO toDto(Commission commission) {
        if (commission == null) {
            return null;
        }

        CommissionDTO dto = new CommissionDTO();
        dto.setId(commission.getId());
        dto.setDateCommission(commission.getDateCommission());
        dto.setHeure(commission.getHeure());
        dto.setLieu(commission.getLieu());
        
        if (commission.getLaboratoire() != null) {
            dto.setLabo(commission.getLaboratoire().getId());
        }
        
/*         if (commission.getCommissionProfesseurs() != null) {
            dto.setCommissionProfesseurs(commission.getCommissionProfesseurs().stream()
                .map(commissionProfesseurMapper::toDto)
                .collect(Collectors.toSet()));
        } */
        
        return dto;
    }

   /* public Commission toEntity(CommissionDTO dto) {
        if (dto == null) {
            return null;
        }

        Commission commission = new Commission();
        commission.setId(dto.getId());
        commission.setDateCommission(dto.getDateCommission());
        commission.setHeure(dto.getHeure());
        commission.setLieu(dto.getLieu());
        
        if (dto.getLaboratoireId() != null) {
            commission.setLaboratoire(laboratoireRepository.findById(dto.getLaboratoireId()).orElse(null));
        }
        
         if (dto.getCommissionProfesseurs() != null) {
            commission.setCommissionProfesseurs(dto.getCommissionProfesseurs().stream()
                .map(commissionProfesseurMapper::toEntity)
                .collect(Collectors.toSet()));
        } 
        
        return commission;
    }*/

    public List<CommissionDTO> toDtoList(List<Commission> commissions) {
        return commissions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* public List<Commission> toEntityList(List<CommissionDTO> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    } */
}
