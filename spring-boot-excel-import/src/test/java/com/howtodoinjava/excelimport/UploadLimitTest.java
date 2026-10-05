package com.howtodoinjava.excelimport;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/** MockMvc does not apply the multipart limits, so this test sends a real request to Tomcat. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UploadLimitTest {

  @LocalServerPort
  int port;

  @Test
  void fileOverMaxFileSizeGets413() throws Exception {
    byte[] sixMegabytes = new byte[6 * 1024 * 1024];
    Arrays.fill(sixMegabytes, (byte) 'x');
    String boundary = "boundary42";
    byte[] head = ("--" + boundary + "\r\n"
        + "Content-Disposition: form-data; name=\"file\"; filename=\"big.xlsx\"\r\n"
        + "Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.US_ASCII);
    byte[] tail = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.US_ASCII);

    HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/students/import"))
        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
        .POST(HttpRequest.BodyPublishers.ofByteArrays(java.util.List.of(head, sixMegabytes, tail)))
        .build();
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println("LIMIT " + response.statusCode() + " " + response.body());
    assertThat(response.statusCode()).isEqualTo(413);
    assertThat(response.body()).contains("The file is larger than the 5MB upload limit");
  }
}
