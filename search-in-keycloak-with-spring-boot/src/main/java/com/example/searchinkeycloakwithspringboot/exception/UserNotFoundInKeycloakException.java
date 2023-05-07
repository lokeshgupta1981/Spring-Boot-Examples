package com.example.searchinkeycloakwithspringboot.exception;

public class UserNotFoundInKeycloakException extends Exception{
    public UserNotFoundInKeycloakException(String message) {
        super(message);
    }
}
