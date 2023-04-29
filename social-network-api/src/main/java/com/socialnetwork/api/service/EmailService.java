package com.socialnetwork.api.service;

import com.socialnetwork.api.model.ConfirmationToken;
import com.socialnetwork.api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class EmailService {

  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String emailAddressFrom;

  @Value("${email.confirmation.url}")
  private String confirmAccountUrl;

  @Async
  public void sendEmail(User user, ConfirmationToken token) {
    MimeMessagePreparator mailMessage = mimeMessage -> {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
      try {
        message.setFrom(emailAddressFrom, "FP3 Social Network");
        message.addTo(user.getEmailAddress());
        message.setSubject("Complete Registration");
        message.setText("To confirm your account, please click here: "
                + confirmAccountUrl + token.getConfirmationToken());
      } catch (Exception e) {
        throw new Exception("Unexpected error");
      }
    };

    javaMailSender.send(mailMessage);
  }
}