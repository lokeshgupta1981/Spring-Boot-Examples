package com.howtodoinjava.mail;

import static org.assertj.core.api.Assertions.assertThat;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class OrderMailServiceTest {
  static final GreenMail mailServer = new GreenMail(new ServerSetup(0, "127.0.0.1", "smtp"));

  static {
    mailServer.start();
  }

  @DynamicPropertySource
  static void smtpProperties(DynamicPropertyRegistry properties) {
    properties.add("spring.mail.host", () -> "127.0.0.1");
    properties.add("spring.mail.port", () -> mailServer.getSmtp().getPort());
  }

  @Autowired OrderMailService orders;

  @AfterAll
  static void stopServer() {
    mailServer.stop();
  }

  @Test
  void sendsPlainAndMultipartOrderConfirmation() throws Exception {
    orders.sendPlainConfirmation("buyer@example.test", "A-17");
    byte[] csv = "order,total\nA-17,24.50\n".getBytes(StandardCharsets.UTF_8);
    orders.sendConfirmation("buyer@example.test", "A-17", csv);

    assertThat(mailServer.waitForIncomingEmail(5000, 2)).isTrue();
    MimeMessage[] received = mailServer.getReceivedMessages();
    assertThat(received).hasSize(2);
    assertThat(received[0].getContent()).isEqualTo("Thanks for your order A-17.");

    MimeMessage confirmation = received[1];
    assertThat(confirmation.getSubject()).isEqualTo("Order A-17 confirmation");
    List<Part> leaves = new ArrayList<>();
    collectLeaves(confirmation, leaves);
    assertThat(leaves).anySatisfy(part ->
        assertThat(part.getContent().toString()).contains("cid:brand-mark"));
    Part attachment = leaves.stream()
        .filter(part -> "receipt-A-17.csv".equals(safeFileName(part)))
        .findFirst().orElseThrow();
    assertThat(attachment.getDisposition()).isEqualTo(Part.ATTACHMENT);
    String receivedCsv = new String(attachment.getInputStream().readAllBytes(),
        StandardCharsets.UTF_8).replace("\r\n", "\n");
    assertThat(receivedCsv).isEqualTo(new String(csv, StandardCharsets.UTF_8));
    assertThat(leaves).anySatisfy(part -> {
      assertThat(part.getDisposition()).isEqualTo(Part.INLINE);
      assertThat(part.getHeader("Content-ID")).contains("<brand-mark>");
    });
  }

  private static void collectLeaves(Part part, List<Part> leaves) throws Exception {
    Object content = part.getContent();
    if (content instanceof Multipart multipart) {
      for (int i = 0; i < multipart.getCount(); i++) {
        collectLeaves(multipart.getBodyPart(i), leaves);
      }
    } else {
      leaves.add(part);
    }
  }

  private static String safeFileName(Part part) {
    try {
      return part.getFileName();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
