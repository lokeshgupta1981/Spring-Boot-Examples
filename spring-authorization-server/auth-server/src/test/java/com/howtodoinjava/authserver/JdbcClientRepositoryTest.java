package com.howtodoinjava.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("jdbc")
class JdbcClientRepositoryTest {

  @Autowired
  RegisteredClientRepository clients;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  MockMvc mvc;

  @Test
  void clientsAreReadFromTheDatabase() throws Exception {
    assertThat(clients).isInstanceOf(JdbcRegisteredClientRepository.class);

    RegisteredClient cli = clients.findByClientId("recipe-cli");
    assertThat(cli).isNotNull();
    assertThat(cli.getClientSecret()).startsWith("{bcrypt}");

    Integer rows = jdbcTemplate.queryForObject(
        "select count(*) from oauth2_registered_client", Integer.class);
    assertThat(rows).isEqualTo(2);

    mvc.perform(post("/oauth2/token")
            .with(httpBasic("recipe-cli", "secret"))
            .param("grant_type", "client_credentials"))
        .andExpect(status().isOk());

    Integer authorizations = jdbcTemplate.queryForObject(
        "select count(*) from oauth2_authorization", Integer.class);
    assertThat(authorizations).isEqualTo(1);
  }
}
