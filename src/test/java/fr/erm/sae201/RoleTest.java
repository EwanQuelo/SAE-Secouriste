package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Role;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de test pour l'énumération Role.
 * Utilise JUnit 4 et suit un format spécifique.
 */
public class RoleTest {

    // --- Tests pour la méthode statique valueOf() ---
    // Cette méthode convertit une chaîne de caractères en une valeur de l'enum.

    @Test
    public void testValueOf() {
        System.out.println("** testValueOf() **");

        System.out.println("Cas normaux :");
        testCasValueOf("SECOURISTE", Role.SECOURISTE, null);
        testCasValueOf("ADMINISTRATEUR", Role.ADMINISTRATEUR, null);
        
        System.out.println("Cas erreur :");
        // Test avec une chaîne qui ne correspond à aucune valeur
        testCasValueOf("UTILISATEUR", null, IllegalArgumentException.class);
        // Test avec une casse incorrecte
        testCasValueOf("secouriste", null, IllegalArgumentException.class);
        // Test avec une chaîne vide
        testCasValueOf("", null, IllegalArgumentException.class);
        // Test avec une valeur null (doit lever une NullPointerException)
        testCasValueOf(null, null, NullPointerException.class);
    }

    /**
     * Méthode d'aide pour tester Role.valueOf().
     * Tente de convertir une chaîne et vérifie si le résultat ou l'exception est conforme.
     * @param nom Le nom de la constante enum à chercher.
     * @param resultatAttendu La valeur enum attendue.
     * @param exceptionAttendue Le type d'exception attendu.
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
    
    
    // --- Tests pour la méthode toString() ---
    // Cette méthode retourne le nom de la constante enum sous forme de chaîne.

    @Test
    public void testToString() {
        System.out.println("\n** testToString() **");

        System.out.println("Cas normaux :");
        testCasToString(Role.SECOURISTE, "SECOURISTE");
        testCasToString(Role.ADMINISTRATEUR, "ADMINISTRATEUR");
    }

    /**
     * Méthode d'aide pour tester la méthode toString() de l'enum.
     * @param role La valeur de l'enum à tester.
     * @param resultatAttendu La chaîne de caractères attendue.
     */
    private void testCasToString(Role role, String resultatAttendu) {
        assertEquals("La représentation String de l'enum n'est pas correcte.", resultatAttendu, role.toString());
    }


    // --- Test des propriétés fondamentales de l'enum ---
    // Ce test ne suit pas le format "testCas" car il vérifie des propriétés de base.

    @Test
    public void testEnumProperties() {
        System.out.println("\n** testEnumProperties() **");

        // On vérifie que les valeurs ne sont pas nulles
        assertNotNull("La constante SECOURISTE ne doit pas être nulle.", Role.SECOURISTE);
        assertNotNull("La constante ADMINISTRATEUR ne doit pas être nulle.", Role.ADMINISTRATEUR);

        // On vérifie que les valeurs sont distinctes
        assertNotEquals("Les constantes de l'enum doivent être distinctes.", Role.SECOURISTE, Role.ADMINISTRATEUR);

        // On vérifie le nombre total de valeurs dans l'enum
        assertEquals("L'enum Role doit contenir exactement 2 valeurs.", 2, Role.values().length);
    }
}