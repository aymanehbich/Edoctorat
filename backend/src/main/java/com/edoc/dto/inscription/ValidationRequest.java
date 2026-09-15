package com.edoc.dto.inscription;

import lombok.Data;

@Data
public class ValidationRequest {
    private String remarque;
    private Boolean valider;
}