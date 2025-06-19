package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Site;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de tests unitaires pour la classe de persistance Site.
 * Ces tests valident le comportement du constructeur, des setters (avec validation
 * des chaînes de caractères), et le contrat des méthodes equals et hashCode.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SiteTest {

    /**
     * Méthode de test principale pour le constructeur de la classe Site.
     * Étant donné que le constructeur appelle les setters, ce test se concentre sur
     * la validation des cas qui pourraient lever une exception via ces setters.
     */
    @Test
    public void testConstructor() {
        System.out.println("** testConstructor() **");

        System.out.println("Cas normal :");
        testCasConstructor("SDF", "Stade de France", 2.360f, 48.924f, null);

        System.out.println("Cas erreur :");
        // Test avec un code null.
        testCasConstructor(null, "Stade de France", 2.360f, 48.924f, IllegalArgumentException.class);
        // Test avec un nom vide ou composé uniquement d'espaces.
        testCasConstructor("SDF", "   ", 2.360f, 48.924f, IllegalArgumentException.class);
    }

    /**
     * Méthode d'aide qui exécute un scénario de test pour le constructeur.
     *
     * @param code Le code du site à tester.
     * @param nom Le nom du site à tester.
     * @param longitude La longitude du site.
     * @param latitude La latitude du site.
     * @param exceptionAttendue La classe de l'exception attendue, ou null si aucune n'est attendue.
     */
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


    /**
     * Méthode de test principale pour le setter setCode.
     */
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

    /**
     * Méthode d'aide qui exécute un scénario de test pour setCode.
     *
     * @param code Le code à tester.
     * @param resultatAttendu Le résultat attendu après l'appel.
     * @param exceptionAttendue La classe de l'exception attendue, ou null.
     */
    private void testCasSetCode(String code, String resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        Site site = new Site("INIT", "Initial", 0, 0);
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
    
    /**
     * Méthode de test principale pour le setter setNom.
     */
    @Test
    public void testSetNom() {
        System.out.println("\n** testSetNom() **");
        
        System.out.println("Cas normal :");
        testCasSetNom("Tour Eiffel", "Tour Eiffel", null);

        System.out.println("Cas erreur :");
        testCasSetNom(null, null, IllegalArgumentException.class);
        testCasSetNom(" ", null, IllegalArgumentException.class);
    }

    /**
     * Méthode d'aide qui exécute un scénario de test pour setNom.
     *
     * @param nom Le nom à tester.
     * @param resultatAttendu Le résultat attendu après l'appel.
     * @param exceptionAttendue La classe de l'exception attendue, ou null.
     */
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
    
    /**
     * Méthode de test principale pour le setter setLongitude.
     * Ce setter n'a pas de validation, donc on ne teste que les assignations.
     */
    @Test
    public void testSetLongitude() {
        System.out.println("\n** testSetLongitude() **");
        
        System.out.println("Cas normal :");
        testCasSetLongitude(12.345f, 12.345f);
        
        System.out.println("Cas limite :");
        testCasSetLongitude(0.0f, 0.0f);
        testCasSetLongitude(-50.12f, -50.12f);
    }
    
    /**
     * Méthode d'aide qui exécute un scénario de test pour setLongitude.
     *
     * @param longitude La longitude à assigner.
     * @param resultatAttendu Le résultat attendu.
     */
    private void testCasSetLongitude(float longitude, float resultatAttendu) {
        Site site = new Site("INIT", "Initial", 0, 0);
        site.setLongitude(longitude);
        // L'utilisation d'une tolérance (delta) est nécessaire pour comparer des flottants.
        assertEquals("La longitude n'a pas été modifiée correctement.", resultatAttendu, site.getLongitude(), 0.0001f);
    }
    
    /**
     * Valide l'implémentation des méthodes equals et hashCode.
     * L'égalité est basée sur le code du site, qui est son identifiant unique.
     */
    @Test
    public void testEqualsAndHashCode() {
        System.out.println("\n** testEqualsAndHashCode() **");

        Site s1 = new Site("SDF", "Stade de France", 2.36f, 48.92f);
        Site s2 = new Site("SDF", "Autre Nom", 1.0f, 1.0f);
        Site s3 = new Site("TDF", "Tour Eiffel", 2.29f, 48.85f);

        System.out.println("Cas normal : deux objets avec le même code");
        assertTrue("Deux sites avec le même code doivent être égaux.", s1.equals(s2));
        assertEquals("Les hashCodes de deux sites égaux doivent être identiques.", s1.hashCode(), s2.hashCode());

        System.out.println("Cas erreur : deux objets avec des codes différents");
        assertFalse("Deux sites avec des codes différents ne doivent pas être égaux.", s1.equals(s3));
    }
}