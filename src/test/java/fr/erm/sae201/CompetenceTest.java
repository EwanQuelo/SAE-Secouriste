package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Competence;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Classe de test pour la classe de persistance Competence.
 * Suit un format spécifique avec des méthodes de test principales
 * et des méthodes d'aide pour chaque cas.
 */
public class CompetenceTest {

    @Test
    public void testSetIntitule() {
        System.out.println("** testSetIntitule() **");

        System.out.println("Cas normaux :");
        testCasSetIntitule("Java", "Java", null);

        System.out.println("Cas limite :");
        testCasSetIntitule("C", "C", null);
        testCasSetIntitule("  SQL  ", "  SQL  ", null);

        System.out.println("Cas erreur :");
        testCasSetIntitule(null, null, IllegalArgumentException.class);
        testCasSetIntitule("", null, IllegalArgumentException.class);
        testCasSetIntitule("   ", null, IllegalArgumentException.class);
    }

    private void testCasSetIntitule(String intitule, String resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        Competence c = new Competence("ValideInitial"); // On part d'un objet valide
        try {
            c.setIntitule(intitule);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("L'intitulé n'est pas celui attendu.", resultatAttendu, c.getIntitule());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }

    // --- Tests pour les Prérequis ---

    @Test
    public void testSetPrerequisites() {
        System.out.println("\n** testSetPrerequisites() **");

        System.out.println("Cas normaux :");
        Set<Competence> normalSet = new HashSet<>();
        normalSet.add(new Competence("Base de données"));
        testCasSetPrerequisites(normalSet, normalSet, null);

        System.out.println("Cas limite :");
        Set<Competence> emptySet = new HashSet<>();
        testCasSetPrerequisites(emptySet, emptySet, null);

        System.out.println("Cas erreur :");
        testCasSetPrerequisites(null, null, IllegalArgumentException.class);
        Set<Competence> errorSet = new HashSet<>();
        errorSet.add(new Competence("Java"));
        errorSet.add(null);
        testCasSetPrerequisites(errorSet, null, IllegalArgumentException.class);
    }

    private void testCasSetPrerequisites(Set<Competence> prerequis, Set<Competence> resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        Competence c = new Competence("Programmation Avancée");
        try {
            c.setPrerequisites(prerequis);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals(resultatAttendu, c.getPrerequisites());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }

    @Test
    public void testAddPrerequisite() {
        System.out.println("\n** testAddPrerequisite() **");

        System.out.println("Cas normaux :");
        testCasAddPrerequisite(new Competence("Java"), 1, null);

        System.out.println("Cas limite :");
        Competence c = new Competence("Data Science");
        c.addPrerequisite(new Competence("SQL"));
        c.addPrerequisite(new Competence("SQL")); 
        assertEquals("L'ajout d'un doublon ne doit pas changer la taille du set.", 1, c.getPrerequisites().size());

        System.out.println("Cas erreur :");
        testCasAddPrerequisite(null, 0, IllegalArgumentException.class);
    }

    private void testCasAddPrerequisite(Competence prerequis, int tailleAttendue, Class<? extends Throwable> exceptionAttendue) {
        Competence c = new Competence("Programmation Avancée");
        try {
            c.addPrerequisite(prerequis);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals(tailleAttendue, c.getPrerequisites().size());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }
}