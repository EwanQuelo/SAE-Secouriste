package fr.erm.sae201.exception;

/**
 * Exception personnalisée levée lorsqu'une entité attendue n'est pas trouvée
 * dans une source de données, typiquement une base de données.
 * <p>
 * Elle hérite de RuntimeException pour ne pas obliger les développeurs à la
 * déclarer dans les signatures de méthode (checked exception), ce qui simplifie
 * le code des couches de service et des contrôleurs.
 * </p>
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Construit une nouvelle EntityNotFoundException avec un message détaillé.
     *
     * @param message Le message d'erreur expliquant quelle entité n'a pas été trouvée.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}