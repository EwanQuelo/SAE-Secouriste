package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.*;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;

/**
 * Classe de test pour la classe de persistance Affectation.
 * Suit un format spécifique avec des méthodes de test principales
 * et des méthodes d'aide pour chaque cas. (VERSION CORRIGÉE)
 */
public class AffectationTest {

    // --- Méthodes d'aide pour créer des objets de test valides en utilisant les VRAIS constructeurs ---

    private DPS createTestDps(long id) {
        Site site = new Site("S01", "Site Test", 2.0f, 48.0f);
        Journee journee = new Journee(26, 7, 2024);
        Sport sport = new Sport("SPORT1", "Sport Test");
        return new DPS(id, new int[]{9, 0}, new int[]{18, 0}, site, journee, sport);
    }

    private Secouriste createTestSecouriste(long id) {
        return new Secouriste(id, "Dupont", "Jean", new Date(), "jean@test.com", "0102030405", "Adresse Test");
    }

    // --- Tests pour le Constructeur d'Affectation ---

    @Test
    public void testConstructor() {
        System.out.println("** testConstructor() **");

        System.out.println("Cas normal :");
        testCasConstructor(createTestDps(1L), createTestSecouriste(10L), new Competence("PSE1"), null);

        System.out.println("Cas erreur :");
        // Test avec un DPS null
        testCasConstructor(null, createTestSecouriste(10L), new Competence("PSE1"), IllegalArgumentException.class);
        // Test avec un Secouriste null
        testCasConstructor(createTestDps(1L), null, new Competence("PSE1"), IllegalArgumentException.class);
        // Test avec une Competence null
        testCasConstructor(createTestDps(1L), createTestSecouriste(10L), null, IllegalArgumentException.class);
    }

    private void testCasConstructor(DPS dps, Secouriste secouriste, Competence competence, Class<? extends Throwable> exceptionAttendue) {
        try {
            Affectation affectation = new Affectation(dps, secouriste, competence);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            // Si la création réussit, on vérifie que les getters retournent les objets corrects.
            assertEquals("Le getter de DPS ne retourne pas le bon objet.", dps, affectation.getDps());
            assertEquals("Le getter de Secouriste ne retourne pas le bon objet.", secouriste, affectation.getSecouriste());
            assertEquals("Le getter de Competence ne retourne pas le bon objet.", competence, affectation.getCompetence());

        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }
}