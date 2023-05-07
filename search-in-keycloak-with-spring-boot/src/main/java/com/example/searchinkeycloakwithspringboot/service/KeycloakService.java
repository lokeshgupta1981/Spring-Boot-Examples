package com.example.searchinkeycloakwithspringboot.service;

import com.example.searchinkeycloakwithspringboot.exception.UserNotFoundInKeycloakException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class KeycloakService {


  @Autowired
  private Keycloak keycloak;

  public UserRepresentation searchById(String id) {
    UserResource userResource = keycloak.realm("master").users().get(id);
    return userResource.toRepresentation();
  }

  public UserRepresentation searchByEmail(String email) throws UserNotFoundInKeycloakException {
    List<UserRepresentation> userRepresentations = keycloak.realm("master").users()
        .search(null, null, null, email, 0, 1);
    if (userRepresentations.isEmpty()) {
      throw new UserNotFoundInKeycloakException("User with email " + email + " not found");
    }
    return userRepresentations.get(0);
  }

  public UserRepresentation searchByUsername(String username)
      throws UserNotFoundInKeycloakException {
    List<UserRepresentation> userRepresentations = keycloak.realm("master").users()
        .search(username, true);
    if (userRepresentations.isEmpty()) {
      throw new UserNotFoundInKeycloakException("User with username " + username + " not found");
    }
    return userRepresentations.get(0);
  }

  public UserRepresentation searchByPhone(String phone) throws UserNotFoundInKeycloakException {
    List<UserRepresentation> userRepresentations = keycloak.realm("master").users()
        .searchByAttributes("phone:" + phone);
    if (userRepresentations.isEmpty()) {
      throw new UserNotFoundInKeycloakException("User with phone number " + phone + " not found");
    }
    return userRepresentations.get(0);
  }

  public Set<UserRepresentation> searchByRole(String roleName) {
    return keycloak.realm("master").roles().get(roleName).getRoleUserMembers();
  }

}
