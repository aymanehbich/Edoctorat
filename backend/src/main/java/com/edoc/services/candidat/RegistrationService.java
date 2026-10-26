package com.edoc.services.candidat;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edoc.dto.candidat.PreRegistrationDTO;
import com.edoc.dto.candidat.RegistrationDTO;
import com.edoc.exception.exceptionEmail.*;
import com.edoc.models.Candidat;
import com.edoc.models.User;
import com.edoc.models.VerificationToken;
import com.edoc.repository.CandidatRepository;
import com.edoc.repository.GroupRepository;
import com.edoc.repository.UserRepository;
import com.edoc.repository.VerificationTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegistrationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CandidatRepository candidatRepository; 

    @Autowired
    private GroupRepository groupRepository;
    
    // ------------------   pre-register user ------------------------------
        public void preRegister(PreRegistrationDTO dto) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new EmailAlreadyExistsException();
            }
            
            User user = new User();
            user.setEmail(dto.getEmail());
            user.setUsername(dto.getEmail());
            user.setFirstName(dto.getPrenom());
            user.setLastName(dto.getNom());
            userRepository.save(user);
            
            String token = generateVerificationToken();
            createVerificationToken(user, token);
            
            sendVerificationEmail(dto.getEmail(), token, dto.getOrigin());
        }
    
    // ------------------  verifier token si expire ou non ------------------------------

        public PreRegistrationDTO verifyToken(String token) {
          VerificationToken verificationToken = tokenRepository.findByToken(token)
              .orElseThrow(() -> new InvalidTokenException());
              
          if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
              User user = verificationToken.getUser();
              if (!user.isActive()) {
                  userRepository.deleteById(user.getId());
              }
              tokenRepository.deleteById(verificationToken.getId());
          }
          
          User user = verificationToken.getUser();
          
          return mapToPreRegistrationDTO(user);
        }
    
    // ------------------  add user and delete token ------------------------------
        public void registerUser(RegistrationDTO dto) {

            VerificationToken verificationToken = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new InvalidTokenException());
                
            // initialise fields of user
            User user = verificationToken.getUser();

            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setActive(true);
            user.setAddress(dto.getAdresse());
            user.setPhoneNumber(dto.getTelephone());
            user = userRepository.save(user);

            // initialise fields of candidat
            Candidat candidat = new Candidat();
            candidat.setAdresse(dto.getAdresse());
            candidat.setCin(dto.getCin());
            candidat.setCne(dto.getCne());
            candidat.setDateDeNaissance(dto.getDateDeNaissance().toString());
            candidat.setNomCandidatAr(dto.getNomCandidatAr());
            candidat.setPrenomCandidatAr(dto.getPrenomCandidatAr());
            candidat.setPays(dto.getPays());
            candidat.setSexe(dto.getSexe());
            candidat.setTelCandidat(dto.getTelephone());
            candidat.setVilleDeNaissance(dto.getVilleDeNaissance());
            candidat.setVilleDeNaissanceAr(dto.getVilleDeNaissanceAr());
            candidat.setVille(dto.getVille());
            candidat.setAcademie("");
            candidat.setEtatDossier(false);
            candidat.setPathCv("");
            candidat.setPathPhoto("");
            candidat.setSituation_familiale(dto.getSituationFamiliale());
            candidat.setTypeDeHandicape(dto.getTypeDehandicape());
            candidat.setFonctionnaire("oui");
            candidat.setAcademie(null);
            candidat.setResidence(dto.getVille()); 
            candidat.setUser(user);            
            candidatRepository.save(candidat);


            // add user group
            var candidatGroup = groupRepository.findByName("candidat")
                .orElseThrow(() -> new IllegalStateException(
                    "Required 'candidat' group is missing. Restart the backend to initialize it."));
            user.getGroups().add(candidatGroup);
            userRepository.save(user);
    
                        
            tokenRepository.deleteById(verificationToken.getId());
        }


    // ------------------  save token  ------------------------------
      private void createVerificationToken(User user, String token) {
          VerificationToken verificationToken = new VerificationToken();
          verificationToken.setUser(user);
          verificationToken.setToken(token);
          verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
          tokenRepository.save(verificationToken);
      }
    
    // ------------------  send email ------------------------------
      private void sendVerificationEmail(String email, String token, String origin) {
          String url = origin + "?token=" + token;
        
          String htmlMessage = "<!DOCTYPE html>\n" +
          "<html>\n" +
          "<head>\n" +
          "    <meta charset=\"UTF-8\">\n" +
          "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
          "</head>\n" +
          "<body style=\"margin: 0; padding: 20px; background-color: #f4f4f4; font-family: Arial, sans-serif;\">\n" +
          "    <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);\">\n" +
          "        <h2 style=\"color: #333333; margin-bottom: 20px;\">Email Verification</h2>\n" +
          "        <p style=\"color: #666666; margin-bottom: 30px;\">Thank you for signing up! Please verify your email address by clicking the button below:</p>\n" +
          "        <a href=\"" + url + "\"\n" +
          "           style=\"display: inline-block; \n" +
          "                  padding: 12px 24px; \n" +
          "                  background-color: #4CAF50; \n" +
          "                  color: #ffffff; \n" +
          "                  text-decoration: none; \n" +
          "                  border-radius: 4px; \n" +
          "                  font-weight: bold;\">\n" +
          "            Verify Email\n" +
          "        </a>\n" +
          "        <p style=\"color: #999999; margin-top: 30px; font-size: 12px;\">If the button doesn't work, copy and paste this link into your browser:</p>\n" +
          "        <p style=\"color: #999999; font-size: 12px;\">" + url + "</p>\n" +
          "    </div>\n" +
          "</body>\n" +
          "</html>";

          MimeMessage mimeMessage = mailSender.createMimeMessage();
          try {
              MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
              helper.setTo(email);
              helper.setSubject("Email Verification");
              helper.setText(htmlMessage, true);
              mailSender.send(mimeMessage);
          } catch (MessagingException e) {
              throw new RuntimeException("Failed to send verification email", e);
          }
      }
    
    //  ------------------  generate token ------------------------------

      private String generateVerificationToken() {
          return UUID.randomUUID().toString();
      }


    // ------------------  map to preRegistrationDTO ------------------------------

      private PreRegistrationDTO mapToPreRegistrationDTO(User user) {
        PreRegistrationDTO dto = new PreRegistrationDTO();
        dto.setEmail(user.getEmail());
        dto.setNom(user.getLastName());
        dto.setPrenom(user.getFirstName());
        return dto;
      }
}
