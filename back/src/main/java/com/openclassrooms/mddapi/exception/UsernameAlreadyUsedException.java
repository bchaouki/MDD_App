package com.openclassrooms.mddapi.exception;

public class UsernameAlreadyUsedException extends RuntimeException {

    public UsernameAlreadyUsedException(String username) { super("Nom d'utilisateur déjà utilisé : " + username); }
}
