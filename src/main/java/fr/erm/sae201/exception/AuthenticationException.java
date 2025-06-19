package fr.erm.sae201.exception;

/**
 * Exception personnalisée levée lorsqu'une tentative d'authentification échoue.
 * 
 * Cette exception est typiquement utilisée pour des cas comme un mot de passe
 * incorrect ou un utilisateur non autorisé, où l'identifiant existe mais les
 * informations de connexion sont invalides.
 * 
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Construit une nouvelle AuthenticationException avec un message détaillé.
     *
     * @param message Le message d'erreur expliquant la cause de l'échec.
     */
    public AuthenticationException(String message) {
        super(message);
    }
}