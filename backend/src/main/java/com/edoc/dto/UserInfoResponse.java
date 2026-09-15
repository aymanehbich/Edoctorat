package com.edoc.dto;


import java.util.Set;


import lombok.Data;

@Data
public class UserInfoResponse {
  private String email;
  private String nom;
  private String prenom;
  private Set<String> pathPhoto;
  private Set<String> groups;
  private String phoneNumber;
  private String address;
  private boolean isActive;
  private Long laboratoireDirecteurId;
}