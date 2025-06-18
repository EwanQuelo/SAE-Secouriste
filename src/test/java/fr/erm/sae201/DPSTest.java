package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Classe de test pour la classe de persistance DPS.
 * Utilise JUnit 4 et suit un format spécifique. (VERSION CORRIGÉE)
 */
public class DPSTest {

    // --- Méthodes d'aide pour créer des objets de test valides en utilisant les VRAIS constructeurs ---

    private Site createValidSite() {
        // Utilise le vrai constructeur : Site(code, nom, longitude, latitude)
        return new Site("SDF", "Stade de France", 2.3600f, 48.9244f);
    }

    private Journee createValidJournee() {
        // Utilise le vrai constructeur : Journee(jour, mois, annee)
        return new Journee(26, 7, 2024);
    }

    private Sport createValidSport() {
        // Utilise le vrai constructeur : Sport(code, nom)
        return new Sport("ATH", "Athlétisme");
    }

    private DPS createValidDPS() {
        // Crée un DPS valide en utilisant les helpers ci-dessus
        return new DPS(1L, new int[]{9, 0}, new int[]{18, 0}, createValidSite(), createValidJournee(), createValidSport());
    }

    // --- Tests pour le Constructeur ---

    @Test
    public void testConstructor() {
        System.out.println("** testConstructor() **");

        System.out.println("Cas normal :");
        testCasConstructor(1L, new int[]{8, 30}, new int[]{17, 30}, createValidSite(), createValidJournee(), createValidSport(), null);

        System.out.println("Cas erreur :");
        // Test avec un Site null
        testCasConstructor(1L, new int[]{9, 0}, new int[]{17, 0}, null, createValidJournee(), createValidSport(), IllegalArgumentException.class);
    }

    private void testCasConstructor(long id, int[] hDepart, int[] hFin, Site site, Journee journee, Sport sport, Class<? extends Throwable> exceptionAttendue) {
        try {
            DPS dps = new DPS(id, hDepart, hFin, site, journee, sport);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals(id, dps.getId());
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

        DPS dps1 = createValidDPS();
        dps1.setId(100L);
        
        DPS dps2 = createValidDPS();
        dps2.setId(100L); // Même ID mais autres détails
        
        DPS dps3 = createValidDPS();
        dps3.setId(101L); // ID différent

        System.out.println("Cas normal : deux objets avec le même ID");
        assertTrue("Deux DPS avec le même ID doivent être égaux.", dps1.equals(dps2));
        assertEquals("Les hashCodes de deux DPS égaux doivent être identiques.", dps1.hashCode(), dps2.hashCode());
        
        System.out.println("Cas erreur : deux objets avec des ID différents");
        assertFalse("Deux DPS avec des ID différents ne doivent pas être égaux.", dps1.equals(dps3));
    }
}