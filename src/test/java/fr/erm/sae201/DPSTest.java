package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Classe de tests unitaires pour la classe de persistance DPS.
 * Ces tests valident le comportement du constructeur et le contrat des méthodes
 * equals et hashCode, en utilisant des méthodes d'aide pour structurer
 * les différents cas de test.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class DPSTest {

    /**
     * Crée un objet Site valide pour les besoins des tests.
     *
     * @return Un nouvel objet Site avec des données de test valides.
     */
    private Site createValidSite() {
        return new Site("SDF", "Stade de France", 2.3600f, 48.9244f);
    }

    /**
     * Crée un objet Journee valide pour les besoins des tests.
     *
     * @return Un nouvel objet Journee avec des données de test valides.
     */
    private Journee createValidJournee() {
        return new Journee(26, 7, 2024);
    }

    /**
     * Crée un objet Sport valide pour les besoins des tests.
     *
     * @return Un nouvel objet Sport avec des données de test valides.
     */
    private Sport createValidSport() {
        return new Sport("ATH", "Athlétisme");
    }

    /**
     * Crée un objet DPS complet et valide en utilisant les autres méthodes d'aide.
     *
     * @return Un nouvel objet DPS avec des données de test valides.
     */
    private DPS createValidDPS() {
        return new DPS(1L, new int[]{9, 0}, new int[]{18, 0}, createValidSite(), createValidJournee(), createValidSport());
    }

    /**
     * Méthode de test principale pour le constructeur de la classe {@code DPS}.
     * Elle orchestre les tests en appelant une méthode d'aide pour un cas de création
     * valide et un cas d'erreur (paramètre null).
     */
    @Test
    public void testConstructor() {
        System.out.println("** testConstructor() **");

        System.out.println("Cas normal :");
        testCasConstructor(1L, new int[]{8, 30}, new int[]{17, 30}, createValidSite(), createValidJournee(), createValidSport(), null);

        System.out.println("Cas erreur :");
        testCasConstructor(1L, new int[]{9, 0}, new int[]{17, 0}, null, createValidJournee(), createValidSport(), IllegalArgumentException.class);
    }

    /**
     * Méthode d'aide qui exécute un scénario de test pour le constructeur de {@code DPS}.
     * Elle tente de créer un objet et vérifie soit que l'objet est créé correctement,
     * soit qu'une exception du type attendu est levée.
     *
     * @param id L'identifiant à tester.
     * @param hDepart L'horaire de départ à tester.
     * @param hFin L'horaire de fin à tester.
     * @param site Le site à tester.
     * @param journee La journée à tester.
     * @param sport Le sport à tester.
     * @param exceptionAttendue La classe de l'exception attendue, ou null si aucune n'est attendue.
     */
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
    
    /**
     * Valide l'implémentation des méthodes {@code equals} et {@code hashCode}.
     * Le test vérifie que deux objets {@code DPS} sont considérés comme égaux si et
     * seulement si leurs identifiants (ID) sont identiques, et que leurs hashCodes
     * sont alors également identiques.
     */
    @Test
    public void testEqualsAndHashCode() {
        System.out.println("\n** testEqualsAndHashCode() **");

        DPS dps1 = createValidDPS();
        dps1.setId(100L);
        
        DPS dps2 = createValidDPS();
        dps2.setId(100L);
        
        DPS dps3 = createValidDPS();
        dps3.setId(101L);

        System.out.println("Cas normal : deux objets avec le même ID");
        assertTrue("Deux DPS avec le même ID doivent être égaux.", dps1.equals(dps2));
        assertEquals("Les hashCodes de deux DPS égaux doivent être identiques.", dps1.hashCode(), dps2.hashCode());
        
        System.out.println("Cas erreur : deux objets avec des ID différents");
        assertFalse("Deux DPS avec des ID différents ne doivent pas être égaux.", dps1.equals(dps3));
    }
}