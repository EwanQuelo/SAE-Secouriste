package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Role;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de tests unitaires pour l'énumération Role.
 * Ces tests valident le comportement des méthodes statiques comme valueOf,
 * la représentation textuelle via toString, et les propriétés fondamentales
 * de l'énumération (nombre de valeurs, non-nullité, etc.).
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class RoleTest {

    /**
     * Méthode de test principale pour la méthode statique valueOf.
     * Elle vérifie la conversion d'une chaîne de caractères en une valeur de l'énumération.
     */
    @Test
    public void testValueOf() {
        System.out.println("** testValueOf() **");

        System.out.println("Cas normaux :");
        testCasValueOf("SECOURISTE", Role.SECOURISTE, null);
        testCasValueOf("ADMINISTRATEUR", Role.ADMINISTRATEUR, null);
        
        System.out.println("Cas erreur :");
        // Test avec une chaîne qui ne correspond à aucune valeur de l'énumération.
        testCasValueOf("UTILISATEUR", null, IllegalArgumentException.class);
        // Test avec une casse incorrecte, valueOf est sensible à la casse.
        testCasValueOf("secouriste", null, IllegalArgumentException.class);
        // Test avec une chaîne vide.
        testCasValueOf("", null, IllegalArgumentException.class);
        // Test avec une valeur null.
        testCasValueOf(null, null, NullPointerException.class);
    }

    /**
     * Méthode d'aide pour tester un scénario de Role.valueOf.
     *
     * @param nom Le nom de la constante à rechercher.
     * @param resultatAttendu La valeur de l'énumération attendue.
     * @param exceptionAttendue La classe de l'exception attendue, ou null si aucune n'est attendue.
     */
    private void testCasValueOf(String nom, Role resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        try {
            Role roleObtenu = Role.valueOf(nom);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("La valeur de l'enum retournée n'est pas correcte.", resultatAttendu, roleObtenu);
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }
    
    
    /**
     * Méthode de test principale pour la méthode toString.
     * Elle vérifie que la représentation textuelle de chaque constante est correcte.
     */
    @Test
    public void testToString() {
        System.out.println("\n** testToString() **");

        System.out.println("Cas normaux :");
        testCasToString(Role.SECOURISTE, "SECOURISTE");
        testCasToString(Role.ADMINISTRATEUR, "ADMINISTRATEUR");
    }

    /**
     * Méthode d'aide pour tester la méthode toString.
     *
     * @param role La valeur de l'énumération à tester.
     * @param resultatAttendu La chaîne de caractères attendue.
     */
    private void testCasToString(Role role, String resultatAttendu) {
        assertEquals("La représentation String de l'enum n'est pas correcte.", resultatAttendu, role.toString());
    }


    /**
     * Teste les propriétés fondamentales de l'énumération Role.
     * Vérifie que les constantes ne sont pas nulles, qu'elles sont distinctes,
     * et que le nombre total de valeurs est correct.
     */
    @Test
    public void testEnumProperties() {
        System.out.println("\n** testEnumProperties() **");

        assertNotNull("La constante SECOURISTE ne doit pas être nulle.", Role.SECOURISTE);
        assertNotNull("La constante ADMINISTRATEUR ne doit pas être nulle.", Role.ADMINISTRATEUR);

        assertNotEquals("Les constantes de l'enum doivent être distinctes.", Role.SECOURISTE, Role.ADMINISTRATEUR);

        assertEquals("L'enum Role doit contenir exactement 2 valeurs.", 2, Role.values().length);
    }
}