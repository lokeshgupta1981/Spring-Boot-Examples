package com.example.searchinkeycloakwithspringboot.web;

import com.example.searchinkeycloakwithspringboot.exception.UserNotFoundInKeycloakException;
import com.example.searchinkeycloakwithspringboot.service.KeycloakService;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/search")
@AllArgsConstructor
public class SearchController {




    private KeycloakService keycloakService ;



    @GetMapping("/id/{id}")
    public ResponseEntity<UserRepresentation> searchById(@PathVariable String id) {
       return ResponseEntity.ok().body(keycloakService.searchById(id));
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserRepresentation> searchByEmail(@PathVariable String email ) throws UserNotFoundInKeycloakException {
        return ResponseEntity.ok().body(keycloakService.searchByEmail(email));
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<UserRepresentation> searchByUsername(@PathVariable String username) throws UserNotFoundInKeycloakException {
        return ResponseEntity.ok().body(keycloakService.searchByUsername(username));
    }
    @GetMapping("/phone/{phone}")
    public ResponseEntity<UserRepresentation> searchByPhoneNumber(@PathVariable String phone) throws UserNotFoundInKeycloakException {
        return ResponseEntity.ok().body(keycloakService.searchByPhone(phone));
    }
    @GetMapping("/role/{role}")
    public ResponseEntity<Set<UserRepresentation>> searchByRole(@PathVariable String role) {
        return ResponseEntity.ok().body(keycloakService.searchByRole(role));
    }

}
