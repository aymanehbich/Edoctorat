package com.edoc.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserInfo {
  private String access;
  private String refresh;
  private String nom;
  private String prenom;
  private String email;
  private Set<String> pathPhoto;
  private Set<String> groups;
}