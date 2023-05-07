package com.example.searchinkeycloakwithspringboot;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchInKeycloakWithSpringBootApplicationTests {

  @Autowired
  private Keycloak keycloak;

  @Test
  void testSearchById() {
    String id = "test-id";
    UserResource userResource = keycloak.realm("master").users().get(id);
    Assertions.assertEquals(id, userResource.toRepresentation().getId());
  }

  @Test
  void testSearchByEmail() {
    String email = "test-email";
    List<UserRepresentation> userRepresentationsList = keycloak.realm("master").users()
        .searchByEmail(email, true);
    Assertions.assertEquals(1, userRepresentationsList.size());
    Assertions.assertEquals(email, userRepresentationsList.get(0).getEmail());
  }

  @Test
  void testSearchByUsername() {
    String username = "username";
    List<UserRepresentation> userRepresentationsList = keycloak.realm("master").users()
        .searchByUsername(username, true);
    Assertions.assertEquals(1, userRepresentationsList.size());
    Assertions.assertEquals(username, userRepresentationsList.get(0).getUsername());
  }

}
