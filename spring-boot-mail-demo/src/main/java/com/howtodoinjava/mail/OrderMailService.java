package com.howtodoinjava.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderMailService {
  private final JavaMailSender sender;
  private final String from;

  public OrderMailService(JavaMailSender sender, @Value("${app.mail.from}") String from) {
    this.sender = sender;
    this.from = from;
  }

  public void sendConfirmation(String to, String orderId, byte[] receiptCsv)
      throws MessagingException {
    if (to == null || to.isBlank() || orderId == null || !orderId.matches("[A-Z0-9-]+")) {
      throw new IllegalArgumentException("Recipient and valid order ID are required");
    }

    MimeMessage message = sender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(
        message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
        StandardCharsets.UTF_8.name());
    helper.setFrom(from);
    helper.setTo(to);
    helper.setSubject("Order " + orderId + " confirmation");
    helper.setText("<p>Thanks for your order " + orderId + ".</p>"
        + "<img src='cid:brand-mark' alt='Store mark'>", true);
    helper.addInline("brand-mark", new ClassPathResource("mail/brand-mark.png"), "image/png");
    helper.addAttachment("receipt-" + orderId + ".csv",
        new ByteArrayResource(receiptCsv), "text/csv");
    sender.send(message);
  }

  public void sendPlainConfirmation(String to, String orderId) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    message.setSubject("Order " + orderId + " confirmation");
    message.setText("Thanks for your order " + orderId + ".");
    sender.send(message);
  }
}
