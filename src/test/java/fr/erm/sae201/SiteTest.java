package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Site;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de test pour la classe de persistance Site.
 * Utilise JUnit 4 et suit un format spécifique.
 */
public class SiteTest {

    // --- Tests pour le Constructeur ---
    // Comme le constructeur appelle les setters, on teste principalement les cas d'erreur qui peuvent s'y produire.

    @Test
    public void testConstructor() {
        System.out.println("** testConstructor() **");

        System.out.println("Cas normal :");
        testCasConstructor("SDF", "Stade de France", 2.360f, 48.924f, null);

        System.out.println("Cas erreur :");
        // Test avec un code null
        testCasConstructor(null, "Stade de France", 2.360f, 48.924f, IllegalArgumentException.class);
        // Test avec un nom vide
        testCasConstructor("SDF", "   ", 2.360f, 48.924f, IllegalArgumentException.class);
    }

    private void testCasConstructor(String code, String nom, float longitude, float latitude, Class<? extends Throwable> exceptionAttendue) {
        try {
            Site site = new Site(code, nom, longitude, latitude);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("Le code n'a pas été correctement assigné.", code, site.getCode());
            assertEquals("Le nom n'a pas été correctement assigné.", nom, site.getNom());
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
        testCasSetCode("CDG", "CDG", null);
        
        System.out.println("Cas limite :");
        testCasSetCode("A", "A", null);

        System.out.println("Cas erreur :");
        testCasSetCode(null, null, IllegalArgumentException.class);
        testCasSetCode("", null, IllegalArgumentException.class);
        testCasSetCode("   ", null, IllegalArgumentException.class);
    }

    private void testCasSetCode(String code, String resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        Site site = new Site("INIT", "Initial", 0, 0); // Objet de départ valide
        try {
            site.setCode(code);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("Le code n'a pas été modifié correctement.", resultatAttendu, site.getCode());
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
        testCasSetNom("Tour Eiffel", "Tour Eiffel", null);

        System.out.println("Cas erreur :");
        testCasSetNom(null, null, IllegalArgumentException.class);
        testCasSetNom(" ", null, IllegalArgumentException.class);
    }

    private void testCasSetNom(String nom, String resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        Site site = new Site("INIT", "Initial", 0, 0);
        try {
            site.setNom(nom);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("Le nom n'a pas été modifié correctement.", resultatAttendu, site.getNom());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }
    
    @Test
    public void testSetLongitude() {
        System.out.println("\n** testSetLongitude() **");
        
        System.out.println("Cas normal :");
        testCasSetLongitude(12.345f, 12.345f);
        
        System.out.println("Cas limite :");
        testCasSetLongitude(0.0f, 0.0f);
        testCasSetLongitude(-50.12f, -50.12f);
    }
    
    private void testCasSetLongitude(float longitude, float resultatAttendu) {
        Site site = new Site("INIT", "Initial", 0, 0);
        site.setLongitude(longitude);
        // Pour les floats, il faut utiliser une tolérance (delta) dans assertEquals
        assertEquals("La longitude n'a pas été modifiée correctement.", resultatAttendu, site.getLongitude(), 0.0001f);
    }
    
    // --- Test pour Equals et HashCode ---

    @Test
    public void testEqualsAndHashCode() {
        System.out.println("\n** testEqualsAndHashCode() **");

        Site s1 = new Site("SDF", "Stade de France", 2.36f, 48.92f);
        Site s2 = new Site("SDF", "Autre Nom", 1.0f, 1.0f); // Même code, autres champs différents
        Site s3 = new Site("TDF", "Tour Eiffel", 2.29f, 48.85f); // Code différent

        System.out.println("Cas normal : deux objets avec le même code");
        assertTrue("Deux sites avec le même code doivent être égaux.", s1.equals(s2));
        assertEquals("Les hashCodes de deux sites égaux doivent être identiques.", s1.hashCode(), s2.hashCode());

        System.out.println("Cas erreur : deux objets avec des codes différents");
        assertFalse("Deux sites avec des codes différents ne doivent pas être égaux.", s1.equals(s3));
    }
}