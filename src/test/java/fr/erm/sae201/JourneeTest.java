package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Journee;
import org.junit.Test;
import java.time.LocalDate;
import java.time.DateTimeException;
import static org.junit.Assert.*;

/**
 * Classe de test pour la classe de persistance Journee.
 * Utilise JUnit 4 et suit un format spécifique. (VERSION CORRIGÉE)
 */
public class JourneeTest {

    // --- Tests pour le Constructeur avec LocalDate ---

    @Test
    public void testConstructorWithLocalDate() {
        System.out.println("** testConstructorWithLocalDate() **");

        System.out.println("Cas normal :");
        testCasConstructorWithLocalDate(LocalDate.of(2024, 7, 26), null);
        
        System.out.println("Cas erreur :");
        testCasConstructorWithLocalDate(null, IllegalArgumentException.class);
    }

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


    // --- Tests pour le Constructeur avec des entiers (jour, mois, annee) ---

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


    // --- Tests pour le setter setDate() ---

    @Test
    public void testSetDate() {
        System.out.println("\n** testSetDate() **");
        
        System.out.println("Cas normal :");
        testCasSetDate(LocalDate.of(2025, 1, 1), null);

        System.out.println("Cas erreur :");
        testCasSetDate(null, IllegalArgumentException.class);
    }
    
    private void testCasSetDate(LocalDate nouvelleDate, Class<? extends Throwable> exceptionAttendue) {
        // CORRIGÉ : L'ordre est jour, mois, annee
        Journee journee = new Journee(1, 1, 2024); // Objet de départ
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

    // --- Test pour Equals et HashCode ---

    @Test
    public void testEqualsAndHashCode() {
        System.out.println("\n** testEqualsAndHashCode() **");

        // CORRIGÉ : L'ordre est jour, mois, annee
        Journee j1 = new Journee(26, 7, 2024);
        Journee j2 = new Journee(LocalDate.of(2024, 7, 26)); // Même date, autre constructeur
        // CORRIGÉ : L'ordre est jour, mois, annee
        Journee j3 = new Journee(27, 7, 2024); // Date différente

        System.out.println("Cas normal : deux objets avec la même date");
        assertTrue("Deux journées avec la même date doivent être égales.", j1.equals(j2));
        assertEquals("Les hashCodes de deux journées égales doivent être identiques.", j1.hashCode(), j2.hashCode());
        
        System.out.println("Cas erreur : deux objets avec des dates différentes");
        assertFalse("Deux journées avec des dates différentes ne doivent pas être égales.", j1.equals(j3));
    }
}