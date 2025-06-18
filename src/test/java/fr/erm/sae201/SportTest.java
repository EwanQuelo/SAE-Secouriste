package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Sport;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de test pour la classe de persistance Sport.
 * Utilise JUnit 4 et suit un format spécifique.
 */
public class SportTest {

    // --- Tests pour le Constructeur ---

    @Test
    public void testConstructor() {
        System.out.println("** testConstructor() **");

        System.out.println("Cas normal :");
        testCasConstructor("ATH", "Athlétisme", null);

        System.out.println("Cas erreur :");
        // Test avec un code null
        testCasConstructor(null, "Athlétisme", IllegalArgumentException.class);
        // Test avec un nom vide
        testCasConstructor("ATH", "   ", IllegalArgumentException.class);
    }

    private void testCasConstructor(String code, String nom, Class<? extends Throwable> exceptionAttendue) {
        try {
            Sport sport = new Sport(code, nom);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("Le code n'a pas été correctement assigné.", code, sport.getCode());
            assertEquals("Le nom n'a pas été correctement assigné.", nom, sport.getNom());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }


    // --- Tests pour les Setters ---

    @Test
    public void testSetCode() {
        System.out.println("\n** testSetCode() **");
        
        System.out.println("Cas normal :");
        testCasSetCode("FTB", "FTB", null);
        
        System.out.println("Cas limite :");
        testCasSetCode("X", "X", null);

        System.out.println("Cas erreur :");
        testCasSetCode(null, null, IllegalArgumentException.class);
        testCasSetCode("", null, IllegalArgumentException.class);
        testCasSetCode("   ", null, IllegalArgumentException.class);
    }

    private void testCasSetCode(String code, String resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        Sport sport = new Sport("INIT", "Initial"); // Objet de départ valide
        try {
            sport.setCode(code);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("Le code n'a pas été modifié correctement.", resultatAttendu, sport.getCode());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }
    
    @Test
    public void testSetNom() {
        System.out.println("\n** testSetNom() **");
        
        System.out.println("Cas normal :");
        testCasSetNom("Basketball", "Basketball", null);

        System.out.println("Cas erreur :");
        testCasSetNom(null, null, IllegalArgumentException.class);
        testCasSetNom(" ", null, IllegalArgumentException.class);
    }

    private void testCasSetNom(String nom, String resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        Sport sport = new Sport("INIT", "Initial");
        try {
            sport.setNom(nom);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("Le nom n'a pas été modifié correctement.", resultatAttendu, sport.getNom());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }
    
    // --- Test pour Equals et HashCode ---

    @Test
    public void testEqualsAndHashCode() {
        System.out.println("\n** testEqualsAndHashCode() **");

        Sport s1 = new Sport("ATH", "Athlétisme");
        Sport s2 = new Sport("ATH", "Un autre nom pour le même sport"); // Même code
        Sport s3 = new Sport("FTB", "Football"); // Code différent

        System.out.println("Cas normal : deux objets avec le même code");
        assertTrue("Deux sports avec le même code doivent être égaux.", s1.equals(s2));
        assertEquals("Les hashCodes de deux sports égaux doivent être identiques.", s1.hashCode(), s2.hashCode());

        System.out.println("Cas erreur : deux objets avec des codes différents");
        assertFalse("Deux sports avec des codes différents ne doivent pas être égaux.", s1.equals(s3));
    }
}