// Fichier: src/main/java/fr/erm/sae201/Scenario.java
package fr.erm.sae201;

import fr.erm.sae201.exception.AuthenticationException;
import fr.erm.sae201.exception.EntityNotFoundException;
import fr.erm.sae201.metier.persistence.CompteUtilisateur;
import fr.erm.sae201.metier.persistence.Journee;
import fr.erm.sae201.metier.persistence.Secouriste;
import fr.erm.sae201.metier.service.AuthService;
import fr.erm.sae201.metier.service.SecouristeMngt;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

/**
 * Classe principale pour implémenter et tester le scénario modélisé
 * dans le diagramme de séquence
 * <p>
 * Ce scénario simule, via des appels aux couches de service, le parcours
 * complet d'un secouriste dans l'application :
 * <ol>
 *     <li>Connexion au système.</li>
 *     <li>Consultation et modification de ses disponibilités.</li>
 *     <li>Déconnexion.</li>
 * </ol>
 * Chaque étape est affichée dans la console pour suivre le déroulement.
 * <p>
 * <b>Prérequis : login: "test@mail.com" avec mdp : "password" dans la bdd
 *
 * @author Ewan QUELO
 * @author Raphael MILLE
 * @author Matheo BIET
 * @version 1.1
 */
public class Scenario {

    /**
     * Point d'entrée principal pour l'exécution du scénario de test.
     *
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        System.out.println("--- DÉBUT DU SCÉNARIO DE TEST ---");

        // Instanciation des services qui contiennent la logique métier
        AuthService authService = new AuthService();
        SecouristeMngt secouristeMngt = new SecouristeMngt();

        // CONNEXION 
        System.out.println("\n[ÉTAPE 1] Tentative de connexion du secouriste...");
        CompteUtilisateur compteConnecte = null;
        try {
            String login = "test@mail.com";
            String motDePasseEnClair = "password"; 

            // Appel au service d'authentification comme le ferait le LoginController
            compteConnecte = authService.login(login, motDePasseEnClair);

            System.out.println("-> Connexion réussie pour : " + compteConnecte.getLogin());
            System.out.println("   Rôle : " + compteConnecte.getRole());
            System.out.println("   ID Secouriste associé : " + compteConnecte.getIdSecouriste());

        } catch (EntityNotFoundException e) {
            System.out.println("-> Échec de la connexion : Utilisateur inconnu. Assurez-vous que l'utilisateur de test existe en base.");
            terminerScenarioEnErreur();
            return;
        } catch (AuthenticationException e) {
            System.out.println("-> Échec de la connexion : " + e.getMessage());
            terminerScenarioEnErreur();
            return;
        }

        // GESTION DES DISPONIBILITÉS 
        System.out.println("\n[ÉTAPE 2] Gestion des disponibilités...");

        if (compteConnecte.getIdSecouriste() == null) {
            System.out.println("-> Erreur : Le compte connecté n'est pas associé à un profil secouriste.");
            terminerScenarioEnErreur();
            return;
        }
        Long secouristeId = compteConnecte.getIdSecouriste();

        // Consultation des disponibilités avant
        System.out.println("   Consultation des disponibilités actuelles...");
        Secouriste secouriste = secouristeMngt.getSecouriste(secouristeId);
        Set<Journee> disponibilitesInitiales = secouriste.getDisponibilites();
        System.out.println("   Disponibilités avant modification (" + disponibilitesInitiales.size() + ") :");
        disponibilitesInitiales.forEach(j -> System.out.println("     - " + j.getDate()));

        // Simulation de l'ajout d'une disponibilité comme le ferait UserDispoController
        LocalDate dateAManipuler = LocalDate.of(2025, Month.MAY, 23);
        System.out.println("\n   Action : Ajout de la disponibilité pour le " + dateAManipuler);
        boolean ajoutOk = secouristeMngt.addAvailability(secouristeId, dateAManipuler);
        if (ajoutOk) {
            System.out.println("   -> Succès : Disponibilité ajoutée en base de données.");
        } else {
            System.out.println("   -> Échec : La disponibilité n'a pas pu être ajoutée (peut-être existait-elle déjà ?).");
        }

        Secouriste secouristeApresAjout = secouristeMngt.getSecouriste(secouristeId);
        System.out.println("   Disponibilités après ajout (" + secouristeApresAjout.getDisponibilites().size() + ") :");
        secouristeApresAjout.getDisponibilites().forEach(j -> System.out.println("     - " + j.getDate()));

        // Simulation de la suppression de la même disponibilité pour montrer le cycle complet
        System.out.println("\n   Action : Suppression de la disponibilité pour le " + dateAManipuler);
        boolean suppressionOk = secouristeMngt.removeAvailability(secouristeId, dateAManipuler);
        if (suppressionOk) {
            System.out.println("   -> Succès : Disponibilité supprimée.");
        } else {
            System.out.println("   -> Échec : La disponibilité n'a pas pu être supprimée.");
        }

        Secouriste secouristeApresSuppression = secouristeMngt.getSecouriste(secouristeId);
        System.out.println("   Disponibilités finales (" + secouristeApresSuppression.getDisponibilites().size() + ") :");
        secouristeApresSuppression.getDisponibilites().forEach(j -> System.out.println("     - " + j.getDate()));


        // DÉCONNEXION 
        System.out.println("\n[ÉTAPE 3] Déconnexion...");
        compteConnecte = null;
        System.out.println("-> L'utilisateur est déconnecté. La variable 'compteConnecte' est maintenant null.");

        System.out.println("\n--- FIN DU SCÉNARIO DE TEST ---");
    }

    /**
     * Affiche un message standard pour indiquer que le scénario s'est terminé
     * prématurément a cause erreur
     */
    private static void terminerScenarioEnErreur() {
        System.out.println("--- FIN DU SCÉNARIO (ÉCHEC) ---");
    }
}