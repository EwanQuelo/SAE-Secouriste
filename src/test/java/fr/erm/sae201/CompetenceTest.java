package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Competence;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Classe de tests unitaires pour la classe de persistance {@code Competence}.
 * Ces tests valident le comportement des setters et des méthodes de manipulation
 * des prérequis, en couvrant les cas normaux, les cas limites et les cas d'erreur.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class CompetenceTest {

    /**
     * Méthode de test principale pour le setter de l'intitulé ({@code setIntitule}).
     * Elle orchestre l'appel de plusieurs cas de test via une méthode d'aide.
     */
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

    /**
     * Méthode d'aide pour tester un cas spécifique de {@code setIntitule}.
     *
     * @param intitule L'intitulé à tester.
     * @param resultatAttendu La valeur attendue de l'intitulé après l'appel, si aucune exception n'est levée.
     * @param exceptionAttendue Le type d'exception qui doit être levée, ou null si aucune n'est attendue.
     */
    private void testCasSetIntitule(String intitule, String resultatAttendu, Class<? extends Throwable> exceptionAttendue) {
        Competence c = new Competence("ValideInitial");
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

    /**
     * Méthode de test principale pour le setter de l'ensemble des prérequis ({@code setPrerequisites}).
     */
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

    /**
     * Méthode d'aide pour tester un cas spécifique de {@code setPrerequisites}.
     *
     * @param prerequis L'ensemble de prérequis à tester.
     * @param resultatAttendu L'ensemble attendu après l'appel, si aucune exception n'est levée.
     * @param exceptionAttendue Le type d'exception qui doit être levée, ou null si aucune n'est attendue.
     */
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

    /**
     * Méthode de test principale pour l'ajout d'un prérequis ({@code addPrerequisite}).
     * Valide l'ajout simple, le non-ajout d'un doublon et le cas d'erreur (ajout de null).
     */
    @Test
    public void testAddPrerequisite() {
        System.out.println("\n** testAddPrerequisite() **");

        System.out.println("Cas normaux :");
        testCasAddPrerequisite(new Competence("Java"), 1, null);

        System.out.println("Cas limite :");
        Competence c = new Competence("Data Science");
        c.addPrerequisite(new Competence("SQL"));
        // L'ajout d'un doublon ne doit pas augmenter la taille de l'ensemble (Set).
        c.addPrerequisite(new Competence("SQL")); 
        assertEquals("L'ajout d'un doublon ne doit pas changer la taille du set.", 1, c.getPrerequisites().size());

        System.out.println("Cas erreur :");
        testCasAddPrerequisite(null, 0, IllegalArgumentException.class);
    }

    /**
     * Méthode d'aide pour tester un cas spécifique de {@code addPrerequisite}.
     *
     * @param prerequis La compétence à ajouter comme prérequis.
     * @param tailleAttendue La taille attendue de l'ensemble de prérequis après l'ajout.
     * @param exceptionAttendue Le type d'exception qui doit être levée, ou null si aucune n'est attendue.
     */
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