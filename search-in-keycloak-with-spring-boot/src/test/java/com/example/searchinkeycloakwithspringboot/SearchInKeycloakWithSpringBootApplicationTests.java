package com.example.searchinkeycloakwithspringboot;

import java.util.List;
import java.util.Set;

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
    String id = "967a26d3-9a37-4a3b-9c22-c774f2933158";
    UserResource userResource = keycloak.realm("master").users().get(id);
    Assertions.assertEquals(id, userResource.toRepresentation().getId());
  }

  @Test
  void testSearchByEmail() {
    String email = "user1@howtodoinjava.com";
    List<UserRepresentation> userRepresentationsList = keycloak.realm("master").users()
        .searchByEmail(email, true);
    Assertions.assertEquals(1, userRepresentationsList.size());
    Assertions.assertEquals(email, userRepresentationsList.get(0).getEmail());
  }

  @Test
  void testSearchByUsername() {
    String username = "user1";
    List<UserRepresentation> userRepresentationsList = keycloak.realm("master").users()
        .searchByUsername(username, true);
    Assertions.assertEquals(1, userRepresentationsList.size());
    Assertions.assertEquals(username, userRepresentationsList.get(0).getUsername());
  }
  @Test
  void testSearchByPhone() {
    String phone = "1234567890";
    List<UserRepresentation> userRepresentationsList = keycloak.realm("master").users()
            .searchByAttributes("phone:" + phone);
    Assertions.assertEquals(1, userRepresentationsList.size());
    Assertions.assertEquals(phone, userRepresentationsList.get(0).firstAttribute("phone"));
  }
  @Test
  void testSearchByRole() {
    String role = "admin";
    Set<UserRepresentation> userRepresentationSet =keycloak.realm("master").roles().get(role)
            .getRoleUserMembers();
    Assertions.assertEquals(1, userRepresentationSet.size());
    for (UserRepresentation userRepresentation : userRepresentationSet) {
      Assertions.assertEquals("admin" , userRepresentation.getUsername());
    }
  }

}
