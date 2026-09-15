package com.edoc.dto;

import java.util.Set;

import lombok.Data;

@Data
public class AuthProf {
    private String access;
    private String refresh;
    private String email;
    private Set<String> groups;
    private String nom;
    private String prenom;
    private String pathPhoto;
    private String grade;
    private Integer nombreProposer;
    private Integer nombreEncadrer;
}
