package com.edoc.services;
import com.edoc.dto.AuthProf;
import com.edoc.dto.LoginRequest;
import com.edoc.dto.TokenResponse;
import com.edoc.dto.UserInfo;
import com.edoc.dto.UserInfoResponse;
import com.edoc.exception.AuthenticationException;
import com.edoc.models.User;
import com.edoc.repository.LaboratoireRepository;
import com.edoc.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;

@Service
public class AuthService {
    
    @Value("${google.client.id}")
    private String googleClientId;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LaboratoireRepository laboratoireRepository;

    /**
     * Shared by all three login flows: puts the Authentication into the
     * SecurityContextHolder and forces a session to exist so Spring Security's
     * HttpSessionSecurityContextRepository persists it under "SPRING_SECURITY_CONTEXT"
     * at the end of the request — every session-gated controller (Candidat, Sujet,
     * Professeur, Diplome, Ced, Commission, Postuler...) reads that key directly.
     */
    private void establishSession(Authentication authentication, User user, String accessToken, String refreshToken) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
            .getRequest().getSession(true);

        session.setAttribute("ACCESS_TOKEN", accessToken);
        session.setAttribute("REFRESH_TOKEN", refreshToken);
        session.setAttribute("USER_ID", user.getId());
    }

    // -------------------------- login candidat ------------------------------------

    public TokenResponse authenticate(LoginRequest request) {
      try {
          User user = userRepository.findByUsernameOrEmail(request.getUsername(),request.getUsername())
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));
          Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
          );

          String accessToken = jwtService.generateAccessToken(user.getUsername());
          String refreshToken = jwtService.generateRefreshToken(user.getUsername());

          establishSession(authentication, user, accessToken, refreshToken);

          TokenResponse tokenResponse = new TokenResponse();
          tokenResponse.setAccess(accessToken);
          tokenResponse.setRefresh(refreshToken);

          return tokenResponse;
      } catch (AuthenticationException e) {
          throw e;
      }
  }

    // -------------------------- login scolarite ------------------------------------

    public UserInfo authenticateScolarite(LoginRequest request) {

           User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );

            String accessToken = jwtService.generateAccessToken(user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            establishSession(authentication, user, accessToken, refreshToken);

            UserInfoResponse userI = mapUserToUserInfoResponse(user);

            
            
            UserInfo userInfo = new UserInfo();
            userInfo.setAccess(accessToken);
            userInfo.setRefresh(refreshToken);
            userInfo.setNom(userI.getNom());
            userInfo.setPrenom(userI.getPrenom());
            userInfo.setEmail(user.getEmail());
            userInfo.setPathPhoto(userI.getPathPhoto());
            userInfo.setGroups(userI.getGroups());
            
            return userInfo;
    }
    

    // -------------------------- login professor ------------------------------------

    public AuthProf verifyAndAuthenticateProf(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), 
                new GsonFactory()
            )
            .setAudience(Collections.singletonList(googleClientId))
            .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new AuthenticationException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            
            String email = payload.getEmail();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Professor not found"));
            
            Set<String> groupNames = userRepository.findGroupNamesByUserId(user.getId());

            if (!groupNames.contains("professeur")) {
              throw new AuthenticationException("User is not a professor");
          }

            String accessToken = jwtService.generateAccessToken(user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            establishSession(authentication, user, accessToken, refreshToken);

            AuthProf response = new AuthProf();
            response.setAccess(accessToken);
            response.setRefresh(refreshToken);
            response.setEmail(user.getEmail());
            response.setNom(user.getLastName());
            response.setPrenom(user.getFirstName());
            response.setGroups(groupNames);
            response.setPathPhoto(null);
            response.setNombreProposer(5);
            response.setNombreEncadrer(5);
            response.setGrade(refreshToken);
          

            return response;

        } catch (GeneralSecurityException | IOException e) {
            throw new AuthenticationException("Error verifying Google token", e);
        }
    }
    
    // -------------------------- get user info ------------------------------------------

    public UserInfoResponse getUserInfo(String token) {
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
        return mapUserToUserInfoResponse(user);
    }
    
  // -------------------------- map user to user info response ------------------------------------------

    private UserInfoResponse mapUserToUserInfoResponse(User user) {
      UserInfoResponse response = new UserInfoResponse();
      response.setEmail(user.getEmail());
      response.setNom(user.getLastName());
      response.setPrenom(user.getFirstName());
      response.setPhoneNumber(user.getPhoneNumber());
      response.setAddress(user.getAddress());
      response.setActive(user.isActive());

      Set<String> pathPhoto = userRepository.findPhotoPathByUserId(user.getId());
      response.setPathPhoto(pathPhoto);

      Set<String> groupNames = userRepository.findGroupNamesByUserId(user.getId());

      response.setGroups(groupNames);

      laboratoireRepository.findByDirecteurUserId(user.getId())
          .ifPresent(laboratoire -> response.setLaboratoireDirecteurId(laboratoire.getId()));

      return response;
  }

    
  // -------------------------- refresh token ------------------------------------------
    public TokenResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        if (username != null && jwtService.isTokenValid(refreshToken)) {
            String accessToken = jwtService.generateAccessToken(username);
            
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccess(accessToken);
            tokenResponse.setRefresh(refreshToken);
            return tokenResponse;
        }
        throw new RuntimeException("Invalid refresh token");
    }
}