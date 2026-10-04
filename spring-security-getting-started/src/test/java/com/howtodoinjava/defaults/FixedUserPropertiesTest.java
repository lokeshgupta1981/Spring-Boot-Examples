package com.howtodoinjava.defaults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * spring.security.user.name and spring.security.user.password replace the generated password.
 */
@SpringBootTest(properties = {
    "spring.security.user.name=lokesh",
    "spring.security.user.password=secret"
})
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class FixedUserPropertiesTest {

  @Autowired MockMvc mvc;

  @Test
  void fixedUserLogsInAndNoPasswordIsGenerated(CapturedOutput output) throws Exception {
    mvc.perform(get("/hello").with(httpBasic("lokesh", "secret")))
        .andExpect(status().isOk());
    mvc.perform(get("/hello").with(httpBasic("user", "secret")))
        .andExpect(status().isUnauthorized());
    assertThat(output).doesNotContain("Using generated security password");
  }
}
