package fr.erm.sae201.exception;

/**
 * Exception levée lors d'un échec d'authentification (ex: mot de passe incorrect).
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}