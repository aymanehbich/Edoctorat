package com.edoc.services.inscription;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edoc.dto.InscriptionDTO;
import com.edoc.dto.Result;
import com.edoc.dto.mapper.InscriptionMapper;
import com.edoc.repository.InscriptionRepository;

@Service
public class InscriptionService {
    @Autowired
    private InscriptionRepository inscriptionRepository;
    @Autowired
    private InscriptionMapper inscriptionMapper;
    public Result<InscriptionDTO> getAllInscriptions() {
        List<InscriptionDTO> inscriptions = inscriptionRepository.findAll().stream().map(inscriptionMapper::toDto).collect(Collectors.toList());
        return new Result<>( inscriptions.size(), null, null, inscriptions);
    }
}
