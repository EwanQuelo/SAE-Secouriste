package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Journee;
import org.junit.Test;
import java.time.LocalDate;
import java.time.DateTimeException;
import static org.junit.Assert.*;

/**
 * Classe de tests unitaires pour la classe de persistance Journee.
 * Elle valide le comportement des deux constructeurs, du setter, ainsi que
 * le contrat des méthodes equals et hashCode.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class JourneeTest {

    /**
     * Méthode de test principale pour le constructeur qui accepte un objet LocalDate.
     * Valide un cas normal et un cas d'erreur (paramètre null).
     */
    @Test
    public void testConstructorWithLocalDate() {
        System.out.println("** testConstructorWithLocalDate() **");

        System.out.println("Cas normal :");
        testCasConstructorWithLocalDate(LocalDate.of(2024, 7, 26), null);
        
        System.out.println("Cas erreur :");
        testCasConstructorWithLocalDate(null, IllegalArgumentException.class);
    }

    /**
     * Méthode d'aide pour tester un scénario du constructeur avec LocalDate.
     *
     * @param date L'objet LocalDate à passer au constructeur.
     * @param exceptionAttendue La classe de l'exception attendue, ou null si aucune n'est attendue.
     */
    private void testCasConstructorWithLocalDate(LocalDate date, Class<? extends Throwable> exceptionAttendue) {
        try {
            Journee journee = new Journee(date);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("La date n'a pas été correctement assignée.", date, journee.getDate());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }


    /**
     * Méthode de test principale pour le constructeur avec des entiers (jour, mois, annee).
     * Valide un cas normal, un cas limite (année bissextile), et plusieurs cas d'erreur (date invalide).
     */
    @Test
    public void testConstructorWithInts() {
        System.out.println("\n** testConstructorWithInts() **");

        System.out.println("Cas normal :");
        testCasConstructorWithInts(26, 7, 2024, null);
        
        System.out.println("Cas limite (année bissextile) :");
        testCasConstructorWithInts(29, 2, 2024, null);

        System.out.println("Cas erreur :");
        testCasConstructorWithInts(32, 1, 2024, DateTimeException.class);
        testCasConstructorWithInts(1, 13, 2024, DateTimeException.class);
        testCasConstructorWithInts(29, 2, 2025, DateTimeException.class);
    }

    /**
     * Méthode d'aide pour tester un scénario du constructeur avec des entiers.
     *
     * @param jour Le jour du mois.
     * @param mois Le mois de l'année.
     * @param annee L'année.
     * @param exceptionAttendue La classe de l'exception attendue, ou null si aucune n'est attendue.
     */
    private void testCasConstructorWithInts(int jour, int mois, int annee, Class<? extends Throwable> exceptionAttendue) {
        try {
            Journee journee = new Journee(jour, mois, annee);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("La date n'a pas été correctement créée.", LocalDate.of(annee, mois, jour), journee.getDate());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }


    /**
     * Méthode de test principale pour le setter setDate.
     * Valide la modification normale d'une date et le cas d'erreur (paramètre null).
     */
    @Test
    public void testSetDate() {
        System.out.println("\n** testSetDate() **");
        
        System.out.println("Cas normal :");
        testCasSetDate(LocalDate.of(2025, 1, 1), null);

        System.out.println("Cas erreur :");
        testCasSetDate(null, IllegalArgumentException.class);
    }
    
    /**
     * Méthode d'aide pour tester un scénario du setter setDate.
     *
     * @param nouvelleDate La nouvelle date à assigner.
     * @param exceptionAttendue La classe de l'exception attendue, ou null si aucune n'est attendue.
     */
    private void testCasSetDate(LocalDate nouvelleDate, Class<? extends Throwable> exceptionAttendue) {
        Journee journee = new Journee(1, 1, 2024);
        try {
            journee.setDate(nouvelleDate);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("La date n'a pas été modifiée correctement.", nouvelleDate, journee.getDate());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }

    /**
     * Valide l'implémentation des méthodes equals et hashCode.
     * Le test vérifie que deux objets Journee sont égaux si et seulement si leurs dates sont identiques.
     */
    @Test
    public void testEqualsAndHashCode() {
        System.out.println("\n** testEqualsAndHashCode() **");

        Journee j1 = new Journee(26, 7, 2024);
        Journee j2 = new Journee(LocalDate.of(2024, 7, 26)); // Même date, autre constructeur
        Journee j3 = new Journee(27, 7, 2024); // Date différente

        System.out.println("Cas normal : deux objets avec la même date");
        assertTrue("Deux journées avec la même date doivent être égales.", j1.equals(j2));
        assertEquals("Les hashCodes de deux journées égales doivent être identiques.", j1.hashCode(), j2.hashCode());
        
        System.out.println("Cas erreur : deux objets avec des dates différentes");
        assertFalse("Deux journées avec des dates différentes ne doivent pas être égales.", j1.equals(j3));
    }
}