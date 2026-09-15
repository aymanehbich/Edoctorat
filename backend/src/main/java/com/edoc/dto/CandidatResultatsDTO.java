package com.edoc.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatResultatsDTO {
    private List<ExaminerDTO> examens;
    private List<InscriptionDTO> inscriptions;
}
