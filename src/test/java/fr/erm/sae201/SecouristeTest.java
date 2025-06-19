package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.Competence;
import fr.erm.sae201.metier.persistence.Secouriste;
import org.junit.Test;
import java.util.Date;

import java.util.Set;
import static org.junit.Assert.*;

/**
 * Classe de tests unitaires pour la classe de persistance Secouriste.
 * Ces tests valident le comportement des constructeurs, des setters (notamment
 * avec validation de format pour l'email), la copie défensive des objets mutables,
 * et le contrat des méthodes equals et hashCode.
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.0
 */
public class SecouristeTest {

    /**
     * Crée un objet Secouriste valide pour les besoins des tests.
     *
     * @return Un nouvel objet Secouriste avec des données de test valides.
     */
    private Secouriste createValidSecouriste() {
        return new Secouriste(1L, "Dupont", "Jean", new Date(), "jean.dupont@email.com", "0612345678",
                "1 rue de la Paix");
    }

    /**
     * Méthode de test principale pour les constructeurs de la classe Secouriste.
     * Elle valide un cas de création normal et plusieurs cas d'erreur (ID invalide, nom null).
     */
    @Test
    public void testConstructors() {
        System.out.println("** testConstructors() **");

        System.out.println("Cas normal (avec ID) :");
        testCasConstructor(1L, "Martin", "Paul", new Date(), "paul.martin@email.com", null);

        System.out.println("Cas erreur (avec ID) :");
        // ID invalide (<= 0)
        try {
            new Secouriste(0L, "Nom", "Prenom", new Date(), "test@test.com", null, null);
            fail("Exception attendue pour ID <= 0 mais non levée.");
        } catch (IllegalArgumentException e) {
            // Succès attendu
        }
        // Nom null
        try {
            new Secouriste(1L, null, "Prenom", new Date(), "test@test.com", null, null);
            fail("Exception attendue pour nom null mais non levée.");
        } catch (IllegalArgumentException e) {
            // Succès attendu
        }
    }

    /**
     * Méthode d'aide qui exécute un scénario de test pour le constructeur.
     *
     * @param id L'identifiant à tester.
     * @param nom Le nom à tester.
     * @param prenom Le prénom à tester.
     * @param date La date de naissance à tester.
     * @param email L'email à tester.
     * @param exceptionAttendue La classe de l'exception attendue, ou null si aucune n'est attendue.
     */
    private void testCasConstructor(long id, String nom, String prenom, Date date, String email,
            Class<? extends Throwable> exceptionAttendue) {
        try {
            Secouriste s = new Secouriste(id, nom, prenom, date, email, null, null);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals(id, s.getId());
            assertEquals(nom, s.getNom());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }

    /**
     * Méthode de test principale pour le setter de l'email, qui inclut une validation de format.
     */
    @Test
    public void testSetEmail() {
        System.out.println("\n** testSetEmail() **");

        System.out.println("Cas normal :");
        testCasSetEmail("valide@email.org", null);

        System.out.println("Cas limite :");
        testCasSetEmail("a@b.c", null);

        System.out.println("Cas erreur :");
        testCasSetEmail(null, IllegalArgumentException.class);
        testCasSetEmail("  ", IllegalArgumentException.class);
        testCasSetEmail("email.sans.arobase.com", IllegalArgumentException.class);
        testCasSetEmail("email@sanspoint", IllegalArgumentException.class);
    }

    /**
     * Méthode d'aide qui exécute un scénario de test pour setEmail.
     *
     * @param email L'adresse email à tester.
     * @param exceptionAttendue La classe de l'exception attendue, ou null si aucune n'est attendue.
     */
    private void testCasSetEmail(String email, Class<? extends Throwable> exceptionAttendue) {
        Secouriste s = createValidSecouriste();
        try {
            s.setEmail(email);
            if (exceptionAttendue != null) {
                fail("Exception attendue mais non levée: " + exceptionAttendue.getName());
            }
            assertEquals("L'email n'a pas été correctement défini.", email, s.getEmail());
        } catch (Throwable e) {
            if (exceptionAttendue == null) {
                fail("Exception inattendue levée: " + e.getClass().getName());
            }
            assertEquals("Le type d'exception levée n'est pas correct.", exceptionAttendue, e.getClass());
        }
    }

    /**
     * Teste la robustesse des getters en vérifiant qu'ils retournent des copies
     * défensives des objets mutables (Date et Set), empêchant ainsi toute
     * modification externe de l'état interne de l'objet Secouriste.
     */
    @Test
    public void testGettersReturnCopies() {
        System.out.println("\n** testGettersReturnCopies() **");
        Secouriste s = createValidSecouriste();

        // Test pour DateNaissance
        System.out.println("Cas : Modification de la date de naissance récupérée");
        Date dateRecup = s.getDateNaissance();
        dateRecup.setTime(0);
        assertNotEquals("La date originale ne doit pas être modifiée (copie défensive).", dateRecup.getTime(),
                s.getDateNaissance().getTime());

        // Test pour Competences
        System.out.println("Cas : Modification du set de compétences récupéré");
        s.addCompetence(new Competence("PSE1"));
        Set<Competence> setRecup = s.getCompetences();
        setRecup.add(new Competence("CI"));
        assertEquals("Le set original ne doit contenir qu'un élément.", 1, s.getCompetences().size());
    }

    /**
     * Valide l'implémentation des méthodes equals et hashCode.
     * L'égalité est basée sur l'identifiant (ID) du secouriste.
     */
    @Test
    public void testEqualsAndHashCode() {
        System.out.println("\n** testEqualsAndHashCode() **");

        Secouriste s1 = new Secouriste(100L, "NomA", "PrenomA", new Date(), "a@a.com", null, null);
        Secouriste s2 = new Secouriste(100L, "NomB", "PrenomB", new Date(), "b@b.com", null, null);
        Secouriste s3 = new Secouriste(101L, "NomA", "PrenomA", new Date(), "a@a.com", null, null);

        System.out.println("Cas normal : deux objets avec le même ID");
        assertTrue("Deux Secouristes avec le même ID doivent être égaux.", s1.equals(s2));
        assertEquals("Les hashCodes de deux Secouristes égaux doivent être identiques.", s1.hashCode(), s2.hashCode());

        System.out.println("Cas erreur : deux objets avec des ID différents");
        assertFalse("Deux Secouristes avec des ID différents ne doivent pas être égaux.", s1.equals(s3));
    }
}