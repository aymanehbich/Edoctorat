package com.edoc.services.reset_password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edoc.dto.reset_password.PasswordResetDTO;
import com.edoc.dto.reset_password.PasswordResetRequestDTO;
import com.edoc.exception.exceptionEmail.InvalidTokenException;
import com.edoc.models.User;
import com.edoc.models.VerificationToken;
import com.edoc.repository.UserRepository;
import com.edoc.repository.VerificationTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PasswordRecoveryService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JavaMailSender mailSender;

    // ------------------  request password reset  ------------------------------
    public boolean requestPasswordReset(PasswordResetRequestDTO dto) {
        try {
          Optional<User> user = userRepository.findByEmail(dto.getEmail());
          if (user.isPresent()) {

            String token = UUID.randomUUID().toString();
            createVerificationToken(user.get(), token);
            String resetLink = dto.getRedirectUrl() + "?token=" + token;
            sendHtmlEmail(user.get().getEmail(), "Password Reset Request", resetLink);
            return true;
          }else{
            return false;
          }
            
        } catch (Exception e) {
            return false;
        }
    }


    // ------------------  save token  ------------------------------
    private void createVerificationToken(User user, String token) {
          VerificationToken verificationToken = new VerificationToken();
          verificationToken.setUser(user);
          verificationToken.setToken(token);
          verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
          tokenRepository.save(verificationToken);
    }

    // ------------------  reset password  ------------------------------
    public boolean resetPassword(PasswordResetDTO dto) {
        try {
            VerificationToken verificationToken = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new InvalidTokenException());
                
            User user = verificationToken.getUser();

            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user = userRepository.save(user);
            tokenRepository.deleteById(verificationToken.getId());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------  send email ------------------------------
    private void sendHtmlEmail(String to, String subject, String resetLink) {
          String htmlContent = "<!DOCTYPE html>\n" +
          "<html>\n" +
          "<head>\n" +
          "    <meta charset=\"UTF-8\">\n" +
          "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
          "</head>\n" +
          "<body style=\"margin: 0; padding: 20px; background-color: #f4f4f4; font-family: Arial, sans-serif;\">\n" +
          "    <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);\">\n" +
          "        <h2 style=\"color: #333333; margin-bottom: 20px;\">Password Reset Request</h2>\n" +
          "        <p style=\"color: #666666; margin-bottom: 30px;\">We received a request to reset your password. Click the button below to create a new password:</p>\n" +
          "        <a href=\"" + resetLink + "\"\n" +
          "           style=\"display: inline-block; \n" +
          "                  padding: 12px 24px; \n" +
          "                  background-color: #4CAF50; \n" +
          "                  color: #ffffff; \n" +
          "                  text-decoration: none; \n" +
          "                  border-radius: 4px; \n" +
          "                  font-weight: bold;\">\n" +
          "            Reset Password\n" +
          "        </a>\n" +
          "        <p style=\"color: #999999; margin-top: 30px; font-size: 12px;\">If the button doesn't work, copy and paste this link into your browser:</p>\n" +
          "        <p style=\"color: #999999; font-size: 12px;\">" + resetLink + "</p>\n" +
          "    </div>\n" +
          "</body>\n" +
          "</html>";


        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + subject, e);
        }
    }
}