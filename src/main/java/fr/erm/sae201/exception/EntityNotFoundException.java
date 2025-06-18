package fr.erm.sae201.exception;

/**
 * Exception levée lorsqu'une entité attendue n'est pas trouvée dans la base de données.
 * C'est une RuntimeException pour ne pas alourdir les signatures de méthodes avec "throws".
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}