package com.howtodoinjava.demo.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest
public class WireMockTestAnnotationTest {

  @Test
  void simpleStubTesting(WireMockRuntimeInfo wmRuntimeInfo) {
    String responseBody = "Hello World !!";
    String apiUrl = "/resource";

    stubFor(get("/resource").willReturn(ok(responseBody)));

    String apiResponse = getContent(wmRuntimeInfo.getHttpBaseUrl() + apiUrl);
    assertEquals(apiResponse, responseBody);

    verify(getRequestedFor(urlEqualTo(apiUrl)));
  }

  private String getContent(String url) {

    TestRestTemplate testRestTemplate = new TestRestTemplate();
    return testRestTemplate
        .getForObject(url, String.class);
  }
}
