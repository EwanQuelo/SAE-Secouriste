package fr.erm.sae201;

import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Role; // CHANGEMENT : Import de la vraie énumération
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de test pour la classe de persistance CompteUtilisateur.
 * Utilise JUnit 4 et suit un format spécifique. (VERSION CORRIGÉE)
 */
public class CompteUtilisateurTest {

    // --- Tests pour le Constructeur et les Getters ---

    @Test
    public void testConstructorAndGetters() {
        System.out.println("** testConstructorAndGetters() **");

        System.out.println("Cas normal (Secouriste) :");
        // Un compte typique pour un secouriste, avec un ID associé.
        testCasConstructor("secouriste@test.com", "hash123", Role.SECOURISTE, 10L);

        System.out.println("Cas limite (Administrateur) :");
        // Un compte pour un admin, qui n'a pas d'ID de secouriste (idSecouriste est null).
        testCasConstructor("admin@test.com", "hash456", Role.ADMINISTRATEUR, null);
    }

    /**
     * Méthode d'aide pour tester le constructeur.
     * Crée un CompteUtilisateur et vérifie que tous les getters retournent les bonnes valeurs initiales.
     */
    private void testCasConstructor(String login, String hash, Role role, Long idSecouriste) {
        // Il n'y a pas d'exception attendue ici, car le constructeur est simple.
        try {
            CompteUtilisateur compte = new CompteUtilisateur(login, hash, role, idSecouriste);
            
            assertEquals("Le login n'est pas correct.", login, compte.getLogin());
            assertEquals("Le hash du mot de passe n'est pas correct.", hash, compte.getMotDePasseHash());
            assertEquals("Le rôle n'est pas correct.", role, compte.getRole());
            assertEquals("L'ID du secouriste n'est pas correct.", idSecouriste, compte.getIdSecouriste());

        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée par le constructeur : " + e.getMessage());
        }
    }

    // --- Tests pour les Setters ---

    @Test
    public void testSetRole() {
        System.out.println("\n** testSetRole() **");

        System.out.println("Cas normal :");
        // Changer un rôle existant pour un autre.
        testCasSetRole(Role.ADMINISTRATEUR, Role.ADMINISTRATEUR);

        System.out.println("Cas limite :");
        // Définir le rôle à null.
        testCasSetRole(null, null);
    }

    /**
     * Méthode d'aide pour tester un setter.
     * Crée un compte, modifie une valeur, et vérifie que le changement a bien eu lieu.
     */
    private void testCasSetRole(Role nouveauRole, Role resultatAttendu) {
        CompteUtilisateur compte = new CompteUtilisateur("test@test.com", "hash", Role.SECOURISTE, 1L);
        try {
            compte.setRole(nouveauRole);
            assertEquals("Le rôle n'a pas été modifié correctement.", resultatAttendu, compte.getRole());
        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée par un setter simple : " + e.getMessage());
        }
    }
    
    // --- Test pour Equals et HashCode ---

    @Test
    public void testEqualsAndHashCode() {
        System.out.println("\n** testEqualsAndHashCode() **");

        CompteUtilisateur c1 = new CompteUtilisateur("user@test.com", "hash1", Role.SECOURISTE, 1L);
        CompteUtilisateur c2 = new CompteUtilisateur("user@test.com", "hash2", Role.ADMINISTRATEUR, 2L); // Même login
        CompteUtilisateur c3 = new CompteUtilisateur("other@test.com", "hash1", Role.SECOURISTE, 1L); // Login différent

        System.out.println("Cas normal : deux objets avec le même login");
        assertTrue("Deux comptes avec le même login doivent être égaux.", c1.equals(c2));
        assertEquals("Les hashCodes de deux comptes égaux doivent être identiques.", c1.hashCode(), c2.hashCode());

        System.out.println("Cas limite : comparaison avec soi-même");
        assertTrue("Un compte doit être égal à lui-même.", c1.equals(c1));

        System.out.println("Cas erreur : deux objets avec des logins différents");
        assertFalse("Deux comptes avec des logins différents ne doivent pas être égaux.", c1.equals(c3));
        
        System.out.println("Cas erreur : comparaison avec null");
        assertFalse("Un compte ne doit pas être égal à null.", c1.equals(null));

        System.out.println("Cas erreur : comparaison avec un autre type d'objet");
        assertFalse("Un compte ne doit pas être égal à un objet d'un autre type.", c1.equals("user@test.com"));
    }
}